package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AlbertoSarco on 18/10/2016.
 */
public class OrderTrackingState implements Parcelable {

    private int id;
    private String title;
    private String iconResName;
    private String iconFileName;
    private int background_R_Color;
    private int background_G_Color;
    private int background_B_Color;
    private int border_R_Color;
    private int border_G_Color;
    private int border_B_Color;
    private int title_R_Color;
    private int title_G_Color;
    private int title_B_Color;
    private int icon_R_Color;
    private int icon_G_Color;
    private int icon_B_Color;
    private boolean isAlwaysVisible;

    public OrderTrackingState() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    private OrderTrackingState(Parcel in) {
        id = in.readInt();
        title = in.readString();
        iconResName = in.readString();
        iconFileName = in.readString();
        background_R_Color = in.readInt();
        background_G_Color = in.readInt();
        background_B_Color = in.readInt();
        border_R_Color = in.readInt();
        border_G_Color = in.readInt();
        border_B_Color = in.readInt();
        title_R_Color = in.readInt();
        title_G_Color = in.readInt();
        title_B_Color = in.readInt();
        icon_R_Color = in.readInt();
        icon_G_Color = in.readInt();
        icon_B_Color = in.readInt();
        isAlwaysVisible = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(iconResName);
        dest.writeString(iconFileName);
        dest.writeInt(background_R_Color);
        dest.writeInt(background_G_Color);
        dest.writeInt(background_B_Color);
        dest.writeInt(border_R_Color);
        dest.writeInt(border_G_Color);
        dest.writeInt(border_B_Color);
        dest.writeInt(title_R_Color);
        dest.writeInt(title_G_Color);
        dest.writeInt(title_B_Color);
        dest.writeInt(icon_R_Color);
        dest.writeInt(icon_G_Color);
        dest.writeInt(icon_B_Color);
        dest.writeByte((byte) (isAlwaysVisible ? 1 : 0));
    }

    public static final Creator<OrderTrackingState> CREATOR = new Creator<OrderTrackingState>() {
        @Override
        public OrderTrackingState createFromParcel(Parcel in) {
            return new OrderTrackingState(in);
        }

        @Override
        public OrderTrackingState[] newArray(int size) {
            return new OrderTrackingState[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconResName() {
        return iconResName;
    }

    public void setIconResName(String iconResName) {
        this.iconResName = iconResName;
    }

    public String getIconFileName() {
        return iconFileName;
    }

    public void setIconFileName(String iconFileName) {
        this.iconFileName = iconFileName;
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

    public int getBorder_R_Color() {
        return border_R_Color;
    }

    public void setBorder_R_Color(int border_R_Color) {
        this.border_R_Color = border_R_Color;
    }

    public int getBorder_G_Color() {
        return border_G_Color;
    }

    public void setBorder_G_Color(int border_G_Color) {
        this.border_G_Color = border_G_Color;
    }

    public int getBorder_B_Color() {
        return border_B_Color;
    }

    public void setBorder_B_Color(int border_B_Color) {
        this.border_B_Color = border_B_Color;
    }

    public int getTitle_R_Color() {
        return title_R_Color;
    }

    public void setTitle_R_Color(int title_R_Color) {
        this.title_R_Color = title_R_Color;
    }

    public int getTitle_G_Color() {
        return title_G_Color;
    }

    public void setTitle_G_Color(int title_G_Color) {
        this.title_G_Color = title_G_Color;
    }

    public int getTitle_B_Color() {
        return title_B_Color;
    }

    public void setTitle_B_Color(int title_B_Color) {
        this.title_B_Color = title_B_Color;
    }

    public int getIcon_R_Color() {
        return icon_R_Color;
    }

    public void setIcon_R_Color(int icon_R_Color) {
        this.icon_R_Color = icon_R_Color;
    }

    public int getIcon_G_Color() {
        return icon_G_Color;
    }

    public void setIcon_G_Color(int icon_G_Color) {
        this.icon_G_Color = icon_G_Color;
    }

    public int getIcon_B_Color() {
        return icon_B_Color;
    }

    public void setIcon_B_Color(int icon_B_Color) {
        this.icon_B_Color = icon_B_Color;
    }

    public boolean isAlwaysVisible() {
        return isAlwaysVisible;
    }

    public void setAlwaysVisible(boolean alwaysVisible) {
        isAlwaysVisible = alwaysVisible;
    }
}
