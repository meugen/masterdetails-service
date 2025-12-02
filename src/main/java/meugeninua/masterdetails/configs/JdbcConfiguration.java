package meugeninua.masterdetails.configs;

import meugeninua.masterdetails.configs.impls.jdbc_connection_details.AwsSecretJdbcConnectionDetails;
import meugeninua.masterdetails.configs.impls.jdbc_connection_details.EnvVarsJdbcConnectionDetails;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class JdbcConfiguration {

    @Bean
    public JdbcConnectionDetails jdbcConnectionDetails(Environment environment) {
        if (AwsSecretJdbcConnectionDetails.isValidConfig(environment)) {
            return new AwsSecretJdbcConnectionDetails(environment);
        }
        if (EnvVarsJdbcConnectionDetails.isValidConfig(environment)) {
            return new EnvVarsJdbcConnectionDetails(environment);
        }
        throw new RuntimeException("Invalid configuration");
    }
}
