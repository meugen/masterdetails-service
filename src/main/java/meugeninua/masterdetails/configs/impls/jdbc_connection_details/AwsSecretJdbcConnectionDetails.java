package meugeninua.masterdetails.configs.impls.jdbc_connection_details;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.util.Map;

public class AwsSecretJdbcConnectionDetails extends AbstractJdbcConnectionDetails {

    private static final String ENV_NAME_AWS_SECRET = "AWS_PGSQL_SECRET";

    public static boolean isValidConfig(Environment environment) {
        return environment.containsProperty(ENV_NAME_AWS_SECRET);
    }

    private Map<String, String> secretMap;

    public AwsSecretJdbcConnectionDetails(Environment environment) {
        super(environment);
    }

    private String getSecretString() {
        var arn = environment.getProperty(ENV_NAME_AWS_SECRET, "");
        var clientBuilder = SecretsManagerClient.builder()
            .region(Region.of(arn.split(":")[3]));
        try (var client = clientBuilder.build()) {
            var request = GetSecretValueRequest.builder()
                .secretId(arn)
                .build();
            return client.getSecretValue(request).secretString();
        }
    }

    private Map<String, String> getSecretMap() {
        if (secretMap == null) {
            var secretString = getSecretString();
            try {
                secretMap = new ObjectMapper()
                    .readValue(secretString, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return secretMap;
    }

    @Override
    public String getUsername() {
        return getSecretMap().get("username");
    }

    @Override
    public String getPassword() {
        return getSecretMap().get("password");
    }
}
