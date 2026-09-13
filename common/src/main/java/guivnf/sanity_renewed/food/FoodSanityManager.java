package guivnf.sanity_renewed.food;

import com.mojang.serialization.JsonOps;
import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.net.PacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Loads data pack food sanity rules from {@code data/<namespace>/sanity/food/*.json} and keeps them
 * synced with clients so that food tooltips can show the resulting sanity value.
 */
public final class FoodSanityManager implements ResourceManagerReloadListener
{
    public static final FoodSanityManager INSTANCE = new FoodSanityManager();
    public static final String DIRECTORY = "sanity/food";

    private static volatile List<FoodSanityRule> rules = List.of();
    private static MinecraftServer currentServer;

    private FoodSanityManager() {}

    /**
     * Called from the common server lifecycle events. Used to push updated rules after a data pack reload.
     */
    public static void setServer(MinecraftServer server)
    {
        currentServer = server;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager)
    {
        List<FoodSanityRule> parsed = new ArrayList<>();
        Map<net.minecraft.resources.ResourceLocation, Resource> resources =
                resourceManager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json"));

        for (Map.Entry<net.minecraft.resources.ResourceLocation, Resource> entry : resources.entrySet())
        {
            try (Reader reader = entry.getValue().openAsReader())
            {
                FoodSanityRule.FILE_CODEC.parse(JsonOps.INSTANCE, FoodSanityRule.parseJson(readAll(reader)))
                        .resultOrPartial(error -> SanityMod.LOGGER.error("Failed to parse food sanity rule {}: {}", entry.getKey(), error))
                        .ifPresent(parsed::addAll);
            }
            catch (Exception e)
            {
                SanityMod.LOGGER.error("Failed to read food sanity rule {}", entry.getKey(), e);
            }
        }

        rules = List.copyOf(parsed);
        SanityMod.LOGGER.info("Loaded {} food sanity rule(s)", rules.size());

        if (currentServer != null)
            sendToAll(currentServer);
    }

    private static String readAll(Reader reader) throws java.io.IOException
    {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int read;
        while ((read = reader.read(buffer)) != -1)
            builder.append(buffer, 0, read);
        return builder.toString();
    }

    /**
     * @return The product of all matching rule modifiers (1.0 when no rule matches).
     */
    public static float getModifier(ItemStack stack)
    {
        float modifier = 1f;
        for (FoodSanityRule rule : rules)
        {
            if (rule.ingredient().test(stack))
                modifier *= rule.modifier();
        }
        return modifier;
    }

    public static List<FoodSanityRule> getRules()
    {
        return rules;
    }

    public static void setRules(List<FoodSanityRule> newRules)
    {
        rules = List.copyOf(newRules);
    }

    public static String toJson()
    {
        return FoodSanityRule.LIST_CODEC.encodeStart(JsonOps.INSTANCE, rules)
                .result()
                .map(Object::toString)
                .orElse("[]");
    }

    public static void loadFromJson(String json)
    {
        try
        {
            FoodSanityRule.LIST_CODEC.parse(JsonOps.INSTANCE, FoodSanityRule.parseJson(json))
                    .resultOrPartial(error -> SanityMod.LOGGER.error("Failed to parse synced food sanity rules: {}", error))
                    .ifPresent(FoodSanityManager::setRules);
        }
        catch (Exception e)
        {
            SanityMod.LOGGER.error("Failed to parse synced food sanity rules", e);
        }
    }

    public static void sendToAll(MinecraftServer server)
    {
        String json = toJson();
        for (ServerPlayer player : server.getPlayerList().getPlayers())
            PacketHandler.sendFoodSanityToPlayer(player, json);
    }

    public static void sendToPlayer(ServerPlayer player)
    {
        PacketHandler.sendFoodSanityToPlayer(player, toJson());
    }
}
