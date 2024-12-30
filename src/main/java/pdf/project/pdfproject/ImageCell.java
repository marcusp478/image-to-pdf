package pdf.project.pdfproject;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class ImageCell extends ListCell<Path>
{
    private Label fileName = new Label("");
    private ImageView icon;
    private HBox hbox = new HBox();
    private Pane blankSpace = new Pane();
    private Button removeButton = new Button("X");
    private FileController fc;

    public ImageCell(FileController fc)
    {
        super();

        this.fc = fc;

        // Retrieve image in source files
        Path imgPath = Paths.get("src", "main", "resources", "img-icon.png").toAbsolutePath();
        try
        {
            Image img = new Image(imgPath.toUri().toURL().toString());
            this.icon = new ImageView(img);
            icon.setFitWidth(20);
            icon.setFitHeight(20);
            icon.setPreserveRatio(true);
        } 
        catch (Exception e) {}

        this.hbox.getChildren().addAll(icon, fileName, blankSpace, removeButton);
        HBox.setHgrow(blankSpace, Priority.ALWAYS);

        removeButton.setOnAction(e -> {
            Path item = getItem();
            getListView().getItems().remove(getItem());
            this.fc.getImgFilePathsList().remove(item);
        });
    }

    protected void updateItem(Path item, boolean empty)
    {
        super.updateItem(item, empty);

        if(item != null && !empty)
        {
            fileName.setText(item.getFileName().toString());
            setGraphic(hbox);
        }
        else
        {
            setText(null);
            setGraphic(null);
        }
    }
}
