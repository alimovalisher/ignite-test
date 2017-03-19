package com.example;

import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.jetbrains.annotations.Nullable;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class NoOpCacheStoreImpl implements CacheStore<Long, Long>, Serializable {
    @Override
    public void loadCache(IgniteBiInClosure<Long, Long> clo, @Nullable Object... args) throws CacheLoaderException {
        throw new RuntimeException();
    }

    @Override
    public void sessionEnd(boolean commit) throws CacheWriterException {
        throw new RuntimeException();
    }

    @Override
    public Long load(Long key) throws CacheLoaderException {
        throw new RuntimeException();
    }

    @Override
    public Map<Long, Long> loadAll(Iterable<? extends Long> keys) throws CacheLoaderException {
        throw new RuntimeException();
    }

    @Override
    public void write(Cache.Entry<? extends Long, ? extends Long> entry) throws CacheWriterException {
        throw new RuntimeException();
    }

    @Override
    public void writeAll(Collection<Cache.Entry<? extends Long, ? extends Long>> entries) throws CacheWriterException {
        throw new RuntimeException();
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        throw new RuntimeException();
    }

    @Override
    public void deleteAll(Collection<?> keys) throws CacheWriterException {
        throw new RuntimeException();
    }
}