package com.jasgcorp.ids.utils;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;

import net.iharder.Base64;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

/**
 * Created by stein on 21/7/2016.
 */
public class DataBaseRemoteManagement {

    /**
     *
     * @param user
     * @param batchMaxLength
     * @param sql
     * @return
     */
    public static Object getJsonBase64CompressedQueryResult(Context context, User user, float batchMaxLength, String sql){
        ArrayList<String> preview;
        ArrayList<String> result;
        Cursor cursor;
        if (user==null) {
            cursor = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI,
                    null, sql, null, null);
        } else {
            cursor = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null, sql, null, null);
        }

        try {

            result = new ArrayList<>();
            preview = new ArrayList<>();
            if(cursor != null){
                int columnCount = cursor.getColumnCount();

                JSONObject json = new JSONObject();
                for(int i = 1; i <= columnCount; i++){
                    try {
                        json.put(String.valueOf(i), cursor.getColumnName(i));
                    } catch(NullPointerException e){
                        json.remove(String.valueOf(i));
                    } catch (JSONException e) {	}
                }
                preview.add(json.toString());

                //for(int i = 1; i <= columnCount; i++){
                //    try {
                //        json.put(String.valueOf(i), rs.getMetaData().getColumnType(i));
                //    } catch(NullPointerException e){
                //        json.remove(String.valueOf(i));
                //    } catch (JSONException e) {	}
                //}
                //preview.add(json.toString());

                if (cursor!=null) {
                    while (cursor.moveToNext()){
                        for(int i = 1; i <= columnCount; i++){
                            try{
                                json.put(String.valueOf(i), cursor.getString(i).trim());
                            } catch(NullPointerException e){
                                json.remove(String.valueOf(i));
                            } catch (JSONException e) {	}
                        }
                        preview.add(json.toString());
                        //Si el tamano del archivo es mayor a batchMaxLenght Bytes
                        if(preview.toString().getBytes("UTF-8").length>batchMaxLength){
                            if(result.isEmpty()){
                                result.add((new StringBuilder("{\"")).append(result.size()).append("\":\"")
                                        .append(Base64.encodeBytes(gzip(preview.toString()), Base64.GZIP)).append("\"").toString());
                            }else{
                                result.add((new StringBuilder("\"")).append(result.size()).append("\":\"")
                                        .append(Base64.encodeBytes(gzip(preview.toString()), Base64.GZIP)).append("\"").toString());
                            }
                            preview.clear();
                        }
                    }
                }
            }
            if(result.isEmpty()){
                result.add((new StringBuilder("{\"")).append(result.size()).append("\":\"")
                        .append(Base64.encodeBytes(gzip(preview.toString()), Base64.GZIP)).append("\"}").toString());
            }else{
                result.add((new StringBuilder("\"")).append(result.size()).append("\":\"")
                        .append(Base64.encodeBytes(gzip(preview.toString()), Base64.GZIP)).append("\"}").toString());
            }
            return Base64.encodeBytes(gzip(result.toString()), Base64.GZIP);
        } catch(Exception e) {
            e.printStackTrace();
            return e;
        } finally {
            try {
                if(cursor != null){
                    cursor.close();
                }

            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * comprime un String y devuelve un byte[]
     * @param s
     * @return
     * @throws Exception
     */
    public static byte[] gzip(String s) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        OutputStreamWriter osw = new OutputStreamWriter(gzip, "UTF-8");
        osw.write(s);
        osw.close();
        return bos.toByteArray();
    }

    /**
     * devuelve la primera palabra en un String,
     * obviando espacios en blanco a la izquierda y derecha
     * @param string
     * @return
     * @throws NullPointerException
     */
    public static String firstWord(String string) throws NullPointerException{
        return string.trim().split("\\s+")[0]; //add " " to string to be sure there is something to split
    }
}
