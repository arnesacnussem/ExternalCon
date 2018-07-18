package SubProcess.LogHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StaticalStringGenerator {
    public static String getTimeWithYear() {
        Date date=new Date();
       SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
       return simpleDateFormat.format(date);
    }

    public static String getTimeOfDay() {
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String getTimePerfix(){
        return "[" + getTimeOfDay() + "] ";
    }
}
