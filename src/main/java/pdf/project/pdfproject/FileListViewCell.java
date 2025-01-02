package pdf.project.pdfproject;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class FileListViewCell extends ListCell<Path>
{
    private Label fileName = new Label("");
    private ImageView icon;
    private HBox hbox = new HBox();
    private Pane blankSpace = new Pane();
    private Button removeButton = new Button("X");

    private FileController fc;

    boolean isImageFile;

    public FileListViewCell(FileController fc, boolean isImageFile)
    {
        super();

        this.fc = fc;
        this.isImageFile = isImageFile;

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
                this.fc.getImgFilePathsList().remove(item);
            }
            else
            {
                this.fc.getFilesToMergeList().remove(item);
            }
        });
    }

    protected void updateItem(Path item, boolean empty)
    {
        Platform.runLater(() -> {
            super.updateItem(item, empty);

            if(item == null || empty)
            {
                setText(null);
                setGraphic(null);
                return;
            }

            fileName.setText(item.getFileName().toString());
            setGraphic(hbox);
        });
    }
}