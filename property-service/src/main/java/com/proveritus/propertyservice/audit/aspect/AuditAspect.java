package com.proveritus.propertyservice.audit.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proveritus.cloudutility.security.CustomPrincipal;
import com.proveritus.propertyservice.audit.annotation.Auditable;
import com.proveritus.propertyservice.audit.entity.AuditLog;
import com.proveritus.propertyservice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            CustomPrincipal principal = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            AuditLog auditLog = new AuditLog();
            auditLog.setMethodName(joinPoint.getSignature().toShortString());
            auditLog.setParams(objectMapper.writeValueAsString(joinPoint.getArgs()));
            auditLog.setUserId(principal.getId());
            auditLog.setUserName(principal.getUsername());
            auditLog.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Log the exception, but don't block the main flow
        }

        return result;
    }
}
