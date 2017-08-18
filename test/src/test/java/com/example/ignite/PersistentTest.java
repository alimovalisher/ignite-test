package com.example.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteLock;
import org.apache.ignite.Ignition;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PersistentTest {


    @Test
    public void failed() throws Exception {
        try (Ignite ignite = getIgnite()) {
            ignite.services().deployNodeSingleton("test", new Service1());

            Thread.sleep(5_000);
        } ;


        try (Ignite ignite = getIgnite()) {
            IgniteCache<String, String> cache = ignite.getOrCreateCache("test");
            Assert.assertEquals("value", cache.get("key"));
        } ;
    }

    private static class Service1 implements Service {
        @IgniteInstanceResource
        private transient Ignite ignite;

        private transient IgniteCache<String, String> cache;

        private transient ScheduledExecutorService executorService;

        @Override
        public void cancel(ServiceContext ctx) {
            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void init(ServiceContext ctx) throws Exception {
            executorService = Executors.newSingleThreadScheduledExecutor();
            cache = ignite.getOrCreateCache("test");
        }

        @Override
        public void execute(ServiceContext ctx) throws Exception {
            executorService.scheduleWithFixedDelay(() -> {
                try (IgniteLock test = ignite.reentrantLock("test", true, false, true)) {
                    if (test.tryLock(1000, TimeUnit.SECONDS)) {
                        try {
                            Thread.sleep(10_000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);


        }
    }

    private Ignite getIgnite() {
        Ignite ignite = Ignition.start("persistent.xml");
        ignite.active(true);

        return ignite;
    }
}
