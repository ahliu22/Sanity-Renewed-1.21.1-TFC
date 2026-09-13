package guivnf.sanity_renewed.net;

import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.food.FoodSanityManager;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class PacketHandler
{
    public static final ResourceLocation SANITY_SYNC = ResourceLocation.fromNamespaceAndPath(SanityMod.MOD_ID, "sanity_sync");
    public static final ResourceLocation FOOD_SANITY_SYNC = ResourceLocation.fromNamespaceAndPath(SanityMod.MOD_ID, "food_sanity_sync");

    private static final int MAX_FOOD_SANITY_JSON = 1_048_576;

    private PacketHandler() {}

    public static void init()
    {
        if (Platform.getEnvironment() == Env.SERVER)
        {
            NetworkManager.registerS2CPayloadType(SANITY_SYNC);
            NetworkManager.registerS2CPayloadType(FOOD_SANITY_SYNC);
        }
    }

    public static void initClient()
    {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SANITY_SYNC, (buf, ctx) ->
        {
            float sanity = buf.readFloat();
            float passive = buf.readFloat();
            ctx.queue(() ->
            {
                if (ctx.getEnvironment() != Env.CLIENT) return;
                Player player = ctx.getPlayer();
                if (player == null) return;
                Sanity s = SanityHolder.get(player);
                if (s == null) return;
                s.setSanity(sanity);
                s.setPassiveIncrease(passive);
                s.setDirty(false);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, FOOD_SANITY_SYNC, (buf, ctx) ->
        {
            String json = buf.readUtf(MAX_FOOD_SANITY_JSON);
            ctx.queue(() ->
            {
                if (ctx.getEnvironment() != Env.CLIENT) return;
                FoodSanityManager.loadFromJson(json);
            });
        });
    }

    public static void sendSanityToPlayer(ServerPlayer player, Sanity sanity)
    {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
        sanity.serialize(buf);
        NetworkManager.sendToPlayer(player, SANITY_SYNC, buf);
    }

    public static void sendFoodSanityToPlayer(ServerPlayer player, String json)
    {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
        buf.writeUtf(json, MAX_FOOD_SANITY_JSON);
        NetworkManager.sendToPlayer(player, FOOD_SANITY_SYNC, buf);
    }
}
