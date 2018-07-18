package SubProcess.StreamHandler;

import SubProcess.SubProcess;

import static SubProcess.Status.FORCE_STOP;
import static SubProcess.Status.RESTARTING;

public class InputPreProcessor {
    private SubProcess subProcess;
    private static final String cmdList[] = {"RESTART", "FORCESTOP"};

    public InputPreProcessor(SubProcess subProcess) {
        this.subProcess = subProcess;
    }

    /**
     * @param str
     * @return <code>true</code> if the command is in {@link InputPreProcessor#cmdList Command List};
     * <code>false</code> if the command not in the list;
     */
    public boolean chenkAndRun(String str) {
        for (int i = 0; i < cmdList.length; i++) {
            if (str.toUpperCase().equals(cmdList[i])) {
                runPreProc(i);
                return true;
            }
        }
        return false;
    }

    private void runPreProc(int i) {
        switch (i) {
            case 0:
                subProcess.setServerStatus(RESTARTING);
                break;
            case 1:
                subProcess.setServerStatus(FORCE_STOP);
                break;
        }
    }
}
