package me.ningyu.app.locator;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class JCasbinTest
{
    @Test
    public void testJdbcAdapter() throws Exception
    {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://192.168.5.251:3306/shuttle_locator";
        String username = "root";
        String password = "123@ningyu.me";

        // The adapter will use the table named "casbin_rule".
        // Use driver, url, username and password to initialize adapter JDBC adapter.
        JDBCAdapter adapter = new JDBCAdapter(driver, url, username, password);

        // Recommend use DataSource to initialize adapter JDBC adapter.
        // Implementer of DataSource interface, such as hikari, c3p0, durid, etc.
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        adapter = new JDBCAdapter(dataSource);

        Model model = new Model();
        File file = new File("src/main/resources/jcasbin/rbac_model.conf");
        String canonicalPath = file.getCanonicalPath();
        model.loadModel(canonicalPath);

        Enforcer e = new Enforcer(model, adapter);

        // Check the permission.
        e.enforce("alice", "data1", "read");

        // Modify the policy.
        // e.addPolicy(...);
        // e.removePolicy(...);

        // Save the policy back to DB.
        e.savePolicy();
        // Close the connection.
        adapter.close();
    }
}
