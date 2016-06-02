package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;

import com.jasgcorp.ids.model.User;

import java.util.ArrayList;

/**
 * Created by stein on 1/6/2016.
 */
public class MainPageDB {

    private Context mContext;
    private User mUser;

    public MainPageDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<Object> getMainPageList(){
        ArrayList<Object> mainPageList = new ArrayList<>();

        mainPageList.add(BannerSectionDB.getBannerSection());

        mainPageList.addAll((new MainPageSectionDB(mContext, mUser)).getActiveMainPageSections());

        return mainPageList;
    }
}
