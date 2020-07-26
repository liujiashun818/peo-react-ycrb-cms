package cn.people.one.modules.live.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * User: 张新征
 * Date: 2017/3/26 15:22
 * Description:
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public SpringUtil springUtil2() {
        return new SpringUtil();
    }
}
