package org.zoho.server.actions.user;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.zoho.server.persist.UserDAO;
import org.zoho.server.utility.AuditLogUtils;
import org.zoho.server.utility.LoginUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.util.Map;

import static org.zoho.server.persist.validation.Validation.*;
import static org.zoho.server.utility.PrintUtility.print;
import static org.zoho.server.utility.SecretKeyUtils.generateSecretKey;

public class UserAccountManager extends ActionSupport {

    private static final Logger LOG = LogManager.getLogger(UserAccountManager.class);
    UserDAO userDAO = new UserDAO();
    JSONObject jsonResponse = null;
    Map<String, String[]> params = null;

    public String register() throws Exception {

        HttpServletRequest req = ServletActionContext.getRequest();
        HttpServletResponse res = ServletActionContext.getResponse();
        params = req.getParameterMap();

        String username = params.get("username")[0];
        String email = params.get("email")[0];
        String mobileNo = params.get("mobileNo")[0];
        String password = params.get("password")[0];

        int roleId;
        try {
            roleId = Integer.parseInt(params.get("roleId")[0]);
        } catch (NumberFormatException e) {
            LOG.info("PLEASE SELECT ONE");
            return "failure";
        }

        String msg;
        if (validateUser(username, email, mobileNo, password)) {
            if (!userDAO.checkCredentials(username, password)) {
                int createdBy = 0;
                int updatedBy = 0;
                msg = userDAO.registerUser(username, email, mobileNo, password, createdBy, updatedBy, roleId);
            } else {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                LOG.info("USERNAME AND PASSWORD ALREADY PRESENT");
                return "conflict";
            }
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "bad request";
        }

        jsonResponse = new JSONObject();
        jsonResponse.put("username", username);
        jsonResponse.put("email", email);
        jsonResponse.put("mobileNo", mobileNo);
        jsonResponse.put("msg", msg);

        res.setStatus(HttpServletResponse.SC_CREATED);
        return print(res, jsonResponse);
    }


    public String login() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        params = request.getParameterMap();

        String username = params.get("username")[0];
        String password = params.get("password")[0];

        String encryptedPassword = LoginUtil.encryption(password);
        ResultSet rs = userDAO.loginUser(username, encryptedPassword);

        if (rs.next()) {
            LOG.info("Session Created");
            String secretKey = generateSecretKey();
            userDAO.storeSecretKey(rs.getInt("ID"), rs.getInt("ROLE_ID"), secretKey);
            Cookie sessionCookie = new Cookie("easyshopper", secretKey);
            sessionCookie.setMaxAge(24 * 3600);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);
            LOG.info("Login Successful");
            return "login";
        }
        LOG.info("Login Failed");
        return "failure";
    }

    public String forgetPassword() throws Exception {
        HttpServletRequest req = ServletActionContext.getRequest();
        HttpServletResponse res = ServletActionContext.getResponse();

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        String username = params.get("username")[0];
        String email = params.get("email")[0];

        boolean verify = userDAO.forgetPassword(username, email);
        jsonResponse.put("verify", verify);

        res.setStatus(HttpServletResponse.SC_OK);
        return print(res, jsonResponse);
    }

    public String forgetAndChangePassword() throws Exception {

        HttpServletRequest req = ServletActionContext.getRequest();
        HttpServletResponse res = ServletActionContext.getResponse();

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        String username = params.get("username")[0];
        String email = params.get("email")[0];
        String newPassword = params.get("newPassword")[0];

        String msg;
        if (validatePassword(newPassword)) {
            msg = userDAO.forgetAndChangePassword(username, email, newPassword);
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            LOG.info("ENTER VALID PASSWORD");
            return null;
        }

        jsonResponse.put("msg", msg);

        return print(res, jsonResponse);
    }

    public String changePassword(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int id = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        String username = params.get("username")[0];
        String password = params.get("password")[0];
        String newPassword = params.get("newPassword")[0];

        String msg;
        if (!userDAO.checkCredentials(username, LoginUtil.encryption(password)) && validatePassword(newPassword)) {
            msg = userDAO.changePassword(username, password, newPassword);
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            LOG.info("USERNAME AND PASSWORD NOT PRESENT");
            return null;
        }
        jsonResponse.put("username", username);
        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(id, roleId, "Password Changed");
        return print(res, jsonResponse);
    }

    public String updateProfile(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int id = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        jsonResponse = new JSONObject();
        params = req.getParameterMap();

        String username = params.get("username")[0];
        String email = params.get("email")[0];
        String mobileNo = params.get("mobileNo")[0];

        String msg;
        if (validateUserName(username) && validateEmail(email) && validateMobileNo(mobileNo)) {
            msg = userDAO.updateProfile(id, username, email, mobileNo, id);
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        jsonResponse.put("username", username);
        jsonResponse.put("email", email);
        jsonResponse.put("mobileNo", mobileNo);
        jsonResponse.put("msg", msg);

        res.setStatus(HttpServletResponse.SC_OK);
        AuditLogUtils.auditLogs(id, roleId, "Update Profile");
        return print(res, jsonResponse);
    }

    public String getUserInfo(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int id = (int) context.get("id");

        jsonResponse = new JSONObject();

        ResultSet rs = userDAO.getUser(id);
        if (rs.next()) {
            jsonResponse.put("id", rs.getInt("ID"));
            jsonResponse.put("username", rs.getString("USERNAME"));
            jsonResponse.put("email", rs.getString("EMAIL"));
            jsonResponse.put("mobileNo", rs.getString("MOBILE_NO"));
            jsonResponse.put("roleId", rs.getInt("ROLE_ID"));
        } else {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        return print(res, jsonResponse);
    }


    public String logout(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int id = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        jsonResponse = new JSONObject();
        String msg = userDAO.removeSession(id);

        jsonResponse.put("msg", msg);
        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
        AuditLogUtils.auditLogs(id, roleId, "Logout");
        return print(res, jsonResponse);

    }
}


