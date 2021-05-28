// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.redisson.starter.queue

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import se.svt.oss.redisson.starter.Assertions.assertThat
import java.time.LocalDateTime
import java.util.UUID

internal class QueueItemTest {

    val now = LocalDateTime.now()

    val oneHourAgo = now.minusHours(1)

    @Nested
    inner class CompareTo {

        @Test
        fun `same item returns 0`() {
            val queueItem = queueItem()
            assertThat(queueItem.compareTo(queueItem))
                .isEqualTo(0)
        }

        @Test
        fun `returns positive number if this item has lower priotity than other`() {
            val item1 = queueItem(priority = 0)
            val item2 = queueItem(priority = 1)
            assertThat(item1.compareTo(item2))
                .isGreaterThan(0)
        }

        @Test
        fun `returns negative number if this item has higher priotity than other`() {
            val item1 = queueItem(priority = 1)
            val item2 = queueItem(priority = 0)
            assertThat(item1.compareTo(item2))
                .isLessThan(0)
        }

        @Nested
        inner class WithSamePriority {

            @Test
            fun `returns negative number if this item was created before other`() {
                val item1 = queueItem(created = oneHourAgo)
                val item2 = queueItem(created = now)
                assertThat(item1.compareTo(item2))
                    .isLessThan(0)
            }

            @Test
            fun `returns positive number if this item was created after other`() {
                val item1 = queueItem(created = now)
                val item2 = queueItem(created = oneHourAgo)
                assertThat(item1.compareTo(item2))
                    .isGreaterThan(0)
            }
        }

        @Nested
        inner class WithSamePriorityAndSameCreationTime {
            @Test
            fun `returns positive number if this has higher id than other`() {
                val item1 = queueItem(id = "b")
                val item2 = queueItem(id = "a")
                assertThat(item1.compareTo(item2))
                    .isGreaterThan(0)
            }

            @Test
            fun `returns negative number if this has lower id than other`() {
                val item1 = queueItem(id = "a")
                val item2 = queueItem(id = "b")
                assertThat(item1.compareTo(item2))
                    .isLessThan(0)
            }

            @Test
            fun `returns 0 if ids are equal`() {
                val item1 = queueItem(id = "a")
                val item2 = queueItem(id = item1.id)
                assertThat(item1.compareTo(item2))
                    .isEqualTo(0)
            }
        }
    }

    fun queueItem(id: String = UUID.randomUUID().toString(), priority: Int = 0, created: LocalDateTime = now) =
        QueueItem(id, priority, created)
}
