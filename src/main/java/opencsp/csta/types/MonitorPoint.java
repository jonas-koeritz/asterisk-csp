package opencsp.csta.types;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class MonitorPoint {
    private CrossReferenceId crossReferenceId;
    private Device monitoredDevice;

    public MonitorPoint(CrossReferenceId crossReferenceId, Device monitoredDevice) {
        this.crossReferenceId = crossReferenceId;
        this.monitoredDevice = monitoredDevice;
    }
}
