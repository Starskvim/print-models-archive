package com.starskvim.print.models.archive.aop

import mu.KLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggTimeAspect {

    @Around("execution(public * *(..)) && @annotation(annotation)")
    fun around(
        point: ProceedingJoinPoint,
        annotation: LoggTime
    ): Any {
        val start = System.currentTimeMillis()
        try {
            return point.proceed()
        } finally {
            val end = System.currentTimeMillis()
            // todo Method execute saveImageWithCompressing OFF
            // Method execute saveAll OFF

            logger.info { "Method execute ${point.signature.name} TIME: ${end - start}" }
        }
    }

    companion object : KLogging()
}