package org.example.trainingapp.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Aspect
@Component
public class TransactionLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(TransactionLoggingAspect.class.getName());

    @Around("execution(* org.example.trainingapp..*Service.*(..))")
    public Object aroundService(ProceedingJoinPoint pjp) throws Throwable {
        String txId = UUID.randomUUID().toString();
        MDC.put("txId", txId);
        log.info("TX-START id={} {}", txId, pjp.getSignature());
        try {
            Object result = pjp.proceed();
            log.info("TX-SUCCESS id={}", txId);
            return result;
        } catch (Throwable ex) {
            log.error("TX-ROLLBACK id={} err={}", txId, ex.getMessage(), ex);
            throw ex;
        } finally {
            MDC.clear();
        }
    }
}

