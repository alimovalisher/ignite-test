package com.example.ignite;

import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestComputeTask3 extends ComputeTaskAdapter<Integer, Integer> {
    @Nullable
    @Override
    public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> subgrid, @Nullable Integer arg) throws IgniteException {
        return subgrid.stream()
                      .collect(Collectors.toMap(
                              n -> new TestJob(),
                              n -> n
                      ));
    }

    @Nullable
    @Override
    public Integer reduce(List<ComputeJobResult> results) throws IgniteException {
        return results.stream()
                      .mapToInt(ComputeJobResult::getData)
                      .sum();
    }

    private static class TestJob implements ComputeJob {

        @Override
        public void cancel() {}

        @Override
        public Object execute() throws IgniteException {
            return 3;
        }
    }
}
