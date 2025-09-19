package com.proveritus.cloudutility.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Around("execution(* com.proveritus..service.*.*(..))")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        logger.info("Entering method: {} with arguments: {}", methodName, Arrays.toString(args));

        Object result = joinPoint.proceed();

        logger.info("Exiting method: {} with result: {}", methodName, result);

        return result;
    }
}
