package org.zoho.server.persist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

import static org.zoho.server.persist.connection.DBConnection.myconnection;

public class CustomerDAO {
    public ResultSet viewProducts() throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM PRODUCT";
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

    private ResultSet getCartId(int customerId) throws Exception {
        try {
            String cartSql = "SELECT * FROM CART WHERE CUSTOMER_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(cartSql);
            ps.setInt(1, customerId);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }


    public String addToCart(int customerId, int productId, int quantity, double price) throws Exception {
        ResultSet rs;
        try {
            rs = getCartId(customerId);
            if (rs != null && rs.next()) {
                int cartId = rs.getInt("ID");
                int oldQuantity = rs.getInt("QUANTITY");
                double oldPrice = rs.getDouble("TOTAL_AMOUNT");
                return addToExistingCart(cartId, productId, quantity, price, oldQuantity, oldPrice);
            } else {
                return addToNewCart(customerId, productId, quantity, price);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String addToNewCart(int customerId, int productId, int quantity, double price) throws Exception {
        try {
            String cartSql = "INSERT INTO CART (CUSTOMER_ID, QUANTITY, TOTAL_AMOUNT) VALUES (?,?,?)";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(cartSql);
            ps.setInt(1, customerId);
            ps.setInt(2, quantity);
            ps.setDouble(3, (quantity * price));
            ps.executeUpdate();

            String cartProductSql = "INSERT INTO CART_PRODUCT (PRODUCT_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE) VALUES (?,?,?,?)";
            ps = Objects.requireNonNull(myconnection()).prepareStatement(cartProductSql);
            ps.setInt(1, productId);
            ps.setInt(2, quantity);
            ps.setDouble(3, price);
            ps.setDouble(4, (quantity * price));
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

    public ResultSet getProduct(int cartId, int productId) throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM CART_PRODUCT WHERE CART_ID=? AND PRODUCT_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            rs = ps.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String addToExistingCart(int cartId, int productId, int quantity, double price, int oldCartQuantity, double oldCartPrice) throws Exception {
        try {
            String cartSql = "UPDATE CART SET QUANTITY=?, TOTAL_AMOUNT=? WHERE ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(cartSql);
            ps.setInt(1, (quantity + oldCartQuantity));
            ps.setDouble(2, ((quantity * price) + oldCartPrice));
            ps.setInt(3, cartId);
            ps.executeUpdate();

            if (productAlreadyInCart(cartId, productId)) {
                ResultSet rs = getProduct(cartId, productId);
                if (rs != null && rs.next()) {
                    int oldProductQuantity = rs.getInt("QUANTITY");
                    double oldProductPrice = rs.getDouble("TOTAL_PRICE");
                    String cartProductSql = "UPDATE CART_PRODUCT SET QUANTITY=?, UNIT_PRICE=?, TOTAL_PRICE=? WHERE CART_ID=? AND PRODUCT_ID=?";
                    ps = Objects.requireNonNull(myconnection()).prepareStatement(cartProductSql);
                    ps.setInt(1, (oldProductQuantity + quantity));
                    ps.setDouble(2, price);
                    ps.setDouble(3, (oldProductPrice + (quantity * price)));
                    ps.setInt(4, cartId);
                    ps.setInt(5, productId);
                    ps.executeUpdate();
                }
            } else {
                String cartProductSql = "INSERT INTO CART_PRODUCT VALUES (?,?,?,?,?)";
                ps = Objects.requireNonNull(myconnection()).prepareStatement(cartProductSql);
                ps.setInt(1, cartId);
                ps.setInt(2, productId);
                ps.setInt(3, quantity);
                ps.setDouble(4, price);
                ps.setDouble(5, (quantity * price));
                ps.executeUpdate();
            }
            return "Successfully Added";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public Boolean productAlreadyInCart(int cartId, int productId) throws Exception {
        ResultSet rs;
        try {
            String cartProductSql = "SELECT * FROM CART_PRODUCT WHERE CART_ID=? AND PRODUCT_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(cartProductSql);
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String removeProductFromCart(int customerId, int productId, int quantity, double price) throws Exception {
        ResultSet rs;
        try {
            rs = getCartId(customerId);
            if (rs != null && rs.next()) {
                int cartId = rs.getInt("ID");
                int oldQuantity = rs.getInt("QUANTITY");
                double oldPrice = rs.getDouble("TOTAL_AMOUNT");

                String cartProductSql = "DELETE FROM CART_PRODUCT WHERE PRODUCT_ID=? AND CART_ID=?";
                PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(cartProductSql);
                ps.setInt(1, productId);
                ps.setInt(2, cartId);
                ps.executeUpdate();

                String updateCartSql = "UPDATE CART SET QUANTITY=?, TOTAL_AMOUNT=? WHERE ID=?";
                ps = Objects.requireNonNull(myconnection()).prepareStatement(updateCartSql);
                ps.setInt(1, (oldQuantity - quantity));
                ps.setDouble(2, (oldPrice - (quantity * price)));
                ps.setInt(3, cartId);
                ps.executeUpdate();
                return "Successfully Deleted";
            } else {
                return "NO Such Product Present in Cart";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public ResultSet viewCart(int customerId) throws Exception {
        ResultSet rs;
        int cartId;
        try {
            rs = getCartId(customerId);
            if (rs != null && rs.next()) {
                cartId = Integer.parseInt(rs.getString("ID"));
                String sql = "SELECT\n" +
                        "    C.ID AS ID,\n" +
                        "    C.CUSTOMER_ID AS CUSTOMER_ID,\n" +
                        "    C.QUANTITY AS QUANTITY,\n" +
                        "    C.TOTAL_AMOUNT AS TOTAL_AMOUNT,\n" +
                        "    CP.PRODUCT_ID AS PRODUCT_ID,\n" +
                        "    CP.QUANTITY AS PRODUCT_QUANTITY,\n" +
                        "    CP.UNIT_PRICE AS PRODUCT_UNIT_PRICE,\n" +
                        "    CP.TOTAL_PRICE AS PRODUCT_PRICE,\n" +
                        "    P.PRODUCT_NAME AS PRODUCT_NAME\n" +
                        "FROM\n" +
                        "    CART AS C\n" +
                        "LEFT JOIN\n" +
                        "    CART_PRODUCT AS CP ON C.ID = CP.CART_ID\n" +
                        "LEFT JOIN\n" +
                        "   PRODUCT AS P ON CP.PRODUCT_ID = P.ID\n" +
                        "WHERE\n" +
                        "    C.ID = ?";

                PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
                ps.setInt(1, cartId);
                return ps.executeQuery();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String makeOrder(int customerId) throws Exception {
        ResultSet rs;
        try {
            rs = getCartId(customerId);
            if (rs != null && rs.next()) {
                int cartId = rs.getInt("ID");
                int totalQuantity = rs.getInt("QUANTITY");
                double totalAmount = rs.getDouble("TOTAL_AMOUNT");

                String orderSql = "INSERT INTO ORDERS ( CUSTOMER_ID, ORDER_DATE, QUANTITY, PURCHASED_AMOUNT, ORDER_STATUS ) VALUES (?,?,?,?,?)";
                PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(orderSql);
                ps.setInt(1, customerId);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setInt(3, totalQuantity);
                ps.setDouble(4, totalAmount);
                ps.setString(5, "PENDING");
                ps.executeUpdate();

                rs = getOrderId(customerId);
                if (rs != null && rs.next()) {
                    int orderId = rs.getInt("ID");
                    String cartProductSql = "SELECT * FROM CART_PRODUCT CP WHERE CART_ID=?";
                    ps = Objects.requireNonNull(myconnection()).prepareStatement(cartProductSql);
                    ps.setInt(1, cartId);
                    rs = ps.executeQuery();

                    if (rs != null) {
                        while (rs.next()) {
                            int productId = rs.getInt("PRODUCT_ID");
                            int quantity = rs.getInt("QUANTITY");
                            double unitPrice = rs.getDouble("UNIT_PRICE");
                            double totalPrice = rs.getDouble("TOTAL_PRICE");

                            String orderProductSql = "INSERT INTO ORDER_PRODUCT (ORDER_ID,PRODUCT_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE) VALUES (?,?,?,?,?)";
                            ps = Objects.requireNonNull(myconnection()).prepareStatement(orderProductSql);
                            ps.setInt(1, orderId);
                            ps.setInt(2, productId);
                            ps.setInt(3, quantity);
                            ps.setDouble(4, unitPrice);
                            ps.setDouble(5, totalPrice);
                            ps.executeUpdate();
                        }
                    }
                }
            }
            return "Successfully ordered";

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    private ResultSet getOrderId(int customerId) throws Exception {
        try {
            String ordersSql = "SELECT * FROM ORDERS WHERE CUSTOMER_ID=? AND ORDER_STATUS=? ";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(ordersSql);
            ps.setInt(1, customerId);
            ps.setString(2, "PENDING");
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }


    public ResultSet viewOrder(int customerId) throws Exception {
        ResultSet rs;
        try {
            rs = getOrderId(customerId);
            if (rs != null && rs.next()) {
                int orderId = rs.getInt("ID");
                String sql = "SELECT\n" +
                        "    O.ID AS ID,\n" +
                        "    O.ORDER_DATE AS ORDER_DATE,\n" +
                        "    O.QUANTITY AS QUANTITY,\n" +
                        "    O.PURCHASED_AMOUNT AS PURCHASED_AMOUNT,\n" +
                        "    OP.PRODUCT_ID AS PRODUCT_ID,\n" +
                        "    OP.QUANTITY AS PRODUCT_QUANTITY,\n" +
                        "    OP.UNIT_PRICE AS PRODUCT_UNIT_PRICE,\n" +
                        "    OP.TOTAL_PRICE AS PRODUCT_PRICE, \n" +
                        "    P.PRODUCT_NAME AS PRODUCT_NAME\n" +
                        "FROM\n" +
                        "    ORDERS AS O\n" +
                        "LEFT JOIN\n" +
                        "    ORDER_PRODUCT AS OP ON O.ID = OP.ORDER_ID\n" +
                        "LEFT JOIN\n" +
                        "    PRODUCT AS P ON OP.PRODUCT_ID = P.ID\n" +
                        "WHERE\n" +
                        "    O.ID = ?";

                PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
                ps.setInt(1, orderId);
                return ps.executeQuery();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (myconnection() != null) {
                Objects.requireNonNull(myconnection()).close();
            }
        }
    }

    public ResultSet viewOrderDetails(int orderId) throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT\n" +
                    "    O.ID AS ID,\n" +
                    "    O.ORDER_DATE AS ORDER_DATE,\n" +
                    "    O.QUANTITY AS QUANTITY,\n" +
                    "    O.PURCHASED_AMOUNT AS PURCHASED_AMOUNT,\n" +
                    "    OP.PRODUCT_ID AS PRODUCT_ID,\n" +
                    "    OP.QUANTITY AS PRODUCT_QUANTITY,\n" +
                    "    OP.UNIT_PRICE AS PRODUCT_UNIT_PRICE,\n" +
                    "    OP.TOTAL_PRICE AS PRODUCT_PRICE, \n" +
                    "    P.PRODUCT_NAME AS PRODUCT_NAME\n" +
                    "FROM\n" +
                    "    ORDERS AS O\n" +
                    "LEFT JOIN\n" +
                    "    ORDER_PRODUCT AS OP ON O.ID = OP.ORDER_ID\n" +
                    "LEFT JOIN\n" +
                    "    PRODUCT AS P ON OP.PRODUCT_ID = P.ID\n" +
                    "WHERE\n" +
                    "    O.ID = ?";

            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, orderId);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (myconnection() != null) {
                Objects.requireNonNull(myconnection()).close();
            }
        }

    }

    public String purchaseOrder(int orderId, int customerId, int pin) throws Exception {
        ResultSet rs;
        try {
            rs = getCartId(customerId);
            if (rs != null && rs.next()) {
                int cartId = rs.getInt("ID");
                if (validateStocks(orderId) != null) {
                    String orderSql = "UPDATE ORDERS SET ORDER_STATUS= ? WHERE ID=? AND CUSTOMER_ID = ?";
                    PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(orderSql);
                    ps.setString(1, "PURCHASED");
                    ps.setInt(2, orderId);
                    ps.setInt(3, customerId);
                    ps.executeUpdate();


                    String orderAmountSql = "SELECT PURCHASED_AMOUNT FROM ORDERS WHERE CUSTOMER_ID=? AND ID=?";
                    ps = Objects.requireNonNull(myconnection()).prepareStatement(orderAmountSql);
                    ps.setInt(1, customerId);
                    ps.setInt(2, orderId);
                    rs = ps.executeQuery();

                    if (rs != null && rs.next()) {
                        double purchaseAmount = rs.getDouble("PURCHASED_AMOUNT");
                        int rewardPoints = (int) purchaseAmount / 100;

                        String getRewardPointsSql = "SELECT REWARD_POINTS FROM CUSTOMER_WALLET WHERE CUSTOMER_ID=?";
                        ps = Objects.requireNonNull(myconnection()).prepareStatement(getRewardPointsSql);
                        ps.setInt(1, customerId);
                        rs = ps.executeQuery();

                        if (rs != null && rs.next()) {
                            int oldRewardPoints = rs.getInt("REWARD_POINTS");
                            String updateRewardPointsSql = "UPDATE CUSTOMER_WALLET SET REWARD_POINTS=? WHERE CUSTOMER_ID = ?";
                            ps = Objects.requireNonNull(myconnection()).prepareStatement(updateRewardPointsSql);
                            ps.setString(1, String.valueOf((oldRewardPoints + rewardPoints)));
                            ps.setString(2, String.valueOf(customerId));
                            ps.executeUpdate();

                            if (viewWalletBalance(customerId, pin) != null) {
                                double balance = viewWalletBalance(customerId, pin);

                                String eWalletSql = "UPDATE CUSTOMER_WALLET SET BALANCE=? WHERE CUSTOMER_ID = ?";
                                ps = Objects.requireNonNull(myconnection()).prepareStatement(eWalletSql);
                                ps.setDouble(1, (balance - purchaseAmount));
                                ps.setInt(2, customerId);
                                ps.executeUpdate();

                                String purchaseHistorySql = "INSERT INTO PURCHASE_HISTORY (CUSTOMER_ID, ORDER_ID, PURCHASE_DATE, AMOUNT_DEBITED, WALLET_BALANCE) VALUES (?,?,?,?,?)";
                                ps = Objects.requireNonNull(myconnection()).prepareStatement(purchaseHistorySql);
                                ps.setInt(1, customerId);
                                ps.setInt(2, orderId);
                                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                                ps.setDouble(4, purchaseAmount);
                                ps.setDouble(5, (balance - purchaseAmount));
                                ps.executeUpdate();
                            }
                            makeCartEmpty(cartId);
                            return "PURCHASED";
                        }
                    }
                } else {
                    return "Sorry !.. Stocks Not Available";
                }
            } else {
                return "No Cart is Found";
            }
            return "Sorry !.. Stocks Not Available";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (myconnection() != null) {
                Objects.requireNonNull(myconnection()).close();
            }
        }
    }

    private void makeCartEmpty(Integer cartId) throws Exception {
        try {
            String cartProductSql = "DELETE FROM CART_PRODUCT WHERE CART_ID= ? ";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(cartProductSql);
            ps.setInt(1, cartId);
            ps.executeUpdate();

            String cartSql = "UPDATE CART SET QUANTITY=?,TOTAL_AMOUNT=? WHERE ID=?";
            ps = Objects.requireNonNull(myconnection()).prepareStatement(cartSql);
            ps.setInt(1, 0);
            ps.setDouble(2, 0.00);
            ps.setInt(3, cartId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    private Boolean validateStocks(int orderId) throws Exception {
        ResultSet rs;
        try {
            String orderProductSql = "SELECT * FROM ORDER_PRODUCT WHERE ORDER_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(orderProductSql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();

            ResultSet rs1;
            if (rs != null) {
                while (rs.next()) {
                    int productId = rs.getInt("PRODUCT_ID");
                    int quantity = rs.getInt("QUANTITY");

                    String productSql = "SELECT QUANTITY_IN_STOCK FROM PRODUCT WHERE ID=?";
                    ps = Objects.requireNonNull(myconnection()).prepareStatement(productSql);
                    ps.setInt(1, productId);
                    rs1 = ps.executeQuery();

                    if (rs1 != null && rs1.next()) {
                        int quantityInStock = rs1.getInt("QUANTITY_IN_STOCK");
                        if (quantityInStock < quantity) {
                            return false;
                        }
                    }
                }
            }
            return productQuantityReduce(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    private boolean productQuantityReduce(int orderId) throws Exception {
        ResultSet rs;
        try {
            String orderProductSql = "SELECT * FROM ORDER_PRODUCT WHERE ORDER_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(orderProductSql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();

            ResultSet rs1;
            if (rs != null) {
                while (rs.next()) {
                    int productId = rs.getInt("PRODUCT_ID");
                    int quantity = rs.getInt("QUANTITY");

                    String productSql = "SELECT QUANTITY_IN_STOCK FROM PRODUCT WHERE ID=?";
                    ps = Objects.requireNonNull(myconnection()).prepareStatement(productSql);
                    ps.setInt(1, productId);
                    rs1 = ps.executeQuery();

                    if (rs1 != null && rs1.next()) {

                        int quantityInStock = rs1.getInt("QUANTITY_IN_STOCK");
                        String updateProductSql = "UPDATE PRODUCT SET QUANTITY_IN_STOCK= ? WHERE ID = ?";
                        ps = Objects.requireNonNull(myconnection()).prepareStatement(updateProductSql);
                        ps.setInt(1, (quantityInStock - quantity));
                        ps.setInt(2, productId);
                        ps.executeUpdate();
                    }
                }
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (myconnection() != null) {
                Objects.requireNonNull(myconnection()).close();
            }
        }
    }

    public Double viewWalletBalance(int customerId, int pin) throws Exception {
        ResultSet rs;
        try {
            String customerWalletSql = "SELECT BALANCE FROM CUSTOMER_WALLET WHERE CUSTOMER_ID=? AND PIN=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(customerWalletSql);
            ps.setInt(1, customerId);
            ps.setInt(2, pin);
            rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                return Double.parseDouble(rs.getString("balance"));
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String cancelOrder(int orderId, int customerId) throws Exception {
        try {
            String orderSql = "UPDATE ORDERS SET ORDER_STATUS= ? WHERE ID=? AND CUSTOMER_ID = ?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(orderSql);
            ps.setString(1, "CANCELLED");
            ps.setInt(2, orderId);
            ps.setInt(3, customerId);
            ps.executeUpdate();

            return "CANCELLED";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String createWalletAccount(int customerId, int pin) throws Exception {
        try {
            String customerWalletSql = "INSERT INTO CUSTOMER_WALLET (CUSTOMER_ID,PIN) VALUES (?,?)";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(customerWalletSql);
            ps.setInt(1, customerId);
            ps.setInt(2, pin);
            ps.executeUpdate();

            return "Wallet Created";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public Boolean isWalletPresent(int customerId) throws SQLException {
        ResultSet rs;
        try {
            String customerWalletSql = "SELECT * FROM CUSTOMER_WALLET WHERE CUSTOMER_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(customerWalletSql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            return rs != null && rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String addMoneyToWallet(int customerId, int amount, int pin) throws Exception {
        ResultSet rs;
        try {
            String customerWalletSql = "SELECT * FROM CUSTOMER_WALLET WHERE CUSTOMER_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(customerWalletSql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                int walletId = rs.getInt("ID");
                double oldBalance = rs.getDouble("BALANCE");
                String updateCustomerWalletSql = "UPDATE CUSTOMER_WALLET SET BALANCE = ? WHERE ID=? AND CUSTOMER_ID = ? AND PIN=?";
                ps = Objects.requireNonNull(myconnection()).prepareStatement(updateCustomerWalletSql);
                ps.setDouble(1, (oldBalance + amount));
                ps.setInt(2, walletId);
                ps.setInt(3, customerId);
                ps.setInt(4, pin);
                ps.executeUpdate();
            }
            return "Successfully Added";

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public ResultSet getRewardPointsAndBalance(int customerId) throws Exception {
        try {
            String sql = "SELECT * FROM CUSTOMER_WALLET WHERE CUSTOMER_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, customerId);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String redeemRewardPoints(int customerId, int pin) throws Exception {
        ResultSet rs;
        try {
            String walletSql = "SELECT REWARD_POINTS FROM CUSTOMER_WALLET WHERE CUSTOMER_ID=? AND PIN=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(walletSql);
            ps.setInt(1, customerId);
            ps.setInt(2, pin);
            rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                int rewardPoints = rs.getInt("REWARD_POINTS");
                if (rewardPoints < 10) {
                    return "You Don't Have Minimum Reward Points";
                }
                int rewardAmount = rewardPoints / 10;
                int afterRewardPoints = rewardPoints % 10;
                double walletBalance = viewWalletBalance(customerId, pin);

                String redeemRewardPointsSql = "UPDATE CUSTOMER_WALLET SET BALANCE=?, REWARD_POINTS=? WHERE CUSTOMER_ID =? AND PIN=?";
                ps = Objects.requireNonNull(myconnection()).prepareStatement(redeemRewardPointsSql);
                ps.setDouble(1, (walletBalance + rewardAmount));
                ps.setInt(2, afterRewardPoints);
                ps.setInt(3, customerId);
                ps.setInt(4, pin);
                ps.executeUpdate();

                return "Successfully Redeemed";
            }
            return "Redeem Failed";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public ResultSet viewPurchaseHistory(int customerId) throws Exception {
        try {
            String sql = "SELECT * FROM PURCHASE_HISTORY WHERE CUSTOMER_ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, customerId);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }
}



