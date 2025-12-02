package meugeninua.masterdetails.configs.impls.jdbc_connection_details;

import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.core.env.Environment;

public abstract class AbstractJdbcConnectionDetails implements JdbcConnectionDetails {

    private static final String ENV_NAME_HOSTNAME = "PGSQL_HOSTNAME";

    final Environment environment;

    public AbstractJdbcConnectionDetails(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getJdbcUrl() {
        var hostname = environment.getProperty(ENV_NAME_HOSTNAME);
        return String.format("jdbc:postgresql://%s:5432/masterdetails", hostname);
    }
}
