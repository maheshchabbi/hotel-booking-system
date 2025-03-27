package com.dlim2012.search.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class ElasticSearchHighLevelClientConfig {

    @Value("${custom.elasticsearch.host}")
    private String host;

    @Value("${custom.elasticsearch.port}")
    private int port;

    @Value("${custom.elasticsearch.useSsl}")
    private boolean useSsl;

    @Value("${custom.elasticsearch.username}")
    private String username;

    @Value("${custom.elasticsearch.password}")
    private String password;

    @Bean
    public RestHighLevelClient restHighLevelClient() throws Exception {
        // Declare the credentials provider as final
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        // Conditionally create an SSLContext if SSL is enabled, and declare it as final
        final SSLContext sslContext;
        if (useSsl) {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        // Trust all client certificates
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        // Trust all server certificates
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
            }, new SecureRandom());
        } else {
            sslContext = null;
        }

        // Determine the scheme based on whether SSL is enabled
        String scheme = useSsl ? "https" : "http";

        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, scheme))
            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    if (useSsl && sslContext != null) {
                        httpClientBuilder.setSSLContext(sslContext)
                                         .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                    }
                    return httpClientBuilder;
                }
            });

        return new RestHighLevelClient(builder);
    }
}

