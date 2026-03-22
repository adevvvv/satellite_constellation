package org.example.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Advice типа Around для методов, помеченных @LogExecutionTime.
     * Измеряет время выполнения метода и выводит его в консоль.
     *
     * @param joinPoint точка соединения (метод, к которому применяется совет)
     * @param logExecutionTime аннотация с параметрами
     * @return результат выполнения исходного метода
     * @throws Throwable если метод выбросил исключение
     */
    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();

        log.info("⏱️ Начало выполнения метода: {}", methodName);

        if (logExecutionTime.verbose()) {
            log.info("   Параметры: {}", Arrays.toString(joinPoint.getArgs()));
        }

        Object result = null;
        Throwable exception = null;

        try {
            // выполнение целевого метода
            result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            exception = t;
            throw t;
        } finally {
            long elapsedTime = System.currentTimeMillis() - start;
            String customName = logExecutionTime.name();
            String displayName = customName.isEmpty() ? methodName : customName;

            if (exception != null) {
                log.info("⏱️ Метод {} выполнился за {} мс (с ошибкой: {})",
                        displayName, elapsedTime, exception.getClass().getSimpleName());
            } else {
                log.info("⏱️ Метод {} выполнился за {} мс", displayName, elapsedTime);
            }

            if (logExecutionTime.verbose() && result != null) {
                log.info("   Результат: {}", result);
            }
        }
    }

    /**
     * Альтернативный вариант через pointcut выражение
     */
    @Around("execution(* seminars.services..*.*(..)) && @annotation(seminars.aop.LogExecutionTime)")
    public Object logExecutionTimeWithPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - start;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        log.info("⏱️ [{}] выполнен за {} мс",
                signature.getMethod().getName(), elapsedTime);

        return result;
    }
}