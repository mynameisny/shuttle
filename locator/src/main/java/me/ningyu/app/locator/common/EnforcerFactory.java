package me.ningyu.app.locator.common;

import me.ningyu.app.locator.common.vo.casbin.Policy;
import me.ningyu.app.locator.config.CasbinAdapterConfig;
import org.apache.commons.io.IOUtils;
import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class EnforcerFactory implements InitializingBean
{
    private Enforcer enforcer;

    private CasbinAdapterConfig casbinAdapterConfig;

    private JDBCAdapter jdbcAdapter;


    public EnforcerFactory(CasbinAdapterConfig casbinAdapterConfig, JDBCAdapter jdbcAdapter)
    {
        this.casbinAdapterConfig = casbinAdapterConfig;
        this.jdbcAdapter = jdbcAdapter;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        Model model = new Model();
        ClassPathResource classPathResource = new ClassPathResource("casbin/rbac_model.conf");

        String text = IOUtils.toString(classPathResource.getInputStream(), StandardCharsets.UTF_8);
        model.loadModelFromText(text);

        enforcer = new Enforcer(model, jdbcAdapter);
    }

    public Enforcer getEnforcer()
    {
        return enforcer;
    }

    public boolean addPolicy(Policy policy)
    {
        boolean addPolicyResult = enforcer.addPolicy(policy.getSub(), policy.getObj(), policy.getAct());
        enforcer.savePolicy();

        return addPolicyResult;
    }

    public boolean removePolicy(Policy policy)
    {
        boolean removePolicyResult = enforcer.removePolicy(policy.getSub(), policy.getObj(), policy.getAct());
        enforcer.savePolicy();

        return removePolicyResult;
    }
}
