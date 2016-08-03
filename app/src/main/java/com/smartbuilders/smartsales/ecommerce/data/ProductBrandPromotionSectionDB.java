package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.ProductBrandPromotionalCard;
import com.smartbuilders.smartsales.ecommerce.model.ProductBrandPromotionalSection;

/**
 * Created by stein on 9/6/2016.
 */
public class ProductBrandPromotionSectionDB {

    private Context mContext;
    private User mUser;

    public ProductBrandPromotionSectionDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ProductBrandPromotionalSection getProductBrandPromotionSection () {
        ProductBrandPromotionalSection productBrandPromotionalSection = new ProductBrandPromotionalSection();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "select BRAND_PROMOTIONAL_CARD_ID, BRAND_ID, IMAGE_FILE_NAME, PROMOTIONAL_TEXT, " +
                    " BACKGROUND_R_COLOR, BACKGROUND_G_COLOR, BACKGROUND_B_COLOR, " +
                    " PROMOTIONAL_TEXT_R_COLOR, PROMOTIONAL_TEXT_G_COLOR, PROMOTIONAL_TEXT_B_COLOR " +
                    " from BRAND_PROMOTIONAL_CARD where IS_ACTIVE = ?",
                    new String[]{"Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    ProductBrandPromotionalCard productBrandPromotionalCard = new ProductBrandPromotionalCard();
                    productBrandPromotionalCard.setId(c.getInt(0));
                    productBrandPromotionalCard.setProductBrandId(c.getInt(1));
                    productBrandPromotionalCard.setImageFileName(c.getString(2));
                    productBrandPromotionalCard.setPromotionalText(c.getString(3));
                    productBrandPromotionalCard.setBackground_R_Color(c.getString(4)==null ? -1 : c.getInt(4));
                    productBrandPromotionalCard.setBackground_G_Color(c.getString(5)==null ? -1 : c.getInt(5));
                    productBrandPromotionalCard.setBackground_B_Color(c.getString(6)==null ? -1 : c.getInt(6));
                    productBrandPromotionalCard.setPromotionalText_R_Color(c.getString(7)==null ? -1 : c.getInt(7));
                    productBrandPromotionalCard.setPromotionalText_G_Color(c.getString(8)==null ? -1 : c.getInt(8));
                    productBrandPromotionalCard.setPromotionalText_B_Color(c.getString(9)==null ? -1 : c.getInt(9));

                    productBrandPromotionalSection.getProductBrandPromotionalCards()
                            .add(productBrandPromotionalCard);
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
        return productBrandPromotionalSection;
    }

}
