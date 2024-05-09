package com.kt.edu.thirdproject.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
@Profile("local") // local인 경우에만 작동
public class LogAspect {

    // 2. Create @Pointcut Method
    @Pointcut("execution(* com.kt.edu.thirdproject.employee..*(..))")
    public void all() {
    }
    @Pointcut("execution(* com.kt.edu.thirdproject.employee..*Controller.*(..))")
    public void controller() {
    }
    @Pointcut("execution(* com.kt.edu.thirdproject.employee..*Service.*(..))")
    public void service(){}
    // 3. Create @Before Method
    //annotation 이 Transactional 인 method 만들어서 로그 찍기
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object getEmployeeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[transaction] {}, args = {}", joinPoint.getSignature(), joinPoint.getArgs());
        return joinPoint.proceed();
    }

    //annotation 이 Ktedu 인 method 만들어서 로그 찍기
    @Around("@annotation(com.kt.edu.thirdproject.employee.service.Ktedu)")
    public Object updateLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[Ktedu] {}, args = {}", joinPoint.getSignature(), joinPoint.getArgs());
        return joinPoint.proceed();
    }

    @Around("all()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            log.info("log = {}" , joinPoint.getSignature());
            log.info("timeMs = {}", timeMs);
        }
    }

    //특정 조인포인트에서 수행될 부가기능을 정리
    @Before("controller() || service()")
    public void beforeLogic(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        log.info("beforeLogic method = {}", method.getName());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if(arg != null) {
                log.info("beforeLogic type = {}", arg.getClass().getSimpleName());
                log.info("beforeLogic value = {}", arg);
            }

        }
    }
    // @After advice
    @After("controller() || service()")
    public void logAfter(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        log.info("After method = {}", method.getName());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                log.info("Type = {}", arg.getClass().getSimpleName());
                log.info("Value = {}", arg);
            }
        }
    }
}
