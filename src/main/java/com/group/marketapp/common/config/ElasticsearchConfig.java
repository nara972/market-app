package com.group.marketapp.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    @Bean
    public RestClient restClient() {
        String[] uriParts = elasticsearchUri.replace("http://", "").split(":");
        String host = uriParts[0];
        int port = Integer.parseInt(uriParts[1]);

        return RestClient.builder(
                new HttpHost(host, port, "http")
        ).build();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return new ElasticsearchClient(
                new RestClientTransport(
                        restClient(),
                        new JacksonJsonpMapper()
                )
        );
    }
}
