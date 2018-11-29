package se.svt.videocore.redisson.testutil

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.MapPropertySource

fun createApplicationContext(configuration: Class<*>, vararg properties: Pair<String, Any>) =
        AnnotationConfigApplicationContext().apply {
            environment.propertySources.addFirst(MapPropertySource("testProperties", properties.toMap()))
            register(configuration)
            refresh()
        }