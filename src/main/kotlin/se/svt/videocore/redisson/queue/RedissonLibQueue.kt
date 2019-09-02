package se.svt.videocore.redisson.queue

import java.util.concurrent.BlockingQueue

class RedissonLibQueue(private val queue: BlockingQueue<QueueItem>) : BlockingQueue<QueueItem> by queue
