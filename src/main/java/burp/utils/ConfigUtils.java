package burp.utils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;


public class ConfigUtils {
    public static final String DIR_SCAN_DEEPER = "dir_scan_deeper";
    public static final String DIR_BYPASS = "dir_bypass";
    public static final String SCAN_POINT = "scan_point";

    public static String get(String name) {
        return Utils.Callback.loadExtensionSetting(name);
    }

    public static String get(String name, String defaultValue) {
        String val = get(name);
        return val == null || val.isEmpty() ? defaultValue : val;
    }

    public static int getInt(String name, int defaultValue) {
        String val = get(name);
        return val == null || val.isEmpty() ? defaultValue : Integer.parseInt(val);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        String val = get(name);
        return val == null || val.isEmpty() ? defaultValue : val.equals("1");
    }

    public static boolean getStrInDict(String name, String str, boolean defaultValue) {
        String val = get(name);
        if (val == null || val.isEmpty())
            return defaultValue;
        try {
            DocumentContext array = JsonPath.parse(val);
            if (array.json() instanceof JSONArray) {
                return ((JSONArray) array.json()).contains(str);
            } else {
                return defaultValue;
            }
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static void setStrToDict(String name, String str, boolean value) {
        String val = get(name);
        if (val == null || val.isEmpty())
            val = "[]";
        DocumentContext array = null;
        try {
            array = JsonPath.parse(val);
        } catch (Exception ex) {
            array = JsonPath.parse("[]");
        }
        JSONArray arr = array.json();
        if (value && !arr.contains(str)) {
            arr.add(str);
        } else if (!value && arr.contains(str)) {
            arr.remove(str);
        }
        set(name, arr.toJSONString());
    }

    public static String[] getDict(String name) {
        String val = get(name);
        if (val == null || val.isEmpty())
            return new String[0];
        DocumentContext array = null;
        try {
            array = JsonPath.parse(val);
        } catch (Exception ex) {
            return new String[0];
        }
        JSONArray arr = array.json();
        return arr.toArray(new String[0]);
    }

    public static void set(String name, String value) {
        Utils.Callback.saveExtensionSetting(name, value);
    }

    public static void setBoolean(String name, boolean value) {
        set(name, value ? "1" : "0");
    }

    public static void setInt(String name, int value) {
        set(name, String.valueOf(value));
    }

    public static void main(String[] args) {

    }
}
