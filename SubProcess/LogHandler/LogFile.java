package SubProcess.LogHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogFile {
    private File file;
    private File logDir;
    private BufferedWriter bufferedWriter;

    public LogFile() {
        logDir = new File("log");
        if (!logDir.exists()) logDir.mkdir();

        file = new File("log" + System.getProperty("file.separator") + StaticalStringGenerator.getTimeWithYear() + ".log");
        try {
            file.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void add(String str) {
        try {
            bufferedWriter.write(str);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
