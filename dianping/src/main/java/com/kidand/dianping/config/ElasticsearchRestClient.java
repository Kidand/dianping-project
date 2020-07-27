package com.kidand.dianping.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
*
*  ██╗  ██╗██╗██████╗  █████╗ ███╗   ██╗██████╗
*  ██║ ██╔╝██║██╔══██╗██╔══██╗████╗  ██║██╔══██╗
*  █████╔╝ ██║██║  ██║███████║██╔██╗ ██║██║  ██║
*  ██╔═██╗ ██║██║  ██║██╔══██║██║╚██╗██║██║  ██║
*  ██║  ██╗██║██████╔╝██║  ██║██║ ╚████║██████╔╝
*  ╚═╝  ╚═╝╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═════╝
*
* @description:ElasticsearchRestClient
* @author: Kidand
* @date: 2020/7/27 9:41 上午
* Copyright © 2019-Kidand. 
*/
@Configuration
public class ElasticsearchRestClient {

    @Value("${elasticsearch.ip}")
    String ipAddress;

    @Bean(name="heighLevelClient")
    public RestHighLevelClient highLevelClient(){
        String[] address = ipAddress.split(":");
        String ip = address[0];
        int port = Integer.valueOf(address[1]);

        HttpHost httpHost = new HttpHost(ip, port, "http");
        return new RestHighLevelClient(RestClient.builder(new HttpHost[]{httpHost}));
    }
}
