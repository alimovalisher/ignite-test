package com.example.ignite;


import com.example.IgniteFactory;
import com.example.NoOpCacheStoreImpl;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.transactions.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;

public class CacheStoreTest {

    private CacheStore noOpCacheStore = new NoOpCacheStoreImpl();

    private Factory noOpCacheWriterFactory;

    private CacheConfiguration noOpCacheConfiguration;

    private Ignite ignite;

    @Before
    public void setUp() throws Exception {
        ignite = IgniteFactory.create();
        noOpCacheWriterFactory = new FactoryBuilder.SingletonFactory(noOpCacheStore);
        noOpCacheConfiguration = getNoOpCacheConfiguration();
    }


    private CacheConfiguration getNoOpCacheConfiguration() {
        CacheConfiguration cacheConfiguration = new CacheConfiguration("test.no-op");
        cacheConfiguration.setWriteThrough(true);
        cacheConfiguration.setCacheWriterFactory(noOpCacheWriterFactory);
        cacheConfiguration.setWriteBehindBatchSize(1);

        return cacheConfiguration;
    }

    @After
    public void tearDown() throws Exception {
        ignite.close();
    }

    /**
     * If transaction was failed than cache store must not be invoked
     */
    @Test
    public void testTransactionFailed() {
        IgniteCache cache = ignite.getOrCreateCache(noOpCacheConfiguration);

        try (Transaction transaction = ignite.transactions().txStart()) {
            cache.put(1, 1);
            cache.put(2, 2);

            transaction.rollback();
        }
    }
}
