// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.redisson.starter

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.redisson.api.RPriorityQueue
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import se.svt.oss.junit5.redis.EmbeddedRedisExtension
import se.svt.oss.redisson.starter.lock.RedissonLockService
import se.svt.oss.redisson.starter.queue.RedissonLibQueue
import se.svt.oss.redisson.starter.testutil.createApplicationContext

@ExtendWith(EmbeddedRedisExtension::class)
class RedissonAutoConfigurationTest {

    val redisUri = "redis://localhost:" + System.getProperty("embedded-redis.port")

    @Test
    fun `objectMapper is created if none existAs`() {
        val context =
            createApplicationContext(
                RedissonAutoConfiguration::class.java,
                "redis.uri" to redisUri
            )

        context.getBean(ObjectMapper::class.java)
    }

    @Test
    fun `Redisson config bean is instantiated`() {
        val context =
            createApplicationContext(
                RedissonAutoConfiguration::class.java,
                "redis.uri" to redisUri
            )

        context.getBean(Config::class.java)
    }

    @Test
    fun `RedissonClient bean is created`() {
        val context =
            createApplicationContext(
                RedissonAutoConfiguration::class.java,
                "redis.uri" to redisUri
            )
        context.getBean(RedissonClient::class.java)
    }

    @Test
    fun `RedissonLockService bean is created if lock name property is set`() {
        val context =
            createApplicationContext(
                RedissonAutoConfiguration::class.java,
                "redis.redisson.lock.name-prefix" to "test-lock",
                "redis.uri" to redisUri
            )
        context.getBean(RedissonLockService::class.java)
    }

    @Test
    fun `RedissonLockService bean is not created if lock name property is not set`() {
        val context =
            createApplicationContext(
                RedissonAutoConfiguration::class.java,
                "redis.uri" to redisUri
            )
        Assertions.assertThatThrownBy { context.getBean(RedissonLockService::class.java) }
            .isInstanceOf(NoSuchBeanDefinitionException::class.java)
    }

    @Test
    fun `Redisson queue is created if queue name property is set`() {
        val queueName = "test-queue"
        val context = createApplicationContext(
            RedissonAutoConfiguration::class.java,
            "redis.redisson.queue.name" to queueName,
            "redis.uri" to redisUri
        )
        context.getBean(RedissonLibQueue::class.java)
    }

    @Test
    fun `Redisson queue is not created if queue name property is not set`() {
        val context =
            createApplicationContext(
                RedissonAutoConfiguration::class.java,
                "redis.uri" to redisUri
            )
        Assertions.assertThatThrownBy { context.getBean(RPriorityQueue::class.java) }
            .isInstanceOf(NoSuchBeanDefinitionException::class.java)
    }
}
