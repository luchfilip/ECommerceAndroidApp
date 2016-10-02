package com.smartbuilders.smartsales.ecommerce.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

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
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.synchronizer.ids.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alberto on 6/4/2016.
 */
public class RecommendedProductsPDFCreator {

    public File generatePDF(ArrayList<Product> products, String fileName, Context ctx, User user) throws Exception {
        File pdfFile;
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
        Document document = new Document(PageSize.LETTER, 40, 40, 130, 40);

        if(pdfFile.exists()) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                PdfWriter.getInstance(document, byteArrayOutputStream);
                document.open();

                //se cargan las lineas del pedido
                addDetails(document, products, ctx, user);

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pdfFile;
    }

    private void addPageHeader(PdfReader reader, PdfStamper stamper,
                               Context ctx) throws DocumentException, IOException {
        Image companyLogo = null;
        Bitmap bmp = Utils.decodeSampledBitmapFromResource(ctx.getResources(), R.drawable.company_logo_pdf_docs, 1024/4, 389/4);
        if(bmp!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            companyLogo = Image.getInstance(stream.toByteArray());
            companyLogo.setAbsolutePosition(50, 680);
        }
        //Loop over the pages and add a header to each page
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            getHeaderTable(i, n, ctx, companyLogo).writeSelectedRows(0, -1, 60, 780, stamper.getOverContent(i));
        }
    }

    /**
     * Create a header table with page X of Y
     * @param x the page number
     * @param y the total number of pages
     * @return a table that can be used as header
     */
    public static PdfPTable getHeaderTable(int x, int y, Context ctx, Image companyLogo)
            throws DocumentException, IOException {
        Font docNameFont;
        try{
            docNameFont = new Font(BaseFont.createFont("assets/fonts/Roboto-Italic.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 15f);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                docNameFont = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED), 15f);
            }catch(Exception e) {
                e.printStackTrace();
                docNameFont = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 15f);
            }
        }

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidths(new float[] {230f, 250f});
        headerTable.setTotalWidth(480);

        PdfPCell companyLogoCell;
        if(companyLogo!=null){
            companyLogoCell = new PdfPCell(companyLogo, true);
        }else {
            companyLogoCell = new PdfPCell();
        }
        companyLogoCell.setPadding(3);
        companyLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        companyLogoCell.disableBorderSide(Rectangle.UNDEFINED);
        headerTable.addCell(companyLogoCell);

        PdfPCell companyDataCell = new PdfPCell();
        companyDataCell.setPadding(3);
        companyDataCell.disableBorderSide(Rectangle.UNDEFINED);
        companyDataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        companyDataCell.addElement(new Paragraph(ctx.getString(R.string.recommended_products_doc_name), docNameFont));
        headerTable.addCell(companyDataCell);

        return headerTable;
    }

    private void addDetails(Document document, ArrayList<Product> products,
                            Context ctx, User user) throws DocumentException, IOException {
        BaseFont bf;
        Font font;
        try{
            bf = BaseFont.createFont("assets/fonts/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
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

        PdfPTable superTable = new PdfPTable(1);
        for(Product product : products){
            PdfPTable borderTable = new PdfPTable(1);
            borderTable.setWidthPercentage(100);

            /****************************************/
            PdfPTable table = new PdfPTable(BuildConfig.USE_PRODUCT_IMAGE ? 2 : 1);
            // Defiles the relative width of the columns
            float[] columnWidths = BuildConfig.USE_PRODUCT_IMAGE ? new float[] {30f, 120f} : new float[] {120f};
            table.setWidths(columnWidths);
            table.setWidthPercentage(100);
            if (BuildConfig.USE_PRODUCT_IMAGE) {
                Bitmap bmp = Utils.getThumbImage(ctx, user, product.getImageFileName());
                if (bmp == null) {
                    bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.no_image_available);
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image productImage = Image.getInstance(stream.toByteArray());
                PdfPCell cell = new PdfPCell(productImage, true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(3);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);
            }

            PdfPCell cell2 = new PdfPCell();
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setPadding(3);
            cell2.setBorder(Rectangle.LEFT);
            cell2.setUseVariableBorders(true);
            cell2.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            cell2.addElement(new Paragraph(ctx.getString(R.string.product_internalCode, product.getInternalCode()), font));
            cell2.addElement(new Paragraph(product.getName(), font));
            cell2.addElement(new Paragraph(ctx.getString(R.string.brand_detail, product.getProductBrand().getName()), font));
            if (!TextUtils.isEmpty(product.getDescription())) {
                cell2.addElement(new Paragraph(ctx.getString(R.string.product_description_detail, product.getDescription()), font));
            }
            if (!TextUtils.isEmpty(product.getPurpose())) {
                cell2.addElement(new Paragraph(ctx.getString(R.string.product_purpose_detail, product.getPurpose()), font));
            }
            table.addCell(cell2);
            /*****************************************/

            PdfPCell borderTableCell = new PdfPCell();
            borderTableCell.setCellEvent(new RoundRectangle());
            borderTableCell.setBorder(PdfPCell.NO_BORDER);
            borderTableCell.addElement(table);
            borderTable.addCell(borderTableCell);

            /***************************************************************/
            PdfPCell superTableCell = new PdfPCell();
            superTableCell.setPadding(5);
            superTableCell.setBorder(PdfPCell.NO_BORDER);
            superTableCell.addElement(borderTable);
            superTable.addCell(superTableCell);
        }
        document.add(superTable);
        document.add(new Phrase("\n"));
    }

    class RoundRectangle implements PdfPCellEvent {

        public void cellLayout(PdfPCell cell, Rectangle rect,
                               PdfContentByte[] canvas) {
            PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
            cb.setColorStroke(new GrayColor(0.8f));
            cb.roundRectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight(), 3);
            cb.stroke();
        }
    }
}