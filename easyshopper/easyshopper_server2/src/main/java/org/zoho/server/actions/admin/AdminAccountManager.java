package org.zoho.server.actions.admin;

import com.opensymphony.xwork2.ActionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zoho.server.persist.AdminDAO;
import org.zoho.server.persist.UserDAO;
import org.zoho.server.utility.AuditLogUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.util.Map;

import static org.zoho.server.persist.validation.Validation.*;
import static org.zoho.server.utility.PrintUtility.print;

public class AdminAccountManager {
    private static final Logger LOG = LogManager.getLogger(AdminAccountManager.class);
    AdminDAO adminDAO = new AdminDAO();
    UserDAO userDAO = new UserDAO();
    JSONObject jsonResponse = null;
    Map<String, String[]> params = null;

    public String createManager(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int createdBy = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        String username = params.get("username")[0];
        String email = params.get("email")[0];
        String mobileNo = params.get("mobileNo")[0];
        String password = params.get("password")[0];

        int roleId;
        try {
            roleId = Integer.parseInt(params.get("roleId")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String msg;
        if (validateUser(username, email, mobileNo, password)) {
            if (!userDAO.checkCredentials(username, password)) {
                msg = userDAO.registerUser(username, email, mobileNo, password, createdBy, createdBy, roleId);
            } else {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                LOG.info("USERNAME AND PASSWORD ALREADY PRESENT");
                return null;
            }
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        jsonResponse = new JSONObject();
        jsonResponse.put("username", username);
        jsonResponse.put("email", email);
        jsonResponse.put("mobileNo", mobileNo);
        jsonResponse.put("msg", msg);

        res.setStatus(HttpServletResponse.SC_CREATED);
        AuditLogUtils.auditLogs(createdBy, roleId, "Create Manager Account");
        return print(res, jsonResponse);
    }

    public String getManagers(HttpServletRequest req, HttpServletResponse res) {
        JSONArray jsonArray = new JSONArray();
        ResultSet rs;
        try {
            rs = adminDAO.viewManagers();
            if (rs != null) {
                while (rs.next()) {
                    jsonResponse = new JSONObject();
                    jsonResponse.put("id", rs.getInt("ID"));
                    jsonResponse.put("username", rs.getString("USERNAME"));
                    jsonResponse.put("email", rs.getString("EMAIL"));
                    jsonResponse.put("mobileNo", rs.getString("MOBILE_NO"));
                    jsonResponse.put("createdBy", rs.getInt("CREATED_BY"));
                    jsonResponse.put("updatedBy", rs.getInt("UPDATED_BY"));
                    jsonResponse.put("createdTime", rs.getTimestamp("CREATED_TIME"));
                    jsonResponse.put("updatedTime", rs.getTimestamp("UPDATED_TIME"));
                    jsonArray.put(jsonResponse);
                    res.setStatus(HttpServletResponse.SC_OK);
                }
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return print(res, jsonArray);
    }

    public String getManager(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int id = (int) context.get("id");

        params = req.getParameterMap();
        int managerId = Integer.parseInt(params.get("managerId")[0]);

        jsonResponse = new JSONObject();
        ResultSet rs;
        try {
            rs = adminDAO.viewManager(managerId);
            if (rs.next()) {
                jsonResponse.put("id", rs.getInt("ID"));
                jsonResponse.put("username", rs.getString("USERNAME"));
                jsonResponse.put("email", rs.getString("EMAIL"));
                jsonResponse.put("mobileNo", rs.getString("MOBILE_NO"));
                jsonResponse.put("createdBy", rs.getInt("CREATED_BY"));
                jsonResponse.put("updatedBy", rs.getInt("UPDATED_BY"));
                jsonResponse.put("createdTime", rs.getTimestamp("CREATED_TIME"));
                jsonResponse.put("updatedTime", rs.getTimestamp("UPDATED_TIME"));
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AuditLogUtils.auditLogs(id, 1, "Manager Account Viewed");
        return print(res, jsonResponse);
    }

    public String updateManager(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int updatedBy = (int) context.get("id");

        params = req.getParameterMap();
        int managerId = Integer.parseInt(params.get("managerId")[0]);

        jsonResponse = new JSONObject();
        params = req.getParameterMap();

        String username = params.get("username")[0];
        String email = params.get("email")[0];
        String mobileNo = params.get("mobileNo")[0];

        String msg;
        if (validateUserName(username) && validateEmail(email) && validateMobileNo(mobileNo)) {
            msg = userDAO.updateProfile(managerId, username, email, mobileNo, updatedBy);
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        jsonResponse.put("username", username);
        jsonResponse.put("email", email);
        jsonResponse.put("mobileNo", mobileNo);
        jsonResponse.put("msg", msg);

        res.setStatus(HttpServletResponse.SC_OK);
        AuditLogUtils.auditLogs(updatedBy, 1, "Update Manager Account");
        return print(res, jsonResponse);
    }

    public String removeManager(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int id = (int) context.get("id");

        params = req.getParameterMap();
        int managerId = Integer.parseInt(params.get("managerId")[0]);

        jsonResponse = new JSONObject();
        String msg = adminDAO.removeManager(managerId);
        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(id, 1, "Remove Manager Account");
        return print(res, jsonResponse);
    }

    public String auditLogs(HttpServletRequest req, HttpServletResponse res) {
        JSONArray jsonArray = new JSONArray();
        ResultSet rs;
        try {
            rs = adminDAO.auditLogs();
            if (rs !=null) {
                while (rs.next()) {
                    jsonResponse = new JSONObject();
                    jsonResponse.put("id", rs.getInt("ID"));
                    jsonResponse.put("userId", rs.getInt("USER_ID"));
                    jsonResponse.put("username", rs.getString("USERNAME"));
                    jsonResponse.put("dateAndTime", rs.getTimestamp("DATE_AND_TIME"));
                    jsonResponse.put("role", rs.getString("ROLE"));
                    jsonResponse.put("action", rs.getString("ACTION"));
                    jsonArray.put(jsonResponse);
                }
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return print(res, jsonArray);
    }
}
