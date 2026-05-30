package dao;

import db.DBConnection;
import models.Mesuesi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesuesiDAO {

    public List<Mesuesi> getAllMesuesit() {
        List<Mesuesi> list = new ArrayList<>();
        String query = "SELECT id, emri, mbiemri, email, telefon FROM mesuesit ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(mapResultSetToMesuesi(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Mesuesi getMesuesiById(int id) {
        String query = "SELECT id, emri, mbiemri, email, telefon FROM mesuesit WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMesuesi(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addMesuesi(Mesuesi mesuesi) {
        String query = "INSERT INTO mesuesit (emri, mbiemri, email, telefon) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, mesuesi.getEmri());
            ps.setString(2, mesuesi.getMbiemri());
            ps.setString(3, mesuesi.getEmail());
            ps.setString(4, mesuesi.getTelefon());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Overloaded addMesuesi that uses an existing Connection for transactional operations
    public int addMesuesi(Connection conn, Mesuesi mesuesi) throws SQLException {
        String query = "INSERT INTO mesuesit (emri, mbiemri, email, telefon) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, mesuesi.getEmri());
            ps.setString(2, mesuesi.getMbiemri());
            ps.setString(3, mesuesi.getEmail());
            ps.setString(4, mesuesi.getTelefon());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public boolean updateMesuesi(Mesuesi mesuesi) {
        String query = "UPDATE mesuesit SET emri = ?, mbiemri = ?, email = ?, telefon = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, mesuesi.getEmri());
            ps.setString(2, mesuesi.getMbiemri());
            ps.setString(3, mesuesi.getEmail());
            ps.setString(4, mesuesi.getTelefon());
            ps.setInt(5, mesuesi.getId());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMesuesi(int id) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Delete teacher user credentials
            String deleteUserSql = "DELETE FROM users WHERE ref_id = ? AND role = 'teacher'";
            try (PreparedStatement psUser = conn.prepareStatement(deleteUserSql)) {
                psUser.setInt(1, id);
                psUser.executeUpdate();
            }

            // 2. Delete teacher (ON DELETE CASCADE deletes entries in mesuesi_lendet,
            // and ON DELETE SET NULL updates grades to NULL mesuesi_id)
            String deleteTeacherSql = "DELETE FROM mesuesit WHERE id = ?";
            int affectedRows;
            try (PreparedStatement psTeacher = conn.prepareStatement(deleteTeacherSql)) {
                psTeacher.setInt(1, id);
                affectedRows = psTeacher.executeUpdate();
            }

            conn.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private Mesuesi mapResultSetToMesuesi(ResultSet rs) throws SQLException {
        Mesuesi m = new Mesuesi();
        m.setId(rs.getInt("id"));
        m.setEmri(rs.getString("emri"));
        m.setMbiemri(rs.getString("mbiemri"));
        m.setEmail(rs.getString("email"));
        m.setTelefon(rs.getString("telefon"));
        return m;
    }
}
