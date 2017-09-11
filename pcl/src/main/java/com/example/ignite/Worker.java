package com.example.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class Worker {
    public static void main(String[] args) {
        Ignite ignite = Ignition.start("worker.xml");

        ignite.services(ignite.cluster().forAttribute("role", "worker")).deployNodeSingleton("test", new TestServiceImpl());
    }
}
