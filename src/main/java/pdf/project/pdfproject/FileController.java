package pdf.project.pdfproject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FileController
{
    private ObservableList<Path> imgFilePaths;
    private ObservableList<Path> filesToMerge;

    private Stage primaryStage;

    private String targetDirectory;
    
    private int mergeCount = 0;

    public FileController(Stage primaryStage) 
    {
        imgFilePaths = FXCollections.observableArrayList();
        filesToMerge = FXCollections.observableArrayList();
        targetDirectory = null;
        this.primaryStage = primaryStage;
    }

    public ObservableList<Path> getImgFilePathsList() { return this.imgFilePaths; }

    public ObservableList<Path> getFilesToMergeList() { return this.filesToMerge; }

    public void setTargetDirectory(DirectoryChooser dc)
    {
        File directory = dc.showDialog(primaryStage);

        if (directory == null) return;

        targetDirectory = directory.getAbsolutePath().toString();
        System.out.println("User chose this file path: " + targetDirectory + "\n");
    }

    public void addToImageFileList()
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
                filesToMerge.add(Paths.get(outputName));

                imgFilePaths.remove(0);
            }

            System.out.println("\nAll images have been converted!");
        }
        catch (Exception e) {}
    }

    public void mergePDF(String fileName)
    {
        try
        {
            String targetDirectory = (this.targetDirectory != null)
                                    ? this.targetDirectory + FileSystems.getDefault().getSeparator()
                                    : filesToMerge.get(0).getParent().toString()
                                      + FileSystems.getDefault().getSeparator();

            PDDocument src, dest = new PDDocument();
            PDFMergerUtility merger = new PDFMergerUtility();
                
            System.out.println();

            // Combine all PDFs into one
            while (!filesToMerge.isEmpty())
            {
                Path curr = filesToMerge.get(0);
                System.out.println("Merging PDF at " + curr + " ...");

                src = PDDocument.load(new File(curr.toAbsolutePath().toString()));
                merger.appendDocument(dest, src);

                filesToMerge.remove(0);
            }
        
            // New file location + file name
            String outputName = (fileName.equals(""))
                                ? targetDirectory + "new_file" + "(" + mergeCount + ")" + ".pdf"
                                : targetDirectory + fileName + ".pdf";
            if(fileName.equals(""))
            {
                mergeCount++;
            }
            dest.save(outputName);
            dest.close(); // free resources

            // Add to list view
            filesToMerge.add(Paths.get(outputName));

            System.out.println("\nNew PDF at " + outputName);
        } 
        catch (Exception ex) {}
    }
}