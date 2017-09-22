package com.jayangapp.healthier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yjj781265 on 8/10/2017.
 */

public class CloudRequest {
    JSONObject image;
    String content;
    JSONArray features;
    JSONObject mJSONObject;
    String type;
    String maxresult;

    public CloudRequest (String content){
        this.content = content;

        JSONObject obj1 =new JSONObject();
        JSONObject obj2 =new JSONObject();
        JSONObject obj3 =new JSONObject();
        try {
            obj1.put("content",content);
            image.put("image",obj1);
            obj2.put("type","LABEL_DETECTION");
            obj2.put("maxResults",1);


            features.put(obj2);
            obj3.put("feature",features);
            mJSONObject.put("0",image).accumulate("0",obj3);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject getImage() {
        return image;
    }

    public String getContent() {
        return content;
    }

    public JSONArray getFeatures() {
        return features;
    }

    public String getType() {
        return type;
    }

    public String getMaxresult() {
        return maxresult;
    }

    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    @Override
    public String toString() {
        return mJSONObject.toString();
    }
}
