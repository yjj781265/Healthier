package com.jayangapp.healthier;

import android.util.Log;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yjj781265 on 7/27/2017.
 */

public class NutritionData {
    private String foodName, photoUrl, servingSize;
    private int calories, protein, totalfat, sugar, totalCarbonhydrate, sodium,
            cholesterol, potassium, diertaryFiber;
    private static final int NULL_VALUE =0;




    public static NutritionData fromJson(JSONObject jsonObject) {
        NutritionData data = new NutritionData();
        try {
            JSONObject foods = jsonObject.getJSONArray("foods").getJSONObject(0);
            data.foodName = foods.getString("food_name");
            data.servingSize = servingSizeMaker(foods);
            if(foods.get("nf_calories")== null){
                data.calories =NULL_VALUE;
        }else {
            data.calories = foods.getInt("nf_calories");
        }
            data.protein = foods.getInt("nf_protein");

            data.totalfat = foods.getInt("nf_total_fat");

            if( foods.getString("nf_sugars").equals("null")){
                data.sugar =NULL_VALUE;
            }else{
                data.sugar = foods.getInt("nf_sugars");
            }

            data.totalCarbonhydrate = foods.getInt("nf_total_carbohydrate");
            data.sodium = foods.getInt("nf_sodium");
            data.cholesterol = foods.getInt("nf_cholesterol");
            data.potassium = foods.getInt("nf_potassium");
            data.diertaryFiber = foods.getInt("nf_dietary_fiber");
            data.photoUrl = foods.getJSONObject("photo").getString("highres");

            Log.d("Success JSON", data.foodName + " " + data.servingSize);

        } catch (JSONException e) {

            Log.d("JSON response", e.toString());

        }

        return data;

    }

    private static String servingSizeMaker(JSONObject foods) {
        String servingSize;
        String servingQty, servingUnit, servingWeight;


        try {
            servingQty = String.valueOf(foods.getInt("serving_qty"));
            servingUnit = foods.getString("serving_unit");
            servingWeight = String.valueOf(foods.getString("serving_weight_grams"));

        } catch (JSONException e) {
            e.printStackTrace();
            servingSize = "not found";
            return servingSize;
        }
        servingSize = servingQty + " " + servingUnit + "(" + servingWeight + "g" + ")";
        return servingSize;

    }

    public String getFoodName() {
        return WordUtils.capitalizeFully(foodName, new char[]{'.'});
    }

    public int getCalories() {
        return calories;
    }

    public int getProtein() {

        return protein;

    }

    public int getTotalfat() {

        return totalfat;

    }

    public int getSugar() {

        return sugar;
    }


    public String getServingSize() {

            return servingSize;


    }

    public int getTotalCarbonhydrate() {

        return totalCarbonhydrate;

    }


    public int getSodium() {

        return sodium;

    }


    public int getCholesterol() {

        return cholesterol;

    }


    public int getPotassium() {

            return potassium;

        }


    public int getDiertaryFiber() {

        return diertaryFiber;

    }


    public String getPhotoUrl() {

        return photoUrl;

    }
}



