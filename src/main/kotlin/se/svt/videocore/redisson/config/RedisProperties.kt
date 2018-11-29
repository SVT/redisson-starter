package se.svt.videocore.redisson.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI
import java.time.Duration

@ConfigurationProperties("redis")
class RedisProperties {
    lateinit var uri: URI

    var db = 0

    var redisson = RedissonProperties()
    override fun toString(): String {
        return "RedisProperties(uri=$uri, db=$db, redisson=$redisson)"
    }
}

class RedissonProperties {
    var lock = RedissonLockProperties()

    var queue = RedissonQueueProperties()
    override fun toString(): String {
        return "RedissonProperties(lock=$lock, queue=$queue)"
    }
}

class RedissonLockProperties {
    var leaseTime = Duration.ofMinutes(60)
    var waitTime = Duration.ZERO
    lateinit var name: String

    override fun toString(): String {
        return "RedissonLockProperties(leaseTime=$leaseTime, waitTime=$waitTime, name='$name')"
    }
}

class RedissonQueueProperties {
    lateinit var name: String

    override fun toString(): String {
        return "RedissonQueueProperties(name='$name')"
    }
}