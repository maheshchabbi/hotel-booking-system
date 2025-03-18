package com.dlim2012.clients.cassandra.config;

import com.amazonaws.cassandra.auth.SigV4AuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.CqlSessionBuilder;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import javax.net.ssl.SSLContext;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspaceName;

    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.data.cassandra.port}")
    private Integer port;

    @Value("${spring.data.cassandra.local-datacenter}")
    private String localDataCenter;

    @Value("${spring.data.cassandra.access-key}")
    private String accessKey;

    @Value("${spring.data.cassandra.secret-key}")
    private String secretKey;

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected String getKeyspaceName() {
        System.out.println("keyspace: " + this.keyspaceName);
        return this.keyspaceName;
    }

    @Override
    protected String getContactPoints() {
        System.out.println("contact points: " + this.contactPoints);
        return this.contactPoints;
    }

    @Override
    protected String getLocalDataCenter() {
        System.out.println("local datacenter: " + this.localDataCenter);
        return this.localDataCenter;
    }

    @Override
    protected int getPort() {
        System.out.println("port: " + this.port);
        return this.port;
    }

    @Bean
    @Override
    public CqlSessionBuilder cassandraSessionBuilder() {
        // Create AWS credentials using the access key and secret key
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCreds);

        // Create the SigV4AuthProvider with the AWS credentials provider
        SigV4AuthProvider authProvider = new SigV4AuthProvider(credentialsProvider);

        try {
            // Amazon Keyspaces requires SSL, so get the default SSL context
            SSLContext sslContext = SSLContext.getDefault();

            // Build the session with authentication and SSL
            return super.cassandraSessionBuilder()
                    .withAuthProvider(authProvider)
                    .withSslContext(sslContext);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SSL context", e);
        }
    }
}
