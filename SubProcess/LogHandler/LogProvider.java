package SubProcess.LogHandler;

import java.util.ArrayList;

/**
 * The type LogProvider.
 */
public class LogProvider extends ArrayList<String> {
    private int maxCapacity = 1000;

    /**
     * Instantiates a new LogProvider.
     * 默认大小为存储1000行日志
     */
    public LogProvider() {
        super(2 * 1000 + 4);
        this.ensureCapacity(2 * 1000 + 4);
    }

    /**
     * Instantiates a new LogProvider.
     *
     * @param maxCapacity the max capacity
     */
    public LogProvider(int maxCapacity) {
        super(2 * maxCapacity + 4);
        this.ensureCapacity(2 * maxCapacity + 4);
    }

    @Override
    public boolean add(String s) {
        gc();
        return super.add(s);
    }

    /**
     * Gc.
     */
    private void gc() {
        if (this.size() >= 2 * maxCapacity) {
            this.removeRange(0, 1000);
            System.out.println("GC" + this.size());
        }
    }
}
