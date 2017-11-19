package com.example.ignite;

import com.example.IgniteFactory;
import com.example.User;
import com.example.UserLog;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class JoinTest {
    private Ignite firstInstance;

    private Ignite secondInstance;

    private IgniteCache<UUID, User> userCache;

    private IgniteCache<UUID, UserLog> userLogCache;

    @Before
    public void setUp() throws Exception {
        firstInstance = IgniteFactory.create();
        secondInstance = IgniteFactory.create();

        userCache = firstInstance.cache("USER");
        userLogCache = firstInstance.cache("USER_LOG");

        for (int u = 0; u < 100_000; u++) {
            UUID id = new UUID(0, u);

            userCache.put(id, new User(id));

            for (int l = 0; l < 20; l++) {
                UUID userLogId = new UUID(u, l);
                userLogCache.put(userLogId, new UserLog(userLogId, id));
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        firstInstance.close();
        secondInstance.close();
    }

    @Test
    public void join() {

        try (FieldsQueryCursor<List<?>> query = userCache.query(new SqlFieldsQuery("select count(*) as cnt from (" +
                                                                                           "select l.user, count(l.id) " +
                                                                                           "from USER u " +
                                                                                           "inner join USER_LOG.USER_LOG l on u.id = l.user " +
                                                                                           "group by l.id" +
                                                                                           ")"))) {
            Object o = query.getAll().get(0).get(0);

            Assert.assertEquals(100_000L, o);
        } ;
    }
}
