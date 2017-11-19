package com.example;

import lombok.Getter;
import lombok.Setter;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;

import java.util.UUID;

@Getter
@Setter
public class UserLog {
    private UUID id;

    @AffinityKeyMapped
    private UUID user;

    public UserLog() {
    }

    public UserLog(UUID id, UUID user) {
        this.id = id;
        this.user = user;
    }
}
