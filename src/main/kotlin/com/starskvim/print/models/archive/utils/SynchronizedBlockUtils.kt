package com.starskvim.print.models.archive.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

// todo to lib
object SynchronizedBlockUtils {

    private val lockMap = ConcurrentHashMap<Any, Lock>()

    fun getLock(key: Any): Lock {
        return lockMap.computeIfAbsent(key) { ReentrantLock() }
    }

    inline fun execute(key: Any, block: () -> Unit) {
        val lock = getLock(key)
        lock.lock()
        try {
            block()
        } finally {
            lock.unlock()
        }
    }

    inline fun <T> executeWithResult(key: Any, block: () -> T): T {
        val lock = getLock(key)
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }
}