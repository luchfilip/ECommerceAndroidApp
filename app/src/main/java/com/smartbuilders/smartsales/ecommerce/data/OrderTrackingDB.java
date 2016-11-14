package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.model.OrderTracking;
import com.smartbuilders.smartsales.ecommerce.model.OrderTrackingState;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by AlbertoSarco on 18/10/2016.
 */
public class OrderTrackingDB {

    private Context mContext;
    private User mUser;

    public OrderTrackingDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public OrderTracking getOrderTracking(int id) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null,
                            "SELECT OT.ECOMMERCE_ORDER_ID, OT.ORDER_TRACKING_STATE_ID, OT.DETAILS, OT.CREATE_TIME, " +
                            " OTS.TITLE, OTS.ICON_RES_NAME, OTS.ICON_FILE_NAME, " +
                            " OTS.BACKGROUND_R_COLOR, OTS.BACKGROUND_G_COLOR, OTS.BACKGROUND_B_COLOR, " +
                            " OTS.BORDER_R_COLOR, OTS.BORDER_G_COLOR, OTS.BORDER_B_COLOR, " +
                            " OTS.TITLE_TEXT_R_COLOR, OTS.TITLE_TEXT_G_COLOR, OTS.TITLE_TEXT_B_COLOR, " +
                            " OTS.ICON_R_COLOR, OTS.ICON_G_COLOR, OTS.ICON_B_COLOR, " +
                            " OTS.IS_ALWAYS_VISIBLE " +
                            " FROM ORDER_TRACKING OT " +
                            " LEFT JOIN ORDER_TRACKING_STATE  OTS ON OTS.ORDER_TRACKING_STATE_ID = OT.ORDER_TRACKING_STATE_ID AND OTS.IS_ACTIVE = ? " +
                            " WHERE OT.ORDER_TRACKING_ID = ? AND OT.USER_ID = ? AND OT.IS_ACTIVE = ?",
                            new String[]{String.valueOf(id), String.valueOf(mUser.getServerUserId()), "Y", "Y"},
                            null);
            if(c!=null && c.moveToNext()){
                OrderTracking orderTracking = new OrderTracking();
                orderTracking.setOrderId(c.getInt(0));
                orderTracking.setOrderTrackingStateId(c.getInt(1));
                orderTracking.setDetails(c.getString(2));
                if (c.getString(3) != null) {
                    try {
                        orderTracking.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(3)).getTime()));
                    } catch (ParseException ex) {
                        try {
                            orderTracking.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(3)).getTime()));
                        } catch (ParseException e) {
                            //empty
                        }
                    } catch (Exception ex) {
                        //empty
                    }
                }
                OrderTrackingState orderTrackingState = new OrderTrackingState();
                orderTrackingState.setId(c.getInt(1));
                orderTrackingState.setTitle(c.getString(4));
                orderTrackingState.setIconResName(c.getString(5));
                orderTrackingState.setIconFileName(c.getString(6));
                orderTrackingState.setBackground_R_Color(c.getString(7)==null ? -1 : c.getInt(7));
                orderTrackingState.setBackground_G_Color(c.getString(8)==null ? -1 : c.getInt(8));
                orderTrackingState.setBackground_B_Color(c.getString(9)==null ? -1 : c.getInt(9));
                orderTrackingState.setBorder_R_Color(c.getString(10)==null ? -1 : c.getInt(10));
                orderTrackingState.setBorder_G_Color(c.getString(11)==null ? -1 : c.getInt(11));
                orderTrackingState.setBorder_B_Color(c.getString(12)==null ? -1 : c.getInt(12));
                orderTrackingState.setTitle_R_Color(c.getString(13)==null ? -1 : c.getInt(13));
                orderTrackingState.setTitle_G_Color(c.getString(14)==null ? -1 : c.getInt(14));
                orderTrackingState.setTitle_B_Color(c.getString(15)==null ? -1 : c.getInt(15));
                orderTrackingState.setIcon_R_Color(c.getString(16)==null ? -1 : c.getInt(16));
                orderTrackingState.setIcon_G_Color(c.getString(17)==null ? -1 : c.getInt(17));
                orderTrackingState.setIcon_B_Color(c.getString(18)==null ? -1 : c.getInt(18));
                orderTrackingState.setAlwaysVisible(String.valueOf(c.getString(19)).equals("Y"));
                orderTracking.setOrderTrackingState(orderTrackingState);
                return orderTracking;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null){
                try {
                    c.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public ArrayList<OrderTracking> getOrderTrackings (int orderId) {
        ArrayList<OrderTracking> orderTrackings = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT OT.ORDER_TRACKING_STATE_ID, OT.DETAILS, OT.CREATE_TIME, " +
                        " OTS.TITLE, OTS.ICON_RES_NAME, OTS.ICON_FILE_NAME, " +
                        " OTS.BACKGROUND_R_COLOR, OTS.BACKGROUND_G_COLOR, OTS.BACKGROUND_B_COLOR, " +
                        " OTS.BORDER_R_COLOR, OTS.BORDER_G_COLOR, OTS.BORDER_B_COLOR, " +
                        " OTS.TITLE_TEXT_R_COLOR, OTS.TITLE_TEXT_G_COLOR, OTS.TITLE_TEXT_B_COLOR, " +
                        " OTS.ICON_R_COLOR, OTS.ICON_G_COLOR, OTS.ICON_B_COLOR, " +
                        " OTS.IS_ALWAYS_VISIBLE " +
                    " FROM ORDER_TRACKING_STATE  OTS " +
                        " LEFT JOIN ORDER_TRACKING OT ON OT.ORDER_TRACKING_STATE_ID = OTS.ORDER_TRACKING_STATE_ID " +
                            " AND OT.ECOMMERCE_ORDER_ID = ? AND OT.USER_ID = ? AND OT.IS_ACTIVE = ? " +
                    " WHERE OTS.IS_ACTIVE = ? " +
                    " ORDER BY OTS.PRIORITY ASC",
                    new String[]{String.valueOf(orderId), String.valueOf(mUser.getServerUserId()), "Y", "Y"},
                    null);
            if(c!=null){
                while(c.moveToNext()){
                    OrderTracking orderTracking = new OrderTracking();
                    orderTracking.setOrderId(orderId);
                    orderTracking.setOrderTrackingStateId(c.getInt(0));
                    orderTracking.setDetails(c.getString(1));
                    if (c.getString(2) != null) {
                        try {
                            orderTracking.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(2)).getTime()));
                        } catch (ParseException ex) {
                            try {
                                orderTracking.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(2)).getTime()));
                            } catch (ParseException e) {
                                //empty
                            }
                        } catch (Exception ex) {
                            //empty
                        }
                    }
                    OrderTrackingState orderTrackingState = new OrderTrackingState();
                    orderTrackingState.setId(c.getInt(0));
                    orderTrackingState.setTitle(c.getString(3));
                    orderTrackingState.setIconResName(c.getString(4));
                    orderTrackingState.setIconFileName(c.getString(5));
                    orderTrackingState.setBackground_R_Color(c.getString(6)==null ? -1 : c.getInt(6));
                    orderTrackingState.setBackground_G_Color(c.getString(7)==null ? -1 : c.getInt(7));
                    orderTrackingState.setBackground_B_Color(c.getString(8)==null ? -1 : c.getInt(8));
                    orderTrackingState.setBorder_R_Color(c.getString(9)==null ? -1 : c.getInt(9));
                    orderTrackingState.setBorder_G_Color(c.getString(10)==null ? -1 : c.getInt(10));
                    orderTrackingState.setBorder_B_Color(c.getString(11)==null ? -1 : c.getInt(11));
                    orderTrackingState.setTitle_R_Color(c.getString(12)==null ? -1 : c.getInt(12));
                    orderTrackingState.setTitle_G_Color(c.getString(13)==null ? -1 : c.getInt(13));
                    orderTrackingState.setTitle_B_Color(c.getString(14)==null ? -1 : c.getInt(14));
                    orderTrackingState.setIcon_R_Color(c.getString(15)==null ? -1 : c.getInt(15));
                    orderTrackingState.setIcon_G_Color(c.getString(16)==null ? -1 : c.getInt(16));
                    orderTrackingState.setIcon_B_Color(c.getString(17)==null ? -1 : c.getInt(17));
                    orderTrackingState.setAlwaysVisible(String.valueOf(c.getString(18)).equals("Y"));
                    orderTracking.setOrderTrackingState(orderTrackingState);
                    orderTrackings.add(orderTracking);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null){
                try {
                    c.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return orderTrackings;
    }

    public OrderTracking getMaxOrderTracking(int orderId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT OT.ORDER_TRACKING_STATE_ID, OT.DETAILS, OT.CREATE_TIME, " +
                        " OTS.TITLE, OTS.ICON_RES_NAME, OTS.ICON_FILE_NAME, " +
                        " OTS.BACKGROUND_R_COLOR, OTS.BACKGROUND_G_COLOR, OTS.BACKGROUND_B_COLOR, " +
                        " OTS.BORDER_R_COLOR, OTS.BORDER_G_COLOR, OTS.BORDER_B_COLOR, " +
                        " OTS.TITLE_TEXT_R_COLOR, OTS.TITLE_TEXT_G_COLOR, OTS.TITLE_TEXT_B_COLOR, " +
                        " OTS.ICON_R_COLOR, OTS.ICON_G_COLOR, OTS.ICON_B_COLOR, " +
                        " OTS.IS_ALWAYS_VISIBLE " +
                    " FROM ORDER_TRACKING OT " +
                        " INNER JOIN ORDER_TRACKING_STATE OTS ON OTS.ORDER_TRACKING_STATE_ID = OT.ORDER_TRACKING_STATE_ID AND OTS.IS_ACTIVE = ? " +
                    " WHERE OT.ECOMMERCE_ORDER_ID = ? AND OT.USER_ID = ? AND OT.IS_ACTIVE = ? " +
                    " ORDER BY OTS.PRIORITY DESC " +
                    " LIMIT 1",
                    new String[]{"Y", String.valueOf(orderId), String.valueOf(mUser.getServerUserId()), "Y"},
                    null);
            if(c!=null && c.moveToNext()){
                OrderTracking orderTracking = new OrderTracking();
                orderTracking.setOrderId(orderId);
                orderTracking.setOrderTrackingStateId(c.getInt(0));
                orderTracking.setDetails(c.getString(1));
                try{
                    orderTracking.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(2)).getTime()));
                }catch(ParseException ex){
                    try {
                        orderTracking.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(2)).getTime()));
                    } catch (ParseException e) {
                        //empty
                    }
                }catch(Exception ex){
                    //empty
                }
                OrderTrackingState orderTrackingState = new OrderTrackingState();
                orderTrackingState.setId(c.getInt(0));
                orderTrackingState.setTitle(c.getString(3));
                orderTrackingState.setIconResName(c.getString(4));
                orderTrackingState.setIconFileName(c.getString(5));
                orderTrackingState.setBackground_R_Color(c.getString(6)==null ? -1 : c.getInt(6));
                orderTrackingState.setBackground_G_Color(c.getString(7)==null ? -1 : c.getInt(7));
                orderTrackingState.setBackground_B_Color(c.getString(8)==null ? -1 : c.getInt(8));
                orderTrackingState.setBorder_R_Color(c.getString(9)==null ? -1 : c.getInt(9));
                orderTrackingState.setBorder_G_Color(c.getString(10)==null ? -1 : c.getInt(10));
                orderTrackingState.setBorder_B_Color(c.getString(11)==null ? -1 : c.getInt(11));
                orderTrackingState.setTitle_R_Color(c.getString(12)==null ? -1 : c.getInt(12));
                orderTrackingState.setTitle_G_Color(c.getString(13)==null ? -1 : c.getInt(13));
                orderTrackingState.setTitle_B_Color(c.getString(14)==null ? -1 : c.getInt(14));
                orderTrackingState.setIcon_R_Color(c.getString(15)==null ? -1 : c.getInt(15));
                orderTrackingState.setIcon_G_Color(c.getString(16)==null ? -1 : c.getInt(16));
                orderTrackingState.setIcon_B_Color(c.getString(17)==null ? -1 : c.getInt(17));
                orderTrackingState.setAlwaysVisible(String.valueOf(c.getString(18)).equals("Y"));
                orderTracking.setOrderTrackingState(orderTrackingState);
                return orderTracking;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null){
                try {
                    c.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public int getProgressPercentage(int orderId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT COUNT(ORDER_TRACKING_STATE_ID) AS TOTAL, " +
                        " (SELECT COUNT(ORDER_TRACKING_STATE_ID) " +
                            " FROM ORDER_TRACKING " +
                            " WHERE ECOMMERCE_ORDER_ID = ? AND USER_ID = ? AND IS_ACTIVE = ?) AS CURRENT " +
                    " FROM ORDER_TRACKING_STATE " +
                    " WHERE IS_ACTIVE = ? AND IS_ALWAYS_VISIBLE = ? ",
                    new String[]{String.valueOf(orderId), String.valueOf(mUser.getServerUserId()), "Y", "Y", "Y"},
                    null);
            if(c!=null && c.moveToNext() && c.getInt(0)!=0){
                return (c.getInt(1)*100) / c.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null){
                try {
                    c.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    public String getProgressText(int orderId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT COUNT(ORDER_TRACKING_STATE_ID) AS TOTAL, " +
                            " (SELECT COUNT(ORDER_TRACKING_STATE_ID) " +
                            " FROM ORDER_TRACKING " +
                            " WHERE ECOMMERCE_ORDER_ID = ? AND USER_ID = ? AND IS_ACTIVE = ?) AS CURRENT " +
                            " FROM ORDER_TRACKING_STATE " +
                            " WHERE IS_ACTIVE = ? AND IS_ALWAYS_VISIBLE = ? ",
                    new String[]{String.valueOf(orderId), String.valueOf(mUser.getServerUserId()), "Y", "Y", "Y"},
                    null);
            if(c!=null && c.moveToNext() && c.getInt(0)!=0){
                if (c.getInt(0) == c.getInt(1)) {
                    return "¡Completado!";
                } else {
                    return "Progreso: " + c.getInt(1) + "/" + c.getInt(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null){
                try {
                    c.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
