package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alberto on 26/3/2016.
 */
public class Utils {

    public static ArrayList<Product> getGenericProductsList(int iterations){
        ArrayList<Product> products = new ArrayList<Product>();
        Product p;
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("Nombre del grupo");
        ProductSubCategory productSubCategory = new ProductSubCategory();
        productSubCategory.setName("Nombre de la partida");
        for(int i = 0; i<iterations; i++) {
            p = new Product();
            p.setName("Bomba 1/2 hp periferica pedrollo");
            p.setImageId(R.drawable.product1);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Capacitador con terminal p/bomba 1/2hp");
            p.setImageId(R.drawable.product2);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Capacitor 25uf semilic");
            p.setImageId(R.drawable.product3);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Cargador de aire 100gl tm");
            p.setImageId(R.drawable.product4);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Manometro 0-90psi semilic");
            p.setImageId(R.drawable.product5);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Mini presostato 20-40 semilic");
            p.setImageId(R.drawable.product6);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Presostato 20-40 semilic");
            p.setImageId(R.drawable.product7);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Rolinera para bomba 1/2hp");
            p.setImageId(R.drawable.product8);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Aspersor pico blanco 3/16\" agroinplast");
            p.setImageId(R.drawable.product9);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Aspersor oscilante bv");
            p.setImageId(R.drawable.product10);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Aspersor plastic triple bv");
            p.setImageId(R.drawable.product11);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);

            p = new Product();
            p.setName("Aspersor plastico triple chesterwood");
            p.setImageId(R.drawable.product12);
            p.setProductCategory(productCategory);
            p.setProductSubCategory(productSubCategory);
            products.add(p);
        }
        return products;
    }

    public static ArrayList<ProductCategory> getAllProductCategories(){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductCategory  pc = new ProductCategory();
        pc.setId(0);
        pc.setName("HERRAMIENTAS MANUALES Y ELECTRICAS");
        pc.setDescription("");
        pc.setInternalCode("Grupo 0");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);

        pc = new ProductCategory();
        pc.setId(1);
        pc.setName("FERRETERIA AGRICOLA");
        pc.setDescription("");
        pc.setInternalCode("Grupo 1");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);

        pc = new ProductCategory();
        pc.setId(2);
        pc.setName("RECUBRIMIENTOS, ADHESIVOS Y ABRASIVOS");
        pc.setDescription("");
        pc.setInternalCode("Grupo 2");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);

        pc = new ProductCategory();
        pc.setId(3);
        pc.setName("SEGURIDAD INDUSTRIAL Y AUTOMOTRIZ");
        pc.setDescription("");
        pc.setInternalCode("Grupo 3");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);

        pc = new ProductCategory();
        pc.setId(4);
        pc.setName("MALLAS Y ALAMBRES");
        pc.setDescription("");
        pc.setInternalCode("Grupo 4");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);

        pc = new ProductCategory();
        pc.setId(5);
        pc.setName("SANITARIOS");
        pc.setDescription("");
        pc.setInternalCode("Grupo 5");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);

        pc = new ProductCategory();
        pc.setId(6);
        pc.setName("CERRADURAS Y CANDADOS");
        pc.setDescription("");
        pc.setInternalCode("Grupo 6");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);

        pc = new ProductCategory();
        pc.setId(7);
        pc.setName("ELECTRICIDAD E ILUMINACION");
        pc.setDescription("");
        pc.setInternalCode("Grupo 7");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);

        pc = new ProductCategory();
        pc.setId(8);
        pc.setName("GRIFERIAS Y VALVULAS");
        pc.setDescription("");
        pc.setInternalCode("Grupo 8");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);

        pc = new ProductCategory();
        pc.setId(9);
        pc.setName("ARTICULOS PESADOS Y CONSTRUCCION");
        pc.setDescription("");
        pc.setInternalCode("Grupo 9");
        pc.setImageId(R.mipmap.ic_launcher);
        categories.add(pc);
        return categories;
    }

    public static ArrayList<ProductCategory> getSubCategoriesByCategoryId(int productCategoryId){
        if(productCategoryId == 0){
            return getCategoriasGrupo0(productCategoryId);
        }else if(productCategoryId == 1){
            return getCategoriasGrupo1(productCategoryId);
        }else if(productCategoryId == 2){
            return getCategoriasGrupo2(productCategoryId);
        }else if(productCategoryId == 3){
            return getCategoriasGrupo3(productCategoryId);
        }else if(productCategoryId == 4){
            return getCategoriasGrupo4(productCategoryId);
        }else if(productCategoryId == 5){
            return getCategoriasGrupo5(productCategoryId);
        }else if(productCategoryId == 6){
            return getCategoriasGrupo6(productCategoryId);
        }else if(productCategoryId == 7){
            return getCategoriasGrupo7(productCategoryId);
        }else if(productCategoryId == 8){
            return getCategoriasGrupo8(productCategoryId);
        }else if(productCategoryId == 9){
            return getCategoriasGrupo9(productCategoryId);
        }
        return new ArrayList<ProductCategory>();
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo0(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1002873, "COMBOS GRUPO 0", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002874, "ACEITERAS", "", "2", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002875, "ALICATES", "", "10", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002876, "JGO. DE HERRAMIENTAS", "", "12", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002877, "PROBADORES DE VOLTAJE / MULTITESTER", "", "20", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002878, "AUTOCLETS - RATCHETS - EXTENSIONES", "", "30", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002879, "BROCHAS - ACCESORIOS DE PINTURA", "", "50", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002924, "MAQUINARIA PESADA", "", "105", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002880, "CINCELES", "", "130", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002925, "CINTA METRICA", "", "140", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002881, "CINTA PASACABLE", "", "141", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002882, "CIZALLAS", "", "150", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002883, "CORTAPORCELANA - CORTATUBO - CORTAVIDRIO", "", "180", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002884, "CUCHARAS DE ALBAÑIL", "", "190", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002885, "CUCHILLAS - NAVAJAS", "", "200", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002886, "DADOS", "", "208", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002887, "CHUPONES DE GOMA PARA CAÑERIA", "", "220", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002888, "DESTORNILLADORES", "", "230", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002889, "ENGRASADORAS", "", "260", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002890, "MEDICION", "", "280", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002891, "ESMERILES", "", "290", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002892, "ESPATULAS", "", "300", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002893, "KIT P/LATONERO", "", "320", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002894, "FORMONES - GUBIAS", "", "340", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002895, "LIJADORAS - TROMPOS", "", "365", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002896, "LIMAS", "", "370", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002897, "LLAVES COMB.- AJUST.- TUBO - ALLEN - CRUZ", "", "380", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004510, "PINTURAS Y ACABADOS PINECO", "", "393", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002898, "MARTILLOS - MACETAS - MAZOS - MANDARRIAS", "", "410", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002899, "MECHAS - MACHOS PARA ROSCA", "", "420", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002900, "NIVELES Y CALIBRADORES", "", "430", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002901, "PALUSTRAS", "", "440", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002902, "PIQUETAS ALBAÑIL", "", "450", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002903, "COMPRESORES - PISTOLAS - ACCESORIOS", "", "460", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002904, "PLOMADA PARA ALBAÑIL", "", "470", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002905, "PORTA-ELECTRODOS", "", "480", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002906, "PINZA TIERRA SOLDAR", "", "485", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002907, "PRENSAS", "", "490", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002908, "REMACHES - REMACHADORAS", "", "510", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002909, "RIBETEADOR", "", "540", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002910, "SALPICADORAS Y REPUESTOS", "", "550", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002911, "SEGUETAS - SIERRA COPA - HOJAS P/ CALADORA", "", "560", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002912, "ARCOS P/SEGUETA", "", "561", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002913, "SERRUCHOS", "", "570", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002914, "TRONZADORAS - INGLETEADORAS - SIERRAS", "", "580", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002915, "MAQ.SOLDAR - EQUIP.OXICORTE - CAUTIN - ESTAÑO", "", "590", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002916, "SOPLETES", "", "600", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002917, "TALADROS", "", "610", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002918, "MANDRIL - LLAVE MANDRIL - KIT CERRAD.- JGOS.VARIOS", "", "611", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002919, "TENAZAS", "", "620", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002920, "TERRAJAS - DADOS PARA TERRAJA", "", "630", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002921, "TIJERAS HOJALATERA - AVIACION", "", "640", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002922, "TIRALINEAS - TIZAS", "", "650", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004188, "DEMOLEDORES", "", "700", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002923, "EXHIBIDORES,MATERIAL POP, GENERAL MERCHANDISING.", "", "998", 0);
        categories.add(pc);

        return categories;
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo1(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1002926, "COMBOS GRUPO 1", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002927, "BARRAS PARA HOYAR", "", "10", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002955, "HIDROLAVADORAS", "", "19", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002928, "BOMBAS", "", "20", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002949, "HIDRONEUMATICOS", "", "21", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002950, "MOTOBOMBAS", "", "22", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002956, "GENERADORES", "", "24", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002929, "DESMALEZADORA", "", "30", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002951, "CORTAGRAMA Y SOPLADORA", "", "31", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002952, "MOTOSIERRA", "", "32", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002953, "ACC. PARA DESM/CORTG/MOTOSIERRA", "", "33", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002930, "CABOS DE MADERA", "", "40", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004082, "MOTORES", "", "50", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004304, "ALMACENAMIENTO AGRICOLA", "", "70", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004298, "BEBEDEROS/COMEDEROS", "", "80", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004183, "REPELENTES", "", "90", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002931, "CAVADORAS", "", "120", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002945, "ESCOBAS DE JARDIN Y RASTRILLOS", "", "130", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002932, "CHICURAS", "", "140", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002946, "CONEXIONES Y ACCESORIOS DE RIEGO", "", "141", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002954, "ACC DE RIEGO PARA JARDIN", "", "142", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002948, "MANGUERAS DE RIEGO Y JARDIN", "", "150", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002933, "ESCARDILLA", "", "160", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004508, "EQUIPOS PARA SIEMBRA", "", "170", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004505, "MATEROS", "", "180", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002947, "TIJERAS PARA JARDIN", "", "190", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002934, "KIT JARDINERIA", "", "200", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002935, "FUMIGADORAS,ROCIADORES SPRAY", "", "220", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002936, "HACHAS", "", "260", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002937, "CUCHILLA GUADAÑADORA", "", "270", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002943, "CUCHILLOS", "", "290", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002938, "MACHETES", "", "300", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002939, "PALAS", "", "340", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002940, "PALINES", "", "360", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002941, "PICOS (PUNTA Y PALA - GAMELOTEROS)", "", "400", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002942, "ACCESORIOS PARA CERCAS TIPO CICLON", "", "440", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002957, "EXHIBIDORES G1", "", "998", 0);
        categories.add(pc);
        return categories;
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo2(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1002958, "COMBOS GRUPO 2", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002959, "BOTELLON Y ACC DE SURTIDOR DE AGUA", "", "4", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002960, "CADENAS P/PERROS Y MATEROS, PECHERAS Y COLLARES", "", "8", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002961, "COCINAS Y REGULADORES", "", "11", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002962, "MOLINO PARA MAIZ", "", "12", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003982, "PLASTICO STRETCH", "", "30", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002963, "ESCALERAS D/ALUMINIO TIJERA", "", "40", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003880, "SILLAS, BANQUETAS Y MESAS", "", "50", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002964, "TRAMPAS Y COLA PARA RATAS", "", "92", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002989, "LIMPIADOR DE GRIFERIA", "", "120", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002965, "CEPILLOS P/BARRER", "", "129", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002966, "MANGUERAS", "", "140", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002967, "ANZUELOS", "", "205", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002968, "BALINES PARA FLOWERS", "", "208", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002969, "PLOMO EN LAMINA Y DE PESCA", "", "233", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002970, "NYLON DE PESCA", "", "235", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002971, "CARRETE PLASTICO", "", "236", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002972, "LIJA, ROLLOS, FIBRODISCOS", "", "320", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002987, "DISCOS DE CORTE Y DESBASTE", "", "321", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002988, "CEPILLOS DE COPA, CIRCULARES Y DE USO MANUAL", "", "322", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002973, "LANA DE ACERO", "", "324", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002974, "TIRROS Y CINTAS", "", "330", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002975, "ACEITE P/ROSCA, SILICONAS", "", "352", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002976, "PEGA METYLAN Y COLA BLANCA", "", "365", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002977, "MASTIQUE Y PASTA PROF.", "", "370", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002978, "PEGAS P/TUBERIAS PVC", "", "375", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002979, "CEMENTOS DE CONTACTO Y CREOLINA", "", "380", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002980, "PEGAS EPOXICAS E INSTANTANEAS", "", "385", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002981, "PINT. SAPOLIN, SPRAY, FONDOS Y MASILLAS", "", "390", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002982, "PINTURAS Y ACABADOS", "", "391", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003778, "PINTURAS Y ACABADOS QUIMICOLOR", "", "392", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004610, "PINTURAS Y ACABADOS PINECO", "", "393", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002983, "TEFLONES, CINTAS ELECTRICAS Y DOBLE FAZ", "", "395", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004189, "REPUESTOS", "", "400", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004190, "CAMPING", "", "450", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004299, "PRODUCTOS DE LIMPIEZA", "", "500", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004300, "SOLVENTES Y REMOVEDOR", "", "600", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004302, "ACCESORIO DE LIMPIEZA", "", "700", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002984, "PESOS, BALANZAS Y BASCULAS", "", "960", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002985, "TERMOS Y GRIFOS P/TERMOS", "", "990", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002986, "EXHIBIDORES Y POP", "", "998", 0);
        categories.add(pc);
        return categories;
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo3(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1002990, "COMBOS GRUPO 3", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003013, "ABRAZADERA DE METAL", "", "2", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003011, "BIDONES Y EMBUDOS", "", "4", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002991, "GUARAL SINTETICO Y CABUYA BALER TWINE", "", "10", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002992, "CABUYA SISAL", "", "11", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003010, "LINTERNAS Y PILAS", "", "18", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002993, "LAMPARA AUXILIARES, BORNES DE BATERIAS", "", "20", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003006, "EQUIPOS AUXILIARES DE MANTENIMIENTO AUTOMOTRIZ", "", "29", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002994, "DRIZAS DE NYLON", "", "30", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002995, "ENCERADOS LONA VERDE(BAJA TENACIDAD)", "", "40", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002996, "ENCERADOS PLASTICOS(ALTA TENACIDAD)", "", "41", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003002, "CINTAS  RATCHET Y GANCHO", "", "43", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002997, "PLASTICO(TRANSPARENTE Y NEGRO)", "", "45", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004185, "PISOS ALTO TRAFICO", "", "46", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003578, "SACOS", "", "50", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002998, "ESTOPAS,TRAPOS Y ALGODON", "", "60", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003012, "CAJAS DE HERRAMIENTAS Y ORGANIZADORES", "", "70", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1002999, "IMPERMEABLES,CORDONES ELAST.", "", "80", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003000, "MECATE (TODO TIPO)", "", "100", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003001, "CARBONES PARA EQUIPOS Y HERRAMIENTAS", "", "150", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003022, "PROTEC. VISUAL, RESP. SOP. ESPALDA Y ESLINGA", "", "256", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004506, "UNIFORMES", "", "257", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003016, "BOTAS DE SEGURIDAD", "", "258", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003018, "CASCOS", "", "265", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003019, "PROTECCION  AUDITIVA", "", "267", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003020, "GUANTES", "", "270", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003021, "SEÑALES", "", "300", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003015, "SEGURIDAD VIAL", "", "349", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003004, "GATOS DE BOTELLA, GATOS CAIMAN, GATO PARA RUSTICOS", "", "350", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003014, "EXTINTORES", "", "351", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003005, "QUIMICOS P/ MANTENIMIENTO AUTOMOTRIZ", "", "352", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003008, "GRASAS Y LUBRICANTES", "", "360", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003017, "PAPEL AUTOMOTRIZ", "", "370", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003007, "LLAVES DE BUJIAS, LLAVES DE CRUZ, LLAVES CAMION", "", "380", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003003, "SEÑORITAS Y WINCHES", "", "420", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004294, "BICICLETAS Y ACCESORIOS", "", "500", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004301, "PISCINAS Y ACCESORIOS", "", "505", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004504, "MESAS DE JUEGO", "", "510", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003009, "EXHIBIDORES, MATERIAL POP Y RELACIONADOS", "", "998", 0);
        categories.add(pc);
        return categories;
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo4(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1003122, "ALAMBRE DE PUAS", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003123, "ALAMBRE LISO", "", "2", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003148, "CONCERTINAS", "", "3", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003124, "AMARRE PARA TECHOS", "", "6", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003881, "ARMELLAS", "", "10", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003125, "BARRAS ROSCADAS", "", "15", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003126, "TUERCAS", "", "16", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003127, "ARANDELAS", "", "17", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003128, "CLAVOS EN GENERAL", "", "20", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003129, "CADENAS GALVANIZADAS", "", "22", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003130, "HERRADURAS", "", "30", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003131, "ACCS. DE CERCO ELECTRICO", "", "35", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003132, "ENERGIZADOR DE CERCO ELECTRICO", "", "36", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003133, "GANCHOS PARA TECHO", "", "40", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003134, "GRAPAS", "", "50", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003135, "MALLA ALFAJOL", "", "80", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003136, "RAMPLUGS", "", "90", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003137, "MALLA CEDAZO", "", "100", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003138, "MALLA GALLINERO", "", "120", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003139, "MALLAS PAJARERA, POLLITO Y UNIMALLAS", "", "140", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003140, "MALLA MOSQUITERO", "", "160", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003141, "MALLA NASA", "", "180", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003274, "MALLA PARA PISOS", "", "200", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003143, "MALLA RIPLEX, ESQUINEROS", "", "220", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003144, "TORNILLOS", "", "300", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003151, "TORNILLOS AUTORROSCANTES CATO", "", "301", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003150, "ESTANTILLO", "", "440", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004303, "REFIRGERANCION COMERCIAL-AUTOMOTRIZ Y ACCESORIOS", "", "500", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003147, "FLETES", "", "888", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003146, "Exhibidores merchandising", "", "998", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003145, "CARGOS A CLIENTES 4", "", "999", 0);
        categories.add(pc);
        return categories;
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo5(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1003023, "COMBOS GRUPO 5", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003024, "ACCES. DE BAÑO METALICOS", "", "3", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003031, "ASIENTOS", "", "8", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003027, "FILTROS", "", "35", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003028, "CALENTADORES Y RESISTENCIAS", "", "60", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003029, "FREGADEROS", "", "90", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003030, "GABINETES Y ESPEJOS", "", "100", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003034, "SEGURIDAD PARA BAÑOS", "", "150", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003025, "ACCESORIOS DE BAÑO CERAMICOS", "", "200", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003026, "SANITARIOS", "", "203", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003033, "BLOQUES DE VIDRIO", "", "300", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003879, "AIRES ACONDICIONADOS", "", "400", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004083, "NEVERAS Y CONGELADORES", "", "401", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004084, "DISPENSADORES DE AGUA", "", "402", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004186, "REVESTIMIENTO DE PAREDES", "", "450", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004187, "REVESTIMIENTO DE PISOS", "", "451", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003032, "EXHIBIDORES", "", "998", 0);
        categories.add(pc);
        return categories;
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo6(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1003035, "COMBOS GRUPO 6", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003036, "BISAGRAS DE TODO TIPO", "", "10", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003037, "CANDADOS", "", "20", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003038, "CERRADURAS SEGURIDAD Y CAJAS FUERTES", "", "29", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003039, "CERRADURAS Y CILINDROS", "", "30", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003040, "CERRADURAS GATER", "", "31", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003041, "CERRADURAS DE POMO Y MANILLA", "", "32", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003060, "MANILLAS P/ CERRADURA DE EMBUTIR", "", "33", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003042, "BLANCOS DE LLAVE", "", "35", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003043, "CIERRAPUERTAS PROTECTORES CILINDROS", "", "40", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003044, "CREMALLERAS", "", "50", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003045, "SOPORTES PARA CORTINAS", "", "60", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003046, "ORGANIZADORES/AEROCLOSET", "", "70", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003047, "SOPORTES T/ESPAÑOL", "", "90", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003048, "ACCESORIOS P/PUERTA", "", "95", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003059, "TUBO CORTINERO", "", "110", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003049, "PASADORES P/PUERTAS", "", "120", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003050, "ALDABAS/PORTACANDADOS", "", "130", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003051, "PIE DE AMIGOS", "", "140", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003052, "BUZON PARA CORREO", "", "150", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003053, "CORREDERAS", "", "160", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003054, "ROLDANAS, POLEAS Y GARRUCHAS", "", "170", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003055, "GARRUCHAS P/PUERTAS", "", "171", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003056, "GANCHOS P/COLGAR", "", "200", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003062, "TIRADORES", "", "210", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003475, "PUERTAS Y REJAS", "", "300", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003061, "ACCESORIOS PARA TRABAJOS DE HERRERIA", "", "301", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004611, "MOTOR ELECTRICO P/PORTON Y ACCESORIOS", "", "305", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003476, "PUERTAS", "", "400", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003058, "EXIBIDORES", "", "998", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003057, "DEBITOS A CLIENTES 6", "", "999", 0);
        categories.add(pc);
        return categories;
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo7(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1003063, "COMBOS VARIOS", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003064, "ABRAZADERAS, AMARRES Y GRAPAS", "", "2", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003090, "ACCESORIOS DE VIDEO Y AUDIO", "", "5", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003065, "CABLES", "", "8", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004613, "ALAMBRE ESMALTADO", "", "9", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003066, "ANILLOS Y CONECTORES", "", "15", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003067, "BALASTOS", "", "25", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003068, "BOMBILLOS BEST VALUE", "", "38", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003069, "BOMBILLERIA", "", "40", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003070, "LAMPARAS", "", "41", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003678, "LAMPARAS LED", "", "42", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004184, "BOMBILLOS LED ", "", "43", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003071, "CABEZOTES", "", "45", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003072, "CAJETINES PARA CONEXIONES", "", "60", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003073, "BARRAS,TERMINALES ELECTRICOS", "", "62", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003094, "GUAYAS Y ACCESORIOS", "", "63", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003074, "CUCHILLAS DE PORCELANA", "", "70", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003075, "ENCHUFES", "", "80", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003076, "EXTENSIONES Y REGLETAS", "", "90", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003095, "VENTILADORES Y EXTRACTORES", "", "96", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003077, "STARTER, RECEPTACULOS", "", "110", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003078, "SOCATES VARIOS.", "", "130", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003079, "TOMACORRIENTES", "", "160", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003080, "BENJAMIN", "", "170", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003081, "TIMBRES DOMESTICOS.", "", "171", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003082, "TAPAS P/TOMAS E INTERRUPTORES", "", "180", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003096, "INTERRUPTORES Y TOMAS ECONOMICAS", "", "300", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003092, "SIEMENS INTERRUPTORES Y TOMAS", "", "400", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003097, "EQUIPOS DE CONTROL", "", "402", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003083, "BTICINO", "", "500", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003084, "TAPAS GALVANIZADAS", "", "501", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003085, "TOMAS Y ENCHUFES", "", "502", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003086, "BREAKERS", "", "503", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003087, "PROTECTORES DE VOLTAJE", "", "504", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003088, "INTERRUPTORES, DIMMER Y SWITCHES", "", "505", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003098, "CONTACTOS Y RELES", "", "506", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003091, "TABLEROS, MEDIDORES Y CAJAS DE DISTRIBUCION", "", "550", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004509, "PROTONIC", "", "700", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003093, "FLETE", "", "888", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003089, "EXHIBIDORES", "", "998", 0);
        categories.add(pc);
        return categories;
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo8(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1003120, "COMBOS GRIFERIAS", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003273, "COMBOS GRIFERIAS", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003100, "BOYAS Y FLOTANTES PARA TANQUES", "", "30", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003101, "CONEXIONES P/TANQUES DE AGUA", "", "31", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003102, "DESAGUES P/FREGADEROS-LAVAMANOS Y W.C.", "", "40", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003103, "DUCHAS TELEFONO", "", "50", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003104, "REPUESTOS P/GRIFERIA Y VALVULAS", "", "60", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003105, "REPUESTOS PARA SANITARIOS", "", "61", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003106, "FLUXOMETROS PARA WC Y URINARIO", "", "80", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003107, "GRIFERIAS", "", "120", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003108, "HERRAJES DE WC", "", "140", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003109, "INODOROS (ALUMINIO-BRONCE)", "", "150", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003110, "VALVULERIA", "", "170", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003111, "MANGUERAS PARA LAVAMANO Y W.C (CANILLAS)", "", "180", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003121, "KITS DE INSTALACIÓN PARA SALAS DE BAÑO", "", "190", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003112, "BRAZO P/REGADERA Y CONEXIONES CROMADAS", "", "218", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003113, "REGADERAS PARA BAÑO", "", "220", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003114, "REJILLAS P/INODOROS (BRONCE-ALUMINIO-PLASTICAS)", "", "230", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003115, "SIFONES PARA LAVAMANOS Y FREGADEROS", "", "240", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003116, "SURTIDORES PARA WC", "", "250", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003117, "TUBOS DE EXTENSION P/FREGADEROS Y LAV.", "", "260", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003118, "VALVULAS CHECK Y FILTROS P/VALVULA", "", "280", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003119, "EXHIBIDORES Y RELACIONADOS", "", "998", 0);
        categories.add(pc);
        return categories;
    }

    private static ArrayList<ProductCategory> getCategoriasGrupo9(int productCategoryId){
        ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
        ProductSubCategory  pc;
        pc = new ProductSubCategory(productCategoryId, 1003152, "COMBOS GRUPO 9", "", "1", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003153, "ANGULOS Y PERFILES DE HIERRO", "", "4", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004404, "ANGULOS Y PERFILES DE ALUMINIO", "", "5", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003182, "PRODUCTOS SIKA", "", "19", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003154, "IMPERMEABILIZANTES Y ASFALTOS", "", "29", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003155, "CARTON CHAPAFORTE", "", "30", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003156, "NIPLES GALVANIZADOS", "", "38", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003187, "NIPLES DE PVC", "", "39", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003157, "CONEXIONES GALVANIZADAS  M", "", "40", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003158, "CONEXIONES PVC Y PLASTICAS", "", "41", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003177, "CONEX GALV GENERICA", "", "42", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003183, "CONEXIONES CROMADAS", "", "43", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003184, "CONEXIONES GALVANIZADAS EN CAJA", "", "44", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003186, "PVC ROSCADO", "", "45", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003159, "LAMINAS DE ZINC", "", "51", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004193, "LÁMINAS DE ACERO ", "", "53", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004507, "LÁMINAS DE POLIURETANO", "", "54", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003160, "OXIDOS PARA PISO", "", "60", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003161, "CERCHAS", "", "70", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003162, "PLETINAS DE HIERRO", "", "80", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003163, "CARRETILLAS", "", "100", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003180, "RUEDAS Y TRIPAS", "", "101", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003181, "RODAPIES", "", "102", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003164, "FLEJES PLASTICOS PARA PISO", "", "105", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003165, "ELECTRODOS Y SOLDADURAS", "", "110", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003166, "ROMANILLAS Y VENTANAS", "", "115", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003167, "TUBOS PARA CERCAS", "", "130", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003168, "TUBERIA DE COBRE", "", "140", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003169, "CONEXIONES TUB. COBRE", "", "150", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003170, "TUBERIA PULIDA", "", "160", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003171, "TUBOS GALVANIZADOS PARA AGUA", "", "170", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1004194, "TUBOS HIERRO NEGRO PARA HERRERIA", "", "175", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003981, "TUBOS ESTRUCTURALES", "", "180", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003172, "TUBOS Y CANALES AGUAS BLANCAS", "", "190", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003173, "TUBOS ELECTRICIDAD", "", "200", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003174, "VARILLAS, CABILLAS, ZUNCHOS", "", "210", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003185, "SOPORTES PARA TUBERIA", "", "290", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003179, "ACCESORIOS PARA CERCA", "", "440", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003175, "TOBO ALBAÑIL", "", "500", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003577, "TANQUE", "", "600", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003178, "SERVICIO FELTE", "", "888", 0);
        categories.add(pc);
        pc = new ProductSubCategory(productCategoryId, 1003176, "EXHIBIDORES Y MATERIAL POP, RELACIONADOS.", "", "998", 0);
        categories.add(pc);
        return categories;
    }

    public static ArrayList<ProductBrand> generateProductBrandsListByLetter(String letter, int qty){
        ArrayList<ProductBrand> productBrands = new ArrayList<ProductBrand>();
        ProductBrand productBrand = new ProductBrand();
        for(int i=1; i<=qty; i++){
            productBrand = new ProductBrand();
            productBrand.setName(letter+i+" marca por la "+letter);
            productBrand.setDescription("Descripcion completa de la marca por la "+letter+i+".");
            productBrands.add(productBrand);
        }
        return productBrands;
    }

    /**
     *
     * @return
     */
    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param ctx
     * @throws Throwable
     */
    public static void showPromptShareApp(Context ctx) throws Throwable {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, ctx.getString(R.string.checkout_my_app,
                    ctx.getString(R.string.company_name), ctx.getPackageName()));
            sendIntent.setType("text/plain");
            ctx.startActivity(sendIntent);
        } catch(android.content.ActivityNotFoundException ex){
            Toast.makeText(ctx, ctx.getString(R.string.no_app_installed_to_share), Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    /**
     *
     * @param ctx
     * @param product
     * @param fileName
     * @return
     */
    public static Intent createShareProductIntent(Context ctx, Product product, String fileName){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_TEXT, product.getName() + " - " + ctx.getString(R.string.company_name));
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://"
                + CachedFileProvider.AUTHORITY + "/" + fileName));
        return shareIntent;
    }

    /**
     *
     * @param fileName
     * @param resId
     * @param ctx
     */
    public static void createFileInCacheDir(String fileName, int resId, Context ctx){
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            Toast.makeText(ctx, ctx.getString(R.string.external_storage_unavailable), Toast.LENGTH_LONG).show();
        } else {
            //path for the image file in the external storage
            File imageFile = new File(ctx.getCacheDir() + File.separator + fileName);
            try {
                imageFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(imageFile);
                Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(), resId);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fo.write(bytes.toByteArray());
            } catch (IOException e1) {
                e1.printStackTrace();
                Toast.makeText(ctx, e1.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
