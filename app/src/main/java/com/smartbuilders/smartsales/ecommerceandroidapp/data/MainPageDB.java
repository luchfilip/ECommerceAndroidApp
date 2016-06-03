package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrandsPromotionSection;

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

        ProductBrandsPromotionSection productBrandsPromotionSection = new ProductBrandsPromotionSection();
        ArrayList<ProductBrand> productBrands = new ArrayList<>();
        ProductBrand productBrand = new ProductBrand();
        productBrand.setId(1028613);
        productBrand.setDescription("Chesterwood - Siempre útil");
        productBrand.setImageId(R.drawable.logo_chesterwood);
        productBrands.add(productBrand);

        productBrand = new ProductBrand();
        productBrand.setId(1009934);
        productBrand.setDescription("3M - Ciencia. Aplicada a la vida");
        productBrand.setImageId(R.drawable.logo_3m);
        productBrands.add(productBrand);

        productBrand = new ProductBrand();
        productBrand.setId(1010089);
        productBrand.setDescription("Lorenzetti - Duchas Eléctricas");
        productBrand.setImageId(R.drawable.logo_lorenzetti);
        productBrands.add(productBrand);

        productBrand = new ProductBrand();
        productBrand.setId(1009431);
        productBrand.setDescription("Stanley - Security Solutions");
        productBrand.setImageId(R.drawable.logo_stanley);
        productBrands.add(productBrand);

        productBrand = new ProductBrand();
        productBrand.setId(1009565);
        productBrand.setDescription("Bellota - Tu mejor ayuda");
        productBrand.setImageId(R.drawable.logo_bellota);
        productBrands.add(productBrand);

        productBrand = new ProductBrand();
        productBrand.setId(1009669);
        productBrand.setDescription("Dewalt - Herramientas");
        productBrand.setImageId(R.drawable.logo_dewalt);
        productBrands.add(productBrand);


        productBrandsPromotionSection.setProductBrands(productBrands);
        mainPageList.add(productBrandsPromotionSection);

        mainPageList.addAll((new MainPageSectionDB(mContext, mUser)).getActiveMainPageSections());

        return mainPageList;
    }
}
