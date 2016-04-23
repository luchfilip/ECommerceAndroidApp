package com.smartbuilders.smartsales.ecommerceandroidapp.model;

/**
 * Created by Alberto on 23/3/2016.
 */
public class ProductBrand extends Model {

    private String name;
    private String description;
    private int imageId;

    public ProductBrand(){

    }

    public ProductBrand(int id, String name, String description){
        setId(id);
        setName(name);
        setDescription(description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
