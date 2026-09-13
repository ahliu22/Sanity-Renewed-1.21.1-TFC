package guivnf.sanity_renewed.config;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ConfigPassiveBlock
{
    public float m_sanity;
    public float m_rad;
    public boolean m_naturallyGend;
    public boolean m_isTag;
    public ResourceLocation m_name;
    public Map<String, Boolean> m_props = new HashMap<>();
    public Map<String, IntPropertyCondition> m_intProps = new HashMap<>();

    /**
     * A comparison against an integer block state property, e.g. {@code heat_level>0}.
     */
    public record IntPropertyCondition(String property, Operator operator, int value)
    {
        public enum Operator
        {
            GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL, EQUAL
        }

        public boolean test(int actual)
        {
            return switch (operator)
            {
                case GREATER -> actual > value;
                case GREATER_OR_EQUAL -> actual >= value;
                case LESS -> actual < value;
                case LESS_OR_EQUAL -> actual <= value;
                case EQUAL -> actual == value;
            };
        }

        public static IntPropertyCondition parse(String entry)
        {
            String[] operators = {">=", "<=", ">", "<", "="};
            for (String op : operators)
            {
                int index = entry.indexOf(op);
                if (index <= 0) continue;

                String key = entry.substring(0, index).trim();
                String valueStr = entry.substring(index + op.length()).trim();
                try
                {
                    int parsed = Integer.parseInt(valueStr);
                    Operator operator = switch (op)
                    {
                        case ">=" -> Operator.GREATER_OR_EQUAL;
                        case "<=" -> Operator.LESS_OR_EQUAL;
                        case ">" -> Operator.GREATER;
                        case "<" -> Operator.LESS;
                        default -> Operator.EQUAL;
                    };
                    return new IntPropertyCondition(key, operator, parsed);
                }
                catch (NumberFormatException e)
                {
                    return null;
                }
            }
            return null;
        }
    }
}
