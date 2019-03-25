package se.svt.videocore.redisson.lock

import mu.KotlinLogging
import org.redisson.api.RedissonClient
import se.svt.videocore.redisson.config.RedisProperties
import java.time.Duration
import java.util.concurrent.TimeUnit

open class RedissonLockService(
    private val redissonClient: RedissonClient,
    redisProperties: RedisProperties
) {

    private val log = KotlinLogging.logger {}

    private val lockProperties = redisProperties.redisson.lock

    @JvmOverloads
    open fun tryWithLock(
        lockName: String,
        waitTime: Duration = lockProperties.waitTime,
        leaseTime: Duration = lockProperties.leaseTime,
        action: () -> Unit
    ): Boolean {
        val lock = redissonClient.getLock(lockProperties.namePrefix!! + lockName)
        log.debug { "Acquiring lock: $lockName" }
        return if (lock.tryLock(waitTime.toMillis(), leaseTimeInMillis(leaseTime), TimeUnit.MILLISECONDS)) {
            try {
                log.debug { "Acquired lock: $lockName" }
                action.invoke()
            } finally {
                log.debug { "Releasing lock in finally: $lockName" }
                lock.unlock()
            }
            true
        } else {
            log.debug { "Failed to acquired lock: $lockName" }
            false
        }
    }

    // Transform marker from conf for watchdog extends every 30th seconds automatically (-1)
    private fun leaseTimeInMillis(leaseTime: Duration): Long {
        return if (leaseTime == Duration.ZERO) -1 else leaseTime.toMillis()
    }
}