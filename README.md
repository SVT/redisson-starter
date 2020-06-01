[![REUSE status](https://api.reuse.software/badge/git.fsfe.org/reuse/api)](https://api.reuse.software/info/git.fsfe.org/reuse/api)

redisson-starter
===

Spring boot starter that configures a [redisson](https://github.com/redisson/redisson) client.
 In addition to configuring the redisson client, it also optionally provides additional services for working with
 redisson locks and queues:
 
 - **RedissonLockService**
 A service that provied a ```tryWithLock``` method that uses a redisson lock. A bean will be provided 
 if `redis.redisson.lock.name-prefix` property is set.
 - **RedissonLibQeueue**
If `redis.redisson.queue.name property` is set, a redisson priority queue will be
provided.

This library is written in kotlin.

### Usage ###

Add the lib as a dependency to your build.gradle

```
implementation 'se.svt.oss.lib:redisson-starter:1.1.0'
```


Configure in `application.yml` (or other property source) :

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

# Mocking the RedissonLockService with mockk
Calling `RedissonLockService.tryWithLock` on a mockk object without specifiying all parameters will fail, because
the default parameter values reference an instance field that will not be available. To get around this, a spy can
be used instead:
```
private val redissonLockService = spyk(RedissonLockService(mockk(), mockk(relaxed = true)))
``` 

### Contributing ###

#### Tests ####

run `./gradlew check` for unit tests and code quality checks
  
## Update version and release to jcenter/bintray (for maintainers)

1. Make sure you are on master branch and that everything is pushed to master
2. ./gradlew release to tag a new version (this uses Axion release plugin). The new tag is automatically pushed to github,
   and the github pipeline will build the project and publish the new version to bintray.

## License

Copyright 2020 Sveriges Television AB

This software is released under the Apache 2.0 License.

## Primary Maintainers

SVT Videocore team <videocore@teams.svt.se>
