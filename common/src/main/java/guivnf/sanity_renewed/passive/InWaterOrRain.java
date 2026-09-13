package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import org.jetbrains.annotations.NotNull;

public class InWaterOrRain implements IPassiveSanitySource
{
    private static final ResourceLocation SPRING_WATER = ResourceLocation.fromNamespaceAndPath("tfc", "spring_water");
    private static final ResourceLocation FLOWING_SPRING_WATER = ResourceLocation.fromNamespaceAndPath("tfc", "flowing_spring_water");
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        if (isInHotSpring(player))
            return ConfigProxy.getHotSpring(dim);

        boolean inRain = isInRain(player);
        boolean inWater = !inRain && player.isInWater();
        if (inRain || (inWater && ConfigProxy.getAffectInWater(dim)))
            return ConfigProxy.getRaining(dim);

        return 0;
    }

    private static boolean isInHotSpring(ServerPlayer player)
    {
        FluidState fluid = player.level().getFluidState(player.blockPosition());
        Fluid source = BuiltInRegistries.FLUID.get(SPRING_WATER);
        Fluid flowing = BuiltInRegistries.FLUID.get(FLOWING_SPRING_WATER);
        return (source != null && fluid.getType() == source)
                || (flowing != null && fluid.getType() == flowing);
    }

    private static boolean isInRain(ServerPlayer player)
    {
        BlockPos pos = player.blockPosition();
        return player.level().isRainingAt(pos)
                || player.level().isRainingAt(BlockPos.containing(pos.getX(), player.getBoundingBox().maxY, pos.getZ()));
    }
}
