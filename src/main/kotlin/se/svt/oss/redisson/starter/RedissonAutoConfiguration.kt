// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.redisson.starter

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.JsonJacksonCodec
import org.redisson.config.Config
import org.redisson.config.SingleServerConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import se.svt.oss.redisson.starter.config.RedisProperties
import se.svt.oss.redisson.starter.lock.RedissonLockService
import se.svt.oss.redisson.starter.queue.QueueItem
import se.svt.oss.redisson.starter.queue.RedissonLibQueue

@EnableConfigurationProperties(RedisProperties::class)
@Configuration
class RedissonAutoConfiguration {

    private val log = KotlinLogging.logger {}

    @ConditionalOnMissingBean
    @Bean
    fun objectMapper(): ObjectMapper =
        ObjectMapper().findAndRegisterModules()

    @ConditionalOnMissingBean
    @Bean
    fun redissonConfig(redisProperties: RedisProperties, objectMapper: ObjectMapper): Config {
        log.debug { "Using $redisProperties" }
        return Config()
            .setCodec(JsonJacksonCodec(objectMapper))
            .setNettyThreads(64)
            .apply {
                useSingleServer()
                    .setDatabase(redisProperties.db)
                    .setAddress(redisProperties.uri.toString())
                    .setTimeout(redisProperties.redisson.timeout.toMillis().toInt())
                    .apply {
                        setDnsMonitoringInterval(redisProperties, this)
                        setConnectionPoolSize(redisProperties, this)
                        setSubscriptionConnectionPoolSize(redisProperties, this)
                        setSubscriptionsPerConnection(redisProperties, this)
                        setConnectionMinimumIdleSize(redisProperties, this)
                    }
            }
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "shutdown")
    fun redissonClient(config: Config): RedissonClient {
        return Redisson.create(config)
    }

    @ConditionalOnProperty("redis.redisson.lock.name-prefix")
    @Bean
    fun redissonLockService(redissonClient: RedissonClient, redisProperties: RedisProperties) =
        RedissonLockService(redissonClient, redisProperties)

    @ConditionalOnProperty("redis.redisson.queue.name")
    @Bean
    fun redissonPriorityQueue(redisProperties: RedisProperties, redisson: RedissonClient): RedissonLibQueue {
        val priorityQueue = redisson.getPriorityBlockingQueue<QueueItem>(redisProperties.redisson.queue.name)
        return RedissonLibQueue(priorityQueue)
    }

    private fun setDnsMonitoringInterval(redisProperties: RedisProperties, config: SingleServerConfig) =
        redisProperties.dnsMonitoringInterval?.let {
            config.setDnsMonitoringInterval(it)
        }

    private fun setConnectionPoolSize(redisProperties: RedisProperties, config: SingleServerConfig) =
        redisProperties.connectionPoolSize?.let {
            config.setConnectionPoolSize(it)
        }

    private fun setSubscriptionConnectionPoolSize(redisProperties: RedisProperties, config: SingleServerConfig) =
        redisProperties.subscriptionConnectionPoolSize?.let {
            config.setSubscriptionConnectionPoolSize(it)
        }

    private fun setSubscriptionsPerConnection(redisProperties: RedisProperties, config: SingleServerConfig) =
        redisProperties.subscriptionsPerConnection?.let {
            config.setSubscriptionsPerConnection(it)
        }

    private fun setConnectionMinimumIdleSize(redisProperties: RedisProperties, config: SingleServerConfig) =
        redisProperties.connectionMinimumIdleSize?.let {
            config.setConnectionMinimumIdleSize(it)
        }
}
