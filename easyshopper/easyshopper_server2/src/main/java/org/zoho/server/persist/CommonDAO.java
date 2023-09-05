package org.zoho.server.persist;

import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.zoho.server.persist.connection.DBConnection.myconnection;

public class CommonDAO {
    public String addInventory(String description, int createdBy, int updatedBy) throws Exception {
        try {
            String sql = "INSERT INTO INVENTORY (DESCRIPTION, CREATED_BY, UPDATED_BY, CREATED_TIME, UPDATED_TIME) VALUES (?,?,?,?,?)";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, description);
            ps.setInt(2, createdBy);
            ps.setInt(3, updatedBy);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();

            return "Successfully Added";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public ResultSet viewInventories() throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM INVENTORY";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            rs = ps.executeQuery(sql);
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String updateInventory(int id, String description, int updatedBy) throws Exception {
        try {
            String sql = "UPDATE INVENTORY SET DESCRIPTION=?, UPDATED_BY=?, UPDATED_TIME=? WHERE ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, description);
            ps.setInt(2, updatedBy);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setInt(4, id);
            ps.executeUpdate();

            return "Successfully Updated";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }


    public String removeInventory(int id) throws Exception {
        ResultSet rs;
        try {
            String productIdSql = "SELECT PRODUCT_ID FROM PRODUCT_INVENTORY WHERE INVENTORY_ID = ?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(productIdSql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            List<Integer> productIds = null;
            int productId;
            if (rs != null) {
                productIds = new ArrayList<>();
                while (rs.next()) {
                    productId = rs.getInt("PRODUCT_ID");
                    productIds.add(productId);
                }
            }

            String productInventorySql = "DELETE FROM PRODUCT_INVENTORY WHERE INVENTORY_ID=?";
            ps = Objects.requireNonNull(myconnection()).prepareStatement(productInventorySql);
            ps.setInt(1, id);
            ps.executeUpdate();

            String productIdString = StringUtils.join(productIds, ",");
            if (productIdString !=null && !productIdString.isEmpty() ) {
                String productSql = "DELETE FROM PRODUCT WHERE ID IN ( " + productIdString + " )";
                ps = Objects.requireNonNull(myconnection()).prepareStatement(productSql);
                ps.executeUpdate(productSql);
            }

            String inventorySql = "DELETE FROM INVENTORY WHERE ID=?";
            ps = Objects.requireNonNull(myconnection()).prepareStatement(inventorySql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return "Successfully Deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }


    public String addProduct(int inventoryId, String name, String description, double unitPrice, int createdBy, int updatedBy, int quantityInStock) throws Exception {
        try {
            String productSql = "INSERT INTO PRODUCT ( PRODUCT_NAME, DESCRIPTION, UNIT_PRICE, CREATED_BY, UPDATED_BY, CREATED_TIME, UPDATED_TIME, QUANTITY_IN_STOCK) " +
                    "VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(productSql);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDouble(3, unitPrice);
            ps.setInt(4, createdBy);
            ps.setInt(5, updatedBy);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            ps.setInt(8, quantityInStock);
            ps.executeUpdate();

            String productInventorySql = "INSERT INTO PRODUCT_INVENTORY (INVENTORY_ID) VALUES (?)";
            ps = Objects.requireNonNull(myconnection()).prepareStatement(productInventorySql);
            ps.setInt(1, inventoryId);
            ps.executeUpdate();

            return "Successfully Added";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {

            Objects.requireNonNull(myconnection()).close();

        }
    }

    public ResultSet getProduct(int productId) throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM PRODUCT WHERE ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, productId);
            rs = ps.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

            Objects.requireNonNull(myconnection()).close();

        }
    }

    public String updateProduct(int productId, String name, String description, double unitPrice, int updatedBy, int quantityInStock) throws Exception {
        try {
            String productSql = "UPDATE PRODUCT SET PRODUCT_NAME=?, DESCRIPTION=?, UNIT_PRICE=?, UPDATED_BY=?, UPDATED_TIME=?, QUANTITY_IN_STOCK=? WHERE ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(productSql);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDouble(3, unitPrice);
            ps.setInt(4, updatedBy);
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.setInt(6, quantityInStock);
            ps.setInt(7, productId);
            ps.executeUpdate();

            return "Successfully updated";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {

            Objects.requireNonNull(myconnection()).close();

        }
    }

    public String removeProduct(int id) throws Exception {
        try {
            String productInventorySql = "DELETE FROM PRODUCT_INVENTORY WHERE PRODUCT_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(productInventorySql);
            ps.setInt(1, id);
            ps.executeUpdate();

            String productSql = "DELETE FROM PRODUCT WHERE ID=?";
            ps = Objects.requireNonNull(myconnection()).prepareStatement(productSql);
            ps.setInt(1, id);
            ps.executeUpdate();

            return "Successfully Deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public ResultSet viewInventory(int inventoryId) throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT\n" +
                    "    I.ID AS INVENTORY_ID,\n" +
                    "    I.DESCRIPTION AS INVENTORY_DESCRIPTION,\n" +
                    "    I.CREATED_BY AS INVENTORY_CREATED_BY,\n" +
                    "    I.UPDATED_BY AS INVENTORY_UPDATED_BY,\n" +
                    "    P.ID AS PRODUCT_ID,\n" +
                    "    P.PRODUCT_NAME AS PRODUCT_NAME,\n" +
                    "    P.DESCRIPTION AS PRODUCT_DESCRIPTION,\n" +
                    "    P.UNIT_PRICE AS PRODUCT_UNIT_PRICE,\n" +
                    "    P.CREATED_BY AS PRODUCT_CREATED_BY,\n" +
                    "    P.UPDATED_BY AS PRODUCT_UPDATED_BY,\n" +
                    "    P.CREATED_TIME AS PRODUCT_CREATED_TIME,\n" +
                    "    P.UPDATED_TIME AS PRODUCT_UPDATED_TIME,\n" +
                    "    P.QUANTITY_IN_STOCK AS PRODUCT_QUANTITY_IN_STOCK\n" +
                    "FROM\n" +
                    "    INVENTORY AS I\n" +
                    "LEFT JOIN\n" +
                    "    PRODUCT_INVENTORY AS PI ON I.ID = PI.INVENTORY_ID\n" +
                    "LEFT JOIN\n" +
                    "    PRODUCT AS P ON PI.PRODUCT_ID = P.ID\n" +
                    "WHERE\n" +
                    "    I.ID = ? ";

            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, inventoryId);
            rs = ps.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }
}

