package guivnf.sanity_renewed.food;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * A single data pack rule from {@code data/<namespace>/sanity/food/*.json}:
 * <pre>
 * {
 *   "ingredient": { "item": "tfc:food/beef" } // or { "tag": "sanity_renewed:badfoods" }
 *   "modifier": -0.5
 * }
 * </pre>
 * The modifier is multiplied with the sanity value computed from a food's TFC nutrition.
 */
public record FoodSanityRule(Ingredient ingredient, float modifier)
{
    public static final Codec<FoodSanityRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(FoodSanityRule::ingredient),
            Codec.FLOAT.fieldOf("modifier").forGetter(FoodSanityRule::modifier)
    ).apply(instance, FoodSanityRule::new));

    public static final Codec<java.util.List<FoodSanityRule>> LIST_CODEC = CODEC.listOf();

    /**
     * A data pack file may contain either a single rule object or an array of rules.
     */
    public static final Codec<java.util.List<FoodSanityRule>> FILE_CODEC = Codec.either(CODEC, LIST_CODEC)
            .xmap(either -> either.map(java.util.List::of, list -> list), list -> list.size() == 1 ? com.mojang.datafixers.util.Either.left(list.get(0)) : com.mojang.datafixers.util.Either.right(list));

    public static JsonElement parseJson(String json)
    {
        return JsonParser.parseString(json);
    }
}
