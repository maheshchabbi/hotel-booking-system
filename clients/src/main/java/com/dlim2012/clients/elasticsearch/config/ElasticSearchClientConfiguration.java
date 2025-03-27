package com.dlim2012.clients.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.http.HttpHeaders;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Configuration
@ComponentScan(basePackages = {"com.dlim2012.searchconsumer", "com.dlim2012.search"})
public class ElasticSearchClientConfiguration extends ElasticsearchConfiguration {

    @Value("${custom.elasticsearch.server}")
    private String hostAndPort;
    @Value("${custom.elasticsearch.username}")
    private String username;
    @Value("${custom.elasticsearch.password}")
    private String password;
    @Value("${custom.elasticsearch.useSsl}")
    private String useSsl;

    private SSLContext createSSLContext() {
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (x509Certificates, s) -> true)
                    .build();
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Override
    public ClientConfiguration clientConfiguration() {
        // Use Spring's HttpHeaders for default headers
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.set("Accept", "application/json");
        defaultHeaders.set("Content-Type", "application/json");

        if (Objects.equals(this.useSsl, "true")) {
            return ClientConfiguration.builder()
                    .connectedTo(this.hostAndPort)
                    .usingSsl(createSSLContext())
                    .withDefaultHeaders(defaultHeaders)
                    .withBasicAuth(this.username, this.password)
                    .build();
        } else if (Objects.equals(this.useSsl, "false")) {
            return ClientConfiguration.builder()
                    .connectedTo(this.hostAndPort)
                    .withDefaultHeaders(defaultHeaders)
                    .build();
        } else {
            throw new IllegalArgumentException("Invalid useSsl: " + this.useSsl);
        }
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // Create the high-level REST client
        RestHighLevelClient highLevelClient = RestClients.create(clientConfiguration()).rest();
        // Obtain the low-level client from the high-level client
        RestClient lowLevelClient = highLevelClient.getLowLevelClient();
        // Build the transport and ElasticsearchClient using the Jackson mapper
        RestClientTransport transport = new RestClientTransport(lowLevelClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}

