package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.MainActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.OrdersListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.SalesOrdersListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.SettingsActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ShoppingCartActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ShoppingSaleActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.WishListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
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
     * @param ctx
     * @param product
     * @param fileName
     * @return
     */
    public static Intent createShareProductIntent(Context ctx, Product product, String fileName){
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
     * @param ctx
     */
    public static void createFileInCacheDir(String fileName, int resId, Context ctx){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Toast.makeText(ctx, ctx.getString(R.string.external_storage_unavailable), Toast.LENGTH_LONG).show();
        } else {
            //path for the image file in the external storage
            File imageFile = new File(ctx.getCacheDir() + File.separator + fileName);
            try {
                imageFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(imageFile);
                Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(), resId);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
            } catch (IOException e1) {
                e1.printStackTrace();
                Toast.makeText(ctx, e1.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     *
     * @param fileName
     * @param image
     * @param ctx
     */
    public static void createFileInCacheDir(String fileName, Bitmap image, Context ctx){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Toast.makeText(ctx, ctx.getString(R.string.external_storage_unavailable), Toast.LENGTH_LONG).show();
        } else {
            //path for the image file in the external storage
            File imageFile = new File(ctx.getCacheDir() + File.separator + fileName);
            try {
                imageFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(imageFile);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
            } catch (IOException e1) {
                e1.printStackTrace();
                Toast.makeText(ctx, e1.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public static Bitmap getImageByFileName(Context context, User user, String fileName){
        if(TextUtils.isEmpty(fileName)){
            return null;
        }
        File imgFile = new File(new StringBuffer(context.getExternalFilesDir(null).toString())
                .append(File.separator).append(user.getUserGroup()).append(File.separator)
                .append(user.getUserName()).append("/Data_In/original/")
                .append(fileName).toString());
        if(imgFile.exists()){
            //return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return decodeSampledBitmap(imgFile.getAbsolutePath(), 250, 250);
        }else{
            return getThumbByFileName(context, user, fileName);
        }
    }

    public static Bitmap getThumbByFileName(Context context, User user, String fileName){
        if(TextUtils.isEmpty(fileName)){
            return null;
        }
        File imgFile = new File(new StringBuffer(context.getExternalFilesDir(null).toString())
                        .append(File.separator).append(user.getUserGroup()).append(File.separator)
                        .append(user.getUserName()).append("/Data_In/thumb/")
                        .append(fileName).toString());
        if(imgFile.exists()){
            //return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return decodeSampledBitmap(imgFile.getAbsolutePath(), 150, 150);
        }
        return null;
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    public static Bitmap decodeSampledBitmap(String pathName,
                                       int reqWidth, int reqHeight) {

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

    public static void setCustomToolbarTitle(final Context context, Toolbar toolbar, final User user,
                                             final boolean goToHome){
        toolbar.setTitle("");
        if (goToHome) {
            for(int i = 0; i < toolbar.getChildCount(); i++){
                View view = toolbar.getChildAt(i);
                /*if(view instanceof TextView){
                    TextView tv = (TextView) view;
                    if(tv.getText().equals(toolbar.getTitle().toString())){
                        if (goToHome) {
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    context.startActivity(new Intent(context,
                                            MainActivity.class).putExtra(MainActivity.KEY_CURRENT_USER, user));
                                }
                            });
                        }
                        tv.setTextSize(22);
                        tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "MyriadPro-Bold.otf"));
                        break;
                    }
                }else*/ if(view instanceof ImageView){
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context,
                                    MainActivity.class).putExtra(MainActivity.KEY_CURRENT_USER, user));
                        }
                    });
                    break;
                }
            }
        }
    }

    public static void setCustomActionbarTitle(final Activity activity, ActionBar actionBar,
                                               final User user, boolean goToHome){
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View customView = activity.getLayoutInflater().inflate(R.layout.actionbar_title, null);
            //TextView customTitle = (TextView) customView.findViewById(R.id.actionbarTitle);
            //if(goToHome) {
            //    customTitle.setOnClickListener(new View.OnClickListener() {
            //        @Override
            //        public void onClick(View v) {
            //            activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(),
            //                    MainActivity.class).putExtra(MainActivity.KEY_CURRENT_USER, user)
            //                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            //        }
            //    });
            //}
            //customTitle.setTextSize(22);
            //customTitle.setTypeface(Typeface.createFromAsset(activity.getAssets(), "MyriadPro-Bold.otf"));
            ImageView customLogo = (ImageView) customView.findViewById(R.id.actionbarLogo);
            if(goToHome) {
                customLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(),
                                MainActivity.class).putExtra(MainActivity.KEY_CURRENT_USER, user)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
     * @param user
     */
    public static void navigationItemSelectedBehave(int itemId, Context context, User user) {
        switch (itemId){
            case R.id.nav_shopping_cart:
                context.startActivity(new Intent(context, ShoppingCartActivity.class)
                        .putExtra(ShoppingCartActivity.KEY_CURRENT_USER, user)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY));
            break;
            case R.id.nav_shopping_sale:
                context.startActivity(new Intent(context, ShoppingSaleActivity.class)
                        .putExtra(ShoppingSaleActivity.KEY_CURRENT_USER, user)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY));
                break;
            case R.id.nav_whish_list:
                context.startActivity(new Intent(context, WishListActivity.class)
                        .putExtra(WishListActivity.KEY_CURRENT_USER, user)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY));
            break;
            case R.id.nav_orders:
                context.startActivity(new Intent(context, OrdersListActivity.class)
                        .putExtra(OrdersListActivity.KEY_CURRENT_USER, user)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY));
            break;
            case R.id.nav_sales_orders:
                context.startActivity(new Intent(context, SalesOrdersListActivity.class)
                        .putExtra(SalesOrdersListActivity.KEY_CURRENT_USER, user)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY));
            break;

            case R.id.nav_settings:
                context.startActivity(new Intent(context, SettingsActivity.class)
                        .putExtra(SettingsActivity.KEY_CURRENT_USER, user));
            break;
            //case R.id.nav_statement_of_account:
            //    Intent intent = new Intent(MainActivity.this, StatementOfAccountActivity.class);
            //    intent.putExtra(StatementOfAccountActivity.KEY_CURRENT_USER, mCurrentUser);
            //    startActivity(intent);
            //break;
            case R.id.nav_share:
                try{
                    showPromptShareApp(context);
                }catch(Throwable e){
                    e.printStackTrace();
                }
            break;
            case R.id.nav_send:
            break;
            case R.id.nav_report_error:
            break;
        }
    }

    public static void createImageFiles(Context context, User user){
        File folderThumb = new File(context.getExternalFilesDir(null) + File.separator + user.getUserGroup()
                + File.separator + user.getUserName() + "/Data_In/thumb/");//-->Android/data/package.name/files/...
        // if the directory does not exist, create it
        if (!folderThumb.exists()) {
            try {
                if (!folderThumb.createNewFile()) {
                    Log.w(TAG, "Failed to create folder: " + folderThumb.getPath() + ".");
                }
            } catch (SecurityException se) {
                se.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File folderOriginal = new File(context.getExternalFilesDir(null) + File.separator + user.getUserGroup()
                + File.separator + user.getUserName() + "/Data_In/original/");//-->Android/data/package.name/files/...
        // if the directory does not exist, create it
        if (!folderOriginal.exists()) {
            try {
                if (!folderOriginal.createNewFile()) {
                    Log.w(TAG, "Failed to create folder: " + folderOriginal.getPath() + ".");
                }
            } catch (SecurityException se) {
                se.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
