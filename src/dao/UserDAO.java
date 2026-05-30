package dao;

import db.DBConnection;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User authenticate(String username, String hashedPassword) {
        String query = "SELECT id, username, role, ref_id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    
                    int refId = rs.getInt("ref_id");
                    if (rs.wasNull()) {
                        user.setRefId(null);
                    } else {
                        user.setRefId(refId);
                    }
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createUser(String username, String hashedPassword, String role, Integer refId) {
        String query = "INSERT INTO users (username, password, role, ref_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, role);
            if (refId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, refId);
            }
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Overload that uses an existing Connection (useful for transactions)
    public boolean createUser(Connection conn, String username, String hashedPassword, String role, Integer refId) throws SQLException {
        String query = "INSERT INTO users (username, password, role, ref_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, role);
            if (refId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, refId);
            }

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean isUsernameExists(String username) {
        String query = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUserByRef(int refId, String role) {
        String query = "DELETE FROM users WHERE ref_id = ? AND role = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, refId);
            ps.setString(2, role);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
