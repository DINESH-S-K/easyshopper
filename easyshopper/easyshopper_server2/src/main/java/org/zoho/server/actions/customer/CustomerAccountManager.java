package org.zoho.server.actions.customer;

import com.opensymphony.xwork2.ActionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zoho.server.persist.CustomerDAO;
import org.zoho.server.utility.AuditLogUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.zoho.server.utility.PrintUtility.print;

public class CustomerAccountManager {
    private static final Logger LOG = LogManager.getLogger(CustomerAccountManager.class);
    CustomerDAO customerDAO = new CustomerDAO();
    JSONObject jsonResponse = null;
    Map<String, String[]> params = null;

    public String viewProducts(HttpServletRequest req, HttpServletResponse res) {
        JSONArray jsonArray = new JSONArray();
        ResultSet rs;
        try {
            rs = customerDAO.viewProducts();
            if (rs != null) {
                while (rs.next()) {
                    jsonResponse = new JSONObject();
                    jsonResponse.put("productId", rs.getInt("ID"));
                    jsonResponse.put("productName", rs.getString("PRODUCT_NAME"));
                    jsonResponse.put("description", rs.getString("DESCRIPTION"));
                    jsonResponse.put("unitPrice", rs.getInt("UNIT_PRICE"));
                    jsonResponse.put("createdBy", rs.getInt("CREATED_BY"));
                    jsonResponse.put("updatedBy", rs.getInt("UPDATED_BY"));
                    jsonResponse.put("createdTime", rs.getTimestamp("CREATED_TIME"));
                    jsonResponse.put("updatedTime", rs.getTimestamp("UPDATED_TIME"));
                    jsonResponse.put("quantityInStock", rs.getInt("QUANTITY_IN_STOCK"));
                    jsonArray.put(jsonResponse);
                }
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            res.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return print(res, jsonArray);
    }

    public String getProductDetails(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();

        int productId;
        try {
            productId = Integer.parseInt(params.get("productId")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        jsonResponse = new JSONObject();
        ResultSet rs;
        try {
            rs = customerDAO.getProduct(productId);
            if (rs.next()) {
                jsonResponse.put("productId", rs.getInt("ID"));
                jsonResponse.put("productName", rs.getString("PRODUCT_NAME"));
                jsonResponse.put("description", rs.getString("DESCRIPTION"));
                jsonResponse.put("unitPrice", rs.getInt("UNIT_PRICE"));
                jsonResponse.put("createdTime", rs.getTimestamp("CREATED_TIME"));
                jsonResponse.put("updatedTime", rs.getTimestamp("UPDATED_TIME"));
                jsonResponse.put("quantityInStock", rs.getInt("QUANTITY_IN_STOCK"));
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AuditLogUtils.auditLogs(customerId,3,"Product Details Viewed");
        return print(res, jsonResponse);
    }

    public String addToCart(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        int productId, quantity;
        double unitPrice;
        try {
            productId = Integer.parseInt(params.get("productId")[0]);
            quantity = Integer.parseInt(params.get("quantity")[0]);
            unitPrice = Double.parseDouble(params.get("unitPrice")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String msg = customerDAO.addToCart(customerId, productId, quantity, unitPrice);

        jsonResponse.put("customerId", customerId);
        jsonResponse.put("productId", productId);
        jsonResponse.put("quantity", quantity);
        jsonResponse.put("price", unitPrice);
        jsonResponse.put("msg", msg);

        res.setStatus(HttpServletResponse.SC_CREATED);
        AuditLogUtils.auditLogs(customerId,3,"Product Added In Cart");
        return print(res, jsonResponse);
    }

    public String removeProductFromCart(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        int productId, quantity;
        double unitPrice;
        try {
            productId = Integer.parseInt(params.get("productId")[0]);
            quantity = Integer.parseInt(params.get("quantity")[0]);
            unitPrice = Double.parseDouble(params.get("unitPrice")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String msg = customerDAO.removeProductFromCart(customerId, productId, quantity, unitPrice);
        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(customerId,3,"Product Removed From Cart");
        return print(res, jsonResponse);
    }

    public String viewCart(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        JSONArray productArray = new JSONArray();
        JSONObject cartObject = new JSONObject();

        ResultSet rs;
        try {
            rs = customerDAO.viewCart(customerId);
            if (rs != null) {
                while (rs.next()) {
                    cartObject.put("id", rs.getInt("ID"));
                    cartObject.put("customerId", rs.getString("CUSTOMER_ID"));
                    cartObject.put("quantity", rs.getInt("QUANTITY"));
                    cartObject.put("totalAmount", rs.getInt("TOTAL_AMOUNT"));

                    jsonResponse = new JSONObject();
                    jsonResponse.put("productId", rs.getInt("PRODUCT_ID"));
                    jsonResponse.put("productName", rs.getString("PRODUCT_NAME"));
                    jsonResponse.put("quantity", rs.getInt("PRODUCT_QUANTITY"));
                    jsonResponse.put("unitPrice", rs.getInt("PRODUCT_UNIT_PRICE"));
                    jsonResponse.put("price", rs.getInt("PRODUCT_PRICE"));
                    productArray.put(jsonResponse);
                }
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            cartObject.put("productList", productArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AuditLogUtils.auditLogs(customerId,3,"Cart Viewed");
        return print(res, cartObject);
    }

    public String makeOrder(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");
        String msg = customerDAO.makeOrder(customerId);
        jsonResponse = new JSONObject();
        jsonResponse.put("msg", msg);
        res.setStatus(HttpServletResponse.SC_CREATED);
        AuditLogUtils.auditLogs(customerId,3,"Order Placed");
        return print(res, jsonResponse);
    }

    public String viewOrder(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        JSONArray productArray = new JSONArray();
        JSONObject orderObject = new JSONObject();

        ResultSet rs;
        try {
            rs = customerDAO.viewOrder(customerId);
            if (rs != null) {
                while (rs.next()) {
                    orderObject.put("id", rs.getInt("ID"));
                    orderObject.put("quantity", rs.getString("QUANTITY"));
                    orderObject.put("orderDate", rs.getTimestamp("ORDER_DATE"));
                    orderObject.put("purchasedAmount", rs.getDouble("PURCHASED_AMOUNT"));

                    jsonResponse = new JSONObject();
                    jsonResponse.put("productId", rs.getInt("PRODUCT_ID"));
                    jsonResponse.put("productName", rs.getString("PRODUCT_NAME"));
                    jsonResponse.put("quantity", rs.getInt("PRODUCT_QUANTITY"));
                    jsonResponse.put("unitPrice", rs.getInt("PRODUCT_UNIT_PRICE"));
                    jsonResponse.put("price", rs.getInt("PRODUCT_PRICE"));
                    productArray.put(jsonResponse);
                }
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            orderObject.put("productList", productArray);
        } catch (SQLException | JSONException e) {
            return e.getMessage();
        }
        return print(res, orderObject);
    }

    public String viewOrderDetails(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");
        params = req.getParameterMap();

        int orderId;
        try {
            orderId = Integer.parseInt(params.get("orderId")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        JSONArray productArray = new JSONArray();
        JSONObject orderObject = new JSONObject();

        ResultSet rs;
        try {
            rs = customerDAO.viewOrderDetails(orderId);
            if (rs != null) {
                while (rs.next()) {
                    orderObject.put("id", rs.getInt("ID"));
                    orderObject.put("quantity", rs.getString("QUANTITY"));
                    orderObject.put("orderDate", rs.getTimestamp("ORDER_DATE"));
                    orderObject.put("purchasedAmount", rs.getDouble("PURCHASED_AMOUNT"));

                    jsonResponse = new JSONObject();
                    jsonResponse.put("productId", rs.getInt("PRODUCT_ID"));
                    jsonResponse.put("productName", rs.getString("PRODUCT_NAME"));
                    jsonResponse.put("quantity", rs.getInt("PRODUCT_QUANTITY"));
                    jsonResponse.put("unitPrice", rs.getInt("PRODUCT_UNIT_PRICE"));
                    jsonResponse.put("price", rs.getInt("PRODUCT_PRICE"));
                    productArray.put(jsonResponse);
                }
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            orderObject.put("productList", productArray);
        } catch (SQLException | JSONException e) {
            return e.getMessage();
        }
        AuditLogUtils.auditLogs(customerId,3,"Order Details Viewed");
        return print(res, orderObject);
    }

    public String purchaseOrder(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        int orderId, pin;
        try {
            orderId = Integer.parseInt(params.get("orderId")[0]);
            pin = Integer.parseInt(params.get("pin")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String msg = customerDAO.purchaseOrder(orderId, customerId, pin);

        jsonResponse.put("msg", msg);
        AuditLogUtils.auditLogs(customerId,3,"Order Purchased");
        return print(res, jsonResponse);
    }

    public String cancelOrder(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        int orderId;
        try {
            orderId = Integer.parseInt(params.get("orderId")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String msg = customerDAO.cancelOrder(orderId, customerId);

        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(customerId,3,"Order Cancelled");
        return print(res, jsonResponse);
    }

    public String createWalletAccount(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        int pin;
        try {
            pin = Integer.parseInt(params.get("pin")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String msg;
        if(!customerDAO.isWalletPresent(customerId) && customerDAO.isWalletPresent(customerId) !=null) {
            msg = customerDAO.createWalletAccount(customerId, pin);
        }else {
            res.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
        res.setStatus(HttpServletResponse.SC_CREATED);

        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(customerId,3,"Wallet Account Created");
        return print(res, jsonResponse);

    }

    public String addMoneyToWallet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        int amount,pin;
        try {
            amount = Integer.parseInt(params.get("amount")[0]);
            pin = Integer.parseInt(params.get("pin")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String msg = customerDAO.addMoneyToWallet(customerId, amount, pin);

        jsonResponse.put("msg", msg);
        AuditLogUtils.auditLogs(customerId,3,"Money Added in Wallet");
        return print(res, jsonResponse);
    }

    public String viewWalletBalance(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        int pin;
        double balance;
        try {
            pin = Integer.parseInt(params.get("pin")[0]);
            balance = customerDAO.viewWalletBalance(customerId, pin);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        jsonResponse.put("balance", balance);
        AuditLogUtils.auditLogs(customerId,3,"Wallet Balance Viewed");
        return print(res, jsonResponse);
    }

    public String getRewardPointsAndBalance(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        ResultSet rs = customerDAO.getRewardPointsAndBalance(customerId);
        if (rs != null && rs.next()) {
            int rewardPoints = rs.getInt("REWARD_POINTS");
            double oldBalance = rs.getDouble("BALANCE");
            jsonResponse.put("rewardPoints", rewardPoints);
            jsonResponse.put("oldBalance", oldBalance);
        }else{
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return null;
        }
        return print(res, jsonResponse);
    }

    public String redeemRewardPoints(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        int pin;
        try {
            pin = Integer.parseInt(params.get("pin")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String msg = customerDAO.redeemRewardPoints(customerId, pin);

        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(customerId,3,"Redeem Reward Points");
        return print(res, jsonResponse);
    }

    public String viewPurchaseHistory(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ActionContext.getContext();
        int customerId = (int) context.get("id");

        JSONArray jsonArray = new JSONArray();
        ResultSet rs;
        try {
            rs = customerDAO.viewPurchaseHistory(customerId);
            if (rs != null) {
                while (rs.next()) {
                    jsonResponse = new JSONObject();
                    jsonResponse.put("id", rs.getInt("ID"));
                    jsonResponse.put("orderId", rs.getInt("ORDER_ID"));
                    jsonResponse.put("purchaseDate", rs.getTimestamp("PURCHASE_DATE"));
                    jsonResponse.put("amountDebited", rs.getDouble("AMOUNT_DEBITED"));
                    jsonResponse.put("walletBalance", rs.getDouble("WALLET_BALANCE"));
                    jsonArray.put(jsonResponse);
                }
            }else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AuditLogUtils.auditLogs(customerId,3,"Purchase History Viewed");
        return print(res, jsonArray);
    }
}

