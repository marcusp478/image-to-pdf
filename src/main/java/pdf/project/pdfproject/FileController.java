package pdf.project.pdfproject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FileController
{
    private ArrayList<Path> imgFilePaths;
    private ArrayList<String> filesToMerge;
    private Stage primaryStage;
    private String targetDirectory;
    private ListView<Path> imgListView;
    private ListView<Path> mergeFilesListView;

    public FileController(Stage primaryStage, 
                          ListView<Path> imgListView,
                          ListView<Path> mergeFilesListView) 
    {
        imgFilePaths = new ArrayList<Path>();
        filesToMerge = new ArrayList<String>();
        targetDirectory = null;
        this.primaryStage = primaryStage;
        this.imgListView = imgListView;
        this.mergeFilesListView = mergeFilesListView;
    }

    public ArrayList<Path> getImgFilePathsList() { return this.imgFilePaths; }

    public ArrayList<String> getFilesToMergeList() { return this.filesToMerge; }

    public void setTargetDirectory(DirectoryChooser dc)
    {
        File directory = dc.showDialog(primaryStage);

        if (directory == null) return;

        targetDirectory = directory.getAbsolutePath().toString();
        System.out.println("User chose this file path: " + targetDirectory + "\n");
    }

    public void addToImageFileList(ListView<Path> imgListView)
    {
        System.out.println("\nOpening file explorer...");

        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Image Files");
        fc.getExtensionFilters().addAll(new ExtensionFilter(
            "Image Files (*.gif, *.png, *.jpg, *.jpeg, *.tif, *.tiff)", 
            "*.jpg", 
            "*.jpeg", 
            "*.JPG", 
            "*.JPEG", 
            "*.png", 
            "*.PNG",
            "*.gif",
            "*.GIF",
            "*.tif", 
            "*.TIF",
            "*.tiff", 
            "*.TIFF"
        ));
        List<File> selectedFiles = fc.showOpenMultipleDialog(this.primaryStage);

        // If no files selected, do nothing
        System.out.println(
            (selectedFiles == null) ? "\nNo files selected\n" : "\nFiles have been selected\n"
        );

        try
        {
            for (File file : selectedFiles)
            {
                Path currPath = Paths.get(file.getAbsolutePath());
                imgFilePaths.add(currPath);
                imgListView.getItems().add(currPath);
            }
            selectedFiles = null;
        }
        catch (Exception e) {}
    }

    public void convertImagesToPDF()
    {
        try
        {
            // Directory to store PDF
            String targetDirectory = (this.targetDirectory != null)
                                    ? this.targetDirectory
                                    : Paths.get(imgFilePaths.get(0).getParent().toString()).toString();

            while (!imgFilePaths.isEmpty())
            {
                Path path = imgFilePaths.get(0);

                System.out.println("Converting image at " + path.toString() + " ...");

                // File path for the new PDF
                String fileName = path.getFileName().toString();
                fileName = fileName.substring(0, fileName.indexOf("."));
                String outputName = targetDirectory 
                                    + FileSystems.getDefault().getSeparator() 
                                    + fileName 
                                    + ".pdf";
                FileOutputStream fos = new FileOutputStream(outputName);

                // Grab image to convert and create a new PDF document to write to
                Image img = Image.getInstance(path.toString());
                Document currDocument = new Document(
                    new Rectangle(img.getWidth(), img.getHeight()), 
                    0, 0, 0, 0
                );
                PdfWriter writer = PdfWriter.getInstance(currDocument, fos);

                // Conversion process
                writer.open();
                currDocument.open();
                currDocument.add(img);
                currDocument.close();
                writer.close();

                // Have a list of PDFs that the user can choose to merge
                filesToMerge.add(outputName);
                mergeFilesListView.getItems().add(Paths.get(outputName));

                imgFilePaths.remove(0);
                imgListView.getItems().remove(path);
            }

            System.out.println("\nAll images have been converted!");
        }
        catch (Exception e) {}
    }

    public void mergePDF()
    {
        try 
        {
            String targetDirectory = (this.targetDirectory != null)
                                    ? this.targetDirectory
                                    : Paths.get(filesToMerge.get(0)).getParent().toString();

            PDDocument src, dest = new PDDocument();
            PDFMergerUtility merger = new PDFMergerUtility();
                
            System.out.println();

            // Combine all PDFs into one
            while (!filesToMerge.isEmpty())
            {
                String curr = filesToMerge.get(0);
                System.out.println("Merging PDF at " + curr + " ...");

                src = PDDocument.load(new File(curr));
                merger.appendDocument(dest, src);

                filesToMerge.remove(0);
                mergeFilesListView.getItems().remove(Paths.get(curr));
            }

            // New file location + file name
            String outputName = targetDirectory 
                                + FileSystems.getDefault().getSeparator() 
                                + "test.pdf"; 
            dest.save(outputName);
            dest.close(); // free resources

            System.out.println("\nNew PDF at " + outputName);
        } 
        catch (Exception ex) {}
    }
}