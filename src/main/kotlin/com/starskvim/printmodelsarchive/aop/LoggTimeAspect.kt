package com.starskvim.printmodelsarchive.aop

import mu.KLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import kotlin.reflect.cast

@Aspect
class LoggTimeAspect {

    @Around("execution(* *(..)) && @annotation(LoggTime)")
    fun around(point: ProceedingJoinPoint): Any {
        val start = System.currentTimeMillis()
        val result = point.proceed()
        val end = System.currentTimeMillis()
        logger.info { "Method execute ${MethodSignature::class.cast(point.signature).method.name} TIME: ${end - start}" }
        return result
    }

    companion object : KLogging()
}