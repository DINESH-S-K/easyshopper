package org.zoho.server.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.zoho.server.persist.UserDAO;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;

public class AuthorizationInterceptor extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(AuthorizationInterceptor.class);
    private final UserDAO userDAO = new UserDAO();
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext context = invocation.getInvocationContext();

        HttpServletRequest request = ServletActionContext.getRequest();
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("easyshopper".equals(cookie.getName())) {
                    ResultSet rs = userDAO.getUser(cookie.getValue());
                    if (rs.next()) {
                        LOG.info("Authorized User");
                        int id = rs.getInt("ID");
                        int roleId = rs.getInt("ROLE_ID");
                        context.put("id",id);
                        context.put("roleId",roleId);
                        return invocation.invoke();
                    }
                }
            }
        }
        LOG.info("Non Authorized User");
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return null;
    }
}
