package com.example.tsis.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    private final long slowThresholdMs;

    public ServiceLoggingAspect(@Value("${app.logging.slow-threshold-ms:200}") long slowThresholdMs) {
        this.slowThresholdMs = slowThresholdMs;
    }

    @Pointcut("within(com.example.tsis..*) && @within(org.springframework.stereotype.Service)")
    void serviceLayer() {
    }

    @Around("serviceLayer()")
    public Object logExecution(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String method = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        log.debug("--> {}({})", method, Arrays.toString(pjp.getArgs()));

        long start = System.nanoTime();
        try {
            Object result = pjp.proceed();
            long tookMs = elapsedMs(start);
            if (tookMs >= slowThresholdMs) {
                log.warn("<-- {} SLOW: {} ms (threshold {} ms)", method, tookMs, slowThresholdMs);
            } else {
                log.info("<-- {} OK: {} ms", method, tookMs);
            }
            return result;
        } catch (Throwable ex) {
            log.warn("<-- {} FAILED: {} ms, {}: {}",
                    method, elapsedMs(start), ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    private static long elapsedMs(long startNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    }
}
