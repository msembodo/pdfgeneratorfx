package net.msembodo.pdfgeneratorfx;

import javafx.concurrent.Task;
import net.msembodo.pdfgeneratorfx.model.AppConfig;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.StatusBar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AppService {

    private final String PDF_GENERATOR_JAR = "pdf-generator-1.0.1-SNAPSHOT.jar";
    private final String TEMPLATE = "tec_voucher_template.json";
    private final String DATA_CSV = "VMWO_IDEMIA-Dynamic_00092.csv";
    private final String OUTPUT_FOLDER_PATH = "C:\\";

    private Main mainApp;
    private MaskerPane maskerPane;
    private StatusBar statusBar;
    private String lastLine;
    private String line;
    private int exitVal;

    private File appConfigFile;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public AppConfig initConfig() {
        maskerPane = mainApp.getMaskerPane();
        statusBar = mainApp.getStatusBar();
        statusBar.setText("Ready");
        appConfigFile = new File("config.xml");
        if (appConfigFile.exists()) {
            // read config.xml
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(AppConfig.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                AppConfig userConfig = (AppConfig) jaxbUnmarshaller.unmarshal(appConfigFile);
                return userConfig;
            } catch (JAXBException e) {
                System.err.println(e.getMessage());
                return null;
            }
        }
        else {
            // initialize default values and write to xml
            AppConfig defaultConfig = new AppConfig(PDF_GENERATOR_JAR, TEMPLATE, DATA_CSV, OUTPUT_FOLDER_PATH);
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(AppConfig.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // pretty print xml
                jaxbMarshaller.marshal(defaultConfig, appConfigFile);
                return defaultConfig;
            } catch (JAXBException e) {
                System.err.println(e.getMessage());
                return null;
            }
        }
    }

    public void saveAppConfig(AppConfig appConfig) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AppConfig.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(appConfig, appConfigFile);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public boolean isWindows() {
        String os = System.getProperty("os.name");
        if (os == null)
            throw new IllegalStateException("os.name");
        os = os.toLowerCase();
        return os.startsWith("windows");
    }

    public File getJreExecutable() throws FileNotFoundException {
        String jreDirectory = System.getProperty("java.home");
        if (jreDirectory == null)
            throw new IllegalStateException("java.home");
        File exe;
        if (isWindows())
            exe = new File(jreDirectory, "bin/java.exe");
        else
            exe = new File(jreDirectory, "bin/java");
        if (!exe.isFile())
            throw new FileNotFoundException((exe.toString()));
        return exe;
    }

    public int launchProcess(List<String> cmdArray) throws IOException, InterruptedException {
        maskerPane.setVisible(true);
        statusBar.setText("Generating..");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );
                while ((line = reader.readLine()) != null) {
                    lastLine = line;
                }
                exitVal = process.waitFor();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                if (exitVal != 0)
                    System.err.println("Failed executing command. Error code: " + exitVal);
                else {
                    maskerPane.setVisible(false);
                    statusBar.setText(lastLine);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();

        return exitVal;
    }

    public void runShellCommand(String executableJar, String template, String data, String outFolder) {
        try {
            List<String> cmdArray = new ArrayList<String>();
            cmdArray.add(getJreExecutable().toString());
            cmdArray.add("-jar");
            cmdArray.add(executableJar);
            cmdArray.add(template);
            cmdArray.add(data);
            cmdArray.add(outFolder);

            launchProcess(cmdArray);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
