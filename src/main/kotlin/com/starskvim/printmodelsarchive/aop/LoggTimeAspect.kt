package com.starskvim.printmodelsarchive.aop

import mu.KLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import kotlin.reflect.cast

@Aspect
@Component
class LoggTimeAspect {

    @Around("@annotation(LoggTime)")
    fun around(point: ProceedingJoinPoint): Any {
        val start = System.currentTimeMillis()
        val result = point.proceed()
        val end = System.currentTimeMillis()
        logger.info { "Method execute ${point.signature.name} TIME: ${end - start}" }
        return result
    }

    companion object : KLogging()
}