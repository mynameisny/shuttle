package me.ningyu.app.shuttle.iam.abac;

import me.ningyu.app.shuttle.iam.domain.IamPolicy;
import me.ningyu.app.shuttle.iam.domain.IamPolicyRepository;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;
import org.casbin.jcasbin.persist.Helper;

import java.util.List;
import java.util.Objects;

/**
 * jCasbin 的 JPA 策略适配器：以 {@code iam_casbin_rule} 表为策略存储。
 *
 * <p>jCasbin 在 {@code Enforcer.addPolicy / addGroupingPolicy / removePolicy} 时会回调
 * 本适配器的 {@code addPolicy / removePolicy}，因此策略的增删会同时写库、无需业务侧手动同步。
 * {@code savePolicy} 采用增量管理故不做全量反向持久化。
 */
public class JpaPolicyAdapter implements Adapter
{
    private final IamPolicyRepository repository;

    public JpaPolicyAdapter(IamPolicyRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public void loadPolicy(Model model)
    {
        List<IamPolicy> policies = repository.findAllByOrderByIdAsc();
        for (IamPolicy policy : policies)
        {
            String line = buildLine(policy);
            if (line != null)
            {
                Helper.loadPolicyLine(line, model);
            }
        }
    }

    @Override
    public void savePolicy(Model model)
    {
        // 增量管理：策略增删已由 addPolicy/removePolicy 持久化，无需全量回写。
    }

    @Override
    public void addPolicy(String sec, String ptype, List<String> rule)
    {
        repository.save(toPolicy(ptype, rule));
    }

    @Override
    public void removePolicy(String sec, String ptype, List<String> rule)
    {
        repository.findAllByOrderByIdAsc().stream()
                .filter(p -> Objects.equals(p.getPtype(), ptype) && matches(p, rule))
                .findFirst()
                .ifPresent(repository::delete);
    }

    @Override
    public void removeFilteredPolicy(String sec, String ptype, int fieldIndex, String... fieldValues)
    {
        List<IamPolicy> policies = repository.findAllByOrderByIdAsc();
        for (IamPolicy policy : policies)
        {
            if (Objects.equals(policy.getPtype(), ptype) && matchesFrom(policy, fieldIndex, fieldValues))
            {
                repository.delete(policy);
            }
        }
    }

    /** 将策略行拼接为 jCasbin 的 "ptype, v0, v1, ..." 文本（跳过空值）。 */
    private String buildLine(IamPolicy policy)
    {
        StringBuilder sb = new StringBuilder(policy.getPtype());
        for (String value : valuesOf(policy))
        {
            if (value == null)
            {
                break;
            }
            sb.append(", ").append(value);
        }
        return sb.toString();
    }

    private IamPolicy toPolicy(String ptype, List<String> rule)
    {
        IamPolicy policy = new IamPolicy();
        policy.setPtype(ptype);
        String[] values = valuesOf(policy);
        int n = Math.min(rule.size(), values.length);
        for (int i = 0; i < n; i++)
        {
            setValue(policy, i, rule.get(i));
        }
        return policy;
    }

    private boolean matches(IamPolicy policy, List<String> rule)
    {
        if (rule.size() > 6)
        {
            return false;
        }
        String[] values = valuesOf(policy);
        for (int i = 0; i < rule.size(); i++)
        {
            if (!Objects.equals(values[i], rule.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    private boolean matchesFrom(IamPolicy policy, int fieldIndex, String[] fieldValues)
    {
        String[] values = valuesOf(policy);
        if (fieldIndex < 0 || fieldIndex + fieldValues.length > values.length)
        {
            return false;
        }
        for (int i = 0; i < fieldValues.length; i++)
        {
            if (!Objects.equals(values[fieldIndex + i], fieldValues[i]))
            {
                return false;
            }
        }
        return true;
    }

    private String[] valuesOf(IamPolicy policy)
    {
        return new String[]{policy.getV0(), policy.getV1(), policy.getV2(),
                policy.getV3(), policy.getV4(), policy.getV5()};
    }

    private void setValue(IamPolicy policy, int index, String value)
    {
        switch (index)
        {
            case 0 -> policy.setV0(value);
            case 1 -> policy.setV1(value);
            case 2 -> policy.setV2(value);
            case 3 -> policy.setV3(value);
            case 4 -> policy.setV4(value);
            case 5 -> policy.setV5(value);
            default -> throw new IndexOutOfBoundsException("策略字段索引越界: " + index);
        }
    }
}
