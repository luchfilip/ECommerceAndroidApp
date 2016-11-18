package com.smartbuilders.smartsales.ecommerce.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.CompanyDB;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.UserCompanyDB;
import com.smartbuilders.smartsales.ecommerce.model.Company;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alberto on 6/4/2016.
 */
public class SalesOrderDetailPDFCreator {

    private Currency currency;

    public File generatePDF(SalesOrder salesOrder, ArrayList<SalesOrderLine> lines, String fileName,
                            Activity activity, Context ctx, User user) throws Exception {
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

        if(pdfFile.exists()){
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                PdfWriter.getInstance(document, byteArrayOutputStream);
                document.open();
                Company company;
                if (user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
                    company = (new UserCompanyDB(ctx, user)).getUserCompany();
                    if(company==null){
                        company = new Company();
                    }
                }else{
                    company = (new CompanyDB(ctx, user)).getCompany();
                    if(company==null){
                        company = new Company();
                    }
                }

                currency = (new CurrencyDB(ctx, user)).getActiveCurrencyById(Parameter.getDefaultCurrencyId(ctx, user));

                //se agrega la informacion del cliente, numero de cotizacion, fecha de emision, etc.
                addSalesOrderHeader(document, ctx, company, salesOrder);

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
                addPageHeader(reader, stamper, company, activity, ctx, user);

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
                               Activity activity, Context ctx, User user) throws DocumentException, IOException {
        //Loop over the pages and add a header to each page
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            getHeaderTable(i, n, userCompany, activity, ctx, user).writeSelectedRows(0, -1, 60, 750,
                    stamper.getOverContent(i));
        }
    }

    /**
     * Create a header table with page X of Y
     * @param x the page number
     * @param y the total number of pages
     * @return a table that can be used as header
     */
    private static PdfPTable getHeaderTable(int x, int y, Company userCompany, Activity activity, Context ctx,
                                           User user) throws DocumentException, IOException {
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

        PdfPTable headerTable;

        if (user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
            headerTable = new PdfPTable(1);
            headerTable.setWidths(new float[] {480f});
            headerTable.setTotalWidth(480);
            Bitmap bmp = getHeaderBmp(activity, ctx, user, userCompany);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            PdfPCell cell = new PdfPCell(Image.getInstance(stream.toByteArray()), true);
            stream.close();
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            headerTable.addCell(cell);
        } else {
            headerTable = new PdfPTable(2);
            headerTable.setWidths(new float[] {230f, 250f});
            headerTable.setTotalWidth(480);

            PdfPCell companyLogoCell = new PdfPCell();

            try{
                Bitmap bmp = BitmapFactory.decodeStream(ctx.getAssets().open("companyLogo.jpg"));
                if(bmp!=null){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image companyLogo = Image.getInstance(stream.toByteArray());
                    companyLogo.setAbsolutePosition(50,680);
                    companyLogoCell = new PdfPCell(companyLogo, true);
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
            companyDataCell.addElement(new Paragraph(userCompany.getName(), companyNameFont));
            companyDataCell.addElement(new Paragraph(ctx.getString(R.string.tax_id, userCompany.getTaxId()), font));
            companyDataCell.addElement(new Paragraph(ctx.getString(R.string.phone_detail, userCompany.getPhoneNumber()), font));
            companyDataCell.addElement(new Paragraph(ctx.getString(R.string.email_detail, userCompany.getEmailAddress()), font));
            headerTable.addCell(companyDataCell);
        }

        return headerTable;
    }

    private void addSalesOrderHeader(Document document, Context ctx, Company userCompany, SalesOrder salesOrder)
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
        clientDataCell.addElement(new Paragraph(ctx.getString(R.string.business_partner_name_detail, salesOrder.getBusinessPartner().getName()), font));
        //clientDataCell.addElement(new Paragraph(ctx.getString(R.string.address_detail, salesOrder.getBusinessPartner().getAddress()), font));
        clientDataCell.addElement(new Paragraph(ctx.getString(R.string.tax_id, salesOrder.getBusinessPartner().getTaxId()), font));
        headerTable.addCell(clientDataCell);

        PdfPCell salesOrderDataCell = new PdfPCell();
        salesOrderDataCell.setPadding(3);
        salesOrderDataCell.disableBorderSide(Rectangle.UNDEFINED);
        salesOrderDataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        salesOrderDataCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_date, salesOrder.getCreatedStringFormat()), font));
        salesOrderDataCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_valid_to, salesOrder.getValidToStringFormat()), font));
        if(!TextUtils.isEmpty(userCompany.getContactPerson())) {
            salesOrderDataCell.addElement(new Paragraph(ctx.getString(R.string.contact_person_detail, userCompany.getContactPerson()), font));
        }else{
            salesOrderDataCell.addElement(new Paragraph("", font));
        }
        headerTable.addCell(salesOrderDataCell);
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
        for(SalesOrderLine line : lines){
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
            cell2.setPadding(3);
            cell2.setBorder(BuildConfig.USE_PRODUCT_IMAGE ? (Rectangle.LEFT | Rectangle.RIGHT) : Rectangle.RIGHT);
            cell2.setUseVariableBorders(true);
            cell2.setBorderColorRight(BaseColor.LIGHT_GRAY);
            cell2.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            if (user!=null && user.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID) {
                cell2.addElement(new Paragraph(ctx.getString(R.string.product_internalCode, line.getProduct().getInternalCode()), font));
            }
            cell2.addElement(new Paragraph(line.getProduct().getName(), font));
            cell2.addElement(new Paragraph(ctx.getString(R.string.brand_detail, line.getProduct().getProductBrand().getName()), font));
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell();
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell3.setPadding(3);
            cell3.setBorder(PdfPCell.NO_BORDER);
            cell3.addElement(new Paragraph(ctx.getString(R.string.sales_order_product_price,
                    currency!=null ? currency.getName() : "",
                    line.getPriceStringFormat()), font));
            cell3.addElement(new Paragraph(ctx.getString(R.string.sales_order_tax_amount,
                    currency!=null ? currency.getName() : "",
                    line.getLineTaxAmountStringFormat()), font));
            cell3.addElement(new Paragraph(ctx.getString(R.string.qty_ordered_label_detail, String.valueOf(line.getQuantityOrdered())), font));
            cell3.addElement(new Paragraph(ctx.getString(R.string.sales_order_sub_total_line_amount,
                    currency!=null ? currency.getName() : "",
                    line.getTotalLineAmountStringFormat()), font));
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
        //document.add(new Phrase("\n"));
    }

    private void addSalesOrderFooter(Document document, Context ctx, SalesOrder salesOrder) throws DocumentException, IOException {
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

        PdfPTable salesOrderNumberTable = new PdfPTable(1);
        salesOrderNumberTable.setWidths(new float[] {560f});
        PdfPCell salesOrderNumberCell = new PdfPCell();
        salesOrderNumberCell.setPadding(3);
        salesOrderNumberCell.disableBorderSide(Rectangle.UNDEFINED);
        salesOrderNumberCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        salesOrderNumberCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_sub_total_amount,
                currency!=null ? currency.getName() : "",
                salesOrder.getSubTotalAmountStringFormat()), font));
        salesOrderNumberCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_tax_amount,
                currency!=null ? currency.getName() : "",
                salesOrder.getTaxAmountStringFormat()), font));
        salesOrderNumberCell.addElement(new Paragraph(ctx.getString(R.string.sales_order_total_amount,
                currency!=null ? currency.getName() : "",
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

    private class RoundRectangle implements PdfPCellEvent {

        public void cellLayout(PdfPCell cell, Rectangle rect,
                               PdfContentByte[] canvas) {
            PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
            cb.setColorStroke(new GrayColor(0.8f));
            cb.roundRectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight(), 3);
            cb.stroke();
        }
    }

    private static Bitmap getHeaderBmp(Activity activity, Context ctx, User user, Company userCompany) {
        View view = activity.getLayoutInflater().inflate(R.layout.sales_order_business_partner_pdf_header, null);
        Bitmap bmp = Utils.getUserCompanyImage(ctx, user);
        if (bmp != null) {
            ((ImageView) view.findViewById(R.id.company_logo)).setImageBitmap(bmp);
        } else {
            view.findViewById(R.id.company_logo_container).setVisibility(View.GONE);
        }
        Typeface typefaceBold = Typeface.createFromAsset(ctx.getAssets(),"fonts/Roboto-Bold.ttf");
        if (!TextUtils.isEmpty(userCompany.getName())) {
            ((TextView) view.findViewById(R.id.company_name)).setText(userCompany.getName());
            ((TextView) view.findViewById(R.id.company_name)).setTypeface(typefaceBold);
        }
        if (!TextUtils.isEmpty(userCompany.getAddress())) {
            ((TextView) view.findViewById(R.id.company_address)).setText(userCompany.getAddress());
            ((TextView) view.findViewById(R.id.company_address)).setTypeface(typefaceBold);
        }
        if (!TextUtils.isEmpty(userCompany.getPhoneNumber())) {
            ((TextView) view.findViewById(R.id.company_phone_number))
                    .setText(ctx.getString(R.string.phone_number_label_detail, userCompany.getPhoneNumber()));
            ((TextView) view.findViewById(R.id.company_phone_number)).setTypeface(typefaceBold);
        }
        if (!TextUtils.isEmpty(userCompany.getEmailAddress())) {
            ((TextView) view.findViewById(R.id.company_email_address)).setText(userCompany.getEmailAddress());
            ((TextView) view.findViewById(R.id.company_email_address)).setTypeface(typefaceBold);
        }
        return Utils.getBitmapFromView(view);
    }
}