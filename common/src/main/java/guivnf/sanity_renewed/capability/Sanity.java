package guivnf.sanity_renewed.capability;

import guivnf.sanity_renewed.ActiveSanitySources;
import guivnf.sanity_renewed.util.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public class Sanity implements ISanity, IPassiveSanity, IPersistentSanity
{
    private boolean m_dirty = true;
    private int m_garlandTimer;
    private int m_innerEntityKills;
    private int m_innerEntityKillDecay;
    private int m_sleepTicks;
    private int m_sleepGrace;
    private long m_sleepStartTime;
    private boolean m_sleepAwarded;
    private float m_sanityVal;
    private float m_passive;

    private final int[] m_cds = new int[ActiveSanitySources.AMOUNT];
    private final Map<Integer, Integer> m_itemCds = new HashMap<>();

    public Sanity()
    {
    }

    @Override
    public void serializeNBT(CompoundTag tag)
    {
        tag.putFloat("sanity.sanity", m_sanityVal);
        tag.putInt("sanity.garland_timer", m_garlandTimer);
        tag.putInt("sanity.inner_entity_kills", m_innerEntityKills);
        tag.putInt("sanity.inner_entity_kill_decay", m_innerEntityKillDecay);

        tag.putInt("sanity.sleeping", m_cds[ActiveSanitySources.SLEEPING]);
        tag.putInt("sanity.animal_breeding", m_cds[ActiveSanitySources.BREEDING_ANIMALS]);
        tag.putInt("sanity.eating", m_cds[ActiveSanitySources.EATING]);

        serializeItemCds(tag);
    }

    @Override
    public void deserializeNBT(CompoundTag tag)
    {
        setSanity(tag.getFloat("sanity.sanity"));
        setGarlandTimer(tag.getInt("sanity.garland_timer"));
        setInnerEntityKills(tag.getInt("sanity.inner_entity_kills"));
        setInnerEntityKillDecay(tag.getInt("sanity.inner_entity_kill_decay"));

        m_cds[ActiveSanitySources.SLEEPING] = tag.getInt("sanity.sleeping");
        m_cds[ActiveSanitySources.BREEDING_ANIMALS] = tag.getInt("sanity.animal_breeding");
        m_cds[ActiveSanitySources.EATING] = tag.getInt("sanity.eating");

        deserializeItemCds(tag);
    }

    public void serialize(FriendlyByteBuf buf)
    {
        buf.writeFloat(m_sanityVal);
        buf.writeFloat(m_passive);
    }

    public void deserialize(FriendlyByteBuf buf)
    {
        m_sanityVal = buf.readFloat();
        m_passive = buf.readFloat();
    }

    @Override
    public float getSanity()
    {
        return m_sanityVal;
    }

    @Override
    public void setSanity(float value)
    {
        m_sanityVal = MathHelper.clampNorm(value);
        m_dirty = true;
    }

    @Override
    public float getPassiveIncrease()
    {
        return m_passive;
    }

    @Override
    public void setPassiveIncrease(float value)
    {
        m_passive = value;
        m_dirty = true;
    }

    public boolean getDirty()
    {
        return m_dirty;
    }

    public void setDirty(boolean value)
    {
        m_dirty = value;
    }

    @Override
    public int[] getActiveSourcesCooldowns()
    {
        return m_cds;
    }

    @Override
    public Map<Integer, Integer> getItemCooldowns()
    {
        return m_itemCds;
    }

    @Override
    public void setGarlandTimer(int value)
    {
        m_garlandTimer = value;
    }

    @Override
    public int getGarlandTimer()
    {
        return m_garlandTimer;
    }

    @Override
    public void setInnerEntityKills(int value)
    {
        m_innerEntityKills = value;
    }

    @Override
    public int getInnerEntityKills()
    {
        return m_innerEntityKills;
    }

    @Override
    public void setInnerEntityKillDecay(int value)
    {
        m_innerEntityKillDecay = value;
    }

    @Override
    public int getInnerEntityKillDecay()
    {
        return m_innerEntityKillDecay;
    }

    public void setSleepTicks(int value)
    {
        m_sleepTicks = value;
    }

    public int getSleepTicks()
    {
        return m_sleepTicks;
    }

    public void setSleepGrace(int value)
    {
        m_sleepGrace = value;
    }

    public int getSleepGrace()
    {
        return m_sleepGrace;
    }

    public void setSleepStartTime(long value)
    {
        m_sleepStartTime = value;
    }

    public long getSleepStartTime()
    {
        return m_sleepStartTime;
    }

    public void setSleepAwarded(boolean value)
    {
        m_sleepAwarded = value;
    }

    public boolean isSleepAwarded()
    {
        return m_sleepAwarded;
    }

    private void serializeItemCds(CompoundTag tag)
    {
        long[] itemCds = new long[m_itemCds.size()];
        int i = 0;

        for (Map.Entry<Integer, Integer> entry : m_itemCds.entrySet())
        {
            long val = entry.getKey();
            val <<= Long.SIZE / 2;
            val |= entry.getValue();

            itemCds[i] = val;

            i++;
        }

        tag.putLongArray("sanity.item_cooldowns", itemCds);
    }

    private void deserializeItemCds(CompoundTag tag)
    {
        long[] itemCds = tag.getLongArray("sanity.item_cooldowns");
        m_itemCds.clear();

        for (long itemCd : itemCds)
        {
            m_itemCds.put((int)(itemCd >> Long.SIZE / 2), (int)itemCd);
        }
    }
}
