package dao;

import db.DBConnection;
import models.Lenda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LendaDAO {

    public List<Lenda> getAllLendet() {
        List<Lenda> list = new ArrayList<>();
        String query = "SELECT id, emri_lendes, pershkrimi, kreditet FROM lendet ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(mapResultSetToLenda(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Lenda getLendaById(int id) {
        String query = "SELECT id, emri_lendes, pershkrimi, kreditet FROM lendet WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLenda(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Lenda> getLendetByTeacherId(int teacherId) {
        List<Lenda> list = new ArrayList<>();
        String query = "SELECT l.id, l.emri_lendes, l.pershkrimi, l.kreditet FROM lendet l " +
                       "JOIN mesuesi_lendet ml ON l.id = ml.lenda_id " +
                       "WHERE ml.mesuesi_id = ? ORDER BY l.emri_lendes ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLenda(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Returns a list of custom objects or arrays for: Subject + Teacher Count
    public List<Object[]> getLendetWithTeacherCount() {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT l.id, l.emri_lendes, l.pershkrimi, l.kreditet, COUNT(ml.mesuesi_id) AS num_teachers " +
                       "FROM lendet l " +
                       "LEFT JOIN mesuesi_lendet ml ON l.id = ml.lenda_id " +
                       "GROUP BY l.id, l.emri_lendes, l.pershkrimi, l.kreditet " +
                       "ORDER BY l.id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Lenda lenda = mapResultSetToLenda(rs);
                int teacherCount = rs.getInt("num_teachers");
                list.add(new Object[]{lenda, teacherCount});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int addLenda(Lenda lenda) {
        String query = "INSERT INTO lendet (emri_lendes, pershkrimi, kreditet) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, lenda.getEmriLendes());
            ps.setString(2, lenda.getPershkrimi());
            ps.setInt(3, lenda.getKreditet());
            
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

    public boolean updateLenda(Lenda lenda) {
        String query = "UPDATE lendet SET emri_lendes = ?, pershkrimi = ?, kreditet = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, lenda.getEmriLendes());
            ps.setString(2, lenda.getPershkrimi());
            ps.setInt(3, lenda.getKreditet());
            ps.setInt(4, lenda.getId());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteLenda(int id) {
        String query = "DELETE FROM lendet WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Lenda mapResultSetToLenda(ResultSet rs) throws SQLException {
        Lenda l = new Lenda();
        l.setId(rs.getInt("id"));
        l.setEmriLendes(rs.getString("emri_lendes"));
        l.setPershkrimi(rs.getString("pershkrimi"));
        l.setKreditet(rs.getInt("kreditet"));
        return l;
    }
}
