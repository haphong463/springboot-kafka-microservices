package net.javaguides.product_service.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Ghi log trước khi các method trong các service được thực thi
    @Before("execution(* net.javaguides.product_service.service.impl.*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("Starting method: " + joinPoint.getSignature().getName());
    }

    // Ghi log sau khi các method hoàn thành
    @AfterReturning(pointcut = "execution(* net.javaguides.product_service.service.impl.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Completed method: " + joinPoint.getSignature().getName() + " with result: " + result);
    }

    // Ghi log khi có exception
    @AfterThrowing(pointcut = "execution(* net.javaguides.product_service.service.impl.*.*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in method: " + joinPoint.getSignature().getName() + " with cause: " + error.getMessage());
    }
}
