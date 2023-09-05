package org.zoho.server.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionUtility {

    public Map<String, String> getActionMap() {
        Map<String, String> actionMap = new HashMap<>();
        // USER ACTIONS
        actionMap.put("/changePassword", "UserAccountManager.changePassword");
        actionMap.put("/updateProfile", "UserAccountManager.updateProfile");
        actionMap.put("/getUserInfo", "UserAccountManager.getUserInfo");
        actionMap.put("/logout", "UserAccountManager.logout");

        // Admin Actions

        actionMap.put("/createManager", "AdminAccountManager.createManager");
        actionMap.put("/getManagers", "AdminAccountManager.getManagers");
        actionMap.put("/getManager", "AdminAccountManager.getManager");
        actionMap.put("/updateManager", "AdminAccountManager.updateManager");
        actionMap.put("/removeManager", "AdminAccountManager.removeManager");
        actionMap.put("/auditLogs","AdminAccountManager.auditLogs");

        // ADMIN AND MANAGER ACTIONS

        actionMap.put("/addInventory", "AdminAndManagerAccountManager.addInventory");
        actionMap.put("/viewInventories", "AdminAndManagerAccountManager.viewInventories");
        actionMap.put("/updateInventory", "AdminAndManagerAccountManager.updateInventory");
        actionMap.put("/removeInventory", "AdminAndManagerAccountManager.removeInventory");
        actionMap.put("/addProduct", "AdminAndManagerAccountManager.addProduct");
        actionMap.put("/getProduct", "AdminAndManagerAccountManager.getProduct");
        actionMap.put("/updateProduct", "AdminAndManagerAccountManager.updateProduct");
        actionMap.put("/removeProduct", "AdminAndManagerAccountManager.removeProduct");
        actionMap.put("/viewInventory", "AdminAndManagerAccountManager.viewInventory");

        // CUSTOMER ACTIONS

        actionMap.put("/viewProducts", "CustomerAccountManager.viewProducts");
        actionMap.put("/getProductDetails", "CustomerAccountManager.getProductDetails");
        actionMap.put("/addToCart", "CustomerAccountManager.addToCart");
        actionMap.put("/removeProductFromCart", "CustomerAccountManager.removeProductFromCart");
        actionMap.put("/viewCart", "CustomerAccountManager.viewCart");
        actionMap.put("/makeOrder", "CustomerAccountManager.makeOrder");
        actionMap.put("/viewOrder", "CustomerAccountManager.viewOrder");
        actionMap.put("/viewOrderDetails", "CustomerAccountManager.viewOrderDetails");
        actionMap.put("/purchaseOrder", "CustomerAccountManager.purchaseOrder");
        actionMap.put("/cancelOrder", "CustomerAccountManager.cancelOrder");
        actionMap.put("/createWalletAccount", "CustomerAccountManager.createWalletAccount");
        actionMap.put("/addMoneyToWallet", "CustomerAccountManager.addMoneyToWallet");
        actionMap.put("/viewWalletBalance", "CustomerAccountManager.viewWalletBalance");
        actionMap.put("/getRewardPointsAndBalance", "CustomerAccountManager.getRewardPointsAndBalance");
        actionMap.put("/redeemRewardPoints", "CustomerAccountManager.redeemRewardPoints");
        actionMap.put("/viewPurchaseHistory", "CustomerAccountManager.viewPurchaseHistory");

        return actionMap;
    }

    public Map<Integer, List<String>> getUserMap() {
        Map<Integer, List<String>> userMap = new HashMap<>();
        List<String> user = Arrays.asList("/changePassword", "/forgetPassword", "/forgetAndChangePassword", "/getUser", "/updateProfile", "/getUserInfo", "/logout");

        List<String> admin = Arrays.asList("/createManager", "/getManagers", "/getManager", "/updateManager", "/removeManager", "/addInventory", "/viewInventories",
                "/updateInventory", "/removeInventory", "/addProduct", "/getProduct", "/updateProduct", "/removeProduct", "/viewInventory", "/auditLogs");

        List<String> manager = Arrays.asList("/addInventory", "/viewInventories", "/updateInventory", "/removeInventory", "/addProduct", "/getProduct", "/updateProduct", "/removeProduct", "/viewInventory");

        List<String> customer = Arrays.asList("/viewProducts", "/addToCart", "/getProductDetails", "/removeProductFromCart", "/viewCart", "/makeOrder", "/viewOrder", "/viewOrderDetails"
                , "/purchaseOrder", "/cancelOrder", "/createWalletAccount", "/addMoneyToWallet", "/viewWalletBalance", "/getRewardPointsAndBalance", "/redeemRewardPoints", "/viewPurchaseHistory");

        userMap.put(0, user);
        userMap.put(1, admin);
        userMap.put(2, manager);
        userMap.put(3, customer);

        return userMap;
    }

}
