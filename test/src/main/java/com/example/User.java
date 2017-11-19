package com.example;

import lombok.Getter;
import lombok.Setter;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;

import java.util.UUID;

@Getter
@Setter
public class User {
    @AffinityKeyMapped
    private UUID id;

    public User() {
    }

    public User(UUID id) {
        this.id = id;
    }
}
