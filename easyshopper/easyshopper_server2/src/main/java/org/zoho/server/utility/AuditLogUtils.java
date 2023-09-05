package org.zoho.server.utility;

import org.zoho.server.persist.UserDAO;

import java.sql.ResultSet;

public class AuditLogUtils {
    static UserDAO userDAO = new UserDAO();

    public static void auditLogs(int userId, int roleId, String action) throws Exception {
        ResultSet rs = userDAO.getUser(userId);
        if (rs.next()) {
            String username = rs.getString("USERNAME");
            userDAO.adminLogs(userId, username, roleId, action);
        }
    }
}
