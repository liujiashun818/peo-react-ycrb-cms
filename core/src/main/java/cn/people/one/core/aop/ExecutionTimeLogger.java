package cn.people.one.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by zhangxinzheng on 2016/12/26.
 */
@Aspect
@Component
@Slf4j
public class ExecutionTimeLogger {

    @Pointcut("@annotation(cn.people.one.core.aop.annotation.TimeMonitor)")
    public void timeMonitor() {}

    @Around("timeMonitor()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable{
        StopWatch sw = new StopWatch();
        String name = pjp.getSignature().getName();
        String className = pjp.getThis().getClass().getCanonicalName();
        try {
            sw.start();
            return pjp.proceed();
        } finally {
            sw.stop();
            log.info(className + "." + name + "方法执行时间为: " + sw.getTime() + "ms");
        }
    }
}
