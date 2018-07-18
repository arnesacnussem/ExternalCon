package SubProcess;

import SubProcess.SubProcess;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

public class SubProcLuncher {
    private String[] cmd;
    private Configuration configuration;
    private File cfgFile;

    public SubProcLuncher(String cfgFileName) {
        cfgFile = new File(cfgFileName);
        if (!cfgFile.exists()) {
            cfgFileGen();
        }
        loadConfigurations();
        cmd = cmdBuilder();
    }

    public void lunchAndWait() {
        SubProcess subProcess = new SubProcess("Server", cmd, configuration.getInt("failRetry"), configuration.getBoolean("proc.enableSTDERR"));
        subProcess.start();
        try {
            subProcess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loadConfigurations() {
        Configurations configurations = new Configurations();
        try {
            configuration = configurations.properties(cfgFile);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    private String[] cmdBuilder() {
        String[] cmd = new String[6];
        cmd[0] = "java";
        cmd[1] = "-Xmx" + configuration.getString("cmd.Xmx");
        cmd[2] = "-Xms" + configuration.getString("cmd.Xms");
        cmd[3] = "-jar";
        cmd[4] = configuration.getString("cmd.jar");
        if (configuration.getString("forgeServer.enableGUI").equals("true")) cmd[5] = " ";
        else cmd[5] = "nogui";
        //System.out.println(cmd[0] + " " + cmd[1] + " " + cmd[2] + " " + cmd[3] + " " + cmd[4]);
        return cmd;
    }

    private void cfgFileGen() {
        Configurations configurations = new Configurations();
        try {
            if (cfgFile.createNewFile()) System.out.println("Config file created successful");
            FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configurations.propertiesBuilder(cfgFile);
            Configuration cfg = builder.getConfiguration();
            cfg.addProperty("cmd.jar", "server.jar");
            cfg.addProperty("cmd.Xmx", "1024M");
            cfg.addProperty("cmd.Xms", "1024M");
            cfg.addProperty("proc.enableSTDERR", false);
            cfg.addProperty("forgeServer.enableGUI", false);
            cfg.addProperty("failRetry", 0);
            builder.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
