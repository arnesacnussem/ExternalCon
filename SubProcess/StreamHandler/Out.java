package SubProcess.StreamHandler;

import SubProcess.SubProcess;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Out implements Runnable {
    private SubProcess subProcess;
    private Thread thread;
    private boolean USE_STDOUT, USE_STDERR;

    public Out(SubProcess subProcess, boolean USE_STDOUT, boolean USE_STDERR) {
        this.subProcess = subProcess;
        this.USE_STDOUT = USE_STDOUT;
        this.USE_STDERR = USE_STDERR;
    }

    public void start() {
        thread = new Thread(this, "STREAM_HANDLER_OUTPUT_THREAD");
        thread.start();
    }

    @Override
    public void run() {
        if (USE_STDOUT) {
            new Thread(() -> initOutputStream(), "STREAM_HANDLER_STDOUT_THREAD").start();
        }

        if (USE_STDERR) {
            new Thread(() -> initErrStream(), "STREAM_HANDLER_STDERR_THREAD").start();
        }
    }

    public void initOutputStream() {
        try {
            BufferedReader b = new BufferedReader(new InputStreamReader(subProcess.getProcess().getInputStream(), System.getProperty("sun.jnu.encoding")));
            String line;
            while (subProcess.getProcess().isAlive()) {
                if ((line = b.readLine()) != null) subProcess.getLog().push(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initErrStream() {
        try {
            BufferedReader b = new BufferedReader(new InputStreamReader(subProcess.getProcess().getErrorStream(), System.getProperty("sun.jnu.encoding")));
            String line;
            while (subProcess.getProcess().isAlive()) {
                if ((line = b.readLine()) != null)
                    subProcess.getLog().push("[STDERR]" + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
