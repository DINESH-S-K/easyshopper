package org.zoho.server.actions.common;

import com.opensymphony.xwork2.ActionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zoho.server.persist.CommonDAO;
import org.zoho.server.utility.AuditLogUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.util.Map;

import static org.zoho.server.persist.validation.Validation.validateDescription;
import static org.zoho.server.persist.validation.Validation.validateProductName;
import static org.zoho.server.utility.PrintUtility.print;

public class AdminAndManagerAccountManager {
    private static final Logger LOG = LogManager.getLogger(AdminAndManagerAccountManager.class);
    CommonDAO commonDAO = new CommonDAO();
    JSONObject jsonResponse = null;
    Map<String, String[]> params = null;

    public String addInventory(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int createdBy = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        String description = params.get("description")[0];

        String msg;
        if (validateDescription(description)) {
            msg = commonDAO.addInventory(description, createdBy, createdBy);
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        jsonResponse.put("description", description);
        jsonResponse.put("msg", msg);

        res.setStatus(HttpServletResponse.SC_CREATED);
        AuditLogUtils.auditLogs(createdBy,roleId,"Add Inventory");
        return print(res, jsonResponse);
    }

    public String viewInventories(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int id = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        JSONArray jsonArray = new JSONArray();
        ResultSet rs;
        try {
            rs = commonDAO.viewInventories();
            if (rs != null) {
                while (rs.next()) {
                    jsonResponse = new JSONObject();
                    jsonResponse.put("id", rs.getInt("ID"));
                    jsonResponse.put("description", rs.getString("DESCRIPTION"));
                    jsonArray.put(jsonResponse);
                }
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            res.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AuditLogUtils.auditLogs(id,roleId,"Inventories Viewed");
        return print(res, jsonArray);
    }

    public String updateInventory(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int updatedBy = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        int inventoryId;
        try {
            inventoryId = Integer.parseInt(params.get("inventoryId")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String description = params.get("description")[0];

        String msg;
        if (validateDescription(description)) {
            msg = commonDAO.updateInventory(inventoryId, description, updatedBy);
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        jsonResponse.put("inventoryId", inventoryId);
        jsonResponse.put("description", description);
        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(updatedBy,roleId,"Inventory Updated");
        return print(res, jsonResponse);
    }

    public String removeInventory(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int id = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        params = req.getParameterMap();

        int inventoryId;
        try {
            inventoryId = Integer.parseInt(params.get("inventoryId")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        jsonResponse = new JSONObject();
        String msg = commonDAO.removeInventory(inventoryId);
        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(id,roleId,"Removing Inventory");
        return print(res, jsonResponse);
    }

    public String addProduct(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int createdBy = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        params = req.getParameterMap();
        jsonResponse = new JSONObject();

        String productName = params.get("productName")[0];
        String description = params.get("description")[0];

        int inventoryId, quantityInStock;
        double unitPrice;
        try {
            inventoryId = Integer.parseInt(params.get("inventoryId")[0]);
            unitPrice = Double.parseDouble(params.get("unitPrice")[0]);
            quantityInStock = Integer.parseInt(params.get("quantityInStock")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }

        String msg;
        if (validateDescription(description) && validateProductName(productName)) {
            msg = commonDAO.addProduct(inventoryId, productName, description, unitPrice, createdBy, createdBy, quantityInStock);
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        jsonResponse.put("inventoryId", inventoryId);
        jsonResponse.put("productName", productName);
        jsonResponse.put("description", description);
        jsonResponse.put("unitPrice", unitPrice);
        jsonResponse.put("quantityInStock", quantityInStock);
        jsonResponse.put("msg", msg);

        res.setStatus(HttpServletResponse.SC_CREATED);
        AuditLogUtils.auditLogs(createdBy,roleId,"Add Product");
        return print(res, jsonResponse);
    }

    public String getProduct(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int id = (int) context.get("id");
        int roleId = (int) context.get("roleId");

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
            rs = commonDAO.getProduct(productId);
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
        AuditLogUtils.auditLogs(id,roleId,"Product Viewed");
        return print(res, jsonResponse);
    }

    public String updateProduct(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int updatedBy = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        params = req.getParameterMap();
        String productName = params.get("productName")[0];
        String description = params.get("description")[0];

        int productId, quantityInStock;
        double unitPrice;
        try {
            productId = Integer.parseInt(params.get("productId")[0]);
            unitPrice = Double.parseDouble(params.get("unitPrice")[0]);
            quantityInStock = Integer.parseInt(params.get("quantityInStock")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }
        jsonResponse = new JSONObject();

        String msg = commonDAO.updateProduct(productId, productName, description, unitPrice, updatedBy, quantityInStock);

        jsonResponse.put("productId", productId);
        jsonResponse.put("productName", productName);
        jsonResponse.put("description", description);
        jsonResponse.put("unitPrice", unitPrice);
        jsonResponse.put("quantityInStock", quantityInStock);
        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(updatedBy,roleId,"Product Updated");
        return print(res, jsonResponse);
    }

    public String removeProduct(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int id = (int) context.get("id");
        int roleId = (int) context.get("roleId");

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

        String msg = commonDAO.removeProduct(productId);
        jsonResponse.put("msg", msg);

        AuditLogUtils.auditLogs(id,roleId,"Product Removed");
        return print(res, jsonResponse);
    }

    public String viewInventory(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ActionContext context = ServletActionContext.getContext();
        int id = (int) context.get("id");
        int roleId = (int) context.get("roleId");

        JSONArray productArray = new JSONArray();
        JSONObject inventoryObject = new JSONObject();

        params = req.getParameterMap();
        int inventoryId;
        try {
            inventoryId = Integer.parseInt(params.get("inventoryId")[0]);
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.info("NUMBER FORMAT EXCEPTION");
            return null;
        }
        ResultSet rs;
        try {
            rs = commonDAO.viewInventory(inventoryId);
            if (rs != null) {
                while (rs.next()) {
                    inventoryObject.put("inventoryId", rs.getInt("INVENTORY_ID"));
                    inventoryObject.put("description", rs.getString("INVENTORY_DESCRIPTION"));
                    inventoryObject.put("createdBy", rs.getInt("INVENTORY_CREATED_BY"));
                    inventoryObject.put("updatedBy", rs.getInt("INVENTORY_UPDATED_BY"));

                    jsonResponse = new JSONObject();
                    jsonResponse.put("productId", rs.getInt("PRODUCT_ID"));
                    jsonResponse.put("productName", rs.getString("PRODUCT_NAME"));
                    jsonResponse.put("description", rs.getString("PRODUCT_DESCRIPTION"));
                    jsonResponse.put("unitPrice", rs.getInt("PRODUCT_UNIT_PRICE"));
                    jsonResponse.put("createdBy", rs.getInt("PRODUCT_CREATED_BY"));
                    jsonResponse.put("updatedBy", rs.getInt("PRODUCT_UPDATED_BY"));
                    jsonResponse.put("createdTime", rs.getTimestamp("PRODUCT_CREATED_TIME"));
                    jsonResponse.put("updatedTime", rs.getTimestamp("PRODUCT_UPDATED_TIME"));
                    jsonResponse.put("quantityInStock", rs.getInt("PRODUCT_QUANTITY_IN_STOCK"));
                    productArray.put(jsonResponse);
                }
            } else {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            inventoryObject.put("productList", productArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AuditLogUtils.auditLogs(id,roleId,"Inventory Viewed");
        return print(res, inventoryObject);
    }
}

