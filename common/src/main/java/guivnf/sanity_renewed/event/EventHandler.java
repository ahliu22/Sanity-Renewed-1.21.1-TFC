package guivnf.sanity_renewed.event;

import guivnf.sanity_renewed.SanityProcessor;
import guivnf.sanity_renewed.command.SanityCommand;
import guivnf.sanity_renewed.food.FoodSanityManager;
import guivnf.sanity_renewed.passive.Jukebox;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class EventHandler
{
    private EventHandler() {}

    public static void register()
    {
        TickEvent.PLAYER_POST.register(EventHandler::onPlayerTick);
        TickEvent.SERVER_LEVEL_POST.register(SanityProcessor::tickLevel);

        EntityEvent.LIVING_HURT.register(EventHandler::onLivingHurt);

        PlayerEvent.PLAYER_ADVANCEMENT.register(SanityProcessor::handlePlayerGotAdvancement);
        PlayerEvent.PLAYER_JOIN.register(FoodSanityManager::sendToPlayer);

        CommandRegistrationEvent.EVENT.register((dispatcher, registry, env) ->
                SanityCommand.register(dispatcher));

        LifecycleEvent.SERVER_STARTING.register(FoodSanityManager::setServer);

        LifecycleEvent.SERVER_STOPPING.register(server ->
        {
            FoodSanityManager.setServer(null);
            Jukebox.JUKEBOXES.clear();
            Jukebox.UNSETTLING_JUKEBOXES.clear();
        });
    }

    private static void onPlayerTick(Player player)
    {
        if (player instanceof ServerPlayer sp)
            SanityProcessor.tickPlayer(sp);
    }

    private static EventResult onLivingHurt(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source, float amount)
    {
        if (entity instanceof ServerPlayer sp)
        {
            SanityProcessor.handlePlayerHurt(sp, amount);
        }
        return EventResult.pass();
    }
}
