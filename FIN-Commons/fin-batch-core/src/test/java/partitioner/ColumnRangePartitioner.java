package partitioner;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import pt.scml.fin.batch.core.partitioner.ColumnRangePartitioner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColumnRangePartitionerTest {

    @Test
    void shouldPartitionEvenlyWhenSizeDivisibleByGridSize() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner(9);
        Map<String, ExecutionContext> result = partitioner.partition(3);

        assertEquals(3, result.size());

        assertEquals(0, result.get("partition0").getInt("startIndex"));
        assertEquals(2, result.get("partition0").getInt("stopIndex"));

        assertEquals(3, result.get("partition1").getInt("startIndex"));
        assertEquals(5, result.get("partition1").getInt("stopIndex"));

        assertEquals(6, result.get("partition2").getInt("startIndex"));
        assertEquals(8, result.get("partition2").getInt("stopIndex"));
    }

    @Test
    void shouldDistributeRemainderAcrossFirstPartitions() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner(10);
        Map<String, ExecutionContext> result = partitioner.partition(3);

        assertEquals(3, result.size());

        assertEquals(0, result.get("partition0").getInt("startIndex"));
        assertEquals(3, result.get("partition0").getInt("stopIndex"));

        assertEquals(4, result.get("partition1").getInt("startIndex"));
        assertEquals(6, result.get("partition1").getInt("stopIndex"));

        assertEquals(7, result.get("partition2").getInt("startIndex"));
        assertEquals(9, result.get("partition2").getInt("stopIndex"));
    }

    @Test
    void shouldHandleSizeSmallerThanGridSize() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner(2);
        Map<String, ExecutionContext> result = partitioner.partition(4);

        assertEquals(4, result.size());

        assertEquals(0, result.get("partition0").getInt("startIndex"));
        assertEquals(0, result.get("partition0").getInt("stopIndex"));

        assertEquals(1, result.get("partition1").getInt("startIndex"));
        assertEquals(1, result.get("partition1").getInt("stopIndex"));

        assertEquals(2, result.get("partition2").getInt("startIndex"));
        assertEquals(1, result.get("partition2").getInt("stopIndex"));

        assertEquals(2, result.get("partition3").getInt("startIndex"));
        assertEquals(1, result.get("partition3").getInt("stopIndex"));
    }

    @Test
    void shouldHandleZeroSize() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner(0);
        Map<String, ExecutionContext> result = partitioner.partition(3);

        assertEquals(3, result.size());

        result.forEach((key, ctx) -> {
            assertEquals(0, ctx.getInt("startIndex"));
            assertEquals(-1, ctx.getInt("stopIndex"));
        });
    }
}

