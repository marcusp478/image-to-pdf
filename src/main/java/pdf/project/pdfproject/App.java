package pdf.project.pdfproject;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.GridPane;
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
        FileController fc = new FileController(stage);

        AssignFileNameWindow afnw = new AssignFileNameWindow(fc);

        // Layout element initialization

        VBox layout = new VBox(5);
        GridPane container = new GridPane();
        HBox imageButtons = new HBox(2);
        HBox pdfButtons = new HBox(2);

        MenuItem addImages = new MenuItem("Add Images");
        MenuItem chooseDirectory = new MenuItem("Choose File Destination");
        MenuItem exit = new MenuItem("Exit");
        Menu file = new Menu("File");
        MenuBar menuBar = new MenuBar();

        ListView<Path> imgListView = new ListView<Path>(fc.getImgFilePathsList());
        ListView<Path> mergeFilesListView = new ListView<Path>(fc.getFilesToMergeList());
        imgListView.setCellFactory(param -> new FileListViewCell(fc, true));
        mergeFilesListView.setCellFactory(param -> new FileListViewCell(fc, false));

        Button convertButton = new Button("Convert");
        Button mergePDFButton = new Button("Merge");
        Button clearImagesButton = new Button("Clear");
        Button clearPDFButton = new Button("Clear");

        // Event handling for UI components

        addImages.setOnAction(e -> fc.addToImageFileList());

        chooseDirectory.setOnAction(e -> fc.setTargetDirectory(dc));

        convertButton.setOnAction(e -> fc.convertImagesToPDF());

        mergePDFButton.setOnAction(e -> {
            if(fc.getFilesToMergeList().size() >= 2)
            {
                afnw.toFront();
                afnw.show();
            }
        });

        clearImagesButton.setOnAction(e -> fc.getImgFilePathsList().clear());

        clearPDFButton.setOnAction(e -> fc.getFilesToMergeList().clear());

        exit.setOnAction(e -> stage.close());

        // Create layout

        file.getItems().addAll(addImages, chooseDirectory, new SeparatorMenuItem(), exit);
        menuBar.getMenus().add(file);

        imgListView.setPrefWidth(450);
        mergeFilesListView.setPrefWidth(450);

        imageButtons.getChildren().addAll(convertButton, clearImagesButton);
        pdfButtons.getChildren().addAll(mergePDFButton, clearPDFButton);

        GridPane.setConstraints(imgListView, 0, 0);
        GridPane.setConstraints(mergeFilesListView, 1, 0);
        GridPane.setConstraints(imageButtons, 0, 1);
        GridPane.setConstraints(pdfButtons, 1, 1);

        container.setAlignment(Pos.CENTER);
        container.setHgap(10);

        container.getChildren().addAll(
            imgListView,
            mergeFilesListView,
            imageButtons,
            pdfButtons
        );

        layout.getChildren().addAll(menuBar, container);

        // Create scene
        scene = new Scene(layout, 900, 480);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }

}