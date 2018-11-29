package se.svt.videocore.redisson

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.redisson.Redisson
import org.redisson.api.RPriorityQueue
import org.redisson.config.Config
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import se.svt.videocore.redisson.lock.RedissonLockService
import se.svt.videocore.redisson.testutil.createApplicationContext
import se.svt.videocore.util.junit5.redis.EmbeddedRedisExtension

@ExtendWith(EmbeddedRedisExtension::class)
class RedissonAutoConfigurationTest {

    @Test
    fun `objectMapper is created if none exists` () {
        val context = createApplicationContext(RedissonAutoConfiguration::class.java)

        context.getBean(ObjectMapper::class.java)
    }

    @Test
    fun `Redisson config bean is instantiated` () {
        val context = createApplicationContext(RedissonAutoConfiguration::class.java)

        context.getBean(Config::class.java)
    }

    @Test
    fun `Redisson bean is created` () {
        val context = createApplicationContext(RedissonAutoConfiguration::class.java)
        context.getBean(Redisson::class.java)
    }

    @Test
    fun `RedissonLockService bean is created if lock name property is set` () {
        val context = createApplicationContext(RedissonAutoConfiguration::class.java, "redis.redisson.lock.name" to "test-lock")
        context.getBean(RedissonLockService::class.java)
    }

    @Test
    fun `RedissonLockService bean is not created if lock name property is not set` () {
        val context = createApplicationContext(RedissonAutoConfiguration::class.java)
        Assertions.assertThatThrownBy { context.getBean(RedissonLockService::class.java) }
                .isInstanceOf(NoSuchBeanDefinitionException::class.java)
    }

    @Test
    fun `Redisson queue is created if queue name property is set` () {
        val queueName = "test-queue"
        val context = createApplicationContext(RedissonAutoConfiguration::class.java,
                "redis.redisson.queue.name" to queueName)
        context.getBean(RPriorityQueue::class.java)
    }

    @Test
    fun `Redisson queue is not created if queue name property is not set` () {
        val context = createApplicationContext(RedissonAutoConfiguration::class.java)
        Assertions.assertThatThrownBy { context.getBean(RPriorityQueue::class.java) }
                .isInstanceOf(NoSuchBeanDefinitionException::class.java)
    }

}