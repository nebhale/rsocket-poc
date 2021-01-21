## Start Collector
```plain
$ ./mvnw -pl collector -am spring-boot:run


  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.2)

2021-01-21 15:32:22.288  INFO 10954 --- [           main] com.vmware.collector.Collector           : Starting Collector using Java 11.0.10 on nibbler.local with PID 10954 (/Users/bhale/dev/sources/nebhale/rsocket-poc/collector/target/classes started by bhale in /Users/bhale/dev/sources/nebhale/rsocket-poc/collector)
2021-01-21 15:32:22.289  INFO 10954 --- [           main] com.vmware.collector.Collector           : No active profile set, falling back to default profiles: default
2021-01-21 15:32:22.662  INFO 10954 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
2021-01-21 15:32:22.667  INFO 10954 --- [           main] com.vmware.collector.Collector           : Started Collector in 0.511 seconds (JVM running for 0.651)
```

## Start Sidecar
```plain
$ ./mvnw -pl sidecar -am spring-boot:run

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.2)

2021-01-21 15:32:47.664  INFO 11009 --- [           main] com.vmware.sidecar.Sidecar               : Starting Sidecar using Java 11.0.10 on nibbler.local with PID 11009 (/Users/bhale/dev/sources/nebhale/rsocket-poc/sidecar/target/classes started by bhale in /Users/bhale/dev/sources/nebhale/rsocket-poc/sidecar)
2021-01-21 15:32:47.665  INFO 11009 --- [           main] com.vmware.sidecar.Sidecar               : No active profile set, falling back to default profiles: default
2021-01-21 15:32:47.928  INFO 11009 --- [           main] o.s.s.c.ThreadPoolTaskScheduler          : Initializing ExecutorService 'taskScheduler'
2021-01-21 15:32:47.934  INFO 11009 --- [   scheduling-1] com.vmware.sidecar.FreeMemoryUpdater     : Sending Free Memory from 37d54bc1-7ef4-4cf5-9b79-132600679202
2021-01-21 15:32:47.935  INFO 11009 --- [           main] com.vmware.sidecar.Sidecar               : Started Sidecar in 0.398 seconds (JVM running for 0.538)
2021-01-21 15:32:53.116  INFO 11009 --- [   scheduling-1] com.vmware.sidecar.FreeMemoryUpdater     : Sending Free Memory from 37d54bc1-7ef4-4cf5-9b79-132600679202
2021-01-21 15:32:58.122  INFO 11009 --- [   scheduling-1] com.vmware.sidecar.FreeMemoryUpdater     : Sending Free Memory from 37d54bc1-7ef4-4cf5-9b79-132600679202
2021-01-21 15:33:03.134  INFO 11009 --- [   scheduling-1] com.vmware.sidecar.FreeMemoryUpdater     : Sending Free Memory from 37d54bc1-7ef4-4cf5-9b79-132600679202
2021-01-21 15:33:08.140  INFO 11009 --- [   scheduling-1] com.vmware.sidecar.FreeMemoryUpdater     : Sending Free Memory from 37d54bc1-7ef4-4cf5-9b79-132600679202
...
```

## View Pushed Data
```plain
$ curl -ssL http://localhost:8080/free-memory | jq

{
  "37d54bc1-7ef4-4cf5-9b79-132600679202": {
    "size": 241749528,
    "lastUpdate": "2021-01-21T23:34:38.253933Z"
  }
}
```

## View Pulled Data

_The Sidecar ID is random, so remember to replace the value in the example with the one from your running sidecar_

```plain
$ curl -ssL http://localhost:8080/total-memory/37d54bc1-7ef4-4cf5-9b79-132600679202 | jq
{
  "size": 268435456,
  "lastUpdate": "2021-01-21T23:35:22.598481Z"
}
```
