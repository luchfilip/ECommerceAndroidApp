package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Alberto on 6/4/2016.
 */
public class SalesOrderDetailPDFCreator {
    private static final String TAG = SalesOrderDetailPDFCreator.class.getSimpleName();

    public File generatePDF(Order order, ArrayList<OrderLine> lines, String fileName, Context ctx, User user) {
        Log.d(TAG, "generatePDF(ArrayList<OrderLine> lines, String fileName, Context ctx)");
        File pdfFile = null;
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Toast.makeText(ctx, ctx.getString(R.string.external_storage_unavailable), Toast.LENGTH_LONG).show();
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
        Document document = new Document(PageSize.LETTER, 50, 50, 125, 40);

        if(pdfFile != null){
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                PdfWriter.getInstance(document, byteArrayOutputStream);
                document.open();

                //se agrega la informacion del cliente, numero de cotizacion, fecha de emision, etc.
                addSalesOrderHeader(document, order);

                //agrega el titulo del documento.
                addSalesOrderTitle(document);

                //se cargan las lineas de la cotizacion
                addSalesOrderDetails(document, lines, ctx, user);

                //se le agrega la informacion de subtotal, impuestos y total de la cotizacion
                addSalesOrderFooter(document, order);

                document.close();

                // Create a reader
                PdfReader reader = new PdfReader(byteArrayOutputStream.toByteArray());
                // Create a stamper
                PdfStamper stamper = new PdfStamper(reader,
                        new FileOutputStream(ctx.getCacheDir() + File.separator + fileName));

                //Se le agrega la cabecera a cada pagina
                addPageHeader(reader, stamper, ctx);

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

    private void addPageHeader(PdfReader reader, PdfStamper stamper, Context ctx) throws DocumentException, IOException {
        //Loop over the pages and add a header to each page
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            getHeaderTable(i, n, ctx).writeSelectedRows(0, -1, 50, 760, stamper.getOverContent(i));
        }
    }

    /**
     * Create a header table with page X of Y
     * @param x the page number
     * @param y the total number of pages
     * @return a table that can be used as header
     */
    public static PdfPTable getHeaderTable(int x, int y, Context ctx) throws DocumentException, IOException {
        Font font;
        try{
            font = new Font(BaseFont.createFont("assets/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 10f);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                font = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED), 10f);
            }catch(Exception e) {
                e.printStackTrace();
                font = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 10f);
            }
        }

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidths(new float[] {240f, 260f});
        headerTable.setTotalWidth(520);

        PdfPCell companyLogoCell = new PdfPCell();

        try{
            Bitmap bmp = null;
            if(bmp==null){
                bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.no_image_available);
            }
            if(bmp!=null) {
                bmp = getResizedBitmap(bmp, 250, 80);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image companyLogoImage = Image.getInstance(stream.toByteArray());
                companyLogoCell = new PdfPCell(companyLogoImage, true);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        companyLogoCell.setPadding(3);
        companyLogoCell.disableBorderSide(Rectangle.UNDEFINED);
        headerTable.addCell(companyLogoCell);

        PdfPCell companyDataCell = new PdfPCell();
        companyDataCell.setPadding(3);
        companyDataCell.disableBorderSide(Rectangle.UNDEFINED);
        companyDataCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        companyDataCell.addElement(new Paragraph("Mi Ferreteria", font));
        companyDataCell.addElement(new Paragraph("RIF: ", font));
        companyDataCell.addElement(new Paragraph("Dirección: ", font));
        companyDataCell.addElement(new Paragraph("Tlf.: ", font));
        headerTable.addCell(companyDataCell);

        return headerTable;
    }

    private void addSalesOrderHeader(Document document, Order order)
            throws DocumentException, IOException {
        Font font;
        Font fontBold;
        try{
            font = new Font(BaseFont.createFont("assets/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 9f);
            fontBold = new Font(BaseFont.createFont("assets/Roboto-Bold.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 9f);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                font = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED), 9f);
                fontBold = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED), 9f, Font.BOLD);
            }catch(Exception e) {
                e.printStackTrace();
                font = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 9f);
                fontBold = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 9f, Font.BOLD);
            }
        }

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidths(new float[] {280f, 280f});

        PdfPCell clientDataCell = new PdfPCell();
        clientDataCell.setPadding(3);
        clientDataCell.disableBorderSide(Rectangle.UNDEFINED);
        clientDataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        clientDataCell.addElement(new Paragraph("Cliente: Razon Social", font));
        clientDataCell.addElement(new Paragraph("Dirección: Dirección completa del cliente.", font));
        clientDataCell.addElement(new Paragraph("Rif: J000000000", font));
        headerTable.addCell(clientDataCell);

        PdfPCell SalesOrderDataCell = new PdfPCell();
        SalesOrderDataCell.setPadding(3);
        SalesOrderDataCell.disableBorderSide(Rectangle.UNDEFINED);
        SalesOrderDataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        SalesOrderDataCell.addElement(new Paragraph("Fecha de emision: 01/01/2016", font));
        SalesOrderDataCell.addElement(new Paragraph("Representante de Venta: Jesus Sarco.", font));
        headerTable.addCell(SalesOrderDataCell);
        document.add(headerTable);

        PdfPTable salesOrderNumberTable = new PdfPTable(1);
        salesOrderNumberTable.setWidths(new float[] {560f});
        PdfPCell salesOrderNumberCell = new PdfPCell();
        salesOrderNumberCell.setPadding(3);
        salesOrderNumberCell.disableBorderSide(Rectangle.UNDEFINED);
        salesOrderNumberCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        salesOrderNumberCell.addElement(new Paragraph("COTIZACIÓN No.: " + order.getOrderNumber(), fontBold));
        salesOrderNumberTable.addCell(salesOrderNumberCell);
        document.add(headerTable);

        document.add(new Phrase("\n"));
        document.add(new Phrase("\n"));
    }

    private void addSalesOrderTitle(Document document) throws DocumentException, IOException {
        BaseFont titleBaseFont;
        try{
            titleBaseFont = BaseFont.createFont("assets/Roboto-Italic.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                titleBaseFont = BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED);
            }catch(Exception e) {
                e.printStackTrace();
                titleBaseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }
        }
        Paragraph title = new Paragraph(new Phrase(22, "COTIZACIÓN", new Font(titleBaseFont, 15f, Font.UNDERLINE)));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Phrase("\n"));
        document.add(new Phrase("\n"));
    }

    private void addSalesOrderDetails(Document document, ArrayList<OrderLine> lines, Context ctx, User user) throws DocumentException, IOException {
        BaseFont bf;
        Font font;
        try{
            bf = BaseFont.createFont("assets/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED);
            }catch(Exception e) {
                e.printStackTrace();
                bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }
        }
        font = new Font(bf, 7.5f);

        PdfPTable table = new PdfPTable(3);
        // Defiles the relative width of the columns
        float[] columnWidths = new float[] {30f, 150f, 100f};
        table.setWidths(columnWidths);
        for(OrderLine line : lines){
            Bitmap bmp = null;
            if(!TextUtils.isEmpty(line.getProduct().getImageFileName())){
                bmp = Utils.getThumbByFileName(ctx, user, line.getProduct().getImageFileName());
            }
            if(bmp==null){
                bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.no_image_available);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image productImage = Image.getInstance(stream.toByteArray());
            PdfPCell cell = new PdfPCell(productImage, true);
            cell.setPadding(3);
            cell.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
            cell.setUseVariableBorders(true);
            cell.setBorderColorTop(BaseColor.LIGHT_GRAY);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            cell.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            table.addCell(cell);

            PdfPCell cell2 = new PdfPCell();
            cell2.setPadding(3);
            cell2.setUseVariableBorders(true);
            cell2.setBorderColorTop(BaseColor.LIGHT_GRAY);
            cell2.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            cell2.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            cell2.setBorderColorRight(BaseColor.LIGHT_GRAY);
            cell2.addElement(new Paragraph(line.getProduct().getName(), font));
            cell2.addElement(new Paragraph("Precio: "+line.getPrice(), font));
            cell2.addElement(new Paragraph("(%) Impuesto: "+line.getTaxPercentage(), font));
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell();
            cell3.setPadding(3);
            cell3.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
            cell3.setUseVariableBorders(true);
            cell3.setBorderColorTop(BaseColor.LIGHT_GRAY);
            cell3.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            cell3.setBorderColorRight(BaseColor.LIGHT_GRAY);
            cell3.addElement(new Paragraph("Cant. pedida: "+line.getQuantityOrdered(), font));
            cell3.addElement(new Paragraph("Total linea: "+line.getTotalLineAmount(), font));
            table.addCell(cell3);
        }
        document.add(table);
        document.add(new Phrase("\n"));
    }

    private void addSalesOrderFooter(Document document, Order order) throws DocumentException, IOException {
        BaseFont bf;
        Font font;
        try{
            bf = BaseFont.createFont("assets/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED);
            }catch(Exception e) {
                e.printStackTrace();
                bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }
        }
        font = new Font(bf, 9f);
        document.add(new Phrase("Sub-Total: "+order.getSubTotalAmount(), font));
        document.add(new Phrase("\n"));
        document.add(new Phrase("Impuestos: "+order.getTaxAmount(), font));
        document.add(new Phrase("\n"));
        document.add(new Phrase("Total: "+order.getTotalAmount(), font));
        document.add(new Phrase("\n"));
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}