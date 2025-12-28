package com.badsector.qerb.shared.infra.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.badsector.qerb.modules..adapter.web.controller..*(..)) || " +
            "execution(* com.badsector.qerb.modules..application.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("🟢 START: {}.{}", className, methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("🔴 ERROR: {}.{} -> {}", className, methodName, e.getMessage());
            throw e;
        }

        long executionTime = System.currentTimeMillis() - start;

        log.info("🏁 END: {}.{} [{} ms]", className, methodName, executionTime);
        return result;
    }
}