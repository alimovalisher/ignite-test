package com.example.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.compute.ComputeTask;
import org.apache.ignite.configuration.CacheConfiguration;

public class SimpleClient {
    public static void main(String[] args) {
        Ignite ignite = Ignition.start("client.xml");

        Integer result = ignite.compute(ignite.cluster().forAttribute("role", "data")).execute(new TestComputeTask3(), null);

        System.out.println("result: " + result);

        CacheConfiguration<Integer, ComputeTask> configuration = new CacheConfiguration<>("test");
        configuration.setBackups(2);

        ignite.getOrCreateCache(configuration).put(1, new TestComputeTask3());

        ignite.close();
    }
}
