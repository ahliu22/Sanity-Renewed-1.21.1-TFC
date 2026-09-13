package guivnf.sanity_renewed.capability;

import java.util.Map;

public interface IPersistentSanity
{
    int[] getActiveSourcesCooldowns();

    Map<Integer, Integer> getItemCooldowns();

    void setGarlandTimer(int value);

    int getGarlandTimer();

    void setInnerEntityKills(int value);

    int getInnerEntityKills();

    void setInnerEntityKillDecay(int value);

    int getInnerEntityKillDecay();
}
