package com.smartbuilders.smartsales.ecommerce.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.Product;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Alberto on 6/4/2016.
 */
public class RecommendedProductsPDFCreator {
    private static final String TAG = RecommendedProductsPDFCreator.class.getSimpleName();

    public File generatePDF(ArrayList<Product> products, String fileName, Context ctx) throws Exception {
        Log.d(TAG, "generatePDF(ArrayList<Product> products, String fileName, Context ctx)");
        File pdfFile = null;
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            throw new Exception(ctx.getString(R.string.external_storage_unavailable));
        } else {
            //path for the PDF file in the external storage
            pdfFile = new File(ctx.getCacheDir() + File.separator + fileName);
            try {
                pdfFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        //create a new document
        Document document = new Document(PageSize.LETTER, 50, 50, 70, 40);

        if(pdfFile != null){
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                PdfWriter.getInstance(document, baos);
                document.open();

                try{
                    //the company logo is stored in the assets which is read only
                    //get the logo and print on the document
                    InputStream inputStream = ctx.getAssets().open("logoFebeca.jpeg");
                    Bitmap bmp =  BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image companyLogo = Image.getInstance(stream.toByteArray());
                    companyLogo.setAbsolutePosition(50,680);
                    companyLogo.scalePercent(60);
                    document.add(companyLogo);
                }catch(Exception e){
                    e.printStackTrace();
                }

                BaseFont bf;
                Font font;
                try{
                    bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED);
                }catch (Exception ex){
                    bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                }
                font = new Font(bf, 7.5f);

                document.add(new Phrase("\n"));
                document.add(new Phrase("\n"));
                document.add(new Phrase("\n"));


                Paragraph title = new Paragraph(new Phrase(20, "Productos Recomendados", new Font(bf, 15f)));
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                document.add(new Phrase("\n"));
                document.add(new Phrase("\n"));


                PdfPTable table = new PdfPTable(3);
                // Defiles the relative width of the columns
                float[] columnWidths = new float[] {30f, 150f, 100f};
                table.setWidths(columnWidths);

                /***/

                document.add(table);

                document.close();

                // Create a reader
                PdfReader reader = new PdfReader(baos.toByteArray());
                // Create a stamper
                PdfStamper stamper
                        = new PdfStamper(reader, new FileOutputStream(ctx.getCacheDir() + File.separator + fileName));
                // Close the stamper
                stamper.close();
                reader.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            Log.d(TAG, "pdfFile is null");
        }
        Log.d(TAG, "return pdfFile;");
        return pdfFile;
    }
}