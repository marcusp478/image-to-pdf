package pdf.project.pdfproject;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FileListViewCell extends ListCell<Path>
{
    private Label fileName = new Label("");
    private ImageView icon;
    private HBox hbox = new HBox();
    private Pane blankSpace = new Pane();
    private Button removeButton = new Button("X");

    private ImageDisplayWindow imageDisplay;
    private boolean displayIsOpen;

    public FileListViewCell(FileController fc, boolean isImageFile)
    {
        super();

        // Retrieve the appropriate image in source files
        Path imgPath = isImageFile
                    ? Paths.get("src", "main", "resources", "img-icon.png").toAbsolutePath()
                    : Paths.get("src", "main", "resources", "pdf-icon.png").toAbsolutePath();
        try
        {
            Image img = new Image(imgPath.toUri().toURL().toString());
            this.icon = new ImageView(img);
            icon.setFitWidth(20);
            icon.setFitHeight(20);
            icon.setPreserveRatio(true);
        } 
        catch (Exception e) {}

        this.hbox.setSpacing(10);
        this.hbox.getChildren().addAll(icon, fileName, blankSpace, removeButton);
        HBox.setHgrow(blankSpace, Priority.ALWAYS); // Remove button will always be at the right edge

        removeButton.setOnAction(e -> {
            Path item = getItem();
            if(isImageFile)
            {
                fc.getImgFilePathsList().remove(item);
            }
            else
            {
                fc.getFilesToMergeList().remove(item);
            }
        });

        this.setOnMouseClicked(e -> {
            // 1 window per image and blank cells can't spawn a window
            if(getItem() != null && !displayIsOpen && isImageFile)
            {
                displayIsOpen = true;
                imageDisplay = new ImageDisplayWindow(getItem());
                imageDisplay.toFront();
                imageDisplay.show();
            }
        });
    }

    protected void updateItem(Path item, boolean empty)
    {
        // Must be called
        super.updateItem(item, empty);

        // Remove
        if(item == null || empty)
        {
            setText(null);
            setGraphic(null);
            return;
        }

        // Otherwise, add
        fileName.setText(item.getFileName().toString());
        setGraphic(hbox);
    }

    // Displays the image of the list view cell the user clicks on
    private class ImageDisplayWindow extends Stage
    {
        ImageDisplayWindow(Path imgPath)
        {
            Scene scene;
            StackPane container  = new StackPane();
            try
            {
                ImageView imgContainer = new ImageView(
                    new Image(imgPath.toUri().toURL().toString())
                );
                imgContainer.setFitWidth(400);
                imgContainer.setFitHeight(400);
                imgContainer.setPreserveRatio(true);

                container.getChildren().add(imgContainer);
                scene = new Scene(container, 500, 500);
                this.setScene(scene);
            }
            catch (Exception e) {}

            this.setOnCloseRequest(e -> {
                displayIsOpen = false;
            });
        }
    }
}