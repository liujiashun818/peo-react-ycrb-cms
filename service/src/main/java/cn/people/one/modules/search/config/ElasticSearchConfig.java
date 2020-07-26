package cn.people.one.modules.search.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;

/**
 * Created by lml on 2017/1/17.
 */
@Slf4j
@Configuration
@EnableElasticsearchRepositories("cn.people.one.modules.search.repository")
public class ElasticSearchConfig {

    @Value("${spring.data.elasticsearch.properties.host}")
    private String host;

    @Value("${spring.data.elasticsearch.properties.port}")
    private int port;

    @Value("${spring.data.elasticsearch.cluster-name}")
    private String clusterName;

    @Value("${theone.elasticsearch.index.name}")
    private String indexName;

    @Value("${theone.elasticsearch.type}")
    private String type;

    @Value("${theone.elasticsearch.ask.index.name}")
    private String askIndexName;

    @Value("${theone.elasticsearch.ask.type}")
    private String askType;

    /**
     * 问政标题用联想词index
     */
    private static String askSuggetIndexName = "ask_suggest_index";

    /**
     * 问政标题用联想词index
     */
    private static String askSuggetTypeName = "ask_suggest";


    @Bean
    public Client initClient(){
        Client client = null;
        try {
            Settings settings = Settings.settingsBuilder(). put("cluster.name", clusterName)
                    //.put("client.transport.sniff", true)
                    .build();
            client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        }catch (Exception ex){
            log.error("es配置失败",ex);
        }finally {
            return client;
        }
    }

    @Bean
    public String elasticsearchIndexName(){
        return indexName;
    }

    @Bean
    public String elasticsearchIndexType(){
        return type;
    }

    @Bean
    public String askSuggetIndexName(){
        return askSuggetIndexName;
    }

    @Bean
    public String askSuggetTypeName(){
        return askSuggetTypeName;
    }

    @Bean
    public String elasticsearchAskIndexName(){
        return askIndexName;
    }

    @Bean
    public String elasticsearchAskIndexType(){
        return askType;
    }
}
