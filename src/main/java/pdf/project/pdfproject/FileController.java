package pdf.project.pdfproject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FileController
{
    private Queue<Path> imgFilePaths;
    private Queue<String> filesToMerge;
    private Stage primaryStage;
    private String targetDirectory;

    public FileController(Stage primaryStage) 
    {
        imgFilePaths = new LinkedList<Path>();
        filesToMerge = new LinkedList<String>();
        targetDirectory = null;
        this.primaryStage = primaryStage;
    }

    public Queue<Path> getImgFilePathsQueue() { return this.imgFilePaths; }

    public Queue<String> getFilesToMergeQueue() { return this.filesToMerge; }

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

        for (File file : selectedFiles)
        {
            imgFilePaths.offer(Paths.get(file.getAbsolutePath()));
        }
    }

    public void convertImagesToPDF()
    {
        try
        {
            // Directory to store PDF
            String targetDirectory = (this.targetDirectory != null)
                                    ? this.targetDirectory
                                    : Paths.get(imgFilePaths.peek().getParent().toString()).toString();

            while(!imgFilePaths.isEmpty())
            {
                Path path = imgFilePaths.poll();

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
                filesToMerge.offer(outputName);
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
                                    : Paths.get(filesToMerge.peek()).getParent().toString();

            PDDocument src, dest = new PDDocument();
            PDFMergerUtility merger = new PDFMergerUtility();
                
            System.out.println();

            // Combine all PDFs into one
            while (!filesToMerge.isEmpty())
            {
                String curr = filesToMerge.poll();
                System.out.println("Merging PDF at " + curr + " ...");

                src = PDDocument.load(new File(curr));
                merger.appendDocument(dest, src);
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
