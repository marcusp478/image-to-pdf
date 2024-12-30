package pdf.project.pdfproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class App extends Application 
{

    private static Scene scene;

    @Override
    public void start(Stage stage)
    {
        stage.setTitle("Image to PDF");
        FileController fileController = new FileController(stage);
        DirectoryChooser dc = new DirectoryChooser();

        VBox layout = new VBox(5);
        Button addFilesButton = new Button("Add Files");
        Button convertButton = new Button("Convert Images to PDF");
        Button mergePDFButton = new Button("Merge PDF Files");
        Button chooseDirectoryButton = new Button("Choose Directory");
        Button clearImagesButton = new Button("Remove Image Files");
        Button clearPDFButton = new Button("Remove PDF Files");

        addFilesButton.setOnAction(e -> { fileController.addToImageFileList(); });

        chooseDirectoryButton.setOnAction(e -> { fileController.setTargetDirectory(dc); });

        convertButton.setOnAction(e -> { fileController.convertImagesToPDF(); });

        mergePDFButton.setOnAction(e -> { fileController.mergePDF(); });

        clearImagesButton.setOnAction(e -> { fileController.getImgFilePathsQueue().clear(); });

        clearPDFButton.setOnAction(e -> { fileController.getFilesToMergeQueue().clear(); });

        HBox row = new HBox(5);
        row.getChildren().add(addFilesButton);
        row.getChildren().add(chooseDirectoryButton);
        row.getChildren().add(clearImagesButton);
        row.getChildren().add(clearPDFButton);
        row.getChildren().add(convertButton);
        row.getChildren().add(mergePDFButton);
        layout.getChildren().add(row);

        // Create scene
        scene = new Scene(layout, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) 
    {
        launch();
    }

}