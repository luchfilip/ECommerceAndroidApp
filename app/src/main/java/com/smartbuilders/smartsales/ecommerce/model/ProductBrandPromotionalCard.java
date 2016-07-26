package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 9/6/2016.
 */
public class ProductBrandPromotionalCard extends Model implements Parcelable {

    private int productBrandId;
    private String imageFileName;
    private String promotionalText;
    private int background_R_Color;
    private int background_G_Color;
    private int background_B_Color;
    private int promotionalText_R_Color;
    private int promotionalText_G_Color;
    private int promotionalText_B_Color;

    public ProductBrandPromotionalCard(){

    }

    protected ProductBrandPromotionalCard(Parcel in) {
        super(in);
        productBrandId = in.readInt();
        imageFileName = in.readString();
        promotionalText = in.readString();
        background_R_Color = in.readInt();
        background_G_Color = in.readInt();
        background_B_Color = in.readInt();
        promotionalText_R_Color = in.readInt();
        promotionalText_G_Color = in.readInt();
        promotionalText_B_Color = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(productBrandId);
        dest.writeString(imageFileName);
        dest.writeString(promotionalText);
        dest.writeInt(background_R_Color);
        dest.writeInt(background_G_Color);
        dest.writeInt(background_B_Color);
        dest.writeInt(promotionalText_R_Color);
        dest.writeInt(promotionalText_G_Color);
        dest.writeInt(promotionalText_B_Color);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductBrandPromotionalCard> CREATOR = new Creator<ProductBrandPromotionalCard>() {
        @Override
        public ProductBrandPromotionalCard createFromParcel(Parcel in) {
            return new ProductBrandPromotionalCard(in);
        }

        @Override
        public ProductBrandPromotionalCard[] newArray(int size) {
            return new ProductBrandPromotionalCard[size];
        }
    };

    public int getProductBrandId() {
        return productBrandId;
    }

    public void setProductBrandId(int productBrandId) {
        this.productBrandId = productBrandId;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getPromotionalText() {
        return promotionalText;
    }

    public void setPromotionalText(String promotionalText) {
        this.promotionalText = promotionalText;
    }

    public int getBackground_R_Color() {
        return background_R_Color;
    }

    public void setBackground_R_Color(int background_R_Color) {
        this.background_R_Color = background_R_Color;
    }

    public int getBackground_G_Color() {
        return background_G_Color;
    }

    public void setBackground_G_Color(int background_G_Color) {
        this.background_G_Color = background_G_Color;
    }

    public int getBackground_B_Color() {
        return background_B_Color;
    }

    public void setBackground_B_Color(int background_B_Color) {
        this.background_B_Color = background_B_Color;
    }

    public int getPromotionalText_R_Color() {
        return promotionalText_R_Color;
    }

    public void setPromotionalText_R_Color(int promotionalText_R_Color) {
        this.promotionalText_R_Color = promotionalText_R_Color;
    }

    public int getPromotionalText_G_Color() {
        return promotionalText_G_Color;
    }

    public void setPromotionalText_G_Color(int promotionalText_G_Color) {
        this.promotionalText_G_Color = promotionalText_G_Color;
    }

    public int getPromotionalText_B_Color() {
        return promotionalText_B_Color;
    }

    public void setPromotionalText_B_Color(int promotionalText_B_Color) {
        this.promotionalText_B_Color = promotionalText_B_Color;
    }
}
