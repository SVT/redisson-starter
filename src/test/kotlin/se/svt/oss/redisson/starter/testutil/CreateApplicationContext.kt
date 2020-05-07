// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.redisson.starter.testutil

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.MapPropertySource

fun createApplicationContext(configuration: Class<*>, vararg properties: Pair<String, Any>) =
    AnnotationConfigApplicationContext().apply {
        environment.propertySources.addFirst(MapPropertySource("testProperties", properties.toMap()))
        register(configuration)
        refresh()
    }