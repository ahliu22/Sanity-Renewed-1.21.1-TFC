package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.dries007.tfc.common.entities.livestock.CommonAnimalBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Pet implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        AABB box = new AABB(
                player.position().add(new Vec3(-8.0f, -8.0f, -8.0f)),
                player.position().add(new Vec3(8.0f, 8.0f, 8.0f)));

        float pet = ConfigProxy.getPet(dim);
        if (pet != 0.0f)
        {
            float familiarity = ConfigProxy.getPetFamiliarity(dim);
            List<Entity> tamedAround = new ArrayList<>();
            player.level().getEntities(
                    EntityTypeTest.forClass(Entity.class),
                    box,
                    e -> e instanceof CommonAnimalBehavior animal
                            && animal.getFamiliarity() > familiarity
                            && player.hasLineOfSight(e),
                    tamedAround,
                    1);
            if (!tamedAround.isEmpty())
                return pet;
        }
        return 0;
    }
}
