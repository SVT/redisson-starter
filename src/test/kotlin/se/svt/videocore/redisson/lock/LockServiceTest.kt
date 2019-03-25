package se.svt.videocore.redisson.lock

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import se.svt.videocore.redisson.config.RedisProperties
import se.svt.videocore.redisson.config.RedissonLockProperties
import se.svt.videocore.redisson.config.RedissonProperties
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit

internal class LockServiceTest {

    private val defaultNamePrefix = "the-test-lock"

    private val defaultLeaseTime = Duration.ofSeconds(60)

    private val defaultWaitTime = Duration.ofSeconds(30)

    private val redisProperties = RedisProperties().apply {
        redisson = RedissonProperties().apply {
            lock = RedissonLockProperties().apply {
                namePrefix = defaultNamePrefix
                this.waitTime = defaultWaitTime
                this.leaseTime = defaultLeaseTime
            }
        }
    }

    private val redissonClient = mockk<RedissonClient>()
    private val lock = mockk<RLock>()
    private val mockAction = mockk<() -> Unit>()
    private val lockService = RedissonLockService(redissonClient, redisProperties)

    @BeforeEach
    fun `Setup test`() {
        every { redissonClient.getLock(match { it.startsWith(defaultNamePrefix) }) } returns lock
        every { lock.tryLock(any(), any(), any()) } returns true
        every { lock.unlock() } just Runs
        every { mockAction.invoke() } just Runs
    }

    @Test
    fun `Lock acquisition is successful, invokes action`() {
        val lockName = "lockName"

        lockService.tryWithLock(lockName = lockName, action = mockAction)

        verify { redissonClient.getLock(defaultNamePrefix + lockName) }
        verify { lock.tryLock(defaultWaitTime.toMillis(), defaultLeaseTime.toMillis(), TimeUnit.MILLISECONDS) }
        verify { lock.unlock() }
        verify { mockAction.invoke() }
    }

    @Test
    fun `Lock acquisition is successful, Duration zero gives -1 l`() {
        val lockName = "lockName"

        lockService.tryWithLock(lockName = lockName, leaseTime = Duration.ZERO, action = mockAction)

        verify { redissonClient.getLock(defaultNamePrefix + lockName) }
        verify { lock.tryLock(defaultWaitTime.toMillis(), -1, TimeUnit.MILLISECONDS) }
        verify { lock.unlock() }
        verify { mockAction.invoke() }
    }

    @Test
    fun `Lock acquisition fails, does not invoke action`() {
        every { lock.tryLock(any(), any(), any()) } returns false
        val lockName = "lockName"

        lockService.tryWithLock(lockName = lockName, action = mockAction)

        verify { redissonClient.getLock(defaultNamePrefix + lockName) }
        verify { lock.tryLock(defaultWaitTime.toMillis(), defaultLeaseTime.toMillis(), TimeUnit.MILLISECONDS) }
        verify(exactly = 0) { lock.unlock() }
        verify(exactly = 0) { mockAction.invoke() }
    }

    @Test
    fun `Default values for name, waitTime, and leaseTime can be overridden`() {
        val name = UUID.randomUUID().toString()
        val waitTime = Duration.ofHours(1)
        val leaseTime = Duration.ofHours(5)

        lockService.tryWithLock(name, waitTime, leaseTime, mockAction)

        verify { redissonClient.getLock(defaultNamePrefix + name) }
        verify { lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS) }
        verify { lock.unlock() }
        verify { mockAction.invoke() }
    }
}