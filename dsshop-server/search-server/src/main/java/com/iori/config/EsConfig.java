package com.iori.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {
    @Value("${es.server}")
    private String servers;

    @Bean
    public RestHighLevelClient highLevelClient() {
        String[] arr = servers.split(",");
        HttpHost[] httpHosts = new HttpHost[arr.length];
        for (int i = 0; i < httpHosts.length; i++) {
             httpHosts[i] = new HttpHost(arr[i].split(":")[0],
                    Integer.parseInt(arr[i].split(":")[1]), "http");
        }
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }

}
