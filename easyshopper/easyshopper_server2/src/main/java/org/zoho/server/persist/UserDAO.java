package org.zoho.server.persist;

import org.zoho.server.utility.LoginUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Objects;
import java.sql.SQLException;

import static org.zoho.server.persist.connection.DBConnection.myconnection;

public class UserDAO {

    public String registerUser(String username, String email, String mobileNo, String password, int createdBy, int updatedBy, int roleId) throws Exception {
        try {
            String sql = "INSERT INTO USERS (USERNAME, EMAIL, MOBILE_NO, PASSWORD, CREATED_TIME, UPDATED_TIME, ROLE_ID, CREATED_BY, UPDATED_BY) " +
                    "VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, mobileNo);
            ps.setString(4, LoginUtil.encryption(password));
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setInt(7, roleId);
            ps.setInt(8, createdBy);
            ps.setInt(9, updatedBy);
            ps.executeUpdate();

            return "Registration Successful!";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public boolean checkCredentials(String username, String password) throws SQLException {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, LoginUtil.encryption(password));
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public ResultSet loginUser(String username, String password) throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }


    public void storeSecretKey(int id, int roleId, String secretKey) throws SQLException {
        try {
            String sql = "INSERT INTO USERS_SECRET_KEY (ID, ROLE_ID, SECRET_KEY) " +
                    "VALUES (?,?,?)";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, roleId);
            ps.setString(3, secretKey);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public boolean forgetPassword(String username, String email) throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM USERS WHERE USERNAME=? AND EMAIL=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String forgetAndChangePassword(String username, String email, String newPassword) throws Exception {
        try {
            String sql = "UPDATE USERS SET PASSWORD=? WHERE USERNAME=? AND EMAIL=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, LoginUtil.encryption(newPassword));
            ps.setString(2, username);
            ps.setString(3, email);
            ps.executeUpdate();

            return "Password Changed";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String changePassword(String username, String password, String newPassword) throws Exception {
        try {
            String sql = "UPDATE USERS SET PASSWORD=? WHERE USERNAME=? AND PASSWORD=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, LoginUtil.encryption(newPassword));
            ps.setString(2, username);
            ps.setString(3, LoginUtil.encryption(password));
            ps.executeUpdate();

            return "Password Changed";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String updateProfile(int id, String username, String email, String mobileNo, int updatedBy)
            throws Exception {
        try {
            String sql = "UPDATE USERS SET USERNAME=?, EMAIL=?, MOBILE_NO=?, UPDATED_TIME=?, UPDATED_BY=? WHERE ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, mobileNo);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setInt(5, updatedBy);
            ps.setInt(6, id);
            ps.executeUpdate();

            return "Successfully Updated";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public ResultSet getUser(String secretKey) throws SQLException {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM USERS_SECRET_KEY WHERE SECRET_KEY=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setString(1, secretKey);
            rs = ps.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public ResultSet getUser(int id) throws SQLException {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM USERS WHERE ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String removeSession(int id) throws SQLException {
        try {
            String sql = "DELETE FROM USERS_SECRET_KEY WHERE ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return "Logout Successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }
    public String adminLogs(int userId, String username,int roleId,String action) throws Exception {
        try {
            String role = roleId==1 ? "admin": roleId==2 ? "manager" : "customer";
            String sql = "INSERT INTO AUDITLOG (USER_ID, USERNAME, ROLE, ACTION) VALUES (?,?,?,?)";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, username);
            ps.setString(3, role);
            ps.setString(4, action);
            ps.executeUpdate();

            return "Successfully Added";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }
}
