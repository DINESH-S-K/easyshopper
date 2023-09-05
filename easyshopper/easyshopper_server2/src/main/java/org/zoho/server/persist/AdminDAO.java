package org.zoho.server.persist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

import static org.zoho.server.persist.connection.DBConnection.myconnection;

public class AdminDAO {
    public ResultSet viewManagers() throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM USERS WHERE ROLE_ID=2";
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

    public ResultSet viewManager(int managerId) throws Exception {
        ResultSet rs ;
        try {
            String sql = "SELECT * FROM USERS WHERE ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
            ps.setInt(1, managerId);
            rs = ps.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Objects.requireNonNull(myconnection()).close();
        }
    }

    public String removeManager(int id) throws Exception {
        try {
            String sql = "DELETE FROM USERS WHERE ID=?";
            PreparedStatement ps = Objects.requireNonNull(myconnection()).prepareStatement(sql);
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

    public ResultSet auditLogs() throws Exception {
        ResultSet rs;
        try {
            String sql = "SELECT * FROM AUDITLOG";
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
}



