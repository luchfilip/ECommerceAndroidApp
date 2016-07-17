package com.jasgcorp.ids.datamanager;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ThumbImagesReceiverFromServer extends Thread {

	private static final String TAG = ThumbImagesReceiverFromServer.class.getSimpleName();

	private Context context;
	private boolean sync = true;
	private String exceptionMessage;
	private String exceptionClass;
	private float syncPercentage;
	private User mUser;

	public ThumbImagesReceiverFromServer(User user, Context context) throws Exception{
		this.context = context;
		this.mUser = user;
	}
	/**
	 * detiene el hilo de sincronizacion
	 */
	public void stopSynchronization(){
		Log.d(TAG, "stopSynchronization()");
		sync = false;
	}
	
	public String getExceptionMessage(){
		return exceptionMessage;
	}
	
	public String getExceptionClass(){
		return exceptionClass;
	}
	
	@Override
	public void run() {
		Log.d(TAG, "run()");
		try {
            long initTime = System.currentTimeMillis();
			if(sync){
				getProductsThumbImageFromServer(context, mUser);
			}
            Log.d(TAG, "Total Load Time: "+(System.currentTimeMillis() - initTime)+"ms");
		} catch (Exception e) {
			e.printStackTrace();
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();
		}
		sync = false;
	}

	private void getProductsThumbImageFromServer(Context context, User user){
		Cursor c = null;
		try {
			c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
					"SELECT FILE_NAME FROM PRODUCT_IMAGE WHERE IS_ACTIVE='Y' AND PRIORITY=1",
					null, null);
			if(c!=null){
				List<String> filesName = new ArrayList<>();
				while(c.moveToNext()){
					filesName.add(c.getString(0));
					if(Utils.getFileInThumbDirByFileName(context, c.getString(0))==null){
						downloadImage(c.getString(0), context, user);
					}
				}
                //se limpia la carpeta de los archivos que ya no pertenezcan
				List<String> filesInThumbDir = Utils.getListOfFilesInThumbDir(context);
                filesInThumbDir.removeAll(filesName);
                for (String fileNameToRemove : filesInThumbDir) {
                    try {
                        (new File (Utils.getImagesThumbFolderPath(context), fileNameToRemove)).delete();
                    } catch (Exception e) {
                        Log.e(TAG, "Error removing file: \""+fileNameToRemove+
                                "\", ExceptionMessage: "+e.getMessage());
                    }
                }
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(c != null) {
				try {
					c.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Creates Bitmap from InputStream and returns it
	private void downloadImage(String fileName, Context context, User user) throws IOException {
		try {
			OutputStream outputStream = null;
			InputStream inputStream = null;
			try {
				inputStream = getHttpConnection(user.getServerAddress() +
						"/IntelligentDataSynchronizer/GetThumbImage?fileName=" + fileName);
				// write the inputStream to a FileOutputStream
				try{
					outputStream = new FileOutputStream(new File(Utils.getImagesThumbFolderPath(context), fileName));
				} catch (FileNotFoundException e){
					(new File(Utils.getImagesThumbFolderPath(context))).mkdirs();
					outputStream = new FileOutputStream(new File(Utils.getImagesThumbFolderPath(context), fileName));
				}
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			} catch (IOException e) {
				throw e;
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
		} catch (IOException e){
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Makes HttpURLConnection and returns InputStream
	private InputStream getHttpConnection(String urlString) throws Exception {
		try {
			HttpURLConnection httpConnection = (HttpURLConnection) (new URL(urlString)).openConnection();
			httpConnection.setConnectTimeout(1000);
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();
			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return httpConnection.getInputStream();
			} else {
				if(httpConnection.getResponseCode()==404){
					throw new IOException("httpConnection.getResponseCode(): " + httpConnection.getResponseCode());
				}
				throw new Exception("httpConnection.getResponseCode(): " + httpConnection.getResponseCode());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public float getSyncPercentage() {
		return syncPercentage;
	}

}
