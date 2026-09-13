package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TFCAnimalProperties.class)
public interface MixinTFCAnimalProperties
{
    @Inject(method = "onFertilized(Lnet/dries007/tfc/common/entities/livestock/TFCAnimalProperties;)V",
            at = @At("HEAD"))
    private void sanity_renewed$onFertilized(TFCAnimalProperties male, CallbackInfo ci)
    {
        if ((Object) this instanceof Entity entity && !entity.level().isClientSide())
            SanityProcessor.handleTfcAnimalsBred(entity);
    }
}
