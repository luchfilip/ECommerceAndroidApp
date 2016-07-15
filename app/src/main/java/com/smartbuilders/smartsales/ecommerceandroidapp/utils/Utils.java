package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.model.UserProfile;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.BusinessPartnersListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.CompanyActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ContactUsActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.MainActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.OrdersListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.RecommendedProductsListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ShoppingSaleActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ShoppingSalesListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.SalesOrdersListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.SettingsActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ShoppingCartActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.WishListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alberto on 26/3/2016.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    /**
     *
     * @return
     */
    public static boolean isExternalStorageReadOnly() {
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    /**
     *
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     *
     * @param ctx
     * @throws Throwable
     */
    public static void showPromptShareApp(Context ctx) throws Throwable {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, ctx.getString(R.string.checkout_my_app,
                    ctx.getString(R.string.company_name), ctx.getPackageName()));
            sendIntent.setType("text/plain");
            ctx.startActivity(sendIntent);
        } catch(android.content.ActivityNotFoundException ex){
            Toast.makeText(ctx, ctx.getString(R.string.no_app_installed_to_share), Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    /**
     *
     * @param product
     * @param context
     * @param user
     * @return
     */
    public static Intent createShareProductIntent(Product product, Context context, User user){
        String fileName = "tmpImg.jpg";
        if(product.getImageFileName()!=null){
            Bitmap productImage = Utils.getImageFromOriginalDirByFileName(context, product.getImageFileName());
            if(productImage==null){
                //Si el archivo no existe entonces se descarga
                GetFileFromServlet getFileFromServlet =
                        new GetFileFromServlet(product.getImageFileName(), false, user, context);
                try {
                    productImage = getFileFromServlet.execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if(productImage==null){
                productImage = Utils.getImageFromThumbDirByFileName(context, product.getImageFileName());
            }
            if(productImage!=null){
                createFileInCacheDir(fileName, productImage, context);
            }else{
                createFileInCacheDir(fileName, R.drawable.no_image_available, context);
            }
        }else{
            createFileInCacheDir(fileName, R.drawable.no_image_available, context);
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_TEXT, product.getName() + " - "
                + "http://www.febeca.com:8080/products/compra/");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://"
                + CachedFileProvider.AUTHORITY + File.separator + fileName));
        return shareIntent;
    }

    /**
     *
     * @param fileName
     * @param resId
     * @param context
     */
    public static void createFileInCacheDir(String fileName, int resId, Context context){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Toast.makeText(context, context.getString(R.string.external_storage_unavailable), Toast.LENGTH_LONG).show();
        } else if (!TextUtils.isEmpty(fileName) && context!=null){
            //path for the image file in the external storage
            File imageFile = new File(context.getCacheDir() + File.separator + fileName);
            try {
                imageFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(imageFile);
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), resId);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                Toast.makeText(context, e1.getMessage(), Toast.LENGTH_LONG).show();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param fileName
     * @param image
     * @param context
     */
    public static void createFileInCacheDir(String fileName, Bitmap image, Context context){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Toast.makeText(context, context.getString(R.string.external_storage_unavailable), Toast.LENGTH_LONG).show();
        } else if (!TextUtils.isEmpty(fileName) && image!=null && context!=null){
            //path for the image file in the external storage
            File imageFile = new File(context.getCacheDir() + File.separator + fileName);
            try {
                imageFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(imageFile);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                Toast.makeText(context, e1.getMessage(), Toast.LENGTH_LONG).show();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param fileName
     * @param image
     * @param context
     */
    public static void createFileInThumbDir(String fileName, Bitmap image, Context context){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Log.e(TAG, context.getString(R.string.external_storage_unavailable));
        } else if (!TextUtils.isEmpty(fileName) && image!=null && context!=null
                && context.getExternalFilesDir(null)!=null){
            //path for the image file in the external storage
            File imageFile = new File(getImagesThumbFolderPath(context), fileName);
            try {
                try {
                    imageFile.createNewFile();
                } catch (IOException e) {
                    new File(getImagesThumbFolderPath(context)).mkdirs();
                    imageFile = new File(getImagesThumbFolderPath(context), fileName);
                    imageFile.createNewFile();
                }
                FileOutputStream fo = new FileOutputStream(imageFile);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException | NullPointerException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     *
     * @param fileName
     * @param image
     * @param context
     */
    public static void createFileInOriginalDir(String fileName, Bitmap image, Context context){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Log.e(TAG, context.getString(R.string.external_storage_unavailable));
        } else if (!TextUtils.isEmpty(fileName) && image!=null && context!=null
                && context.getExternalFilesDir(null)!=null){
            //path for the image file in the external storage
            File imageFile = new File(getImagesOriginalFolderPath(context), fileName);
            try {
                try {
                    imageFile.createNewFile();
                } catch (IOException e) {
                    new File(getImagesOriginalFolderPath(context)).mkdirs();
                    imageFile = new File(getImagesOriginalFolderPath(context), fileName);
                    imageFile.createNewFile();
                }
                FileOutputStream fo = new FileOutputStream(imageFile);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException | NullPointerException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     *
     * @param context
     * @param isLandscape
     * @param fileName
     * @param image
     */
    public static void createFileInBannerDir(Context context, boolean isLandscape, String fileName, Bitmap image){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Log.e(TAG, context.getString(R.string.external_storage_unavailable));
        } else if (!TextUtils.isEmpty(fileName) && image!=null && context!=null
                && context.getExternalFilesDir(null)!=null){
            //path for the image file in the external storage
            File imageFile = new File(isLandscape ? getImagesBannerLandscapeFolderPath(context)
                    : getImagesBannerPortraitFolderPath(context), fileName);
            try {
                try {
                    imageFile.createNewFile();
                } catch (IOException e) {
                    new File(isLandscape ? getImagesBannerLandscapeFolderPath(context)
                            : getImagesBannerPortraitFolderPath(context)).mkdirs();
                    imageFile = new File(isLandscape ? getImagesBannerLandscapeFolderPath(context)
                            : getImagesBannerPortraitFolderPath(context), fileName);
                    imageFile.createNewFile();
                }
                FileOutputStream fo = new FileOutputStream(imageFile);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException | NullPointerException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     *
     * @param fileName
     * @param image
     * @param context
     */
    public static void createFileInProductBrandPromotionalDir(Context context, boolean isLandscape, String fileName, Bitmap image){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Log.e(TAG, context.getString(R.string.external_storage_unavailable));
        } else if (!TextUtils.isEmpty(fileName) && image!=null && context!=null
                && context.getExternalFilesDir(null)!=null){
            //path for the image file in the external storage
            File imageFile = new File(isLandscape ? getImagesProductBrandPromotionalLandscapeFolderPath(context)
                    : getImagesProductBrandPromotionalPortraitFolderPath(context), fileName);
            try {
                try {
                    imageFile.createNewFile();
                } catch (IOException e) {
                    new File(isLandscape ? getImagesProductBrandPromotionalLandscapeFolderPath(context)
                            : getImagesProductBrandPromotionalPortraitFolderPath(context)).mkdirs();
                    imageFile = new File(isLandscape ? getImagesProductBrandPromotionalLandscapeFolderPath(context)
                            : getImagesProductBrandPromotionalPortraitFolderPath(context), fileName);
                    imageFile.createNewFile();
                }
                FileOutputStream fo = new FileOutputStream(imageFile);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException | NullPointerException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static File getFileInOriginalDirByFileName(Context context, String fileName){
        try {
            File imgFile = new File(getImagesOriginalFolderPath(context), fileName);
            if(imgFile.exists()){
                return imgFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void loadOriginalImageByFileName(final Context context, final User user,
                                                   final String fileName, final ImageView imageView){
        try {
            if(!TextUtils.isEmpty(fileName)){
                Drawable drawable = Drawable.createFromPath(getImagesThumbFolderPath(context) + fileName);
                if (drawable==null) {
                    drawable = getNoImageAvailableDrawable(context);
                }

                File img = Utils.getFileInOriginalDirByFileName(context, fileName);
                if(img!=null && img.exists()){
                    Picasso.with(context).load(img).placeholder(drawable).error(drawable).into(imageView);
                }else{
                    Picasso.with(context)
                            .load(user.getServerAddress() + "/IntelligentDataSynchronizer/GetOriginalImage?fileName=" + fileName)
                            .placeholder(drawable)
                            .error(drawable)
                            .into(imageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    Utils.createFileInOriginalDir(fileName,
                                            ((BitmapDrawable)imageView.getDrawable()).getBitmap(), context);
                                }

                                @Override
                                public void onError() { }
                            });
                }
            }else{
                Picasso.with(context).load(R.drawable.no_image_available).into(imageView);
            }
        } catch (Exception e) {
            if(imageView!=null){
                Picasso.with(context).load(R.drawable.no_image_available).into(imageView);
            }
            e.printStackTrace();
        }
    }

    public static File getFileInBannerDirByFileName(Context context, boolean isLandscape, String fileName){
        try {
            File imgFile = new File(isLandscape ? getImagesBannerLandscapeFolderPath(context)
                    : getImagesBannerPortraitFolderPath(context), fileName);
            if(imgFile.exists()){
                return imgFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getFileInProductBrandPromotionalDirByFileName(Context context, boolean isLandscape, String fileName){
        try {
            File imgFile = new File(isLandscape ? getImagesProductBrandPromotionalLandscapeFolderPath(context)
                    : getImagesProductBrandPromotionalPortraitFolderPath(context), fileName);
            if(imgFile.exists()){
                return imgFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getFileInThumbDirByFileName(Context context, String fileName){
        try {
            File imgFile = new File(getImagesThumbFolderPath(context), fileName);
            if(imgFile.exists()){
                return imgFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void loadThumbImageByFileName(final Context context, final User user,
                                                final String fileName, final ImageView imageView){
        try {
            if(!TextUtils.isEmpty(fileName)){
                File imgFile = new File(getImagesThumbFolderPath(context), fileName);
                if(imgFile.exists()){
                    Picasso.with(context).load(imgFile).error(getNoImageAvailableDrawable(context)).into(imageView);
                }else{
                    Picasso.with(context)
                            .load(user.getServerAddress() + "/IntelligentDataSynchronizer/GetThumbImage?fileName=" + fileName)
                            .error(getNoImageAvailableDrawable(context))
                            //.placeholder(R.drawable.no_image_available)
                            .into(imageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    Utils.createFileInThumbDir(fileName,
                                            ((BitmapDrawable)imageView.getDrawable()).getBitmap(), context);
                                }

                                @Override
                                public void onError() { }
                            });
                }
            }else{
                Picasso.with(context).load(R.drawable.no_image_available).into(imageView);
            }
        } catch (Exception e) {
            if(imageView!=null){
                Picasso.with(context).load(R.drawable.no_image_available).into(imageView);
            }
            e.printStackTrace();
        }
    }

    public static Bitmap getImageFromOriginalDirByFileName(Context context, String fileName){
        try {
            File imgFile = new File(getImagesOriginalFolderPath(context), fileName);
            if(imgFile.exists()){
                return decodeSampledBitmap(imgFile.getAbsolutePath(), 250, 250);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getImageFromThumbDirByFileName(Context context, String fileName){
        try {
            File imgFile = new File(getImagesThumbFolderPath(context), fileName);
            if(imgFile.exists()){
                return decodeSampledBitmap(imgFile.getAbsolutePath(), 150, 150);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param image
     * @param context
     * @param user
     */
    public static void createFileInUserCompanyDir(Bitmap image, Context context, User user){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Log.e(TAG, context.getString(R.string.external_storage_unavailable));
        } else if (image!=null && context!=null
                && context.getExternalFilesDir(null)!=null){
            //path for the image file in the external storage
            File imageFile = new File(getImagesUserCompanyFolderPath(context, user),
                    "user_company_logo.jpg");
            try {
                try {
                    imageFile.createNewFile();
                } catch (IOException e) {
                    new File(getImagesUserCompanyFolderPath(context, user)).mkdirs();
                    imageFile = new File(getImagesUserCompanyFolderPath(context, user),
                            "user_company_logo.jpg");
                    imageFile.createNewFile();
                }
                FileOutputStream fo = new FileOutputStream(imageFile);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException | NullPointerException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void createFileInUserCompanyDir(Context context, User user, InputStream inputStream){
        OutputStream outputStream = null;
        try {
            // write the inputStream to a FileOutputStream
            try {
                outputStream = new FileOutputStream(new File(getImagesUserCompanyFolderPath(context, user),
                        "user_company_logo.jpg"));
            } catch (FileNotFoundException e) {
                new File(getImagesUserCompanyFolderPath(context, user)).mkdirs();
                outputStream = new FileOutputStream(new File(getImagesUserCompanyFolderPath(context, user),
                        "user_company_logo.jpg"));
            }
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap getImageFromUserCompanyDir(Context context, User user){
        try {
            File imgFile = new File(getImagesUserCompanyFolderPath(context, user), "user_company_logo.jpg");
            if(imgFile.exists()){
                return decodeSampledBitmap(imgFile.getAbsolutePath(), 150, 150);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image_available);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmap(String pathName, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void setCustomToolbarTitle(final Context context, Toolbar toolbar, boolean goHome){
        toolbar.setTitle("");
        if (goHome) {
            for(int i = 0; i < toolbar.getChildCount(); i++){
                View view = toolbar.getChildAt(i);
                if(view instanceof ImageView){
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, MainActivity.class));
                        }
                    });
                    break;
                }
            }
        }
    }

    public static void setCustomActionbarTitle(final Activity activity, ActionBar actionBar, boolean goToHome){
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View customView = activity.getLayoutInflater().inflate(R.layout.actionbar_title, null);
            ImageView customLogo = (ImageView) customView.findViewById(R.id.actionbarLogo);
            if(goToHome) {
                customLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(),
                                MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
            }
            actionBar.setCustomView(customView);
        }
    }

    /**
     *
     * @param itemId
     * @param context
     */
    public static void navigationItemSelectedBehave(int itemId, Context context) {
        switch (itemId){
            case R.id.nav_home:
                context.startActivity(new Intent(context, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.nav_shopping_cart:
                context.startActivity(new Intent(context, ShoppingCartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.nav_shopping_sale:
                User user = getCurrentUser(context);
                if(user!=null && user.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID){
                    try {
                        context.startActivity(new Intent(context, ShoppingSaleActivity.class)
                                .putExtra(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID,
                                        Utils.getAppCurrentBusinessPartnerId(context, user))
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    } catch (Exception e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(context)
                                .setMessage(e.getMessage())
                                .setPositiveButton(R.string.accept, null)
                                .show();
                    }
                }else{
                    context.startActivity(new Intent(context, ShoppingSalesListActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                }
                break;
            case R.id.nav_products_recommended_list:
                context.startActivity(new Intent(context, RecommendedProductsListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.nav_wish_list:
                context.startActivity(new Intent(context, WishListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.nav_orders:
                context.startActivity(new Intent(context, OrdersListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.nav_sales_orders:
                context.startActivity(new Intent(context, SalesOrdersListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.nav_settings:
                context.startActivity(new Intent(context, SettingsActivity.class));
                break;
            case R.id.nav_business_partners:
                context.startActivity(new Intent(context, BusinessPartnersListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.nav_share:
                try{
                    showPromptShareApp(context);
                }catch(Throwable e){
                    e.printStackTrace();
                }
                break;
            case R.id.nav_my_company:
                context.startActivity(new Intent(context, CompanyActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.nav_conctac_us:
                context.startActivity(new Intent(context, ContactUsActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.nav_report_error:
                Intent contactUsEmailIntent = new Intent(Intent.ACTION_SEND);
                contactUsEmailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                // need this to prompts email client only
                contactUsEmailIntent.setType("message/rfc822");
                contactUsEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"smartBuilders.ve@gmail.com"});

                context.startActivity(Intent.createChooser(contactUsEmailIntent, context.getString(R.string.send_error_report)));
                break;
        }
    }

    private static String imagesThumbFolderPath;
    public static String getImagesThumbFolderPath(Context context){
        if(imagesThumbFolderPath==null){
            imagesThumbFolderPath = context.getExternalFilesDir(null) + "/images/thumb/";
        }
        return imagesThumbFolderPath;
    }

    private static String imagesOriginalFolderPath;
    public static String getImagesOriginalFolderPath(Context context){
        if(imagesOriginalFolderPath==null){
            imagesOriginalFolderPath = context.getExternalFilesDir(null) + "/images/original/";
        }
        return imagesOriginalFolderPath;
    }

    private static String imagesBannerPortraitFolderPath;
    public static String getImagesBannerPortraitFolderPath(Context context){
        if(imagesBannerPortraitFolderPath==null){
            imagesBannerPortraitFolderPath = context.getExternalFilesDir(null) + "/images/banner/portrait/";
        }
        return imagesBannerPortraitFolderPath;
    }

    private static String imagesBannerLandscapeFolderPath;
    public static String getImagesBannerLandscapeFolderPath(Context context){
        if(imagesBannerLandscapeFolderPath==null){
            imagesBannerLandscapeFolderPath = context.getExternalFilesDir(null) + "/images/banner/landscape/";
        }
        return imagesBannerLandscapeFolderPath;
    }

    private static String imagesProductBrandPromotionalPortraitFolderPath;
    public static String getImagesProductBrandPromotionalPortraitFolderPath(Context context){
        if(imagesProductBrandPromotionalPortraitFolderPath==null){
            imagesProductBrandPromotionalPortraitFolderPath = context.getExternalFilesDir(null) +
                    "/images/productBrandPromotional/portrait/";
        }
        return imagesProductBrandPromotionalPortraitFolderPath;
    }

    private static String imagesProductBrandPromotionalLandscapeFolderPath;
    public static String getImagesProductBrandPromotionalLandscapeFolderPath(Context context){
        if(imagesProductBrandPromotionalLandscapeFolderPath==null){
            imagesProductBrandPromotionalLandscapeFolderPath = context.getExternalFilesDir(null) +
                    "/images/productBrandPromotional/landscape/";
        }
        return imagesProductBrandPromotionalLandscapeFolderPath;
    }

    public static String getImagesUserCompanyFolderPath(Context context, User user){
        return context.getExternalFilesDir(null) + File.separator + user.getUserGroup()
                + File.separator + user.getUserName() + "/images/userCompany/";
    }

    public static boolean appRequireInitialLoadOfGlobalData(Context context) {
        Cursor c = null;
        try{
            String[] tables = new String[]{"PRODUCT", "BRAND", "CATEGORY", "MAINPAGE_PRODUCT",
                    "MAINPAGE_PRODUCT_SECTION", "PRODUCT_AVAILABILITY", "PRODUCT_IMAGE", "SUBCATEGORY"};
            for (String table : tables){
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI,
                        null, "select count(*) from " + table, null, null);
                if(c!=null && c.moveToNext() && c.getInt(0)==0) {
                    return true;
                }
                if(c!=null){
                    c.close();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            if (c!=null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean appRequireInitialLoadOfUserData(Context context, User user) {
        Cursor c = null;
        try{
            String[] tables = new String[]{"BUSINESS_PARTNER"};
            for (String table : tables){
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                        null, "select count(*) from " + table, null, null);
                if(c!=null && c.moveToNext() && c.getInt(0)==0) {
                    return true;
                }
                if(c!=null){
                    c.close();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            if (c!=null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static User getCurrentUser(Context context) {
        try {
            AccountManager accountManager = AccountManager.get(context);
            final Account availableAccounts[] = accountManager
                    .getAccountsByType(context.getString(R.string.authenticator_acount_type));
            if (availableAccounts.length>0) {
                return ApplicationUtilities.getUserByIdFromAccountManager(context,
                        accountManager.getUserData(availableAccounts[0], AccountGeneral.USERDATA_USER_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getMacAddress (Context context) {
        try {
            return (((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                    .getConnectionInfo()).getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(int dp, Context context){
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(int px, Context context){
        return px / (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void createPdfFileInDownloadFolder(final Context context, String sourceFilePath, String fileName){
        InputStream inStream;
        OutputStream outStream;
        try{
            File sourceFile = new File(sourceFilePath);
            File destinationFile =new File(Environment.getExternalStorageDirectory() +
                    File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + fileName);
            inStream = new FileInputStream(sourceFile);
            outStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int length;
            //copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0){
                outStream.write(buffer, 0, length);
            }
            inStream.close();
            outStream.close();
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Descarga completa, "+fileName)
                            .setContentText("El archivo se encuentra en \"Descargas\".");
            // Creates an explicit intent for an Activity in your app
            final Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setDataAndType(Uri.fromFile(destinationFile), "application/pdf");
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            //stackBuilder.addParentStack(ResultActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT );
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1639, mBuilder.build());
            Toast toast = Toast.makeText(context, "Se creó el archivo "+fileName+" en la carpeta \"Descargas\"", Toast.LENGTH_LONG);
            toast.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(resultIntent);
                }
            });
            toast.show();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Hubo un error creando el archivo en la carpeta de \"Descargas\".", Toast.LENGTH_LONG).show();
        }
    }

    private static Drawable noImageAvailable;
    public static Drawable getNoImageAvailableDrawable(Context context){
        if(noImageAvailable==null){
            try {
                noImageAvailable = context.getResources().getDrawable(R.drawable.no_image_available);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        return noImageAvailable;
    }

    /**
     *
     * @param context
     * @param businessPartnerId
     */
    public static void setAppCurrentBusinessPartnerId(Context context, int businessPartnerId){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(BusinessPartner.CURRENT_APP_BP_ID_SHARED_PREFS_KEY, businessPartnerId);
        editor.apply();
    }

    /**
     * Devuelve el actual businessPartnerId que se está usando en la aplicacion
     * @param context
     * @param user
     * @return businessPartnerId
     * @throws Exception
     */
    public static int getAppCurrentBusinessPartnerId(Context context, User user) throws Exception {
        if(user!=null){
            if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                return user.getBusinessPartnerId();
            }else if(user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                if(!PreferenceManager.getDefaultSharedPreferences(context)
                        .contains(BusinessPartner.CURRENT_APP_BP_ID_SHARED_PREFS_KEY)){
                    setAppCurrentBusinessPartnerId(context, (new BusinessPartnerDB(context, user))
                            .getMaxActiveBusinessPartnerId());
                }
                return PreferenceManager.getDefaultSharedPreferences(context)
                        .getInt(BusinessPartner.CURRENT_APP_BP_ID_SHARED_PREFS_KEY, 0);
            }
            throw new Exception("UserProfileId no identificado en getCurrentBusinessPartnerId(...)");
        }else{
            throw new Exception("Usuario es null en getCurrentBusinessPartnerId(...)");
        }
    }

    public static String getUrlScreenParameters(boolean mIsLandscape, Context context){
        String urlScreenParameters = new String();
        try {
            urlScreenParameters += mIsLandscape ? "&orientation=landscape" : "&orientation=portrait";
            switch (context.getResources().getDisplayMetrics().densityDpi) {
                case DisplayMetrics.DENSITY_LOW:    urlScreenParameters += "&screenPlayDensity=LOW";    break;
                case DisplayMetrics.DENSITY_MEDIUM: urlScreenParameters += "&screenPlayDensity=MEDIUM"; break;
                case DisplayMetrics.DENSITY_TV:     urlScreenParameters += "&screenPlayDensity=TV";     break;
                case DisplayMetrics.DENSITY_HIGH:   urlScreenParameters += "&screenPlayDensity=HIGH";   break;
                case DisplayMetrics.DENSITY_280:    urlScreenParameters += "&screenPlayDensity=280";    break;
                case DisplayMetrics.DENSITY_XHIGH:  urlScreenParameters += "&screenPlayDensity=XHIGH";  break;
                case DisplayMetrics.DENSITY_360:    urlScreenParameters += "&screenPlayDensity=360";    break;
                case DisplayMetrics.DENSITY_400:    urlScreenParameters += "&screenPlayDensity=400";    break;
                case DisplayMetrics.DENSITY_420:    urlScreenParameters += "&screenPlayDensity=420";    break;
                case DisplayMetrics.DENSITY_XXHIGH: urlScreenParameters += "&screenPlayDensity=XXHIGH"; break;
                case DisplayMetrics.DENSITY_560:    urlScreenParameters += "&screenPlayDensity=560";    break;
                case DisplayMetrics.DENSITY_XXXHIGH: urlScreenParameters += "&screenPlayDensity=XXXHIGH"; break;
                default: urlScreenParameters += "&screenPlayDensity="+context.getResources().getDisplayMetrics().densityDpi; break;
            }
            switch(context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
                case Configuration.SCREENLAYOUT_SIZE_LARGE: urlScreenParameters += "&screenSize=LARGE"; break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL: urlScreenParameters += "&screenSize=NORMAL"; break;
                case Configuration.SCREENLAYOUT_SIZE_SMALL: urlScreenParameters += "&screenSize=SMALL"; break;
                default: urlScreenParameters += "&screenSize="+(context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK); break;
            }
            urlScreenParameters += "&smallestWidth="+context.getString(R.string.smallest_width);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlScreenParameters;
    }
}
