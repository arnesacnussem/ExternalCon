package SubProcess.LogHandler;

public class Log {
    private LogProvider log;
    private LogFile logFile;
    private int maxColume = 1000;

    public Log() {
        log = new LogProvider();
        logFile = new LogFile();
    }

    public Log(int maxColume) {
        this.maxColume = maxColume;
        log = new LogProvider(maxColume);
        logFile = new LogFile();
    }

    public void push(String str) {
        System.out.println(str);
        System.out.flush();
        log.add(str);
        logFile.add(str);

    }

    public int getMaxColume() {
        return maxColume;
    }
}
