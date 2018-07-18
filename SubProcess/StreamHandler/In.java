package SubProcess.StreamHandler;

import SubProcess.LogHandler.StaticalStringGenerator;
import SubProcess.SubProcess;

import java.io.IOException;
import java.util.Scanner;

import static SubProcess.Status.RUNNING;
import static SubProcess.Status.STARTING;
import static SubProcess.Status.STOPPED;

public class In implements Runnable {
    private SubProcess subProcess;
    private Thread thread;
    private InputPreProcessor preProcessor;

    public In(SubProcess subProcess) {
        this.subProcess = subProcess;
        preProcessor = new InputPreProcessor(subProcess);
    }

    public void start() {
        thread = new Thread(this, "STREAM_HANDLER_IN_THREAD");
        thread.start();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String inputBuff;
        while (true) {
            if (subProcess.getServerStatus() == STOPPED) break;
            inputBuff = scanner.nextLine();
            subProcess.getLog().push(StaticalStringGenerator.getTimePerfix() + "[SubProcess/StreamHandler/In]: Console issued -> " + inputBuff);
            if (!preProcessor.chenkAndRun(inputBuff) && subProcess.getServerStatus() == RUNNING)
                subProcess.input(inputBuff);
            else System.out.println(StaticalStringGenerator.getTimePerfix()+"[SubProcess/StreamHandler/In]: "+inputBuff+" skipped cause server not running!");
        }
    }


    public void write(String str) {
        str += System.getProperty("line.separator");
        try {
            subProcess.getProcess().getOutputStream().write(str.getBytes(System.getProperty("sun.jnu.encoding")));
            subProcess.getProcess().getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
