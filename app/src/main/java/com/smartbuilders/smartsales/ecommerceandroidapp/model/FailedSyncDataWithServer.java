package com.smartbuilders.smartsales.ecommerceandroidapp.model;

/**
 * Created by stein on 23/7/2016.
 */
public class FailedSyncDataWithServer {

    private int id;
    private String selection;
    private String selectionArgs;
    private int columnCount;

    public FailedSyncDataWithServer(int id, String selection, String selectionArgs, int columnCount){
        setId(id);
        setSelection(selection);
        setSelectionArgs(selectionArgs);
        setColumnCount(columnCount);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getSelectionArgs() {
        return selectionArgs;
    }

    public void setSelectionArgs(String selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
}
