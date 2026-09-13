package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class Thirsty implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        if (!ConfigProxy.getAffectThirsty(dim))
            return 0;
        float thirst = IPlayerInfo.get(player).getThirst();
        return thirst <= ConfigProxy.getThirstyThreshold(dim)
                ? ConfigProxy.getThirsty(dim)
                : 0;
    }
}
