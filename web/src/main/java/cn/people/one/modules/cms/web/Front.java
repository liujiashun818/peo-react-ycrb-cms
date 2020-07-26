package cn.people.one.modules.cms.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * User: 张新征
 * Date: 2017/5/19 09:18
 * Description:
 */
@ConfigurationProperties(prefix = "one.front")
@Data
public class Front {
    private Boolean pushButton;
}
