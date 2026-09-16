package guivnf.sanity_renewed.effect;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import guivnf.sanity_renewed.SanityMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Loads data pack sanity effect rules from {@code data/<namespace>/sanity/effect/*.json}.
 * The effects are applied server-side only, so no client sync is required.
 */
public final class SanityEffectManager implements ResourceManagerReloadListener
{
    public static final SanityEffectManager INSTANCE = new SanityEffectManager();
    public static final String DIRECTORY = "sanity/effect";

    private static volatile List<SanityEffectRule> rules = List.of();

    private SanityEffectManager() {}

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager)
    {
        List<SanityEffectRule> parsed = new ArrayList<>();
        Map<ResourceLocation, Resource> resources =
                resourceManager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json"));

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet())
        {
            try (Reader reader = entry.getValue().openAsReader())
            {
                SanityEffectRule.FILE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(readAll(reader)))
                        .resultOrPartial(error -> SanityMod.LOGGER.error("Failed to parse sanity effect rule {}: {}", entry.getKey(), error))
                        .ifPresent(parsed::addAll);
            }
            catch (Exception e)
            {
                SanityMod.LOGGER.error("Failed to read sanity effect rule {}", entry.getKey(), e);
            }
        }

        rules = List.copyOf(parsed);
        SanityMod.LOGGER.info("Loaded {} sanity effect rule(s)", rules.size());
    }

    private static String readAll(Reader reader) throws java.io.IOException
    {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int read;
        while ((read = reader.read(buffer)) != -1)
            builder.append(buffer, 0, read);
        return builder.toString();
    }

    public static List<SanityEffectRule> getRules()
    {
        return rules;
    }
}
