package SubProcess.StreamHandler;

import SubProcess.LogHandler.StaticalStringGenerator;
import SubProcess.SubProcess;

import java.io.IOException;
import java.util.Scanner;

import static SubProcess.Status.*;

public class In implements Runnable {
    private SubProcess subProcess;
    private Thread thread;
    private static final String cmdList[] = {"RESTART", "FORCESTOP"};

    public In(SubProcess subProcess) {
        this.subProcess = subProcess;
    }

    public void start() {
        thread = new Thread(this, "STREAM_HANDLER_IN_THREAD");
        thread.start();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String inputBuff;
        boolean preProcReturn = false;
        while (true) {
            if (preProcReturn || subProcess.getServerStatus() == STOPPED) thread.stop();
            inputBuff = scanner.nextLine();
            if (inputBuff.equals("")) continue;
            subProcess.getLog().push(StaticalStringGenerator.getTimePerfix() + "[SubProcess/StreamHandler/In]: Console issued -> " + inputBuff);
            preProcReturn = chenkAndRun(inputBuff);
            if (!preProcReturn && subProcess.getServerStatus() == RUNNING) {
                subProcess.input(inputBuff);
                if (inputBuff.toUpperCase().equals("STOP")) close();
            } else
                System.out.println(StaticalStringGenerator.getTimePerfix() + "[SubProcess/StreamHandler/In]: " + inputBuff + " not send to server cause status not RUNNING!");
        }
    }

    public void close() {
        thread.stop();//do not use interrupt() cause i tried!
    }


    /**
     * @param str
     * @return <code>true</code> if the command is in {@link In#cmdList Command List} and do not need pass to server proc;
     * <code>false</code> if the command not in the list and need pass to server;
     */
    private boolean chenkAndRun(String str) {
        if (str.equals("")) return false;
        for (int i = 0; i < cmdList.length; i++) {
            if (str.toUpperCase().equals(cmdList[i])) {
                return runPreProc(i);
            }
        }
        return false;
    }

    private boolean runPreProc(int i) {
        switch (i) {
            case 0:
                if (subProcess.getServerStatus() == RUNNING) {
                    subProcess.setServerStatus(RESTARTING);
                    return true;
                }
                return false;
            case 1:
                subProcess.setServerStatus(FORCE_STOP);
                return true;
        }
        return false;
    }

    public void write(String str) {
        if (str.equals("")) return;
        str += System.getProperty("line.separator");
        try {
            subProcess.getProcess().getOutputStream().write(str.getBytes(System.getProperty("sun.jnu.encoding")));
            subProcess.getProcess().getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
