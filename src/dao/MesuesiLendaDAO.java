package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MesuesiLendaDAO {

    public List<Integer> getLendaIdsForTeacher(int teacherId) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT lenda_id FROM mesuesi_lendet WHERE mesuesi_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("lenda_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean assignSubjectsToTeacher(int teacherId, List<Integer> subjectIds) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Delete existing associations
            String deleteQuery = "DELETE FROM mesuesi_lendet WHERE mesuesi_id = ?";
            try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
                deletePs.setInt(1, teacherId);
                deletePs.executeUpdate();
            }

            // 2. Insert new associations
            if (subjectIds != null && !subjectIds.isEmpty()) {
                String insertQuery = "INSERT INTO mesuesi_lendet (mesuesi_id, lenda_id) VALUES (?, ?)";
                try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
                    for (int subjectId : subjectIds) {
                        insertPs.setInt(1, teacherId);
                        insertPs.setInt(2, subjectId);
                        insertPs.addBatch();
                    }
                    insertPs.executeBatch();
                }
            }

            conn.commit();
            return true;
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
}
