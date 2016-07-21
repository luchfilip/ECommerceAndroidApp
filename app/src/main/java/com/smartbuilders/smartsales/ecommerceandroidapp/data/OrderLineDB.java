package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.OrderLineBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.session.Parameter;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

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
    private boolean mIsManagePriceInOrder;

    public OrderLineDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
        this.mIsManagePriceInOrder = Parameter.isManagePriceInOrder(mContext, mUser);
    }

    public String addOrderLineToFinalizedOrder(OrderLine orderLine, int orderId){
        return addOrderLine(orderLine.getProductId(), orderLine.getQuantityOrdered(),
                orderLine.getPrice(), orderLine.getTaxPercentage(), FINALIZED_ORDER_DOC_TYPE, orderId);
    }

    public String addProductToShoppingCart(Product product, int qtyRequested){
        if (mIsManagePriceInOrder) {
            return addOrderLine(product.getId(), qtyRequested, product.getDefaultProductPriceAvailability().getPrice(),
                    product.getProductTax().getPercentage(), SHOPPING_CART_DOC_TYPE, null);
        } else {
            return addOrderLine(product.getId(), qtyRequested, 0, 0, SHOPPING_CART_DOC_TYPE, null);
        }
    }

    public String addProductToWishList(int productId){
        return addOrderLine(productId, 0, 0, 0, WISH_LIST_DOC_TYPE, null);
    }

    public ArrayList<OrderLine> getActiveOrderLinesFromShoppingCart(){
        return getOrderLines(SHOPPING_CART_DOC_TYPE, null);
    }

    public ArrayList<OrderLine> getWishList(){
        return getOrderLines(WISH_LIST_DOC_TYPE, null);
    }

    public ArrayList<OrderLine> getActiveFinalizedOrderLinesByOrderId(int orderId){
        return getActiveOrderLinesByOrderId(FINALIZED_ORDER_DOC_TYPE, orderId);
    }

    private ArrayList<OrderLine> getActiveOrderLinesByOrderId (String docType, int orderId) {
        return getOrderLines(docType, orderId);
    }

    public int moveShoppingCartToFinalizedOrderByOrderId(int orderId) {
        return moveOrderLinesToOrderByOrderId(orderId, FINALIZED_ORDER_DOC_TYPE, SHOPPING_CART_DOC_TYPE);
    }

    public int getActiveShoppingCartLinesNumber(){
        return getActiveOrderLinesNumber(SHOPPING_CART_DOC_TYPE);
    }

    public String removeProductFromWishList(int productId){
        try {
            int rowsAffected =
                    mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null,
                            "DELETE FROM ECOMMERCE_ORDER_LINE WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND PRODUCT_ID = ? AND DOC_TYPE = ?",
                            new String[]{String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                                    String.valueOf(mUser.getServerUserId()), String.valueOf(productId), WISH_LIST_DOC_TYPE});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String moveOrderLineToShoppingCart(OrderLine orderLine, int qtyRequested){
        try {
            if (mIsManagePriceInOrder) {
                orderLine.setPrice(orderLine.getProduct().getDefaultProductPriceAvailability().getPrice());
                orderLine.setTaxPercentage(orderLine.getProduct().getProductTax().getPercentage());
            }
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null,
                            "UPDATE ECOMMERCE_ORDER_LINE SET DOC_TYPE = ?, QTY_REQUESTED = ?, " +
                                " SALES_PRICE = ?, TAX_PERCENTAGE = ?, TOTAL_LINE = ?, UPDATE_TIME = ? " +
                            " WHERE ECOMMERCE_ORDER_LINE_ID = ? AND USER_ID = ? AND DOC_TYPE=?",
                            new String[]{SHOPPING_CART_DOC_TYPE, String.valueOf(qtyRequested),
                                    String.valueOf(orderLine.getPrice()),
                                    String.valueOf(orderLine.getTaxPercentage()),
                                    String.valueOf(OrderLineBR.getTotalLine(orderLine)), "datetime('now')",
                                    String.valueOf(orderLine.getId()), String.valueOf(mUser.getServerUserId()),
                                    WISH_LIST_DOC_TYPE});
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
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDER_LINE SET QTY_REQUESTED = ?, SALES_PRICE = ?, " +
                        " TAX_PERCENTAGE = ?, TOTAL_LINE = ?, UPDATE_TIME = ? " +
                    " WHERE ECOMMERCE_ORDER_LINE_ID = ? AND USER_ID = ?",
                    new String[]{String.valueOf(orderLine.getQuantityOrdered()),
                    String.valueOf(orderLine.getPrice()), String.valueOf(orderLine.getTaxPercentage()),
                    String.valueOf(orderLine.getTotalLineAmount()), "datetime('now')",
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
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "DELETE FROM ECOMMERCE_ORDER_LINE WHERE ECOMMERCE_ORDER_LINE_ID = ? AND USER_ID = ?",
                    new String[]{String.valueOf(orderLine.getId()), String.valueOf(mUser.getServerUserId())});
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
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null, "DELETE FROM ECOMMERCE_ORDER_LINE WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ?",
                    new String[]{String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), WISH_LIST_DOC_TYPE});
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
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT PRODUCT_ID, QTY_REQUESTED, " +
                        " SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE " +
                    " FROM ECOMMERCE_SALES_ORDER_LINE " +
                    " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                    " ORDER BY ECOMMERCE_SALES_ORDER_LINE_ID DESC",
                    new String[]{String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()),
                            SalesOrderLineDB.FINALIZED_SALES_ORDER_DOC_TYPE, "Y"},
                    null);
            if(c!=null){
                ProductDB productDB = new ProductDB(mContext, mUser);
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
                    orderLine.setProduct(productDB.getProductById(orderLine.getProductId()));
                    if(orderLine.getProduct()!=null){
                        if (mIsManagePriceInOrder) {
                            orderLine.setPrice(orderLine.getProduct().getDefaultProductPriceAvailability().getPrice());
                            orderLine.setTaxPercentage(orderLine.getProduct().getProductTax().getPercentage());
                        }
                        orderLine.setTotalLineAmount(OrderLineBR.getTotalLine(orderLine));
                        orderLines.add(orderLine);
                    }
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
        return orderLines;
    }

    private String addOrderLine(int productId, int qtyRequested, double productPrice,
                                double productTaxPercentage, String docType, Integer orderId) {
        try {
            OrderLine ol = new OrderLine();
            ol.setId(getMaxOrderLineId() + 1);
            ol.setProductId(productId);
            ol.setPrice(productPrice);
            ol.setQuantityOrdered(qtyRequested);
            ol.setTaxPercentage(productTaxPercentage);
            ol.setTotalLineAmount(OrderLineBR.getTotalLine(ol));
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "INSERT INTO ECOMMERCE_ORDER_LINE (ECOMMERCE_ORDER_LINE_ID, USER_ID, BUSINESS_PARTNER_ID, " +
                        " PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE, DOC_TYPE, " +
                        " ECOMMERCE_ORDER_ID, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(ol.getId()), String.valueOf(mUser.getServerUserId()),
                            String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(ol.getProductId()), String.valueOf(ol.getQuantityOrdered()),
                            String.valueOf(ol.getPrice()), String.valueOf(ol.getTaxPercentage()),
                            String.valueOf(ol.getTotalLineAmount()), docType,
                            (orderId==null ? null : String.valueOf(orderId)),
                            Utils.getAppVersionName(mContext), mUser.getUserName(), Utils.getMacAddress(mContext)});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    private ArrayList<OrderLine> getOrderLines(String docType, Integer orderId) {
        ArrayList<OrderLine> orderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT ECOMMERCE_ORDER_LINE_ID, PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE " +
                    " FROM ECOMMERCE_ORDER_LINE " +
                    " WHERE " + (orderId!=null ? " ECOMMERCE_ORDER_ID = "+orderId+ " AND " : "") +
                        " BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                    " ORDER BY CREATE_TIME DESC",
                    new String[]{String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), docType, "Y"}, null);
            if(c!=null){
                ProductDB productDB = new ProductDB(mContext, mUser);
                while(c.moveToNext()){
                    OrderLine orderLine = new OrderLine();
                    orderLine.setId(c.getInt(0));
                    orderLine.setProductId(c.getInt(1));
                    orderLine.setQuantityOrdered(c.getInt(2));
                    orderLine.setPrice(c.getDouble(3));
                    orderLine.setTaxPercentage(c.getDouble(4));
                    orderLine.setTotalLineAmount(c.getDouble(5));
                    orderLine.setProduct(productDB.getProductById(orderLine.getProductId()));
                    if(orderLine.getProduct()!=null){
                        orderLines.add(orderLine);
                    }
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
        return orderLines;
    }

    private int getActiveOrderLinesNumber(String docType) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT COUNT(*) FROM ECOMMERCE_ORDER_LINE " +
                    " WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE=? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), docType, "Y"}, null);
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
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDER_LINE SET ECOMMERCE_ORDER_ID = ?, UPDATE_TIME = ?, " +
                    " DOC_TYPE = ? WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? ",
                    new String[]{String.valueOf(orderId), "datetime('now')", newDocType,
                            String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), currentDocType, "Y"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isProductInWishList(int productId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "SELECT COUNT(*) FROM ECOMMERCE_ORDER_LINE " +
                    " WHERE PRODUCT_ID=? AND BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE=? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(productId), String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), WISH_LIST_DOC_TYPE, "Y"}, null);
            if(c!=null && c.moveToNext()){
                return c.getInt(0)>0;
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
        return false;
    }

    public OrderLine getOrderLineFromShoppingCartByProductId(int productId){
        return getOrderLineByProductIdAndDocType(productId, SHOPPING_CART_DOC_TYPE);
    }

    public OrderLine getOrderLineFromWishListByProductId(int productId){
        return getOrderLineByProductIdAndDocType(productId, WISH_LIST_DOC_TYPE);
    }

    private OrderLine getOrderLineByProductIdAndDocType(int productId, String docType){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT ECOMMERCE_ORDER_LINE_ID, PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE " +
                    " FROM ECOMMERCE_ORDER_LINE " +
                    " WHERE PRODUCT_ID = ? AND BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                    " ORDER BY CREATE_TIME DESC",
                    new String[]{String.valueOf(productId), String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), docType, "Y"}, null);
            if(c!=null && c.moveToNext()){
                OrderLine orderLine = new OrderLine();
                orderLine.setId(c.getInt(0));
                orderLine.setProductId(c.getInt(1));
                orderLine.setQuantityOrdered(c.getInt(2));
                orderLine.setPrice(c.getDouble(3));
                orderLine.setTaxPercentage(c.getDouble(4));
                orderLine.setTotalLineAmount(c.getDouble(5));
                orderLine.setProduct((new ProductDB(mContext, mUser)).getProductById(orderLine.getProductId()));
                if(orderLine.getProduct()!=null){
                    return orderLine;
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
        return null;
    }

    private int getMaxOrderLineId(){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT MAX(ECOMMERCE_ORDER_LINE_ID) FROM ECOMMERCE_ORDER_LINE WHERE USER_ID = ?",
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
