package com.example;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class IgniteFactory {
    public static Ignite create() {
        return Ignition.start("ignite.xml");
    }
}
