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

    @Value("${custom.elasticsearch.username}")
    private String username;

    @Value("${custom.elasticsearch.password}")
    private String password;

    @Bean
    public RestHighLevelClient restHighLevelClient() throws Exception {
        // Create a credentials provider with your basic auth credentials
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        // Create an SSLContext that trusts all certificates (disables certificate validation)
        SSLContext sslContext = SSLContext.getInstance("TLS");
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

        // Build the REST client with HTTPS, credentials, and our custom SSL context
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, 443, "https"))
            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLContext(sslContext)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                }
            });

        return new RestHighLevelClient(builder);
    }
}
