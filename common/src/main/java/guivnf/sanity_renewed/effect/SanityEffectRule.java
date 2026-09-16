package guivnf.sanity_renewed.effect;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.List;

/**
 * A single data pack rule from {@code data/<namespace>/sanity/effect/*.json}:
 * <pre>
 * {
 *   "effect": "minecraft:weakness",
 *   "level": 0,
 *   "sanity": 50
 * }
 * </pre>
 * The effect is applied while the player's sanity is at or below {@code sanity}.
 * {@code level} is the amplifier (0 = level I, 1 = level II, ...).
 */
public record SanityEffectRule(Holder<MobEffect> effect, int level, float sanity)
{
    public static final Codec<SanityEffectRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(SanityEffectRule::effect),
            Codec.INT.optionalFieldOf("level", 0).forGetter(SanityEffectRule::level),
            Codec.FLOAT.fieldOf("sanity").forGetter(SanityEffectRule::sanity)
    ).apply(instance, SanityEffectRule::new));

    public static final Codec<List<SanityEffectRule>> LIST_CODEC = CODEC.listOf();

    /**
     * A data pack file may contain either a single rule object or an array of rules.
     */
    public static final Codec<List<SanityEffectRule>> FILE_CODEC = Codec.either(CODEC, LIST_CODEC)
            .xmap(
                    either -> either.map(List::of, list -> list),
                    list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list));
}
