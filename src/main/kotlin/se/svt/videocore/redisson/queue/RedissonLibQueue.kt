package se.svt.videocore.redisson.queue

import java.util.Queue

class RedissonLibQueue(private val queue: Queue<QueueItem>) : Queue<QueueItem> by queue