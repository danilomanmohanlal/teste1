package pt.scml.fin.batch.core.partitioner;

import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

/**
 * A custom {@link Partitioner} that divides a range of items into sub ranges, assigning each
 * partition a start and stop index.
 *
 * <p>This is useful for processing data in parallel when you have a known number of items (rows,
 * records, etc.) and you want to distribute them evenly across multiple threads or processes.</p>
 * <p>
 * Example of how the partitioning might look for size=10 and gridSize=3:
 * <ul>
 *     <li>partition0: startIndex=0, stopIndex=3</li>
 *     <li>partition1: startIndex=4, stopIndex=6</li>
 *     <li>partition2: startIndex=7, stopIndex=9</li>
 * </ul>
 */
public class ColumnRangePartitioner implements Partitioner {

    private int size;

    public ColumnRangePartitioner(int size) {
        this.size = size;
    }


    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        int baseSize = size / gridSize;  // Even distribution
        int remainder = size % gridSize; // Extra lines to distribute

        Map<String, ExecutionContext> result = new HashMap<>();
        int start = 0;

        for (int i = 0; i < gridSize; i++) {
            int partitionSize = baseSize + (i < remainder ? 1 : 0); // Distribute remainder evenly
            int end = start + partitionSize - 1;

            ExecutionContext context = new ExecutionContext();
            context.putInt("startIndex", start);
            context.putInt("stopIndex", end);

            result.put("partition" + i, context);

            start = end + 1; // Move to the next partition
        }

        return result;
    }
}
