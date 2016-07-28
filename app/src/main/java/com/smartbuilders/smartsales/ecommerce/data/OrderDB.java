package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.model.UserProfile;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.businessRules.OrderBR;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by stein on 4/30/2016.
 */
public class OrderDB {

    private Context mContext;
    private User mUser;

    public OrderDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public String createOrderFromOrderLines(Integer salesOrderId, int businessPartnerId,
                                            ArrayList<OrderLine> orderLines){
        return createOrder(salesOrderId, businessPartnerId, orderLines, true);
    }

    public String createOrderFromShoppingCart(int businessPartnerId){
        return createOrder(null, businessPartnerId, (new OrderLineDB(mContext, mUser)).getActiveOrderLinesFromShoppingCart(), false);
    }

    public ArrayList<Order> getActiveOrders(){
        return getActiveOrders(false);
    }

    public ArrayList<Order> getActiveOrdersFromSalesOrders(){
        return getActiveOrders(true);
    }

    public Order getActiveOrderById(int orderId){
        Cursor c = null;
        Order order = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT ECOMMERCE_ORDER_ID, ECOMMERCE_SALES_ORDER_ID, CREATE_TIME, LINES_NUMBER, " +
                            " SUB_TOTAL, TAX, TOTAL, BUSINESS_PARTNER_ID "+
                    " FROM ECOMMERCE_ORDER " +
                    " WHERE ECOMMERCE_ORDER_ID = ? AND USER_ID = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(orderId), String.valueOf(mUser.getServerUserId()), "Y"}, null);
            if(c!=null && c.moveToNext()){
                order = new Order();
                order.setId(c.getInt(0));
                order.setSalesOrderId(c.getInt(1));
                try{
                    order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(2)).getTime()));
                }catch(ParseException ex){
                    try {
                        order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(2)).getTime()));
                    } catch (ParseException e) {
                        //empty
                    }
                }catch(Exception ex){
                    //empty
                }
                order.setLinesNumber(c.getInt(3));
                order.setSubTotalAmount(c.getDouble(4));
                order.setTaxAmount(c.getDouble(5));
                order.setTotalAmount(c.getDouble(6));
                order.setBusinessPartnerId(c.getInt(7));
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

        if(order!=null && order.getBusinessPartnerId()>0){
            if(order.getSalesOrderId()>0){
                order.setBusinessPartner((new UserBusinessPartnerDB(mContext, mUser))
                        .getActiveUserBusinessPartnerById(order.getBusinessPartnerId()));
            }else{
                order.setBusinessPartner((new BusinessPartnerDB(mContext, mUser))
                        .getActiveBusinessPartnerById(order.getBusinessPartnerId()));
            }
        }
        return order;
    }

    private String createOrder(Integer salesOrderId, int businessPartnerId,
                               ArrayList<OrderLine> orderLines, boolean insertOrderLinesInDB){
        OrderLineDB orderLineDB = new OrderLineDB(mContext, mUser);
        int shoppingCartLinesNumber = orderLineDB.getActiveShoppingCartLinesNumber();
        if((orderLines!=null && insertOrderLinesInDB) || shoppingCartLinesNumber>0){
            int orderId;
            try {
                orderId = getMaxOrderId() + 1;

                double subTotal = OrderBR.getSubTotalAmount(orderLines),
                        tax = OrderBR.getTaxAmount(orderLines),
                        total = OrderBR.getTotalAmount(orderLines);

                int rowsAffected = mContext.getContentResolver()
                        .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                                null,
                                "INSERT INTO ECOMMERCE_ORDER (ECOMMERCE_ORDER_ID, USER_ID, ECOMMERCE_SALES_ORDER_ID, BUSINESS_PARTNER_ID, " +
                                        " DOC_STATUS, DOC_TYPE, CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL) " +
                                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                                new String[]{String.valueOf(orderId), String.valueOf(mUser.getServerUserId()),
                                        salesOrderId==null ? null : String.valueOf(salesOrderId),
                                        String.valueOf(businessPartnerId), "CO",
                                        OrderLineDB.FINALIZED_ORDER_DOC_TYPE, DateFormat.getCurrentDateTimeSQLFormat(),
                                        Utils.getAppVersionName(mContext), mUser.getUserName(), Utils.getMacAddress(mContext),
                                        String.valueOf(orderLines!=null ? orderLines.size() : shoppingCartLinesNumber),
                                        String.valueOf(subTotal), String.valueOf(tax), String.valueOf(total)});
                if(rowsAffected <= 0){
                    return "Error 001 - No se insertó el pedido en la base de datos.";
                }


            } catch (Exception e){
                e.printStackTrace();
                return e.getMessage();
            }
            if (orderLines!=null && insertOrderLinesInDB) {
                for (OrderLine orderLine : orderLines) {
                    orderLineDB.addOrderLineToFinalizedOrder(orderLine, orderId);
                }
            } else {
                if(orderLineDB.moveShoppingCartToFinalizedOrderByOrderId(orderId)<=0){
                    try {
                        int rowsAffected = mContext.getContentResolver()
                                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                                .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                                        null,
                                        "DELETE FROM ECOMMERCE_ORDER WHERE ECOMMERCE_ORDER_ID = ? AND USER_ID = ?",
                                        new String[]{String.valueOf(orderId), String.valueOf(mUser.getServerUserId())});
                        if(rowsAffected <= 0){
                            return "Error 003 - No se insertó el pedido en la base de datos ni se eliminó la cabecera.";
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    return "Error 002 - No se insertó el pedido en la base de datos.";
                }
            }
        }else{
            return "No existen productos en el Carrito de compras.";
        }
        return null;
    }

    /**
     *
     * @return
     */
    private ArrayList<Order> getActiveOrders(boolean fromSalesOrder){
        ArrayList<Order> activeOrders = new ArrayList<>();
        Cursor c = null;
        try {
            if(mUser!=null && mUser.getUserProfileId()==UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT ECOMMERCE_ORDER_ID, DOC_STATUS, CREATE_TIME, UPDATE_TIME, " +
                                " APP_VERSION, APP_USER_NAME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL, " +
                                " ECOMMERCE_SALES_ORDER_ID, BUSINESS_PARTNER_ID " +
                                " FROM ECOMMERCE_ORDER " +
                                " WHERE USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                                " ORDER BY ECOMMERCE_ORDER_ID desc",
                        new String[]{String.valueOf(mUser.getServerUserId()), OrderLineDB.FINALIZED_ORDER_DOC_TYPE, "Y"}, null);
            }else if(mUser!=null && mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT ECOMMERCE_ORDER_ID, DOC_STATUS, CREATE_TIME, UPDATE_TIME, " +
                                " APP_VERSION, APP_USER_NAME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL, " +
                                " ECOMMERCE_SALES_ORDER_ID, BUSINESS_PARTNER_ID " +
                                " FROM ECOMMERCE_ORDER " +
                                " WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                                " ORDER BY ECOMMERCE_ORDER_ID desc",
                        new String[]{String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                                String.valueOf(mUser.getServerUserId()), OrderLineDB.FINALIZED_ORDER_DOC_TYPE, "Y"}, null);
            }

            if(c!=null){
                while(c.moveToNext()){
                    if(fromSalesOrder && c.getInt(10)<=0){
                        continue;
                    }
                    Order order = new Order();
                    order.setId(c.getInt(0));
                    try{
                        order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(2)).getTime()));
                    }catch(ParseException ex){
                        try {
                            order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(2)).getTime()));
                        } catch (ParseException e) {
                            //empty
                        }
                    }catch(Exception ex){
                        //empty
                    }
                    order.setLinesNumber(c.getInt(6));
                    order.setSubTotalAmount(c.getDouble(7));
                    order.setTaxAmount(c.getDouble(8));
                    order.setTotalAmount(c.getDouble(9));
                    order.setSalesOrderId(c.getInt(10));
                    order.setBusinessPartnerId(c.getInt(11));
                    activeOrders.add(order);
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

        UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(mContext, mUser);
        BusinessPartnerDB businessPartnerDB = new BusinessPartnerDB(mContext, mUser);
        ArrayList<Order> ordersToRemove = new ArrayList<>();
        for(Order order : activeOrders){
            if(order.getBusinessPartnerId()>0){
                if(mUser!=null && mUser.getUserProfileId()==UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                    if(order.getSalesOrderId()>0){
                        order.setBusinessPartner(userBusinessPartnerDB.getActiveUserBusinessPartnerById(order.getBusinessPartnerId()));
                    }else{
                        order.setBusinessPartner(businessPartnerDB.getActiveBusinessPartnerById(order.getBusinessPartnerId()));
                    }
                }else if(mUser!=null && mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID){
                    order.setBusinessPartner(businessPartnerDB.getActiveBusinessPartnerById(order.getBusinessPartnerId()));
                }
            }
            if(order.getBusinessPartner()==null) {
                ordersToRemove.add(order);
            }
        }
        //se eliminan los pedidos que no tienen businessPartner asociado.
        activeOrders.removeAll(ordersToRemove);
        return activeOrders;
    }

    private int getMaxOrderId(){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE USER_ID = ?",
                    new String[]{String.valueOf(mUser.getServerUserId())}, null);
            if(c!=null && c.moveToNext()){
                return c.getInt(0);
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
        return 0;
    }
}
