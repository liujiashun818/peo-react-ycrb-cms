package cn.people.one.modules.search;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by wilson on 2018-11-08.
 */
public class ElasticSearchTest {

    @Test
    public void testClient() throws IOException {
        InetAddress address = InetAddress.getByName("10.3.39.206");
        int port = 9300;

        Settings settings = Settings
                .settingsBuilder().put("cluster.name", "people_es")
                .build();

        Client client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(address, port));

        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field("id", 122413)
                .field("content", "smith asfa")
                .endObject();

        IndexResponse response = client.prepareIndex("test_v1", "test").setSource(builder).get();
        System.out.println(response);
        client.close();
    }

}
