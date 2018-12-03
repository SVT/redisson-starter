package se.svt.videocore.redisson.lock

import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import se.svt.videocore.redisson.config.RedisProperties
import java.time.Duration
import java.util.concurrent.TimeUnit

class RedissonLockService(
    private val redissonClient: RedissonClient,
    redisProperties: RedisProperties
) {

    private val log = KotlinLogging.logger {}

    private val lockProperties = redisProperties.redisson.lock

    fun tryWithLock(
        lockName: String = lockProperties.name,
        waitTime: Duration = lockProperties.waitTime,
        leaseTime: Duration = lockProperties.leaseTime,
        action: () -> Unit
    ): Boolean {
        val lock = redissonClient.getLock(lockName)
        log.debug { "Acquiring lock: $lockName" }
        if (lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS)) {
            try {
                log.debug { "Acquired lock: $lockName" }
                action.invoke()
            } finally {
                lock.unlock()
            }
            return true
        } else {
            log.debug { "Failed to acquired lock: $lockName" }
            return false
        }
    }
}