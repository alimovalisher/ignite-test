package com.example.ignite;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.compute.ComputeTask;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.ServiceContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestServiceImpl implements TestService {
    private transient ScheduledExecutorService executorService;

    @IgniteInstanceResource
    private transient Ignite ignite;

    @Override
    public void cancel(ServiceContext ctx) {}

    @Override
    public void init(ServiceContext ctx) throws Exception {
        executorService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {
        executorService.scheduleWithFixedDelay(() -> {
            try {
                Object result = ignite.compute(ignite.cluster().forAttribute("role", "data")).execute(new TestComputeTask3(), null);

                log.info("result: {}", result);

                CacheConfiguration<Integer, ComputeTask> cacheCfg = new CacheConfiguration<>("test");
                cacheCfg.setBackups(2);

                ComputeTask test = ignite.getOrCreateCache(cacheCfg).get(1);

                if (test != null) {
                    result = ignite.compute(ignite.cluster().forAttribute("role", "data")).execute(test, null);

                    log.info("result from data node: {}", result);
                }
            } catch (Exception e) {
                log.error("error", e);
            }

        }, 0, 5, TimeUnit.SECONDS);
    }
}
