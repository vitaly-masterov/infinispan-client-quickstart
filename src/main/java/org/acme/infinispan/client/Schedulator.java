package org.acme.infinispan.client;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

@ApplicationScoped
public class Schedulator {

    private static final Logger LOGGER = Logger.getLogger(Schedulator.class);

    @Inject
    @Remote("mycache")
    RemoteCache<String, Greeting> cache;

    @Scheduled(every = "10s")
    public void run() {
        LOGGER.info("[Schedulator] run...");
        var greeting = new Greeting();
        greeting.name = "Infinispan Client";
        greeting.message = "Hello World, Infinispan is up!";
        cache.put("hello", greeting);
    }

}
