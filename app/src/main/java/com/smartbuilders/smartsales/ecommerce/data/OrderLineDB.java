package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.businessRules.OrderLineBR;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 4/30/2016.
 */
public class OrderLineDB {

    public static final String WISH_LIST_DOC_TYPE = "WL";
    public static final String SHOPPING_CART_DOC_TYPE = "SC";
    public static final String FINALIZED_ORDER_DOC_TYPE = "FO";

    private Context mContext;
    private User mUser;

    public OrderLineDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public String addOrderLineToFinalizedOrder(OrderLine orderLine, int orderId){
        return addOrderLine(orderLine, FINALIZED_ORDER_DOC_TYPE, orderId);
    }

    public String addOrderLineToShoppingCart(OrderLine orderLine){
        return addOrderLine(orderLine, SHOPPING_CART_DOC_TYPE, null);
    }

    public String addProductToWishList(Product product){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "INSERT INTO ECOMMERCE_ORDER_LINE (ECOMMERCE_ORDER_LINE_ID, USER_ID, BUSINESS_PARTNER_ID, " +
                            " PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, DOC_TYPE, " +
                            " CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(UserTableMaxIdDB.getNewIdForTable(mContext, mUser, "ECOMMERCE_ORDER_LINE")),
                            String.valueOf(mUser.getServerUserId()),
                            String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(product.getId()), String.valueOf(product.getDefaultProductPriceAvailability().getAvailability()),
                            String.valueOf(product.getDefaultProductPriceAvailability().getPrice()),
                            String.valueOf(product.getProductTax().getPercentage()), WISH_LIST_DOC_TYPE,
                            DateFormat.getCurrentDateTimeSQLFormat(), Utils.getAppVersionName(mContext),
                            mUser.getUserName(), Utils.getMacAddress(mContext)});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public ArrayList<OrderLine> getActiveOrderLinesFromShoppingCart() throws Exception {
        return getOrderLines(SHOPPING_CART_DOC_TYPE, null, Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
    }

    public ArrayList<OrderLine> getWishList(){
        return getOrderLines(WISH_LIST_DOC_TYPE, null, null);
    }

    public ArrayList<OrderLine> getActiveFinalizedOrderLinesByOrderId(int orderId) throws Exception {
        return getActiveOrderLinesByOrderId(FINALIZED_ORDER_DOC_TYPE, orderId);
    }

    private ArrayList<OrderLine> getActiveOrderLinesByOrderId (String docType, int orderId) throws Exception {
        return getOrderLines(docType, orderId, Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
    }

    public int moveShoppingCartToFinalizedOrderByOrderId(int orderId) {
        return moveOrderLinesToOrderByOrderId(orderId, FINALIZED_ORDER_DOC_TYPE, SHOPPING_CART_DOC_TYPE);
    }

    public int getActiveShoppingCartLinesNumber() throws Exception {
        return getActiveOrderLinesNumber(SHOPPING_CART_DOC_TYPE, Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
    }

    public int getActiveWishListLinesNumber(){
        return getActiveOrderLinesNumber(WISH_LIST_DOC_TYPE, null);
    }

    public String removeProductFromWishList(int productId){
        try {
            int rowsAffected =
                    mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                            null,
                            "UPDATE ECOMMERCE_ORDER_LINE SET IS_ACTIVE = ? WHERE USER_ID = ? AND PRODUCT_ID = ? AND DOC_TYPE = ?",
                            new String[]{"N", String.valueOf(mUser.getServerUserId()), String.valueOf(productId), WISH_LIST_DOC_TYPE});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String updateOrderLine(OrderLine orderLine){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDER_LINE SET QTY_REQUESTED = ?, SALES_PRICE = ?, " +
                        " TAX_PERCENTAGE = ?, TAX_AMOUNT = ?, SUB_TOTAL_LINE = ?, TOTAL_LINE = ?, UPDATE_TIME = ? " +
                    " WHERE ECOMMERCE_ORDER_LINE_ID = ? AND USER_ID = ?",
                    new String[]{String.valueOf(orderLine.getQuantityOrdered()),
                            String.valueOf(orderLine.getProductPrice()), String.valueOf(orderLine.getProductTaxPercentage()),
                            String.valueOf(orderLine.getLineTaxAmount()), String.valueOf(orderLine.getSubTotalLineAmount()),
                            String.valueOf(orderLine.getTotalLineAmount()), DateFormat.getCurrentDateTimeSQLFormat(),
                            String.valueOf(orderLine.getId()), String.valueOf(mUser.getServerUserId())});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String deleteOrderLine(OrderLine orderLine){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDER_LINE SET IS_ACTIVE = ? WHERE ECOMMERCE_ORDER_LINE_ID = ? AND USER_ID = ?",
                    new String[]{"N", String.valueOf(orderLine.getId()), String.valueOf(mUser.getServerUserId())});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String clearWishList(){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDER_LINE SET IS_ACTIVE = ? WHERE USER_ID = ? AND DOC_TYPE = ?",
                    new String[]{"N", String.valueOf(mUser.getServerUserId()), WISH_LIST_DOC_TYPE});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public ArrayList<OrderLine> getOrderLinesBySalesOrderId(int salesOrderId){
        ArrayList<OrderLine> orderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT PRODUCT_ID, QTY_REQUESTED " +
                    " FROM ECOMMERCE_SALES_ORDER_LINE " +
                    " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                    " ORDER BY ECOMMERCE_SALES_ORDER_LINE_ID DESC",
                    new String[]{String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()),
                            SalesOrderLineDB.FINALIZED_SALES_ORDER_DOC_TYPE, "Y"},
                    null);
            if(c!=null){
                cursor:
                while(c.moveToNext()){
                    //se revisa si ya se encuentra ese articulo en el pedido y se suman las cantidades
                    for(OrderLine ol : orderLines){
                        if(ol.getProductId() == c.getInt(0)){
                            ol.setQuantityOrdered(ol.getQuantityOrdered() + c.getInt(1));
                            continue cursor;
                        }
                    }
                    OrderLine orderLine = new OrderLine();
                    orderLine.setProductId(c.getInt(0));
                    orderLine.setQuantityOrdered(c.getInt(1));
                    orderLines.add(orderLine);
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
        ProductDB productDB = new ProductDB(mContext, mUser);
        ArrayList<OrderLine> orderLinesToRemove = new ArrayList<>();
        for (OrderLine orderLine : orderLines) {
            orderLine.setProduct(productDB.getProductById(orderLine.getProductId()));
            if(orderLine.getProduct()!=null){
                OrderLineBR.fillOrderLine(orderLine.getQuantityOrdered(), orderLine.getProduct(), orderLine);
            }else{
                orderLinesToRemove.add(orderLine);
            }
        }
        if (!orderLinesToRemove.isEmpty()) {
            orderLines.removeAll(orderLinesToRemove);
        }
        return orderLines;
    }

    private String addOrderLine(OrderLine orderLine, String docType, Integer orderId) {
        try {
            orderLine.setId(UserTableMaxIdDB.getNewIdForTable(mContext, mUser, "ECOMMERCE_ORDER_LINE"));

            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "INSERT INTO ECOMMERCE_ORDER_LINE (ECOMMERCE_ORDER_LINE_ID, USER_ID, BUSINESS_PARTNER_ID, " +
                            " PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TAX_AMOUNT, SUB_TOTAL_LINE, TOTAL_LINE, DOC_TYPE, " +
                            " ECOMMERCE_ORDER_ID, CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(orderLine.getId()), String.valueOf(mUser.getServerUserId()),
                            String.valueOf(orderLine.getBusinessPartnerId()),
                            String.valueOf(orderLine.getProductId()), String.valueOf(orderLine.getQuantityOrdered()),
                            String.valueOf(orderLine.getProductPrice()), String.valueOf(orderLine.getProductTaxPercentage()),
                            String.valueOf(orderLine.getLineTaxAmount()), String.valueOf(orderLine.getSubTotalLineAmount()),
                            String.valueOf(orderLine.getTotalLineAmount()), docType,
                            (orderId==null ? null : String.valueOf(orderId)), DateFormat.getCurrentDateTimeSQLFormat(),
                            Utils.getAppVersionName(mContext), mUser.getUserName(), Utils.getMacAddress(mContext)});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    private ArrayList<OrderLine> getOrderLines(String docType, Integer orderId, Integer businessPartnerId) {
        ArrayList<OrderLine> orderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT ECOMMERCE_ORDER_LINE_ID, PRODUCT_ID, QTY_REQUESTED, " +
                            " SALES_PRICE, TAX_PERCENTAGE, TAX_AMOUNT, SUB_TOTAL_LINE, TOTAL_LINE " +
                    " FROM ECOMMERCE_ORDER_LINE " +
                    " WHERE " + (orderId!=null ? " ECOMMERCE_ORDER_ID = "+orderId+ " AND " : "") +
                            (businessPartnerId!=null ? " BUSINESS_PARTNER_ID = "+businessPartnerId+" AND " : "") +
                            " USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                    " ORDER BY CREATE_TIME DESC",
                    new String[]{String.valueOf(mUser.getServerUserId()), docType, "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    OrderLine orderLine = new OrderLine();
                    orderLine.setId(c.getInt(0));
                    orderLine.setProductId(c.getInt(1));
                    orderLine.setQuantityOrdered(c.getInt(2));
                    orderLine.setProductPrice(c.getDouble(3));
                    orderLine.setProductTaxPercentage(c.getDouble(4));
                    orderLine.setLineTaxAmount(c.getDouble(5));
                    orderLine.setSubTotalLineAmount(c.getDouble(6));
                    orderLine.setTotalLineAmount(c.getDouble(7));
                    orderLines.add(orderLine);
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
        ProductDB productDB = new ProductDB(mContext, mUser);
        ArrayList<OrderLine> orderLinesToRemove = new ArrayList<>();
        for (OrderLine orderLine : orderLines) {
            orderLine.setProduct(productDB.getProductById(orderLine.getProductId()));
            if(orderLine.getProduct()==null){
                if(docType.equals(WISH_LIST_DOC_TYPE)){
                    orderLinesToRemove.add(orderLine);
                }else{
                    Product product = new Product();
                    product.setId(orderLine.getProductId());
                    orderLine.setProduct(product);
                }
            }
        }
        if(!orderLinesToRemove.isEmpty()){
            orderLines.removeAll(orderLinesToRemove);
        }
        return orderLines;
    }

    private int getActiveOrderLinesNumber(String docType, Integer businessPartnerId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT COUNT(OL.PRODUCT_ID) " +
                    " FROM ECOMMERCE_ORDER_LINE OL " +
                            " INNER JOIN PRODUCT P ON P.PRODUCT_ID = OL.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                    " WHERE " + (businessPartnerId!=null ? " OL.BUSINESS_PARTNER_ID = "+businessPartnerId+" AND " : "") +
                            " OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ?",
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(mUser.getServerUserId()), docType, "Y"}, null);
            if(c!=null && c.moveToNext()){
                return c.getInt(0);
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

    private int moveOrderLinesToOrderByOrderId(int orderId, String newDocType, String currentDocType) {
        try {
            return mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDER_LINE SET ECOMMERCE_ORDER_ID = ?, UPDATE_TIME = ?, " +
                    " DOC_TYPE = ? WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? ",
                    new String[]{String.valueOf(orderId), DateFormat.getCurrentDateTimeSQLFormat(),
                            newDocType, String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), currentDocType, "Y"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public OrderLine getOrderLineFromShoppingCartByProductId(int productId){
        return getOrderLineByProductIdAndDocType(productId, SHOPPING_CART_DOC_TYPE);
    }

    private OrderLine getOrderLineByProductIdAndDocType(int productId, String docType){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT ECOMMERCE_ORDER_LINE_ID, PRODUCT_ID, QTY_REQUESTED, " +
                            " SALES_PRICE, TAX_PERCENTAGE, TAX_AMOUNT, SUB_TOTAL_LINE, TOTAL_LINE " +
                    " FROM ECOMMERCE_ORDER_LINE " +
                    " WHERE PRODUCT_ID = ? AND BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? ",
                    new String[]{String.valueOf(productId), String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), docType, "Y"}, null);
            if(c!=null && c.moveToNext()){
                OrderLine orderLine = new OrderLine();
                orderLine.setId(c.getInt(0));
                orderLine.setProductId(c.getInt(1));
                orderLine.setQuantityOrdered(c.getInt(2));
                orderLine.setProductPrice(c.getDouble(3));
                orderLine.setProductTaxPercentage(c.getDouble(4));
                orderLine.setLineTaxAmount(c.getDouble(5));
                orderLine.setSubTotalLineAmount(c.getDouble(6));
                orderLine.setTotalLineAmount(c.getDouble(7));
                c.close();
                orderLine.setProduct((new ProductDB(mContext, mUser)).getProductById(orderLine.getProductId()));
                if(orderLine.getProduct()!=null){
                    return orderLine;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(c != null && !c.isClosed()) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public int updateProductAvailabilitiesInWishList(){
        try {
            return mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDER_LINE SET UPDATE_TIME = ?, " +
                        " QTY_REQUESTED = (SELECT CASE WHEN SUM(AVAILABILITY) IS NULL THEN 0 ELSE SUM(AVAILABILITY) END AS AVAILABILITY " +
                                            " FROM PRODUCT_PRICE_AVAILABILITY WHERE PRODUCT_PRICE_ID = ? AND PRODUCT_ID = ECOMMERCE_ORDER_LINE.PRODUCT_ID AND IS_ACTIVE='Y') " +
                    " WHERE USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? ",
                    new String[]{DateFormat.getCurrentDateTimeSQLFormat(), "0",
                            String.valueOf(mUser.getServerUserId()), WISH_LIST_DOC_TYPE, "Y"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
