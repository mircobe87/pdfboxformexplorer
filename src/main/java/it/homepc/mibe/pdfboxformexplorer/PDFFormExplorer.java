package it.homepc.mibe.pdfboxformexplorer;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by mirco on 05/05/2016.
 */
public class PDFFormExplorer {

    public static void main(String[] args) {
        InputStream is = null;
        PDDocument doc = null;
        if (args.length == 0) {
            System.out.println("Nessun file PDF specificato.");
        } else {
            String fileName = args[0];
            try {
                is = new FileInputStream(fileName);
                doc = PDDocument.load(is);

                PDDocumentCatalog catalog = doc.getDocumentCatalog();
                PDAcroForm form = catalog.getAcroForm();

                List<PDField> fields = form.getFields();

                for(PDField field : fields) {
                    String fieldInfo = String.format("name    : \"%s\"\nposition: (%s)\n", getFormFieldName(field), getFormFieldPosition(field));
                    System.out.println(fieldInfo);
                }

            } catch (FileNotFoundException e) {
                System.out.println(String.format("\"%s\": file non trovato.", fileName));
            } catch (IOException e) {
                System.out.println(String.format("\"%s\": file non è un PDF valido.", fileName));
            } finally {
                if (is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        System.out.println(String.format("\"%s\": impossibile chiudere lo stream.", fileName));
                    }
                }
                if (doc != null) {
                    try {
                        doc.close();
                    } catch (IOException e) {
                        System.out.println(String.format("\"%s\": impossibile chiudere il documento PDF.", fileName));
                    }
                }
            }
        }
    }

    private static String getFormFieldName(PDField field) {
        String fName = null;
        try {
            fName = field.getFullyQualifiedName();
        } catch (IOException e) {
            System.out.println("Impossibile ottenere il nome del campo.");
        } finally {
            return fName;
        }
    }
    private static String getFormFieldPosition(PDField field) {
        COSArray bbox = (COSArray) field.getDictionary().getDictionaryObject(COSName.RECT);
        float left = (float) ((COSFloat) bbox.get(0)).doubleValue();
        float bottom = (float) ((COSFloat) bbox.get(1)).doubleValue();
        // float right = (float) ((COSFloat) bbox.get(2)).doubleValue();
        // float top = (float) ((COSFloat) bbox.get(3)).doubleValue();
        return String.format("x: %.3f; y: %.3f", left, bottom);
    }
}
