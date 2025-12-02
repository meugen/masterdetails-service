package meugeninua.masterdetails.configs.impls.jdbc_connection_details;

import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.core.env.Environment;

import static meugeninua.masterdetails.util.StringUtil.throwIfEmpty;

public abstract class AbstractJdbcConnectionDetails implements JdbcConnectionDetails {

    private static final String ENV_NAME_HOSTNAME = "PGSQL_HOSTNAME";
    private static final String ENV_NAME_PORT = "PGSQL_PORT";
    private static final String ENV_NAME_DATABASE = "PGSQL_DATABASE";

    final Environment environment;

    public AbstractJdbcConnectionDetails(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getJdbcUrl() {
        var hostname = environment.getProperty(ENV_NAME_HOSTNAME);
        var port = environment.getProperty(ENV_NAME_PORT, "5432");
        var database = environment.getProperty(ENV_NAME_DATABASE, "masterdetails");
        return String.format("jdbc:postgresql://%s:%s/%s",
            throwIfEmpty(hostname, "Host name must not be empty"),
            throwIfEmpty(port, "Port must not be empty"),
            throwIfEmpty(database, "Database must not be empty")
        );
    }
}
