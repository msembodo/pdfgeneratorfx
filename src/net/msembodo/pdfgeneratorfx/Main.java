package net.msembodo.pdfgeneratorfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.msembodo.pdfgeneratorfx.model.AppConfig;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.StatusBar;

import java.io.File;

public class Main extends Application {

    private AppService appService;
    private AppConfig appConfig;

    private Stage primaryStage;

    private TextField templateField;
    private TextField dataField;
    private TextField outputField;

    private MaskerPane maskerPane;
    private StatusBar statusBar;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("eVouchers Generator");
        primaryStage.setMinWidth(690);
        primaryStage.setMinHeight(260);
        maskerPane = new MaskerPane();
        statusBar = new StatusBar();

        appService = new AppService();
        appService.setMainApp(this);
        appConfig = appService.initConfig();

        StackPane stackPane = new StackPane();
        BorderPane borderPane = new BorderPane();

        maskerPane.setVisible(false);
        stackPane.getChildren().add(borderPane);
        stackPane.getChildren().add(maskerPane);

        GridPane gridPane = createAppLayout();
        addControls(gridPane);

        borderPane.setCenter(gridPane);
        borderPane.setBottom(statusBar);

        initializeFields();

        Scene scene = new Scene(stackPane, 650, 220);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createAppLayout() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(25, 40, 15, 40)); // set padding 20px on each side
        gridPane.setHgap(10); // horizontal gap between columns
        gridPane.setVgap(10); // vertical gap between rows

        // column constraints
        ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.LEFT);

        ColumnConstraints columnTwoConstraints = new ColumnConstraints(20, 20, Double.MAX_VALUE);

        ColumnConstraints columnThreeConstraints = new ColumnConstraints(200, 200, Double.MAX_VALUE);
        columnThreeConstraints.setHalignment(HPos.RIGHT);
        columnThreeConstraints.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstraints, columnThreeConstraints);

        return gridPane;
    }

    private void addControls(GridPane gridPane) {
        Label templateLabel = new Label("Template:");
        gridPane.add(templateLabel, 0, 0);
        Button browseTemplateButton = new Button("...");
        browseTemplateButton.setFocusTraversable(false);
        gridPane.add(browseTemplateButton, 1, 0);
        templateField = new TextField();
        templateField.setEditable(false);
        templateField.setFocusTraversable(false);
        gridPane.add(templateField, 2, 0);

        Label dataLabel = new Label("Data (CSV):");
        gridPane.add(dataLabel, 0, 1);
        Button browseDataButton = new Button("Browse...");
        gridPane.add(browseDataButton, 1, 1);
        dataField = new TextField();
        dataField.setEditable(false);
        dataField.setFocusTraversable(false);
        gridPane.add(dataField, 2, 1);

        Label outputLabel = new Label("Output Folder:");
        gridPane.add(outputLabel, 0, 2);
        Button browsePathButton = new Button("Browse...");
        gridPane.add(browsePathButton, 1, 2);
        outputField = new TextField();
        outputField.setEditable(false);
        outputField.setFocusTraversable(false);
        gridPane.add(outputField, 2, 2);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        gridPane.add(separator, 0, 3, 3, 1);

        ButtonBar buttonBar = new ButtonBar();

        Button generateButton = new Button("Generate");
        generateButton.setDefaultButton(true);
        Button quitButton = new Button("Quit");
        buttonBar.getButtons().addAll(generateButton, quitButton);

        gridPane.add(buttonBar, 2, 4);

        // event handlers
        browseTemplateButton.setOnAction(event -> selectTemplate());
        browseDataButton.setOnAction(event -> selectDataCsv());
        browsePathButton.setOnAction(event -> selectOutputFolder());
        generateButton.setOnAction(event -> generateQrCodes());
        quitButton.setOnAction(event -> Platform.exit());
    }

    private void initializeFields() {
        templateField.setText(appConfig.getTemplate());
        dataField.setText(appConfig.getDataCsv());
        outputField.setText(appConfig.getOutputFolderPath());
    }

    private void selectTemplate() {
        FileChooser templateChooser = new FileChooser();
        templateChooser.setTitle("Select Template");
        String templateFolder;
        // set initial folder to last selected
        if (appConfig.getTemplate() != "C:\\") {
            templateFolder = new File(appConfig.getTemplate()).getParent();
            if (templateFolder == null)
                templateFolder = "C:\\";
        }
        else
            templateFolder = "C:\\";
        templateChooser.setInitialDirectory(new File(templateFolder));
        templateChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("eVoucher Template", "*.json"));
        File selectedTemplate = templateChooser.showOpenDialog(primaryStage);
        if (selectedTemplate != null) {
            String selectedTemplateVar = selectedTemplate.getAbsolutePath();
            appConfig.setTemplate(selectedTemplateVar);
            templateField.setText(selectedTemplateVar);
        }
    }

    private void selectDataCsv() {
        FileChooser dataCsvChooser = new FileChooser();
        dataCsvChooser.setTitle("Select Data");
        String dataCsvFolder;
        // set initial folder to last selected
        if (appConfig.getDataCsv() != "C:\\") {
            dataCsvFolder = new File(appConfig.getDataCsv()).getParent();
            if (dataCsvFolder == null)
                dataCsvFolder = "C:\\";
        }
        else
            dataCsvFolder = "C:\\";
        dataCsvChooser.setInitialDirectory(new File(dataCsvFolder));
        dataCsvChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Token Data", "*.csv"));
        File selectedData = dataCsvChooser.showOpenDialog(primaryStage);
        if (selectedData != null) {
            String selectedDataVar = selectedData.getAbsolutePath();
            appConfig.setDataCsv(selectedDataVar);
            dataField.setText(selectedDataVar);
        }
    }

    private void selectOutputFolder() {
        DirectoryChooser outFolderChooser = new DirectoryChooser();
        outFolderChooser.setTitle("Select Output Folder");
        String initialOutFolder;
        // set initial directory
        if (appConfig.getOutputFolderPath() != "C:\\") {
            initialOutFolder = new File(appConfig.getOutputFolderPath()).getAbsolutePath();
            if (initialOutFolder == null)
                initialOutFolder = "C:\\";
        }
        else
            initialOutFolder = "C:\\";
        outFolderChooser.setInitialDirectory(new File(initialOutFolder));
        File outFolder = outFolderChooser.showDialog(primaryStage);
        if (outFolder != null) {
            String outFolderVar = outFolder.getAbsolutePath();
            appConfig.setOutputFolderPath(outFolderVar);
            outputField.setText(outFolderVar);
        }
    }

    private void generateQrCodes() {
        appService.saveAppConfig(appConfig);
        appService.runShellCommand(appConfig.getPdfGeneratorJar(), appConfig.getTemplate(),
                appConfig.getDataCsv(), appConfig.getOutputFolderPath());
    }

    public MaskerPane getMaskerPane() {
        return maskerPane;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
