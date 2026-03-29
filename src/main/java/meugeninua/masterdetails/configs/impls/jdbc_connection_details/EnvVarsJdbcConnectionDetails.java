package meugeninua.masterdetails.configs.impls.jdbc_connection_details;

import org.springframework.core.env.Environment;

public class EnvVarsJdbcConnectionDetails extends AbstractJdbcConnectionDetails {

    private static final String ENV_NAME_USERNAME = "PGSQL_USERNAME";
    private static final String ENV_NAME_PASSWORD = "PGSQL_PASSWORD";

    public static boolean isValidConfig(Environment environment) {
        return new Validator().validate(environment);
    }

    public EnvVarsJdbcConnectionDetails(Environment environment) {
        super(environment);
    }

    @Override
    public String getUsername() {
        return environment.getProperty(ENV_NAME_USERNAME);
    }

    @Override
    public String getPassword() {
        return environment.getProperty(ENV_NAME_PASSWORD);
    }

    private static class Validator extends AbstractJdbcConnectionDetails.Validator {

        @Override
        boolean validate(Environment environment) {
            return super.validate(environment)
                && environment.containsProperty(ENV_NAME_USERNAME)
                && environment.containsProperty(ENV_NAME_PASSWORD);
        }
    }
}
