package opencsp.csta;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class MonitorPoint {
    private static List<Integer> usedCrossReferenceIds;
    private CrossReferenceId crossReferenceId;
    private Device monitoredDevice;

    public MonitorPoint(CrossReferenceId crossReferenceId, Device monitoredDevice) {
        this.crossReferenceId = crossReferenceId;
        this.monitoredDevice = monitoredDevice;
    }

    public MonitorPoint(Device monitoredDevice) {
        this.crossReferenceId = createCrossReferenceId();
        this.monitoredDevice = monitoredDevice;
    }

    /**
     * Generate a unique cross reference id.
     * @return Unique cross reference id
     */
    private CrossReferenceId createCrossReferenceId() {
        int id = 0;
        try {
            id = Collections.max(usedCrossReferenceIds);
        } catch (NoSuchElementException ex) {
            System.out.println("No used CrossReferenceID found. Starting at 0");
        }
        id++;
        usedCrossReferenceIds.add(id);
        return new CrossReferenceId(id);
    }
}
