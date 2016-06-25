package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
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
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.UserCompanyDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Company;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alberto on 6/4/2016.
 */
public class SalesOrderDetailPDFCreator {

    public File generatePDF(SalesOrder salesOrder, ArrayList<SalesOrderLine> lines, String fileName,
                            Context ctx, User user) {
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
        Document document = new Document(PageSize.LETTER, 40, 40, 130, 40);

        if(pdfFile != null){
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                PdfWriter.getInstance(document, byteArrayOutputStream);
                document.open();

                Company userCompany = (new UserCompanyDB(ctx, user)).getUserCompany();
                if(userCompany==null){
                    userCompany = new Company();
                }

                //se agrega la informacion del cliente, numero de cotizacion, fecha de emision, etc.
                addSalesOrderHeader(document, ctx, userCompany, salesOrder);

                //agrega el titulo del documento.
                addSalesOrderTitle(document, ctx);

                //se cargan las lineas de la cotizacion
                addSalesOrderDetails(document, lines, ctx, user);

                //se le agrega la informacion de subtotal, impuestos y total de la cotizacion
                addSalesOrderFooter(document, ctx, salesOrder);

                document.close();

                // Create a reader
                PdfReader reader = new PdfReader(byteArrayOutputStream.toByteArray());
                // Create a stamper
                PdfStamper stamper = new PdfStamper(reader,
                        new FileOutputStream(ctx.getCacheDir() + File.separator + fileName));

                //Se le agrega la cabecera a cada pagina
                addPageHeader(reader, stamper, userCompany, ctx, user);

                // Close the stamper
                stamper.close();
                reader.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return pdfFile;
    }

    private void addPageHeader(PdfReader reader, PdfStamper stamper, Company userCompany,
                               Context ctx, User user) throws DocumentException, IOException {
        //Loop over the pages and add a header to each page
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            getHeaderTable(i, n, userCompany, ctx, user).writeSelectedRows(0, -1, 60, 760,
                    stamper.getOverContent(i));
        }
    }

    /**
     * Create a header table with page X of Y
     * @param x the page number
     * @param y the total number of pages
     * @return a table that can be used as header
     */
    public static PdfPTable getHeaderTable(int x, int y, Company userCompany, Context ctx,
                                           User user) throws DocumentException, IOException {
        Font companyNameFont;
        Font font;
        try{
            font = new Font(BaseFont.createFont("assets/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 9.5f);
            companyNameFont = new Font(BaseFont.createFont("assets/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 13f);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                font = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED), 9.5f);
                companyNameFont = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED), 13f);
            }catch(Exception e) {
                e.printStackTrace();
                font = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 9.5f);
                companyNameFont = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 13f);
            }
        }

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidths(new float[] {230f, 250f});
        headerTable.setTotalWidth(480);

        PdfPCell companyLogoCell = new PdfPCell();

        try{
            Bitmap bmp = Utils.getImageFromCompanyDir(ctx, user);
            if(bmp!=null) {
                bmp = getResizedBitmap(bmp, 230, 80);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image companyLogoImage = Image.getInstance(stream.toByteArray());
                companyLogoCell = new PdfPCell(companyLogoImage, true);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        companyLogoCell.setPadding(3);
        companyLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        companyLogoCell.disableBorderSide(Rectangle.UNDEFINED);
        headerTable.addCell(companyLogoCell);

        PdfPCell companyDataCell = new PdfPCell();
        companyDataCell.setPadding(3);
        companyDataCell.disableBorderSide(Rectangle.UNDEFINED);
        companyDataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        companyDataCell.addElement(new Paragraph(userCompany.getCommercialName(), companyNameFont));
        companyDataCell.addElement(new Paragraph(ctx.getString(R.string.tax_id, userCompany.getTaxId()), font));
        companyDataCell.addElement(new Paragraph(ctx.getString(R.string.address_detail, userCompany.getAddress()), font));
        companyDataCell.addElement(new Paragraph(ctx.getString(R.string.phone_detail, userCompany.getPhoneNumber()), font));
        companyDataCell.addElement(new Paragraph(ctx.getString(R.string.email_detail, userCompany.getEmailAddress()), font));
        headerTable.addCell(companyDataCell);

        return headerTable;
    }

    private void addSalesOrderHeader(Document document, Context ctx, Company userCompany, SalesOrder salesOrder)
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
        clientDataCell.addElement(new Paragraph(ctx.getString(R.string.business_partner_detail, salesOrder.getBusinessPartner().getCommercialName()), font));
        clientDataCell.addElement(new Paragraph(ctx.getString(R.string.address_detail, salesOrder.getBusinessPartner().getAddress()), font));
        clientDataCell.addElement(new Paragraph(ctx.getString(R.string.tax_id, salesOrder.getBusinessPartner().getTaxId()), font));
        headerTable.addCell(clientDataCell);

        PdfPCell SalesOrderDataCell = new PdfPCell();
        SalesOrderDataCell.setPadding(3);
        SalesOrderDataCell.disableBorderSide(Rectangle.UNDEFINED);
        SalesOrderDataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        SalesOrderDataCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_date, salesOrder.getCreatedStringFormat()), font));
        SalesOrderDataCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_valid_to, salesOrder.getValidToStringFormat()), font));
        SalesOrderDataCell.addElement(new Paragraph(ctx.getString(R.string.contact_person_detail, userCompany.getContactPerson()), font));
        headerTable.addCell(SalesOrderDataCell);
        document.add(headerTable);

        PdfPTable salesOrderNumberTable = new PdfPTable(1);
        salesOrderNumberTable.setWidths(new float[] {560f});
        PdfPCell salesOrderNumberCell = new PdfPCell();
        salesOrderNumberCell.setPadding(3);
        salesOrderNumberCell.disableBorderSide(Rectangle.UNDEFINED);
        salesOrderNumberCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        salesOrderNumberCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_number, salesOrder.getSalesOrderNumber()), fontBold));
        salesOrderNumberTable.addCell(salesOrderNumberCell);
        document.add(salesOrderNumberTable);

        document.add(new Phrase("\n"));
    }

    private void addSalesOrderTitle(Document document, Context ctx) throws DocumentException, IOException {
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
        Paragraph title = new Paragraph(new Phrase(22, ctx.getString(R.string.sales_order_doc_name),
                new Font(titleBaseFont, 15f, Font.UNDERLINE)));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
    }

    private void addSalesOrderDetails(Document document, ArrayList<SalesOrderLine> lines,
                                      Context ctx, User user) throws DocumentException, IOException {
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

        document.add(new Phrase("\n"));

        PdfPTable superTable = new PdfPTable(1);
        for(SalesOrderLine line : lines){
            PdfPTable borderTable = new PdfPTable(1);
            borderTable.setWidthPercentage(100);

            /****************************************/
            PdfPTable table = new PdfPTable(3);
            // Defiles the relative width of the columns
            float[] columnWidths = new float[] {30f, 150f, 100f};
            table.setWidths(columnWidths);
            table.setWidthPercentage(100);
            Bitmap bmp = null;
            if(!TextUtils.isEmpty(line.getProduct().getImageFileName())){
                bmp = Utils.getImageFromThumbDirByFileName(ctx, user, line.getProduct().getImageFileName());
            }
            if(bmp==null){
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

            PdfPCell cell2 = new PdfPCell();
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setPadding(3);
            cell2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cell2.setUseVariableBorders(true);
            cell2.setBorderColorRight(BaseColor.LIGHT_GRAY);
            cell2.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            cell2.addElement(new Paragraph(line.getProduct().getName(), font));
            cell2.addElement(new Paragraph(ctx.getString(R.string.sales_order_product_price, line.getPriceStringFormat()), font));
            cell2.addElement(new Paragraph(ctx.getString(R.string.sales_order_tax_amount, line.getTaxPercentageStringFormat()), font));
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell();
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell3.setPadding(3);
            cell3.setBorder(PdfPCell.NO_BORDER);
            cell3.addElement(new Paragraph(ctx.getString(R.string.qty_ordered, String.valueOf(line.getQuantityOrdered())), font));
            cell3.addElement(new Paragraph(ctx.getString(R.string.sales_order_sub_total_line_amount, line.getTotalLineAmountStringFormat()), font));
            table.addCell(cell3);
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

    private void addSalesOrderFooter(Document document, Context ctx, SalesOrder salesOrder) throws DocumentException, IOException {
        Font font;
        try{
            font = new Font(BaseFont.createFont("assets/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 9f);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                font = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED), 9f);
            }catch(Exception e) {
                e.printStackTrace();
                font = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 9f);
            }
        }

        PdfPTable salesOrderNumberTable = new PdfPTable(1);
        salesOrderNumberTable.setWidths(new float[] {560f});
        PdfPCell salesOrderNumberCell = new PdfPCell();
        salesOrderNumberCell.setPadding(3);
        salesOrderNumberCell.disableBorderSide(Rectangle.UNDEFINED);
        salesOrderNumberCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        salesOrderNumberCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_sub_total_amount,
                salesOrder.getSubTotalAmountStringFormat()), font));
        salesOrderNumberCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_tax_amount,
                salesOrder.getTaxAmountStringFormat()), font));
        salesOrderNumberCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_total_amount,
                salesOrder.getTotalAmountStringFormat()), font));
        salesOrderNumberTable.addCell(salesOrderNumberCell);
        document.add(salesOrderNumberTable);
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