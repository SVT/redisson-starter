// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.redisson.starter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI
import java.time.Duration

@ConfigurationProperties("redis")
class RedisProperties {
    lateinit var uri: URI
    var db = 0

    var connectionPoolSize: Int? = null

    var subscriptionConnectionPoolSize: Int? = null

    var subscriptionsPerConnection: Int? = null

    var connectionMinimumIdleSize: Int? = null

    var dnsMonitoringInterval: Long? = null

    var redisson = RedissonProperties()

    override fun toString(): String {
        return "RedisProperties(uri=$uri, db=$db, connectionPoolSize=$connectionPoolSize, subscriptionConnectionPoolSize=$subscriptionConnectionPoolSize, connectionMinimumIdleSize=$connectionMinimumIdleSize, redisson=$redisson)"
    }
}

class RedissonProperties {
    var timeout = Duration.ofMillis(6000)

    var lock = RedissonLockProperties()

    var queue = RedissonQueueProperties()

    override fun toString(): String {
        return "RedissonProperties(timeout=$timeout, lock=$lock, queue=$queue)"
    }
}

class RedissonLockProperties {
    var leaseTime = Duration.ofMillis(-1)
    var waitTime = Duration.ZERO
    var namePrefix: String? = null

    override fun toString(): String {
        return "RedissonLockProperties(leaseTime=$leaseTime, waitTime=$waitTime, namePrefix='$namePrefix')"
    }
}

class RedissonQueueProperties {
    var name: String? = null

    override fun toString(): String {
        return "RedissonQueueProperties(name='$name')"
    }
}
