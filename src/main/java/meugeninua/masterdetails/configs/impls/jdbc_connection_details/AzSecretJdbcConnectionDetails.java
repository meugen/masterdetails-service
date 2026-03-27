package meugeninua.masterdetails.configs.impls.jdbc_connection_details;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.core.env.Environment;

import static meugeninua.masterdetails.util.StringUtil.throwIfEmpty;

public class AzSecretJdbcConnectionDetails extends AbstractJdbcConnectionDetails {

    private static final String ENV_AZ_PGSQL_SECRET = "AZ_PGSQL_SECRET";
    private static final String ENV_AZ_VAULT_URI = "AZ_VAULT_URI";
    private static final String ENV_PGSQL_USERNAME = "PGSQL_USERNAME";

    private volatile String password;

    public static boolean isValidConfig(Environment environment) {
        return environment.containsProperty(ENV_AZ_PGSQL_SECRET)
            && environment.containsProperty(ENV_AZ_VAULT_URI)
            && environment.containsProperty(ENV_PGSQL_USERNAME);
    }

    public AzSecretJdbcConnectionDetails(Environment environment) {
        super(environment);
    }

    @Override
    public String getUsername() {
        var username = environment.getProperty(ENV_PGSQL_USERNAME);
        return throwIfEmpty(username, "PostgreSQL username is not set in environment variables");
    }

    @Override
    public String getPassword() {
        if (password == null) {
            synchronized (this) {
                if (password == null) {
                    var vaultUri = throwIfEmpty(
                        environment.getProperty(ENV_AZ_VAULT_URI),
                        "Azure Vault URI is not set in environment variables"
                    );
                    var secretClient = new SecretClientBuilder()
                        .credential(new DefaultAzureCredentialBuilder().build())
                        .vaultUrl(vaultUri)
                        .buildClient();
                    var secretName = throwIfEmpty(
                        environment.getProperty(ENV_AZ_PGSQL_SECRET),
                        "Azure PostgreSQL secret name is not set in environment variables"
                    );
                    password = secretClient.getSecret(secretName).getValue();
                }
            }
        }
        return throwIfEmpty(password, "PostgreSQL password is not set in environment variables");
    }
}
