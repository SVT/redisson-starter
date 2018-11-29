package se.svt.videocore.redisson

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.redisson.Redisson
import org.redisson.api.RPriorityQueue
import org.redisson.api.RedissonClient
import org.redisson.codec.JsonJacksonCodec
import org.redisson.config.Config
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import se.svt.videocore.redisson.config.RedisProperties
import se.svt.videocore.redisson.lock.RedissonLockService
import se.svt.videocore.redisson.queue.QueueItem

@EnableConfigurationProperties(RedisProperties::class)
class RedissonAutoConfiguration {

    private val log = KotlinLogging.logger {}

    @ConditionalOnMissingBean
    @Bean
    fun objectMapper() =
            ObjectMapper()

    @ConditionalOnMissingBean
    @Bean
    fun redissonConfig(redisProperties: RedisProperties, objectMapper: ObjectMapper): Config {
        log.debug { "Using $redisProperties" }
        return Config()
                .setCodec(JsonJacksonCodec(objectMapper))
                .apply {
                    useSingleServer()
                            .setDatabase(redisProperties.db)
                            .setAddress(redisProperties.uri.toString())
                }
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "shutdown")
    fun redissonClient(config: Config): RedissonClient {
        return Redisson.create(config)
    }

    @ConditionalOnProperty("redis.redisson.lock.name")
    @Bean
    fun redissonLockService(redisson: Redisson, redisProperties: RedisProperties) =
            RedissonLockService(redisson, redisProperties)

    @ConditionalOnProperty("redis.redisson.queue.name")
    @Bean
    fun redissonPriorityQueue(redisProperties: RedisProperties, redisson: RedissonClient): RPriorityQueue<QueueItem> {
        val queue = redisson.getPriorityQueue<QueueItem>(redisProperties.redisson.queue.name)
        return queue
    }
}