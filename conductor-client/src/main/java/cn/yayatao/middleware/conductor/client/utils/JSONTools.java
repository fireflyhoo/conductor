package cn.yayatao.middleware.conductor.client.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * json 工具类
 */
public abstract class JSONTools {
    private static final Gson gson = new Gson();

    public static String toJSON(Object o) {
        return gson.toJson(o);
    }

    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        return gson.fromJson(jsonStr, clazz);
    }

    public static <T> List<T> parseArray(String jsonStr, Class<T> clazz) {
        JsonArray arrys = JsonParser.parseString(jsonStr).getAsJsonArray();
        List list = new ArrayList(arrys.size());
        for (JsonElement arry : arrys) {
            list.add(gson.fromJson(arry, clazz));
        }
        return list;
    }

}
