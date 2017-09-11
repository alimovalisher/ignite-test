package com.example.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class DataNode {
    public static void main(String[] args) {
        Ignite ignite = Ignition.start("server.xml");
    }
}
