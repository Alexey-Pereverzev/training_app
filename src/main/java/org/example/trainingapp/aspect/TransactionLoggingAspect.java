package org.example.trainingapp.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;


@Aspect
@Component
public class TransactionLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(TransactionLoggingAspect.class.getName());

    @Around("execution(* org.example.trainingapp..*Service.*(..))")
    public Object aroundService(ProceedingJoinPoint pjp) throws Throwable {
        String existing = MDC.get("txId");
        boolean generatedHere = false;
        if (!StringUtils.hasText(existing)) {
            existing = UUID.randomUUID().toString();
            MDC.put("txId", existing);
            generatedHere = true;                       //  flag saying we generate txId on this step
        }
        try {
            Object result = pjp.proceed();
            log.info("TX-SUCCESS id={}", existing);
            return result;
        } catch (Throwable ex) {
            log.error("TX-ROLLBACK id={} err={}", existing, ex.getMessage(), ex);
            throw ex;
        } finally {
            if (generatedHere) MDC.clear();             // created here - clear here
        }
    }
}

