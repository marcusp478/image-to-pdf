package pdf.project.pdfproject;

import java.nio.file.Path;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
        DirectoryChooser dc = new DirectoryChooser();

        VBox layout = new VBox(5);

        Button addFilesButton = new Button("Add Files");
        Button convertButton = new Button("Convert Images to PDF");
        Button mergePDFButton = new Button("Merge PDF Files");
        Button chooseDirectoryButton = new Button("Choose Directory");
        Button clearImagesButton = new Button("Remove Image Files");
        Button clearPDFButton = new Button("Remove PDF Files");
        ListView<Path> imgListView = new ListView<Path>();

        FileController fc = new FileController(stage, imgListView);

        imgListView.setCellFactory(param -> new ImageCell(fc));

        addFilesButton.setOnAction(e -> { fc.addToImageFileList(imgListView); });

        chooseDirectoryButton.setOnAction(e -> { fc.setTargetDirectory(dc); });

        convertButton.setOnAction(e -> { fc.convertImagesToPDF(); });

        mergePDFButton.setOnAction(e -> { fc.mergePDF(); });

        clearImagesButton.setOnAction(e -> { 
            ArrayList<Path> imgFilePaths = fc.getImgFilePathsList();
            imgFilePaths.clear(); 
            imgListView.getItems().clear();
        });

        clearPDFButton.setOnAction(e -> { fc.getFilesToMergeList().clear(); });

        HBox row = new HBox(5);
        row.getChildren().addAll(
            addFilesButton,
            chooseDirectoryButton,
            clearImagesButton,
            clearPDFButton,
            convertButton,
            mergePDFButton
        );

        HBox row2 = new HBox(5);
        row2.getChildren().addAll(imgListView);

        layout.getChildren().addAll(row, row2);

        // Create scene
        scene = new Scene(layout, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }

}