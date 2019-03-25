package se.svt.videocore.redisson

import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import se.svt.videocore.redisson.Assertions.assertThat
import se.svt.videocore.redisson.config.RedisProperties
import se.svt.videocore.redisson.testutil.createApplicationContext
import java.net.URI
import java.time.Duration

class RedissonPropertiesTest {

    @Test
    fun `all redis properties are set correctly`() {
        val db = 2
        val uri = URI.create("redis://some-host:1234")
        val lockWaitTime = "10s"
        val lockLeaseTime = "30s"
        val lockName = "the-lock"
        val queueName = "queue"
        val timeout = "5s"
        val connectionPoolSize = 12
        val subscriptionConnectionPoolSize = 10
        val connectionMinimumIdleSize = 8

        val context = createApplicationContext(
            OnlyPropertiesConfiguration::class.java,
            "redis.db" to db.toString(),
            "redis.uri" to uri,
            "redis.connection-pool-size" to connectionPoolSize,
            "redis.subscription-connection-pool-size" to subscriptionConnectionPoolSize,
            "redis.connection-minimum-idle-size" to connectionMinimumIdleSize,
            "redis.redisson.timeout" to timeout,
            "redis.redisson.lock.wait-time" to lockWaitTime,
            "redis.redisson.lock.lease-time" to lockLeaseTime,
            "redis.redisson.lock.name-prefix" to lockName,
            "redis.redisson.queue.name" to queueName
        )

        val redisProperties = context.getBean(RedisProperties::class.java)

        System.out.println(redisProperties)

        assertThat(redisProperties)
            .isNotNull
            .hasDb(db)
            .hasUri(uri)
            .hasConnectionMinimumIdleSize(connectionMinimumIdleSize)
            .hasConnectionPoolSize(connectionPoolSize)
            .hasSubscriptionConnectionPoolSize(subscriptionConnectionPoolSize)

        assertThat(redisProperties.redisson)
            .hasTimeout(Duration.ofSeconds(5))
            .isNotNull

        assertThat(redisProperties.redisson.lock)
            .isNotNull
            .hasLeaseTime(Duration.ofSeconds(30))
            .hasWaitTime(Duration.ofSeconds(10))
            .hasNamePrefix(lockName)

        assertThat(redisProperties.redisson.queue)
            .isNotNull
            .hasName(queueName)
    }

    @Test
    fun `not all redis properties are set`() {
        val uri = URI.create("redis://some-host:1234")

        val context = createApplicationContext(
            OnlyPropertiesConfiguration::class.java,
            "redis.uri" to uri
        )

        val redisProperties = context.getBean(RedisProperties::class.java)

        System.out.println(redisProperties)

        assertThat(redisProperties)
            .isNotNull
            .hasDb(0)
            .hasUri(uri)
            .hasConnectionPoolSize(32)
            .hasSubscriptionConnectionPoolSize(25)
            .hasConnectionMinimumIdleSize(16)

        assertThat(redisProperties.redisson)
            .isNotNull
            .hasTimeout(Duration.ofSeconds(6))

        assertThat(redisProperties.redisson.lock)
            .isNotNull
            .hasNamePrefix(null)
            .hasLeaseTime(Duration.ZERO)
            .hasWaitTime(Duration.ZERO)

        assertThat(redisProperties.redisson.queue)
            .isNotNull
            .hasName(null)
    }

    @EnableConfigurationProperties(RedisProperties::class)
    @Configuration
    class OnlyPropertiesConfiguration
}