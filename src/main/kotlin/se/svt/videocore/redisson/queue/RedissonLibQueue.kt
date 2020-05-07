// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.videocore.redisson.queue

import java.util.concurrent.BlockingQueue

class RedissonLibQueue(private val queue: BlockingQueue<QueueItem>) : BlockingQueue<QueueItem> by queue