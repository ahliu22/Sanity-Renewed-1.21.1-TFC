package guivnf.sanity_renewed;

import guivnf.sanity_renewed.capability.IPersistentSanity;
import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.config.ConfigItem;
import guivnf.sanity_renewed.config.ConfigItemCategory;
import guivnf.sanity_renewed.config.ConfigProxy;
import guivnf.sanity_renewed.food.FoodSanityManager;
import guivnf.sanity_renewed.item.ItemRegistry;
import guivnf.sanity_renewed.net.PacketHandler;
import guivnf.sanity_renewed.passive.Darkness;
import guivnf.sanity_renewed.passive.Hungry;
import guivnf.sanity_renewed.passive.IPassiveSanitySource;
import guivnf.sanity_renewed.passive.InWaterOrRain;
import guivnf.sanity_renewed.passive.Jukebox;
import guivnf.sanity_renewed.passive.Lightness;
import guivnf.sanity_renewed.passive.Monster;
import guivnf.sanity_renewed.passive.Passive;
import guivnf.sanity_renewed.passive.PassiveBlocks;
import guivnf.sanity_renewed.passive.Pet;
import guivnf.sanity_renewed.passive.PlayerCompany;
import guivnf.sanity_renewed.passive.TemperatureExtreme;
import guivnf.sanity_renewed.passive.Thirsty;
import guivnf.sanity_renewed.util.MathHelper;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class SanityProcessor
{
    private static final RandomSource RAND = RandomSource.create();

    public static final int MAX_GARLAND_TIMER = 60;
    public static final int INNER_ENTITY_KILL_DECAY_TIME = 20 * 30;
    public static final int MIN_SLEEP_TICKS = 100;
    public static final int SLEEP_GRACE_TICKS = 20;
    public static final int SLEEP_SKIP_MARGIN = 40;
    public static final List<IPassiveSanitySource> PASSIVE_SANITY_SOURCES = Arrays.asList(
            new Passive(),
            new InWaterOrRain(),
            new Hungry(),
            new Pet(),
            new Monster(),
            new Darkness(),
            new Lightness(),
            new PassiveBlocks(),
            new PlayerCompany(),
            new Jukebox(),
            new Thirsty(),
            new TemperatureExtreme()
    );

    private SanityProcessor() {}

    private static ResourceLocation dimOf(ServerPlayer player)
    {
        return player.level().dimension().location();
    }

    /**
     * Converts a player-facing sanity percentage (100 = full sanity) into the internal insanity value used by this mod.
     * An effect configured with a sanity threshold applies when {@code getSanity() >= insanityOf(threshold)}.
     */
    public static float insanityOf(float sanityPercent)
    {
        return Mth.clamp(1f - sanityPercent / 100f, 0f, 1f);
    }

    private static float calcPassive(ServerPlayer player, ISanity sanity)
    {
        ResourceLocation dim = dimOf(player);
        float passive = 0;

        for (IPassiveSanitySource pss : PASSIVE_SANITY_SOURCES)
        {
            float val = pss.get(player, sanity, dim);
            val *= getSanityMultiplier(player, val);
            passive += val;
        }

        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.is(ItemRegistry.GARLAND.get()) && sanity instanceof IPersistentSanity ps)
        {
            passive -= .00005 * ConfigProxy.getPosMul(dim);

            int timer = ps.getGarlandTimer() - 1;
            if (timer <= 0)
            {
                headItem.hurtAndBreak(player.isInWaterOrRain() ? 2 : 1, player, EquipmentSlot.HEAD);
                timer = MAX_GARLAND_TIMER;
            }
            ps.setGarlandTimer(timer);
        }

        return passive;
    }

    private static void shareSanity(ServerPlayer player, Sanity cap)
    {
        if (cap.getDirty())
        {
            PacketHandler.sendSanityToPlayer(player, cap);
            cap.setDirty(false);
        }
    }

    public static float getGarlandMultiplier(ServerPlayer player)
    {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ItemRegistry.GARLAND.get()) ? .92f : 1.0f;
    }

    public static float getSanityMultiplier(ServerPlayer player, float value)
    {
        ResourceLocation dim = dimOf(player);
        return value >= 0 ? ConfigProxy.getNegMul(dim) * getGarlandMultiplier(player) : ConfigProxy.getPosMul(dim);
    }

    public static void addSanity(@NotNull ISanity sanity, float value, @NotNull ServerPlayer player)
    {
        if (value == 0.0f)
            return;
        sanity.setSanity(sanity.getSanity() + value * getSanityMultiplier(player, value));
    }

    public static void tickPlayer(final ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator())
            return;

        Sanity s = SanityHolder.get(player);
        if (s == null) return;

        float passive = calcPassive(player, s);
        float snapshot = s.getSanity();
        s.setSanity(s.getSanity() + passive);
        s.setPassiveIncrease(snapshot != s.getSanity() ? passive : 0);

        int[] cds = s.getActiveSourcesCooldowns();
        for (int i = 0; i < cds.length; ++i)
            cds[i] = Mth.clamp(cds[i] - 1, 0, Integer.MAX_VALUE);

        decay(s.getItemCooldowns());

        tickSleep(s, player);
        tickInnerEntityKillDecay(s, player);

        shareSanity(player, s);

        guivnf.sanity_renewed.entity.InnerEntitySpawner.trySpawnForPlayer(player);
    }

    private static void tickSleep(Sanity s, ServerPlayer player)
    {
        if (player.isSleeping())
        {
            if (s.getSleepTicks() == 0)
                s.setSleepStartTime(player.level().getDayTime());
            s.setSleepTicks(s.getSleepTicks() + 1);
            s.setSleepGrace(SLEEP_GRACE_TICKS);
            trySleepAward(s, player);
            return;
        }

        if (s.getSleepGrace() > 0)
        {
            s.setSleepGrace(s.getSleepGrace() - 1);
            trySleepAward(s, player);
            return;
        }

        s.setSleepTicks(0);
        s.setSleepAwarded(false);
    }

    private static void trySleepAward(Sanity s, ServerPlayer player)
    {
        if (s.isSleepAwarded() || s.getSleepTicks() < MIN_SLEEP_TICKS)
            return;
        if (player.level().getDayTime() - s.getSleepStartTime() <= s.getSleepTicks() + SLEEP_SKIP_MARGIN)
            return;

        s.setSleepAwarded(true);
        addSanity(s, ConfigProxy.getSleeping(dimOf(player)), player);
    }

    private static void tickInnerEntityKillDecay(Sanity s, ServerPlayer player)
    {
        int kills = s.getInnerEntityKills();
        float visibleThreshold = insanityOf(ConfigProxy.getInnerEntityVisibleSanity(dimOf(player)));
        if (kills <= 0 || s.getSanity() >= visibleThreshold)
        {
            s.setInnerEntityKillDecay(0);
            return;
        }

        int timer = s.getInnerEntityKillDecay() + 1;
        if (timer >= INNER_ENTITY_KILL_DECAY_TIME)
        {
            s.setInnerEntityKills(kills - 1);
            timer = 0;
        }
        s.setInnerEntityKillDecay(timer);
    }

    private static void decay(Map<Integer, Integer> map)
    {
        for (Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<Integer, Integer> entry = it.next();
            int v = entry.getValue() - 1;
            if (v <= 0) it.remove();
            else entry.setValue(v);
        }
    }

    public static void tickLevel(final ServerLevel level)
    {
        // InnerEntity tracking is deferred to Phase I.
    }

    public static List<Player> getInsanePlayersInArea(final Level levelIn, BlockPos center, int blockRadius)
    {
        if (levelIn == null || center == null)
            return null;
        List<Player> list = new ArrayList<>();
        for (Player player : levelIn.getEntitiesOfClass(
                Player.class,
                AABB.encapsulatingFullBlocks(center.offset(blockRadius, blockRadius, blockRadius), center.offset(-blockRadius, -blockRadius, -blockRadius))))
        {
            Sanity s = SanityHolder.get(player);
            if (s != null && s.getSanity() >= insanityOf(ConfigProxy.getInnerEntityTargetSanity(levelIn.dimension().location())))
                list.add(player);
        }
        return list;
    }

    public static Player getMostInsanePlayer(final Level levelIn)
    {
        if (levelIn == null) return null;
        return getMostInsanePlayer(levelIn, insanityOf(ConfigProxy.getInnerEntityTargetSanity(levelIn.dimension().location())));
    }

    public static Player getMostInsanePlayer(final Level levelIn, float sanityThreshold)
    {
        if (levelIn == null) return null;
        Player toReturn = null;
        float maxSanity = Float.MIN_VALUE;
        for (Player player : levelIn.players())
        {
            if (player.isCreative() || player.isSpectator())
                continue;
            Sanity s = SanityHolder.get(player);
            if (s == null) continue;
            float sanity = s.getSanity();
            if (sanity >= sanityThreshold && sanity > maxSanity)
            {
                maxSanity = sanity;
                toReturn = player;
            }
        }
        return toReturn;
    }

    public static boolean isValidInsaneTarget(LivingEntity target, float sanityThreshold)
    {
        if (!(target instanceof Player player) || player.isCreative() || player.isSpectator())
            return false;

        float threshold = sanityThreshold < 0f
                ? insanityOf(ConfigProxy.getInnerEntityTargetSanity(player.level().dimension().location()))
                : sanityThreshold;

        Sanity s = SanityHolder.get(player);
        return s != null && s.getSanity() >= threshold;
    }

    public static void handleActiveSourceForPlayer(
            ServerPlayer player,
            int id,
            Function<ResourceLocation, Integer> cdSupplier,
            Function<ResourceLocation, Float> sanitySupplier)
    {
        if (player == null || player.isCreative() || player.isSpectator())
            return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;

        ResourceLocation dim = dimOf(player);
        int cd = cdSupplier.apply(dim);

        if (cd <= 0)
        {
            addSanity(s, sanitySupplier.apply(dim), player);
            return;
        }

        int[] cds = s.getActiveSourcesCooldowns();
        int timePassed = cd - cds[id];
        addSanity(s, sanitySupplier.apply(dim) * MathHelper.clampNorm((float) timePassed / cd), player);
        cds[id] = cd;
    }

    public static void handleLevelSleepCompleted(ServerLevel level)
    {
        if (level == null) return;
        for (ServerPlayer player : level.players())
        {
            if (player.isCreative() || player.isSpectator())
                continue;

            Sanity s = SanityHolder.get(player);
            if (s == null || s.isSleepAwarded() || s.getSleepTicks() < MIN_SLEEP_TICKS)
                continue;
            if (!player.isSleeping() && s.getSleepGrace() <= 0)
                continue;

            s.setSleepAwarded(true);
            addSanity(s, ConfigProxy.getSleeping(dimOf(player)), player);
        }
    }

    public static void handlePlayerHurt(ServerPlayer player, float amount)
    {
        if (player == null || player.isCreative() || player.isSpectator() || amount <= 0) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        addSanity(s, amount * ConfigProxy.getHurtRatio(dimOf(player)), player);
    }

    public static void handlePlayerGotAdvancement(ServerPlayer player, AdvancementHolder holder)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        var display = holder.value().display();
        if (display.isEmpty() || !display.get().shouldAnnounceChat()) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        addSanity(s, ConfigProxy.getAdvancement(dimOf(player)), player);
    }

    /**
     * Called when two TFC animals mate. All players within the configured radius of the female animal gain sanity.
     */
    public static void handleTfcAnimalsBred(Entity animal)
    {
        if (animal == null || animal.level().isClientSide()) return;
        ResourceLocation dim = animal.level().dimension().location();
        double radius = ConfigProxy.getAnimalBreedingRadius(dim);
        double radiusSqr = radius * radius;
        for (Player player : animal.level().players())
        {
            if (!(player instanceof ServerPlayer sp) || sp.isCreative() || sp.isSpectator()) continue;
            if (sp.distanceToSqr(animal) <= radiusSqr)
                handleActiveSourceForPlayer(sp, ActiveSanitySources.BREEDING_ANIMALS, ConfigProxy::getAnimalBreedingCooldown, ConfigProxy::getAnimalBreeding);
        }
    }

    public static void handlePlayerAte(ServerPlayer player, ItemStack itemStack)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        IFood food = FoodCapability.get(itemStack);
        if (food == null) return;

        FoodData data = food.getData();
        float total = 0;
        for (Nutrient nutrient : Nutrient.VALUES)
            total += data.nutrient(nutrient);
        if (total <= 0) return;

        ResourceLocation dim = dimOf(player);
        float value = total * ConfigProxy.getEating(dim) * FoodSanityManager.getModifier(itemStack);
        handleActiveSourceForPlayer(player, ActiveSanitySources.EATING, ConfigProxy::getEatingCooldown, d -> value);
    }

    public static void handlePlayerUsedItem(ServerPlayer player, ItemStack itemStack)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;

        ResourceLocation dim = dimOf(player);
        for (ConfigItem citem : ConfigProxy.getItems(dim))
        {
            Item ref = BuiltInRegistries.ITEM.get(citem.m_name);
            if (ref == null || !itemStack.is(ref))
                continue;

            if (!ConfigProxy.getIdToItemCat(dim).containsKey(citem.m_cat))
            {
                SanityMod.LOGGER.warn("player {} used {} from category {}, but no such category is present",
                        player.getDisplayName().getString(), citem.m_name, citem.m_cat);
                return;
            }

            ConfigItemCategory cat = ConfigProxy.getIdToItemCat(dim).get(citem.m_cat);
            if (cat.m_cd <= 0)
            {
                addSanity(s, citem.m_sanity, player);
                return;
            }

            Map<Integer, Integer> itemCds = s.getItemCooldowns();
            if (!itemCds.containsKey(citem.m_cat) || itemCds.get(citem.m_cat) <= 0)
            {
                addSanity(s, citem.m_sanity, player);
            }
            else
            {
                int timePassed = cat.m_cd - itemCds.get(citem.m_cat);
                addSanity(s, citem.m_sanity * MathHelper.clampNorm((float) timePassed / cat.m_cd), player);
            }
            itemCds.put(citem.m_cat, cat.m_cd);
            return;
        }

        if (FoodCapability.has(itemStack))
            handlePlayerAte(player, itemStack);
    }
}
