package pdf.project.pdfproject;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GetFileNameWindow extends Stage {
    private static Scene scene;
    private String fileName;

    public GetFileNameWindow(FileController fc)
    {
        //160, 120
        VBox vbox = new VBox();
        this.setTitle("Enter File Name");
        TextField tf = new TextField();
        StackPane top = new StackPane();
        StackPane bottom = new StackPane();
        Button enter = new Button("OK");
        enter.setPrefWidth(50);

        enter.setOnAction(e -> {
            fileName = tf.getText();
            fc.mergePDF(fileName);
            this.close();
        });

        tf.setMaxWidth(250);
        top.getChildren().add(tf);
        bottom.getChildren().add(enter);
        bottom.setPadding(new Insets(0, 0, 0, 200)); // Right-align with text box
        vbox.getChildren().addAll(top, bottom);

        vbox.setPadding(new Insets(10, 0, 0, 0)); // Bring elements of VBox down
        vbox.setSpacing(10);

        scene = new Scene(vbox, 280, 80);
        this.setResizable(false);
        this.setScene(scene);
        this.setOnCloseRequest(Event::consume); // user must input name (or leave empty)
    }

    public String getFileName()
    {
        return fileName;
    }
}
