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
    private boolean USE_STDERR,USE_STDOUT,USE_STDIN;

    public SubProcess(String name, String[] cmd, int failRetryTimes, boolean USE_STDERR) {
        this.name = name;
        this.cmd = cmd;
        log = new Log();
        this.USE_STDERR = USE_STDERR;
        USE_STDIN=true;
        USE_STDOUT=true;
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
            if (getServerStatus() == STOPPED) {
                in.close();
                setServerStatus(READY);
            } else {
                setServerStatus(FORCE_STOP);
                serverForceStop();
            }
        }

        process = Runtime.getRuntime().exec(cmd);
        in = new In(this);
        out = new Out(this, true, USE_STDERR);
        if(USE_STDOUT)out.start();
        if(USE_STDIN)in.start();
        setServerStatus(STARTING);

        MineStat mineStat = new MineStat("127.0.0.1", 25565, 100);
        while (process.isAlive()) {
            if (mineStat.isServerUp()) {
                setServerStatus(RUNNING);
                break;
            } else {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mineStat.refresh();
            }
        }
    }

    protected void serverForceStop() {
        process.destroy();
        process.destroyForcibly();
        setServerStatus(STOPPED);
        System.exit(-11);
    }

    protected void serverRestart() {
        log.push(StaticalStringGenerator.getTimePerfix() + "[SubProcess]: Parent process issued -> STOP");
        input("stop");
        try {
            setServerStatus(STOPPING);
            while (process.isAlive()) {
                sleep(10);
            }
            setServerStatus(STOPPED);
            process.destroy();
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
        if (getServerStatus() == STOPPING) {
            setServerStatus(STOPPED);
            return;
        } else {
            setServerStatus(ERROR);
            if (failRetryTimes == 0) {
                log.push(StaticalStringGenerator.getTimePerfix() + "[SubProcess]: Server error,no fail retry set,force stop the server");
                serverForceStop();
            }
            while (failRetryTimes > 0) {
                failRetryTimes--;
                log.push(StaticalStringGenerator.getTimePerfix() + "[SubProcess]: Server error,trying to restart,retry left " + failRetryTimes);
                serverRestart();
                if (getServerStatus() == RUNNING) statusListener();
            }
            log.push(StaticalStringGenerator.getTimePerfix() + "[SubProcess]: Server error,retry too many times,force stop the server");
            serverForceStop();
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
        if (str.equals("")) return;
        switch (str.trim().toUpperCase()) {
            case "STOP":
                setServerStatus(STOPPING);
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

    public boolean isUSE_STDOUT() {
        return USE_STDOUT;
    }

    public void setUSE_STDOUT(boolean USE_STDOUT) {
        this.USE_STDOUT = USE_STDOUT;
    }

    public boolean isUSE_STDIN() {
        return USE_STDIN;
    }

    public void setUSE_STDIN(boolean USE_STDIN) {
        this.USE_STDIN = USE_STDIN;
    }
}
