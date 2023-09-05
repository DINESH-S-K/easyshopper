package org.zoho.server.controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.zoho.server.utility.ActionUtility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class ActionController extends ActionSupport {
    ActionUtility actionUtility = new ActionUtility();

    public String execute() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionContext context = ActionContext.getContext();
        Map<Integer, List<String>>  userMap = actionUtility.getUserMap();
        Map<String, String> actionMap = actionUtility.getActionMap();

        List<String> userActions = new ArrayList<>();
        List<String> specificActions = new ArrayList<>();
        List<String> userSpecificActions = new ArrayList<>();

        int roleId = (int) context.get("roleId");

        String actionPath = request.getServletPath();
        System.out.println(actionPath);

        userActions =  userMap.get(0);
        userSpecificActions.addAll(userActions);

        if (userSpecificActions.contains(actionPath)){
            String actionClass = actionMap.get(actionPath);
            return run(request, response, Constants.USER_MODULE, actionClass);
        }

        if (actionUtility.getUserMap().containsKey(roleId)) {
            switch (roleId) {
                case 1:
                    specificActions = userMap.get(roleId);
                    userSpecificActions.addAll(specificActions);
                    if (userSpecificActions.contains(actionPath)) {
                        String actionClass = actionMap.get(actionPath);
                        if(actionClass.contains("AdminAndManagerAccountManager")) {
                            return run(request, response, Constants.COMMON_MODULE, actionClass);
                        }else{
                            return run(request, response, Constants.ADMIN_MODULE, actionClass);
                        }
                    }
                    break;
                case 2:
                    specificActions = userMap.get(roleId);
                    userSpecificActions.addAll(specificActions);
                    if (userSpecificActions.contains(actionPath)) {
                        String actionClass = actionMap.get(actionPath);
                        return run(request, response, Constants.COMMON_MODULE, actionClass);
                    }
                    break;
                case 3:
                    specificActions = userMap.get(roleId);
                    userSpecificActions.addAll(specificActions);
                    if (userSpecificActions.contains(actionPath)) {
                        String actionClass = actionMap.get(actionPath);
                        return run(request, response, Constants.CUSTOMER_MODULE, actionClass);
                    }
                    break;
                default:
                    System.out.println("You are Not a User");
            }
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return null;
    }


    public String run(HttpServletRequest request, HttpServletResponse response, String module, String actionClass) {
        try {
            String[] actionName = actionClass.split("\\.");
            String className = module + actionName[0];
            Class<?> clazz = Class.forName(className);
            String methodName = actionName[1];
            Class<?>[] parameterTypes = {HttpServletRequest.class, HttpServletResponse.class};
            Method method = clazz.getMethod(methodName, parameterTypes);
            Object instance = clazz.newInstance();
            return (String) method.invoke(instance, request, response);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}


