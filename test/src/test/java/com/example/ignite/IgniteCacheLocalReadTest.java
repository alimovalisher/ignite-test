package com.example.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.openjdk.jmh.annotations.*;

import javax.cache.Cache;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class IgniteCacheLocalReadTest {
    @State(Scope.Benchmark)
    public static class Context {
        private static final CacheConfiguration<UUID, UUID> cacheConfig = new CacheConfiguration<UUID, UUID>("cache.first");

        public Ignite ignite;
        public IgniteCache<UUID, UUID> cache;
        public ExecutorService executor;

        @Setup
        public void setUp() {
            ignite = Ignition.start("ignite.xml");

            cacheConfig.setBackups(0);
            cacheConfig.setStoreKeepBinary(false);
            cacheConfig.setReadFromBackup(false);

            cache = ignite.getOrCreateCache(cacheConfig);

            for (int i = 0; i < 1_000_000; i++) {
                cache.put(UUID.randomUUID(), UUID.randomUUID());
            }

            executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }

        @TearDown
        public void tearDown() {
            cache.clear();
            cache.destroy();
            ignite.close();

            executor.shutdown();
        }
    }


    @Benchmark
    @Warmup(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(4)
    public void readCacheLocal(Context context) {
        IgniteCache<UUID, UUID> cache = context.cache;

        long startTime = System.currentTimeMillis();

        cache.localEntries(CachePeekMode.PRIMARY).forEach(entry -> {
        });

        long executionTime = System.currentTimeMillis() - startTime;
        long entriesPerMs = cache.size(CachePeekMode.ALL) / executionTime;
        System.out.println(String.format("Complete in: %dms (%d entries per ms)", executionTime, entriesPerMs));
    }

    @Benchmark
    @Warmup(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(4)
    public void readFromPartitionsWithStream(Context context) {
        IgniteCache<UUID, UUID> cache = context.cache;

        int[] partitions = context.ignite.affinity(cache.getName()).primaryPartitions(context.ignite.cluster().localNode());

        long startTime = System.currentTimeMillis();

        Arrays.stream(partitions)
              .parallel()
              .forEach(partition -> {
                  ScanQuery<Object, Object> qry = new ScanQuery<>(partition);
                  qry.setLocal(true);
                  qry.setPageSize(5_000);


                  QueryCursor<Cache.Entry<Object, Object>> query = cache.query(qry);
                  List<Cache.Entry<Object, Object>> all = query.getAll();
              });

        long executionTime = System.currentTimeMillis() - startTime;
        long entriesPerMs = cache.size(CachePeekMode.ALL) / executionTime;
        System.out.println(String.format("Complete in: %dms (%d entries per ms)", executionTime, entriesPerMs));
    }

    @Benchmark
    @Warmup(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(4)
    public void readFromPartitionsWithExecutors(Context context) throws InterruptedException {
        IgniteCache<UUID, UUID> cache = context.cache;

        int[] partitions = context.ignite.affinity(cache.getName()).primaryPartitions(context.ignite.cluster().localNode());

        long startTime = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(partitions.length);

        for (int partition : partitions) {
            context.executor.submit(() -> {
                ScanQuery<Object, Object> qry = new ScanQuery<>(partition);
                qry.setLocal(true);
                qry.setPageSize(5_000);


                QueryCursor<Cache.Entry<Object, Object>> query = cache.query(qry);
                List<Cache.Entry<Object, Object>> all = query.getAll();

                latch.countDown();
            });
        }


        latch.await();

        long executionTime = System.currentTimeMillis() - startTime;
        long entriesPerMs = cache.size(CachePeekMode.ALL) / executionTime;
        System.out.println(String.format("Complete in: %dms (%d entries per ms)", executionTime, entriesPerMs));
    }
}
