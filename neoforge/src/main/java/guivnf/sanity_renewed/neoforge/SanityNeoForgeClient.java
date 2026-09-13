package guivnf.sanity_renewed.neoforge;

import guivnf.sanity_renewed.config.ConfigProxy;
import guivnf.sanity_renewed.food.FoodSanityManager;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class SanityNeoForgeClient
{
    private SanityNeoForgeClient() {}

    public static void init()
    {
        NeoForge.EVENT_BUS.addListener(SanityNeoForgeClient::onItemTooltip);
    }

    private static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        IFood food = FoodCapability.get(stack);
        if (food == null) return;

        FoodData data = food.getData();
        float total = 0f;
        for (Nutrient nutrient : Nutrient.VALUES)
            total += data.nutrient(nutrient);
        if (total <= 0f) return;

        ResourceLocation dim = Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.dimension().location()
                : null;
        // The config value is stored internally as an insanity delta (positive = sanity loss), so convert
        // it back into a player-facing sanity percentage for the tooltip.
        float internalValue = total * ConfigProxy.getEating(dim) * FoodSanityManager.getModifier(stack);
        float sanityValue = -internalValue * 100f;

        event.getToolTip().add(Component.translatable(
                "item.sanity_renewed.food_sanity",
                String.format("%+.2f", sanityValue)
        ).withStyle(sanityValue >= 0f ? ChatFormatting.GREEN : ChatFormatting.RED));
    }
}
