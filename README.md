redisson-starter
===

This library configures redisson as it is commonly used in core systems. In addition to configuring the redisson
client, it also optionaly configures a RedissonLockService (if `redis.redisson.lock.name` property is set)
and a redison priorityqueue (if `redis.redisson.queue.name property` is set)


### Owner ###

***Videocore*** i-core@svt.se, #video-core


### Usage ###

Add the lib as a dependency to your build.gradle

```
implementation 'se.svt.videocore.lib:redisson-starter:1.0.0'
```


Configure in `application.yml` (note: `redis.uri` and `redis.db` are normally configured in global config
so may not be needed):

```
redis:
  uri: redis://localhost:6379
  db: 0
  redisson:
    lock:
      name: ${service.name}-lock
      wait-time: 0s
      lease-time: 60m
    queue:
      name: ${service.name}-queue
```

For the lock service, name, wait-time and lease-time only configures default values that can be overriden when calling 
the 
lock service:

```
redissonLockService.tryWithLock(name = "my-other-lock", 
                                waitTime = Duration.ofSeconds(10),
                                leaseTime = Duration.ofHours(1)) {
   // DO SOME STUFF WITH LOCK
}
```

# Mocking the RedissonLockService
Calling `RedissonLockService.tryWithLock` on a mockk object without specifiying all parameters will fail, because
the default parameter values reference an instance field that will not be available. To get around this, a spy can
be used instead:
```
private val redissonLockService = spyk(RedissonLockService(mockk(), mockk(relaxed = true)))
``` 

### Contributing ###

#### Tests ####

run `./gradlew check` for unit tests and code quality checks
  
  
#### Publishing ####

* Stable version:
  
```
>./gradlew release
>./gradlew publish

```
* Snapshot version:
  
```
./gradlew publish -Prelease.forceSnapshot

```