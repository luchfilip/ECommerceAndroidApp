package com.smartbuilders.synchronizer.ids.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.synchronizer.ids.model.SyncLog;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.synchronizer.ids.syncadapter.SyncAdapter;
import com.smartbuilders.smartsales.ecommerce.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by stein on 15/8/2016.
 */
public class SyncLogDB {

    private Context mContext;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

    public SyncLogDB(Context context){
        this.mContext = context;
    }

    public void cleanLog(String userId, int daysToKeep){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "DELETE FROM IDS_SYNC_LOG WHERE USER_ID=? AND date(CREATE_TIME) < ? ",
                    new String[]{userId, String.valueOf(addDaysToDate(-daysToKeep))});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanLog(String userId){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "DELETE FROM IDS_SYNC_LOG WHERE USER_ID=?",
                    new String[]{userId});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param user
     * @return
     */
    public ArrayList<SyncLog> getSyncLogByUser(User user){
        ArrayList<SyncLog> syncLog = new ArrayList<>();
        Cursor c = null;
        try{
            if(user!=null){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                        "SELECT CREATE_TIME, LOG_TYPE, LOG_MESSAGE, LOG_MESSAGE_DETAIL FROM IDS_SYNC_LOG WHERE USER_ID=?",
                        new String[]{user.getUserId()}, null);
                if(c!=null){
                    while(c.moveToNext()){
                        syncLog.add(getLogSyncDataParseMessageByType(sdf.parse(c.getString(0)),
                                c.getString(1), c.getString(2), c.getString(3), mContext));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(c!=null){
                c.close();
            }
        }
        return syncLog;
    }

    /**
     *
     * @param user
     * @return
     */
    public ArrayList<SyncLog> getSyncLogByUser(User user, int logVisibility){
        ArrayList<SyncLog> syncLog = new ArrayList<>();
        Cursor c = null;
        try{
            if(user!=null){
                c = mContext.getContentResolver()
                        .query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                                "SELECT CREATE_TIME, LOG_TYPE, LOG_MESSAGE, LOG_MESSAGE_DETAIL " +
                                        " FROM IDS_SYNC_LOG WHERE USER_ID=? AND LOG_VISIBILITY= ?",
                                new String[]{String.valueOf(user.getUserId()), String.valueOf(logVisibility)}, null);
                if(c!=null){
                    while(c.moveToNext()){
                        syncLog.add(getLogSyncDataParseMessageByType(sdf.parse(c.getString(0)), c.getString(1), c.getString(2), c.getString(3), mContext));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(c!=null){
                c.close();
            }
        }
        return syncLog;
    }

    /**
     *
     * @param user
     * @param logType
     * @param logMessage
     * @param logVisibility
     */
    public void registerLogInDataBase(User user, String logType, String logMessage,
                                             String logMessageDetail, int logVisibility){
        try{
            mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI, new ContentValues(),
                            "INSERT INTO IDS_SYNC_LOG (USER_ID, LOG_TYPE, LOG_MESSAGE, LOG_MESSAGE_DETAIL, LOG_VISIBILITY) " +
                                    " VALUES (?, ?, ?, ?, ?)",
                            new String[]{user.getUserId(), logType, logMessage, logMessageDetail,
                                    Integer.valueOf(logVisibility).toString()});
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Devuelve un objeto de tipo SyncLog
     * @param logDate
     * @param logType
     * @param logMessage
     * @param ctx
     * @return
     */
    public SyncLog getLogSyncDataParseMessageByType(Date logDate, String logType, String logMessage, String logMessageDetail, Context ctx){
        if(logType.equals(SyncAdapter.PERIODIC_SYNCHRONIZATION_STARTED)
                || logType.equals(SyncAdapter.SYNCHRONIZATION_STARTED)){
            logMessage = ctx.getString(R.string.sync_started);
        }else if(logType.equals(SyncAdapter.PERIODIC_SYNCHRONIZATION_FINISHED)
                || logType.equals(SyncAdapter.SYNCHRONIZATION_FINISHED)
                || logType.equals(SyncAdapter.FULL_SYNCHRONIZATION_FINISHED)){
            logMessage = ctx.getString(R.string.sync_finished);
        }else if(logType.equals(SyncAdapter.IO_EXCEPTION)){
            logMessage = ctx.getString(R.string.io_exception);
        }else if(logType.equals(SyncAdapter.AUTHENTICATOR_EXCEPTION)){
            logMessage = ctx.getString(R.string.authenticator_exception);
        }else if(logType.equals(SyncAdapter.GENERAL_EXCEPTION)){
            logMessage = ctx.getString(R.string.general_exception);
        }
        return new SyncLog(logDate, logType, logMessage, logMessageDetail);
    }

    // Suma los días recibidos a la fecha
    private Date addDaysToDate(int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, days);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }
}
