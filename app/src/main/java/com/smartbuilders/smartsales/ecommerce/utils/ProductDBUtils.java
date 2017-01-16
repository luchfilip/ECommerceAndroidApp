package com.smartbuilders.smartsales.ecommerce.utils;

import com.smartbuilders.smartsales.ecommerce.model.Product;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by AlbertoSarco on 16/1/2017.
 */
public class ProductDBUtils {

    private static int incidencesByWord(String str, String[] words) {
        int i = words.length;
        for (String word : Arrays.asList(words)) {
            if (!str.contains(word)) {
                i--;
            }
        }
        return i;
    }

    private static int startsWith(String str, String[] words) {
        int i = 0;
        for (String word : Arrays.asList(words)) {
            i += str.indexOf(word);
        }
        return i;
    }

    public static void sortByProductNameContains(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return incidencesByWord(p2.getName().toUpperCase(), aux)
                            - incidencesByWord(p1.getName().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public static void sortByProductReferenceContains(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return incidencesByWord(p2.getReference().toUpperCase(), aux)
                            - incidencesByWord(p1.getReference().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public static void sortByProductPurposeContains(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return incidencesByWord(p2.getPurpose().toUpperCase(), aux)
                            - incidencesByWord(p1.getPurpose().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public static void sortByProductNameStartsWith(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return startsWith(p1.getName().toUpperCase(), aux)
                            - startsWith(p2.getName().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public static void sortByProductReferenceStartsWith(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return startsWith(p1.getReference().toUpperCase(), aux)
                            - startsWith(p2.getReference().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public static void sortByProductPurposeStartsWith(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return startsWith(p1.getPurpose().toUpperCase(), aux)
                            - startsWith(p2.getPurpose().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
