package org.zoho.server.utility;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;

public class PrintUtility {
    public static String print(HttpServletResponse res, JSONObject jsonObject) {
        res.setContentType("application/json");
        try {
            res.getWriter().write(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.clear();
        return null;
    }

    public static String print(HttpServletResponse res, JSONArray jsonArray) {
        res.setContentType("application/json");
        try {
            res.getWriter().write(jsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonArray.clear();
        return null;
    }
}
