package com.smartbuilders.smartsales.ecommerce.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.CompanyDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.Company;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alberto on 6/4/2016.
 */
public class OrderDetailPDFCreator {

    private Currency currency;
    private boolean managePriceInOrder;

    public File generatePDF(Order order, ArrayList<OrderLine> orderLines, String fileName, Context ctx, User user) throws Exception {
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
        Document document = new Document(PageSize.LETTER, 40, 40, 130, 40);

        if(pdfFile.exists()) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                PdfWriter.getInstance(document, byteArrayOutputStream);
                document.open();

                Company company = (new CompanyDB(ctx, user)).getCompany();
                if (company == null) {
                    company = new Company();
                }

                currency = (new CurrencyDB(ctx, user)).getActiveCurrencyById(Parameter.getDefaultCurrencyId(ctx, user));

                managePriceInOrder = Parameter.isManagePriceInOrder(ctx, user);

                //se agrega la informacion del cliente, numero de pedido, fecha de emision, etc.
                addOrderHeader(document, ctx, company, order);

                //agrega el titulo del documento.
                addOrderTitle(document, ctx);

                //se cargan las lineas del pedido
                addOrderDetails(document, orderLines, ctx, user);

                if (managePriceInOrder) {
                    //se le agrega la informacion de subtotal, impuestos y total del pedido
                    addOrderFooter(document, ctx, order);
                }

                document.close();

                // Create a reader
                PdfReader reader = new PdfReader(byteArrayOutputStream.toByteArray());
                // Create a stamper
                PdfStamper stamper = new PdfStamper(reader,
                        new FileOutputStream(ctx.getCacheDir() + File.separator + fileName));

                //Se le agrega la cabecera a cada pagina
                addPageHeader(reader, stamper, company, ctx);

                // Close the stamper
                stamper.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pdfFile;
    }

    private void addPageHeader(PdfReader reader, PdfStamper stamper, Company userCompany,
                               Context ctx) throws DocumentException, IOException {
        Image companyLogo = null;
        Bitmap bmp = Utils.decodeSampledBitmapFromResource(ctx.getResources(), R.drawable.company_logo_pdf_docs, 1024/4, 389/4);
        if(bmp!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            companyLogo = Image.getInstance(stream.toByteArray());
            companyLogo.setAbsolutePosition(50,680);
        }
        //Loop over the pages and add a header to each page
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            getHeaderTable(i, n, userCompany, ctx, companyLogo).writeSelectedRows(0, -1, 60, 780,
                    stamper.getOverContent(i));
        }
    }

    /**
     * Create a header table with page X of Y
     * @param x the page number
     * @param y the total number of pages
     * @return a table that can be used as header
     */
    public static PdfPTable getHeaderTable(int x, int y, Company userCompany, Context ctx, Image companyLogo)
            throws DocumentException, IOException {
        Font companyNameFont;
        Font font;
        try{
            font = new Font(BaseFont.createFont("assets/fonts/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 9.5f);
            companyNameFont = new Font(BaseFont.createFont("assets/fonts/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 13f);
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

        PdfPCell companyLogoCell;
        if(companyLogo!=null){
            companyLogoCell = new PdfPCell(companyLogo, true);
        }else{
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
        companyDataCell.addElement(new Paragraph(userCompany.getName(), companyNameFont));
        companyDataCell.addElement(new Paragraph(ctx.getString(R.string.tax_id, userCompany.getTaxId()), font));
        //Se comento porque a veces la direccion es muy larga y descuadra el formato de la cabecera
        //companyDataCell.addElement(new Paragraph(ctx.getString(R.string.address_detail, userCompany.getAddress()), font));
        companyDataCell.addElement(new Paragraph(ctx.getString(R.string.phone_detail, userCompany.getPhoneNumber()), font));
        companyDataCell.addElement(new Paragraph(ctx.getString(R.string.email_detail, userCompany.getEmailAddress()), font));
        headerTable.addCell(companyDataCell);

        return headerTable;
    }

    private void addOrderHeader(Document document, Context ctx, Company company, Order order)
            throws DocumentException, IOException {
        Font font;
        Font fontBold;
        try{
            font = new Font(BaseFont.createFont("assets/fonts/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 9f);
            fontBold = new Font(BaseFont.createFont("assets/fonts/Roboto-Bold.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 9f);
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
        order.setBusinessPartner(order.getBusinessPartner()==null ? new BusinessPartner() : order.getBusinessPartner());
        clientDataCell.addElement(new Paragraph(ctx.getString(R.string.business_partner_name_detail, order.getBusinessPartner().getName()), font));
        //clientDataCell.addElement(new Paragraph(ctx.getString(R.string.address_detail, order.getBusinessPartner().getAddress()), font));
        clientDataCell.addElement(new Paragraph(ctx.getString(R.string.tax_id, order.getBusinessPartner().getTaxId()), font));
        headerTable.addCell(clientDataCell);

        PdfPCell orderDataCell = new PdfPCell();
        orderDataCell.setPadding(3);
        orderDataCell.disableBorderSide(Rectangle.UNDEFINED);
        orderDataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        orderDataCell.addElement(new Paragraph(ctx.getString(R.string.order_date, order.getCreatedStringFormat()), font));
        //orderDataCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_valid_to, order.getValidToStringFormat()), font));
        if(!TextUtils.isEmpty(company.getContactPerson())) {
            orderDataCell.addElement(new Paragraph(ctx.getString(R.string.contact_person_detail, company.getContactPerson()), font));
        }else{
            orderDataCell.addElement(new Paragraph("", font));
        }
        headerTable.addCell(orderDataCell);
        document.add(headerTable);

        PdfPTable orderNumberTable = new PdfPTable(1);
        orderNumberTable.setWidths(new float[] {560f});
        PdfPCell orderNumberCell = new PdfPCell();
        orderNumberCell.setPadding(3);
        orderNumberCell.disableBorderSide(Rectangle.UNDEFINED);
        orderNumberCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        orderNumberCell.addElement(new Paragraph(ctx.getString(R.string.order_number, order.getOrderNumber()), fontBold));
        orderNumberTable.addCell(orderNumberCell);
        document.add(orderNumberTable);

        document.add(new Phrase("\n"));
    }

    private void addOrderTitle(Document document, Context ctx) throws DocumentException, IOException {
        BaseFont titleBaseFont;
        try{
            titleBaseFont = BaseFont.createFont("assets/fonts/Roboto-Italic.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                titleBaseFont = BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED);
            }catch(Exception e) {
                e.printStackTrace();
                titleBaseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }
        }
        Paragraph title = new Paragraph(new Phrase(22, ctx.getString(R.string.order_doc_name),
                new Font(titleBaseFont, 15f, Font.UNDERLINE)));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
    }

    private void addOrderDetails(Document document, ArrayList<OrderLine> orderLines,
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

        document.add(new Phrase("\n"));

        PdfPTable superTable = new PdfPTable(1);
        for(OrderLine line : orderLines){
            PdfPTable borderTable = new PdfPTable(1);
            borderTable.setWidthPercentage(100);

            /****************************************/
            PdfPTable table = new PdfPTable(BuildConfig.USE_PRODUCT_IMAGE ? 3 : 2);
            // Defiles the relative width of the columns
            float[] columnWidths = BuildConfig.USE_PRODUCT_IMAGE ? new float[] {30f, 150f, 100f} : new float[] {150f, 100f};
            table.setWidths(columnWidths);
            table.setWidthPercentage(100);
            if (BuildConfig.USE_PRODUCT_IMAGE) {
                Bitmap bmp = Utils.getThumbImage(ctx, user, line.getProduct().getImageFileName());
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
                cell.setFixedHeight(50);
                table.addCell(cell);
            }

            PdfPCell cell2 = new PdfPCell();
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            //cell2.setPadding(3);
            cell2.setBorder(BuildConfig.USE_PRODUCT_IMAGE ? (Rectangle.LEFT | Rectangle.RIGHT) : Rectangle.RIGHT);
            cell2.setUseVariableBorders(true);
            cell2.setBorderColorRight(BaseColor.LIGHT_GRAY);
            cell2.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            cell2.addElement(new Paragraph(line.getProduct().getName(), font));
            cell2.addElement(new Paragraph(ctx.getString(R.string.product_internalCode, line.getProduct().getInternalCode()), font));
            cell2.addElement(new Paragraph(ctx.getString(R.string.brand_detail, line.getProduct().getProductBrand().getName()), font));
            //if (BuildConfig.USE_PRODUCT_IMAGE) {
            //    cell2.addElement(new Paragraph(ctx.getString(R.string.product_description_detail, line.getProduct().getDescription()), font));
            //    cell2.addElement(new Paragraph(ctx.getString(R.string.product_purpose_detail, line.getProduct().getPurpose()), font));
            //}
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell();
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            //cell3.setPadding(3);
            cell3.setBorder(PdfPCell.NO_BORDER);
            if (managePriceInOrder) {
                cell3.addElement(new Paragraph(ctx.getString(R.string.sales_order_product_price,
                        currency!=null ? currency.getName() : "",
                        line.getProductPriceStringFormat()), font));
                cell3.addElement(new Paragraph(ctx.getString(R.string.sales_order_tax_amount,
                        currency!=null ? currency.getName() : "",
                        line.getLineTaxAmountStringFormat()), font));
            }
            cell3.addElement(new Paragraph(ctx.getString(R.string.qty_ordered_label_detail, String.valueOf(line.getQuantityOrdered())), font));
            if (managePriceInOrder) {
                cell3.addElement(new Paragraph(ctx.getString(R.string.order_sub_total_line_amount,
                        currency!=null ? currency.getName() : "",
                        line.getTotalLineAmountStringFormat()), font));
            }
            //cell3.addElement(new Paragraph(ctx.getString(R.string.sales_order_sub_total_line_amount, line.getTotalLineAmountStringFormat()), font));
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

    private void addOrderFooter(Document document, Context ctx, Order order) throws DocumentException, IOException {
        Font font;
        try{
            font = new Font(BaseFont.createFont("assets/fonts/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 9f);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                font = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED), 9f);
            }catch(Exception e) {
                e.printStackTrace();
                font = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 9f);
            }
        }

        PdfPTable orderNumberTable = new PdfPTable(1);
        orderNumberTable.setWidths(new float[] {560f});
        PdfPCell orderNumberCell = new PdfPCell();
        orderNumberCell.setPadding(3);
        orderNumberCell.disableBorderSide(Rectangle.UNDEFINED);
        orderNumberCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        orderNumberCell.addElement(new Paragraph(ctx.getString(R.string.order_sub_total_amount,
                currency!=null ? currency.getName() : "",
                order.getSubTotalAmountStringFormat()), font));
        orderNumberCell.addElement(new Paragraph(ctx.getString(R.string.order_tax_amount,
                currency!=null ? currency.getName() : "",
                order.getTaxAmountStringFormat()), font));
        orderNumberCell.addElement(new Paragraph(ctx.getString(R.string.order_total_amount,
                currency!=null ? currency.getName() : "",
                order.getTotalAmountStringFormat()), font));
        orderNumberTable.addCell(orderNumberCell);
        document.add(orderNumberTable);
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