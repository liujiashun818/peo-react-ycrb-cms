package cn.people.one.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * User: 张新征
 * Date: 2017/5/16 09:01
 * Description:
 */
public class NoSSOCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty("theone.sso").equals("false");
    }
}
