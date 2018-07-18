package SubProcess;

import SubProcess.LogHandler.Log;
import SubProcess.LogHandler.StaticalStringGenerator;
import SubProcess.StreamHandler.In;
import SubProcess.StreamHandler.Out;
import me.dilley.MineStat;

import java.io.IOException;

import static SubProcess.Status.*;

public class SubProcess extends Thread {
    private String name;
    private java.lang.Process process;
    private String[] cmd;
    private int failRetryTimes;
    private Log log;
    private In in;
    private Out out;
    private Status serverStatus;
    private boolean USE_STDERR;

    public SubProcess(String name, String[] cmd, int failRetryTimes, boolean USE_STDERR) {
        this.name = name;
        this.cmd = cmd;
        log = new Log();
        this.USE_STDERR = USE_STDERR;
        this.failRetryTimes = failRetryTimes;
        setServerStatus(READY);
    }

    @Override
    public void run() {
        if (cmd.length <= 0) {
            System.err.println("\"cmd\" is empty");
        }
        try {
            serverStart();
            statusListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void serverStart() throws IOException {
        if (getServerStatus() != READY) {
            System.out.println("Server not ready ,checking status");
            if (getServerStatus() == STOPPED) setServerStatus(READY);
            else {
                setServerStatus(FORCE_STOP);
                serverForceStop();
            }
        }

        process = Runtime.getRuntime().exec(cmd);
        in = new In(this);
        out = new Out(this, true, USE_STDERR);
        out.start();
        in.start();
        setServerStatus(STARTING);

        MineStat mineStat = new MineStat("127.0.0.1", 25565, 100);
        while (true) {
            if (mineStat.isServerUp()) break;
            else {
                try {
                    sleep(500);
                    mineStat.refresh();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        setServerStatus(RUNNING);
    }

    protected void serverForceStop() {
        process.destroy();
        process.destroyForcibly();
        setServerStatus(STOPPED);
        System.exit(-11);
    }

    protected void serverRestart() {
        log.push(StaticalStringGenerator.getTimePerfix() + "[SubProcess]: Parent process issued -> STOP");
        in.write("STOP");
        try {
            setServerStatus(STOPPING);
            while (process.isAlive()) {
                sleep(10);
            }
            setServerStatus(STOPPED);
            process.destroy();
            setServerStatus(READY);
            serverStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void statusListener() throws IOException {
        try {
            while (process.isAlive()) {
                sleep(10);
                if (getServerStatus() == RESTARTING) serverRestart();
                if (getServerStatus() == STOPPING) sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (getServerStatus() == STOPPING) setServerStatus(STOPPED);
        if (getServerStatus() == RUNNING) {
            setServerStatus(ERROR);
        }
    }

    public Status getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(Status serverStatus) {
        if (serverStatus == FORCE_STOP) {
            serverStatus = FORCE_STOP;
            log.push(StaticalStringGenerator.getTimePerfix() + "[SubProcess]: Server status changed to " + serverStatus);
            serverForceStop();
        }
        if (serverStatus != this.serverStatus) {
            log.push(StaticalStringGenerator.getTimePerfix() + "[SubProcess]: Server status changed to " + serverStatus);
            this.serverStatus = serverStatus;
        }
    }

    public void input(String str) {
        switch (str.trim().toUpperCase()) {
            case "STOP":
                setServerStatus(STOPPED);
                break;
            case "RESTART":
                setServerStatus(RESTARTING);
                return;
        }
        in.write(str);
    }

    public Log getLog() {
        return log;
    }

    public Process getProcess() {
        return process;
    }
}
