import SubProcess.SubProcLuncher;

public class ConsoleMain {
    public static void main(String[] args) {
        SubProcLuncher luncher = new SubProcLuncher("va.cfg");
        luncher.lunchAndWait();

    }
}
