package com.jasgcorp.ids.utils;

import java.net.URL;
import java.net.URLConnection;

/**
 * Created by stein on 24/7/2016.
 */
public class ServerUtilities {

    public static boolean isServerAvailableByAddress(String serverAddress){
        try {
            URLConnection conn = new URL(serverAddress).openConnection();
            conn.setConnectTimeout(1000*4);//4 seconds
            conn.connect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
