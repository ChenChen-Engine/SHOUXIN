package com.yuntongxun.ecdemo.net.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuntongxun.ecdemo.ECApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class SPUtils {

    private static SharedPreferences sp ;

    static {
        sp = ECApplication.getInstance().getSharedPreferences("localData", Context.MODE_PRIVATE);
    }

    public static void saveGroupHead(HashMap<String,String> map){
        String json = new Gson().toJson(map);
        sp.edit().putString("groupHead",json).commit();
    }

    public static HashMap<String,String> getGroupHead(){
        String json = sp.getString("groupHead", "{}");
        HashMap<String,String> map = new Gson().fromJson(json,new TypeToken<HashMap<String,String>>(){}.getType());
        return map;
    }

    public static void putHead(String id,String url){
        HashMap<String, String> map = getGroupHead();
        map.put(id,url);
        saveGroupHead(map);
    }

    public static String getHead(String id){
        String s = getGroupHead().get(id);
        String head="";
        try {
            JSONObject json = new JSONObject(s);
            head = (String) json.opt("protrait");
        } catch (JSONException e) {
            e.printStackTrace();
        }finally{
            return head;
        }
    }
}
