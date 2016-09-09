package com.smartbuilders.smartsales.ecommerce.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;

/**
 * 
 * Created by Alberto on 6/4/2016.
 *
 */
public class CachedFileProvider extends ContentProvider {

    // The authority is the symbolic name for the provider class 
    public static final String AUTHORITY = BuildConfig.CachedFileProvider_AUTHORITY;

    static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
    
    // UriMatcher used to match against incoming requests 
    private UriMatcher uriMatcher; 
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		 uriMatcher = new UriMatcher(UriMatcher.NO_MATCH); 

        // Add a URI to the matcher which will match against the form 
        // 'content://com.stephendnicholas.gmailattach.provider/*' 
        // and return 1 in the case that the incoming Uri matches this pattern 
        uriMatcher.addURI(AUTHORITY, "*", 1); 
        return true; 
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) 
            throws FileNotFoundException { 
        // Check incoming Uri against the matcher 
        switch (uriMatcher.match(uri)) { 

        // If it returns 1 - then it matches the Uri defined in onCreate 
        case 1: 

            // The desired file name is specified by the last segment of the 
            // path 
            // E.g. 
            // 'content://com.stephendnicholas.gmailattach.provider/Test.txt' 
            // Take this and build the path to the file 
            String fileLocation = getContext().getCacheDir() + File.separator 
                    + uri.getLastPathSegment(); 

            // Create & return a ParcelFileDescriptor pointing to the file 
            // Note: I don't care what mode they ask for - they're only getting 
            // read only 
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File( 
                    fileLocation), ParcelFileDescriptor.MODE_READ_ONLY); 
            return pfd; 

            // Otherwise unrecognised Uri 
        default: 
            throw new FileNotFoundException("Unsupported uri: "
                    + uri.toString()); 
        } 
    } 

	public static void createCachedFile(Context context, String fileName, 
            String content) throws IOException { 

	    File cacheFile = new File(context.getCacheDir() + File.separator 
	                + fileName); 
	    cacheFile.createNewFile(); 
	
	    FileOutputStream fos = new FileOutputStream(cacheFile); 
	    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8"); 
	    PrintWriter pw = new PrintWriter(osw); 
	
	    pw.println(content); 
	
	    pw.flush(); 
	    pw.close(); 
	}
	
}
