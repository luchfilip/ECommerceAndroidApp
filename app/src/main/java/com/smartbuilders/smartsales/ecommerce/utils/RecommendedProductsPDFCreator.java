package com.smartbuilders.smartsales.ecommerce.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
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

    private View view;
    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productBrandTextView;
    private TextView productDescriptionTextView;
    private TextView productPurposeTextView;
    private TextView productCodeTextView;
    private TextView productReferenceTextView;
    private TextView productCommercialPackageTextView;
    private TextView productPriceTextView;
    private boolean mShowProductPriceInRecommendedProductsPdf;
    private boolean mManagePriceInOrder;
    private boolean mShowProductTotalPrice;
    private boolean mShowProductPrice;

    public void generatePDF(ArrayList<Product> products, String fileName, Activity activity, Context ctx, User user) throws Exception {
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
        Document document = new Document(PageSize.LETTER, 20, 20, 90, 60);

        if(pdfFile.exists()) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                PdfWriter.getInstance(document, byteArrayOutputStream);
                document.open();

                mShowProductPriceInRecommendedProductsPdf = Parameter.showProductPriceInRecommendedProductsPdf(ctx, user);
                mManagePriceInOrder = Parameter.isManagePriceInOrder(ctx, user);
                mShowProductTotalPrice = Parameter.showProductTotalPrice(ctx, user);
                mShowProductPrice = Parameter.showProductPrice(ctx, user);

                //se cargan las lineas del pedido
                addDetails(document, products, activity, ctx, user);

                document.close();

                // Create a reader
                PdfReader reader = new PdfReader(byteArrayOutputStream.toByteArray());
                // Create a stamper
                PdfStamper stamper = new PdfStamper(reader,
                        new FileOutputStream(ctx.getCacheDir() + File.separator + fileName));

                //Se le agrega la cabecera y el pie de pagina a cada pagina
                addPageHeaderFooter(reader, stamper, ctx);

                // Close the stamper
                stamper.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addPageHeaderFooter(PdfReader reader, PdfStamper stamper,
                               Context ctx) throws DocumentException, IOException {
        Image headerImage = null;
        Bitmap bmp = Utils.decodeSampledBitmapFromResource(ctx.getResources(), R.drawable.catalog_header_recommended_products, 1325, 180);
        if(bmp!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            headerImage = Image.getInstance(stream.toByteArray());
            stream.close();
        }

        Image footerImage = null;
        bmp = Utils.decodeSampledBitmapFromResource(ctx.getResources(), R.drawable.catalog_footer, 1325, 180);
        if(bmp!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            footerImage = Image.getInstance(stream.toByteArray());
            stream.close();
        }

        Image headerRightImage = null;
        bmp = Utils.decodeSampledBitmapFromResource(ctx.getResources(), R.drawable.catalog_header_right, 1325, 180);
        if(bmp!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            headerRightImage = Image.getInstance(stream.toByteArray());
            stream.close();
        }

        Image footerRightImage = null;
        bmp = Utils.decodeSampledBitmapFromResource(ctx.getResources(), R.drawable.catalog_footer_right, 1325, 180);
        if(bmp!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            footerRightImage = Image.getInstance(stream.toByteArray());
            stream.close();
        }

        //Loop over the pages and add a header to each page
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            if (i%2 == 0) {
                getHeaderTable(i, n, headerRightImage).writeSelectedRows(0, -1, 0, 800, stamper.getOverContent(i));
                getFooterTable(i, n, footerRightImage).writeSelectedRows(0, -1, 0, 40, stamper.getOverContent(i));
            } else {
                getHeaderTable(i, n, headerImage).writeSelectedRows(0, -1, 0, 800, stamper.getOverContent(i));
                getFooterTable(i, n, footerImage).writeSelectedRows(0, -1, 0, 40, stamper.getOverContent(i));
            }
        }
    }

    /**
     * Create a header table with page X of Y
     * @param x the page number
     * @param y the total number of pages
     * @return a table that can be used as header
     */
    private static PdfPTable getHeaderTable(int x, int y, Image companyLogo)
            throws DocumentException, IOException {

        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidths(new float[] {612f});
        headerTable.setTotalWidth(612);

        PdfPCell companyLogoCell = new PdfPCell(companyLogo, true);
        companyLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        companyLogoCell.disableBorderSide(Rectangle.UNDEFINED);
        headerTable.addCell(companyLogoCell);
        return headerTable;
    }

    /**
     * Create a header table with page X of Y
     * @param x the page number
     * @param y the total number of pages
     * @return a table that can be used as header
     */
    private static PdfPTable getFooterTable(int x, int y, Image companyLogo)
            throws DocumentException, IOException {

        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidths(new float[] {612f});
        headerTable.setTotalWidth(612);

        PdfPCell companyLogoCell = new PdfPCell(companyLogo, true);
        companyLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        companyLogoCell.disableBorderSide(Rectangle.UNDEFINED);
        headerTable.addCell(companyLogoCell);
        return headerTable;
    }

    private void addDetails(Document document, ArrayList<Product> products,
                            Activity activity, Context ctx, User user) throws DocumentException, IOException {
        PdfPTable superTable = new PdfPTable(2);
        superTable.setWidths(new int[] {280, 280});
        superTable.setTotalWidth(560);
        for(int i = 0; i<products.size(); i++){
            Bitmap bmp = getProductCardImage(activity, ctx, user, products.get(i));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            PdfPCell cell = new PdfPCell(Image.getInstance(stream.toByteArray()), true);
            stream.close();
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            superTable.addCell(cell);

            if (++i<products.size()) {
                bmp = getProductCardImage(activity, ctx, user, products.get(i));
                stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                cell = new PdfPCell(Image.getInstance(stream.toByteArray()), true);
                stream.close();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
                superTable.addCell(cell);
            } else {
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                superTable.addCell(cell);
            }
        }
        document.add(superTable);
    }

    private Bitmap getProductCardImage(Activity activity, Context ctx, User user, Product product) {
        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.product_pdf_layout, null);
            productImageView = (ImageView) view.findViewById(R.id.productImage_imageView);
            productNameTextView = (TextView) view.findViewById(R.id.productName_textView);
            productBrandTextView = (TextView) view.findViewById(R.id.productBrand_textView);
            productDescriptionTextView = (TextView) view.findViewById(R.id.productDescription_textView);
            productPurposeTextView = (TextView) view.findViewById(R.id.productPurpose_textView);
            productCodeTextView = (TextView) view.findViewById(R.id.productCode_textView);
            productReferenceTextView = (TextView) view.findViewById(R.id.productReference_textView);
            productCommercialPackageTextView = (TextView) view.findViewById(R.id.productCommercialPackage_textView);
            productPriceTextView = (TextView) view.findViewById(R.id.productPrice_textView);
        }

        Bitmap bmp = Utils.getThumbImage(ctx, user, product.getImageFileName());
        if (bmp != null) {
            productImageView.setImageBitmap(bmp);
        } else {
            productImageView.setImageDrawable(Utils.getNoImageAvailableDrawable(ctx));
        }
        productNameTextView.setText(product.getName());
        productBrandTextView.setText(product.getProductBrand().getName());
        productDescriptionTextView.setText(product.getDescription());
        productPurposeTextView.setText(product.getPurpose());
        if(product.getProductCommercialPackage()!=null
                && !TextUtils.isEmpty(product.getProductCommercialPackage().getUnitDescription())){
            productCommercialPackageTextView.setText(ctx.getString(R.string.commercial_package,
                    product.getProductCommercialPackage().getUnitDescription(), product.getProductCommercialPackage().getUnits()));
        }else{
            productCommercialPackageTextView.setVisibility(TextView.GONE);
        }
        productCodeTextView.setText(product.getInternalCodeMayoreoFormat());
        productReferenceTextView.setText(product.getReference());

        if (mManagePriceInOrder && mShowProductPriceInRecommendedProductsPdf
                && product.getProductPriceAvailability().getAvailability()>0
                && product.getProductPriceAvailability().getPrice()>0) {
            if (mShowProductTotalPrice) {
                productPriceTextView.setText(ctx.getString(R.string.product_total_price_detail,
                        product.getProductPriceAvailability().getCurrency().getName(),
                        product.getProductPriceAvailability().getTotalPriceStringFormat()));
                productPriceTextView.setVisibility(View.VISIBLE);
            } else if (mShowProductPrice) {
                productPriceTextView.setText(ctx.getString(R.string.product_price_detail,
                        product.getProductPriceAvailability().getCurrency().getName(),
                        product.getProductPriceAvailability().getPriceStringFormat()));
                productPriceTextView.setVisibility(View.VISIBLE);
            }
        }
        return Utils.getBitmapFromView(view);
    }
}