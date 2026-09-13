package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.dries007.tfc.util.climate.Climate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class TemperatureExtreme implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        float temperature = Climate.getInstantTemperature(player.level(), player.blockPosition());
        if (temperature > ConfigProxy.getHotTemperature(dim) && ConfigProxy.getAffectHot(dim))
            return ConfigProxy.getHot(dim);
        if (temperature < ConfigProxy.getColdTemperature(dim) && ConfigProxy.getAffectCold(dim))
            return ConfigProxy.getCold(dim);
        return 0;
    }
}
