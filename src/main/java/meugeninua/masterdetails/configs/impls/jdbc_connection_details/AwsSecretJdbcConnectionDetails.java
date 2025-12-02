package meugeninua.masterdetails.configs.impls.jdbc_connection_details;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.util.Map;

import static meugeninua.masterdetails.util.StringUtil.parseRegion;
import static meugeninua.masterdetails.util.StringUtil.throwIfEmpty;

public class AwsSecretJdbcConnectionDetails extends AbstractJdbcConnectionDetails {

    private static final String ENV_NAME_AWS_SECRET = "AWS_PGSQL_SECRET";

    public static boolean isValidConfig(Environment environment) {
        return environment.containsProperty(ENV_NAME_AWS_SECRET);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private volatile Map<String, String> secretMap;

    public AwsSecretJdbcConnectionDetails(Environment environment) {
        super(environment);
    }

    private String getSecretString() {
        var arn = environment.getProperty(ENV_NAME_AWS_SECRET);
        var clientBuilder = SecretsManagerClient.builder()
            .region(Region.of(parseRegion(arn)));
        try (var client = clientBuilder.build()) {
            var request = GetSecretValueRequest.builder()
                .secretId(arn)
                .build();
            return client.getSecretValue(request).secretString();
        }
    }

    private Map<String, String> getSecretMap() {
        if (secretMap == null) {
            synchronized (this) {
                if (secretMap == null) {
                    var secretString = getSecretString();
                    try {
                        secretMap = objectMapper
                            .readValue(secretString, new TypeReference<>() {});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to parse AWS secret JSON for database credentials", e);
                    }
                }
            }
        }
        return secretMap;
    }

    @Override
    public String getUsername() {
        var username = getSecretMap().get("username");
        return throwIfEmpty(username, "Username must not be empty");
    }

    @Override
    public String getPassword() {
        var password = getSecretMap().get("password");
        return throwIfEmpty(password, "Password must not be empty");
    }
}
