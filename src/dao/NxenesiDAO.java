package dao;

import db.DBConnection;
import models.Nxenesi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NxenesiDAO {

    public List<Nxenesi> getAllNxenesit() {
        List<Nxenesi> list = new ArrayList<>();
        String query = "SELECT id, emri, mbiemri, email, data_lindjes, klasa, data_regjistrimit FROM nxenesit ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(mapResultSetToNxenesi(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Nxenesi getNxenesiById(int id) {
        String query = "SELECT id, emri, mbiemri, email, data_lindjes, klasa, data_regjistrimit FROM nxenesit WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNxenesi(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Nxenesi> searchNxenesit(String searchText) {
        List<Nxenesi> list = new ArrayList<>();
        String query = "SELECT id, emri, mbiemri, email, data_lindjes, klasa, data_regjistrimit FROM nxenesit " +
                       "WHERE emri ILIKE ? OR mbiemri ILIKE ? OR klasa ILIKE ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            String pattern = "%" + searchText + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNxenesi(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int addNxenesi(Nxenesi nxenesi) {
        String query = "INSERT INTO nxenesit (emri, mbiemri, email, data_lindjes, klasa, data_regjistrimit) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, nxenesi.getEmri());
            ps.setString(2, nxenesi.getMbiemri());
            ps.setString(3, nxenesi.getEmail());
            ps.setDate(4, nxenesi.getDataLindjes());
            ps.setString(5, nxenesi.getKlasa());
            
            if (nxenesi.getDataRegjistrimit() != null) {
                ps.setDate(6, nxenesi.getDataRegjistrimit());
            } else {
                ps.setDate(6, new Date(System.currentTimeMillis()));
            }
            
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

    // Overloaded addNxenesi that uses an existing Connection for transactional operations
    public int addNxenesi(Connection conn, Nxenesi nxenesi) throws SQLException {
        String query = "INSERT INTO nxenesit (emri, mbiemri, email, data_lindjes, klasa, data_regjistrimit) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nxenesi.getEmri());
            ps.setString(2, nxenesi.getMbiemri());
            ps.setString(3, nxenesi.getEmail());
            ps.setDate(4, nxenesi.getDataLindjes());
            ps.setString(5, nxenesi.getKlasa());

            if (nxenesi.getDataRegjistrimit() != null) {
                ps.setDate(6, nxenesi.getDataRegjistrimit());
            } else {
                ps.setDate(6, new Date(System.currentTimeMillis()));
            }

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

    public boolean updateNxenesi(Nxenesi nxenesi) {
        String query = "UPDATE nxenesit SET emri = ?, mbiemri = ?, email = ?, data_lindjes = ?, klasa = ? " +
                       "WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, nxenesi.getEmri());
            ps.setString(2, nxenesi.getMbiemri());
            ps.setString(3, nxenesi.getEmail());
            ps.setDate(4, nxenesi.getDataLindjes());
            ps.setString(5, nxenesi.getKlasa());
            ps.setInt(6, nxenesi.getId());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteNxenesi(int id) {
        // Since database tables have ON DELETE CASCADE for notat, deleting a student automatically cascadingly deletes their grades.
        // But we must also delete their login credentials in users table. We'll run them in transaction if possible or simple sequential deletes.
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Delete student user account
            String deleteUserSql = "DELETE FROM users WHERE ref_id = ? AND role = 'student'";
            try (PreparedStatement psUser = conn.prepareStatement(deleteUserSql)) {
                psUser.setInt(1, id);
                psUser.executeUpdate();
            }

            // 2. Delete student
            String deleteStudentSql = "DELETE FROM nxenesit WHERE id = ?";
            int affectedRows;
            try (PreparedStatement psStudent = conn.prepareStatement(deleteStudentSql)) {
                psStudent.setInt(1, id);
                affectedRows = psStudent.executeUpdate();
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

    private Nxenesi mapResultSetToNxenesi(ResultSet rs) throws SQLException {
        Nxenesi n = new Nxenesi();
        n.setId(rs.getInt("id"));
        n.setEmri(rs.getString("emri"));
        n.setMbiemri(rs.getString("mbiemri"));
        n.setEmail(rs.getString("email"));
        n.setDataLindjes(rs.getDate("data_lindjes"));
        n.setKlasa(rs.getString("klasa"));
        n.setDataRegjistrimit(rs.getDate("data_regjistrimit"));
        return n;
    }
}
