# Quarkus Bug with Infinispan Client and Scheduler
This example reproduces a bug related to using the quarkus-infinispan-client and quarkus-scheduler modules together.
Based on the infinispan-quarkus-quickstart example from [infinispan-client-quickstart](https://github.com/quarkusio/quarkus-quickstarts/blob/main/infinispan-client-quickstart)
I added a "Scheduler" class and slightly modified docker-compose.yaml. I also added the quarkus-scheduler dependency to the pom.xml file. 
Important! This bug only appears if the Infinispan client is running in a docker environment. 
If the Infinispan client is running outside of docker, then everything works fine.

# Steps to reproduce

1. Download sources from GitHub
```shell
git clone git@github.com:vitaly-masterov/infinispan-client-quickstart.git
```
2. Change to the project directory
```shell
cd infinispan-client-quickstart
```
3. Build maven project:
```shell
./mvnw clean package
```
4. Build docker image
```shell
docker build --file src/main/docker/Dockerfile.jvm --tag quarkus/infinispan-client .
```
5. Run docker-compose
```shell
docker-compose up
```

This is the  stack trace snippet after running docker-compose:
```shell
client      | 2023-07-22 04:00:00,324 ERROR [org.inf.HOTROD] (HotRod-client-async-pool-2-12) ISPN004007: Exception encountered. Retry 10 out of 10: io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: /172.25.0.4:11222
client      | Caused by: java.net.ConnectException: Connection refused
client      |   at java.base/sun.nio.ch.SocketChannelImpl.checkConnect(Native Method)
client      |   at java.base/sun.nio.ch.SocketChannelImpl.finishConnect(SocketChannelImpl.java:777)
client      |   at io.netty.channel.socket.nio.NioSocketChannel.doFinishConnect(NioSocketChannel.java:337)
client      |   at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.finishConnect(AbstractNioChannel.java:334)
client      |   at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:776)
client      |   at io.netty.channel.nio.NioEventLoop.processSelectedKeysPlain(NioEventLoop.java:689)
client      |   at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:652)
client      |   at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:562)
client      |   at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997)
client      |   at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
client      |   at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
client      |   at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
client      |   at java.base/java.lang.Thread.run(Thread.java:829)
client      | 
client      | 2023-07-22 04:00:00,326 ERROR [io.qua.sch.com.run.StatusEmitterInvoker] (executor-thread-1) Error occurred while executing task for trigger IntervalTrigger [id=1_org.acme.infinispan.client.Schedulator#run, interval=10000]: java.util.concurrent.CompletionException: java.lang.RuntimeException: Error injecting org.infinispan.client.hotrod.RemoteCache<java.lang.String, org.acme.infinispan.client.Greeting> org.acme.infinispan.client.Schedulator.cache
client      |   at java.base/java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:331)
client      |   at java.base/java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:346)
client      |   at java.base/java.util.concurrent.CompletableFuture.uniWhenComplete(CompletableFuture.java:870)
client      |   at java.base/java.util.concurrent.CompletableFuture.uniWhenCompleteStage(CompletableFuture.java:883)
client      |   at java.base/java.util.concurrent.CompletableFuture.whenComplete(CompletableFuture.java:2251)
client      |   at java.base/java.util.concurrent.CompletableFuture$MinimalStage.whenComplete(CompletableFuture.java:2820)
client      |   at io.quarkus.scheduler.common.runtime.DefaultInvoker.invoke(DefaultInvoker.java:24)
client      |   at io.quarkus.scheduler.common.runtime.StatusEmitterInvoker.invoke(StatusEmitterInvoker.java:35)
client      |   at io.quarkus.scheduler.runtime.SimpleScheduler$ScheduledTask.doInvoke(SimpleScheduler.java:416)
client      |   at io.quarkus.scheduler.runtime.SimpleScheduler$ScheduledTask$1.handle(SimpleScheduler.java:397)
client      |   at io.quarkus.scheduler.runtime.SimpleScheduler$ScheduledTask$1.handle(SimpleScheduler.java:393)
client      |   at io.vertx.core.impl.ContextBase.lambda$null$0(ContextBase.java:137)
client      |   at io.vertx.core.impl.ContextInternal.dispatch(ContextInternal.java:264)
client      |   at io.vertx.core.impl.ContextBase.lambda$executeBlocking$1(ContextBase.java:135)
client      |   at io.quarkus.vertx.core.runtime.VertxCoreRecorder$14.runWith(VertxCoreRecorder.java:581)
client      |   at org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2513)
client      |   at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1538)
client      |   at org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:29)
client      |   at org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:29)
client      |   at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
client      |   at java.base/java.lang.Thread.run(Thread.java:829)
client      | Caused by: java.lang.RuntimeException: Error injecting org.infinispan.client.hotrod.RemoteCache<java.lang.String, org.acme.infinispan.client.Greeting> org.acme.infinispan.client.Schedulator.cache
client      |   at org.acme.infinispan.client.Schedulator_Bean.doCreate(Unknown Source)
client      |   at org.acme.infinispan.client.Schedulator_Bean.create(Unknown Source)
client      |   at org.acme.infinispan.client.Schedulator_Bean.create(Unknown Source)
client      |   at io.quarkus.arc.impl.AbstractSharedContext.createInstanceHandle(AbstractSharedContext.java:113)
client      |   at io.quarkus.arc.impl.AbstractSharedContext$1.get(AbstractSharedContext.java:37)
client      |   at io.quarkus.arc.impl.AbstractSharedContext$1.get(AbstractSharedContext.java:34)
client      |   at io.quarkus.arc.impl.LazyValue.get(LazyValue.java:26)
client      |   at io.quarkus.arc.impl.ComputingCache.computeIfAbsent(ComputingCache.java:69)
client      |   at io.quarkus.arc.impl.AbstractSharedContext.get(AbstractSharedContext.java:34)
client      |   at io.quarkus.arc.impl.ClientProxies.getApplicationScopedDelegate(ClientProxies.java:21)
client      |   at org.acme.infinispan.client.Schedulator_ClientProxy.arc$delegate(Unknown Source)
client      |   at org.acme.infinispan.client.Schedulator_ClientProxy.run(Unknown Source)
client      |   at org.acme.infinispan.client.Schedulator_ScheduledInvoker_run_72e66771a77415a7284d3ae42331659c186071de.invokeBean(Unknown Source)
client      |   ... 15 more
client      | Caused by: jakarta.enterprise.inject.CreationException: Error creating synthetic bean [47af2c2b34baea5f4bf1e7f4e2217e68a09e3aaa]: jakarta.enterprise.inject.CreationException: Error creating synthetic bean [ddee68cd3f60c96290f55bc237588d70e5c96aab]: org.infinispan.client.hotrod.exceptions.TransportException:: io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: /172.25.0.4:11222
client      |   at org.infinispan.client.hotrod.RemoteCache_47af2c2b34baea5f4bf1e7f4e2217e68a09e3aaa_Synthetic_Bean.doCreate(Unknown Source)
client      |   at org.infinispan.client.hotrod.RemoteCache_47af2c2b34baea5f4bf1e7f4e2217e68a09e3aaa_Synthetic_Bean.create(Unknown Source)
client      |   at org.infinispan.client.hotrod.RemoteCache_47af2c2b34baea5f4bf1e7f4e2217e68a09e3aaa_Synthetic_Bean.create(Unknown Source)
client      |   at io.quarkus.arc.impl.AbstractSharedContext.createInstanceHandle(AbstractSharedContext.java:113)
client      |   at io.quarkus.arc.impl.AbstractSharedContext$1.get(AbstractSharedContext.java:37)
client      |   at io.quarkus.arc.impl.AbstractSharedContext$1.get(AbstractSharedContext.java:34)
client      |   at io.quarkus.arc.impl.LazyValue.get(LazyValue.java:26)
client      |   at io.quarkus.arc.impl.ComputingCache.computeIfAbsent(ComputingCache.java:69)
client      |   at io.quarkus.arc.impl.AbstractSharedContext.get(AbstractSharedContext.java:34)
client      |   at org.infinispan.client.hotrod.RemoteCache_47af2c2b34baea5f4bf1e7f4e2217e68a09e3aaa_Synthetic_Bean.get(Unknown Source)
client      |   at org.infinispan.client.hotrod.RemoteCache_47af2c2b34baea5f4bf1e7f4e2217e68a09e3aaa_Synthetic_Bean.get(Unknown Source)
client      |   ... 28 more
client      | Caused by: jakarta.enterprise.inject.CreationException: Error creating synthetic bean [ddee68cd3f60c96290f55bc237588d70e5c96aab]: org.infinispan.client.hotrod.exceptions.TransportException:: io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: /172.25.0.4:11222
client      |   at org.infinispan.client.hotrod.RemoteCacheManager_ddee68cd3f60c96290f55bc237588d70e5c96aab_Synthetic_Bean.doCreate(Unknown Source)
client      |   at org.infinispan.client.hotrod.RemoteCacheManager_ddee68cd3f60c96290f55bc237588d70e5c96aab_Synthetic_Bean.create(Unknown Source)
client      |   at org.infinispan.client.hotrod.RemoteCacheManager_ddee68cd3f60c96290f55bc237588d70e5c96aab_Synthetic_Bean.create(Unknown Source)
client      |   at io.quarkus.arc.impl.AbstractSharedContext.createInstanceHandle(AbstractSharedContext.java:113)
client      |   at io.quarkus.arc.impl.AbstractSharedContext$1.get(AbstractSharedContext.java:37)
client      |   at io.quarkus.arc.impl.AbstractSharedContext$1.get(AbstractSharedContext.java:34)
client      |   at io.quarkus.arc.impl.LazyValue.get(LazyValue.java:26)
client      |   at io.quarkus.arc.impl.ComputingCache.computeIfAbsent(ComputingCache.java:69)
client      |   at io.quarkus.arc.impl.AbstractSharedContext.get(AbstractSharedContext.java:34)
client      |   at io.quarkus.arc.impl.ClientProxies.getApplicationScopedDelegate(ClientProxies.java:21)
client      |   at org.infinispan.client.hotrod.RemoteCacheManager_ddee68cd3f60c96290f55bc237588d70e5c96aab_Synthetic_ClientProxy.arc$delegate(Unknown Source)
client      |   at org.infinispan.client.hotrod.RemoteCacheManager_ddee68cd3f60c96290f55bc237588d70e5c96aab_Synthetic_ClientProxy.getCache(Unknown Source)
client      |   at io.quarkus.infinispan.client.runtime.InfinispanClientProducer.getRemoteCache(InfinispanClientProducer.java:374)
client      |   at io.quarkus.infinispan.client.runtime.InfinispanRecorder$3.apply(InfinispanRecorder.java:54)
client      |   at io.quarkus.infinispan.client.runtime.InfinispanRecorder$3.apply(InfinispanRecorder.java:51)
client      |   at io.quarkus.infinispan.client.runtime.InfinispanRecorder$InfinispanClientSupplier.get(InfinispanRecorder.java:82)
client      |   at io.quarkus.arc.runtime.ArcRecorder$4.apply(ArcRecorder.java:129)
client      |   at io.quarkus.arc.runtime.ArcRecorder$4.apply(ArcRecorder.java:126)
client      |   at org.infinispan.client.hotrod.RemoteCache_47af2c2b34baea5f4bf1e7f4e2217e68a09e3aaa_Synthetic_Bean.createSynthetic(Unknown Source)
client      |   ... 39 more
```
