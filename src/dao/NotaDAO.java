package dao;

import db.DBConnection;
import models.Nota;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotaDAO {

    public List<Nota> getAllNotat() {
        return getNotatWithFilters(null, null, null);
    }

    public List<Nota> getNotatWithFilters(Integer studentId, Integer subjectId, String lloji) {
        List<Nota> list = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT n.id, n.nxenesi_id, n.lenda_id, n.mesuesi_id, n.nota, n.lloji, n.data_dhënies, " +
            "       nx.emri AS nx_emri, nx.mbiemri AS nx_mbiemri, " +
            "       l.emri_lendes AS l_emri, " +
            "       m.emri AS m_emri, m.mbiemri AS m_mbiemri " +
            "FROM notat n " +
            "JOIN nxenesit nx ON n.nxenesi_id = nx.id " +
            "JOIN lendet l ON n.lenda_id = l.id " +
            "LEFT JOIN mesuesit m ON n.mesuesi_id = m.id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (studentId != null && studentId > 0) {
            query.append("AND n.nxenesi_id = ? ");
            params.add(studentId);
        }
        if (subjectId != null && subjectId > 0) {
            query.append("AND n.lenda_id = ? ");
            params.add(subjectId);
        }
        if (lloji != null && !lloji.trim().isEmpty() && !lloji.equals("Te gjitha")) {
            query.append("AND n.lloji = ? ");
            params.add(lloji);
        }

        query.append("ORDER BY n.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNota(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Nota> getNotatByStudentId(int studentId) {
        List<Nota> list = new ArrayList<>();
        String query = "SELECT n.id, n.nxenesi_id, n.lenda_id, n.mesuesi_id, n.nota, n.lloji, n.data_dhënies, " +
                       "       nx.emri AS nx_emri, nx.mbiemri AS nx_mbiemri, " +
                       "       l.emri_lendes AS l_emri, " +
                       "       m.emri AS m_emri, m.mbiemri AS m_mbiemri " +
                       "FROM notat n " +
                       "JOIN nxenesit nx ON n.nxenesi_id = nx.id " +
                       "JOIN lendet l ON n.lenda_id = l.id " +
                       "LEFT JOIN mesuesit m ON n.mesuesi_id = m.id " +
                       "WHERE n.nxenesi_id = ? " +
                       "ORDER BY n.data_dhënies DESC, n.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNota(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Nota> getNotatByTeacherAndSubject(int teacherId, int subjectId) {
        List<Nota> list = new ArrayList<>();
        String query = "SELECT n.id, n.nxenesi_id, n.lenda_id, n.mesuesi_id, n.nota, n.lloji, n.data_dhënies, " +
                       "       nx.emri AS nx_emri, nx.mbiemri AS nx_mbiemri, " +
                       "       l.emri_lendes AS l_emri, " +
                       "       m.emri AS m_emri, m.mbiemri AS m_mbiemri " +
                       "FROM notat n " +
                       "JOIN nxenesit nx ON n.nxenesi_id = nx.id " +
                       "JOIN lendet l ON n.lenda_id = l.id " +
                       "LEFT JOIN mesuesit m ON n.mesuesi_id = m.id " +
                       "WHERE n.mesuesi_id = ? AND n.lenda_id = ? " +
                       "ORDER BY n.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, teacherId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNota(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addNota(Nota nota) {
        String query = "INSERT INTO notat (nxenesi_id, lenda_id, mesuesi_id, nota, lloji, data_dhënies) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, nota.getNxenesiId());
            ps.setInt(2, nota.getLendaId());
            if (nota.getMesuesiId() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, nota.getMesuesiId());
            }
            ps.setDouble(4, nota.getNota());
            ps.setString(5, nota.getLloji());
            if (nota.getDataDhenies() != null) {
                ps.setDate(6, nota.getDataDhenies());
            } else {
                ps.setDate(6, new Date(System.currentTimeMillis()));
            }
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateNota(Nota nota) {
        String query = "UPDATE notat SET nxenesi_id = ?, lenda_id = ?, mesuesi_id = ?, nota = ?, lloji = ?, data_dhënies = ? " +
                       "WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, nota.getNxenesiId());
            ps.setInt(2, nota.getLendaId());
            if (nota.getMesuesiId() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, nota.getMesuesiId());
            }
            ps.setDouble(4, nota.getNota());
            ps.setString(5, nota.getLloji());
            ps.setDate(6, nota.getDataDhenies());
            ps.setInt(7, nota.getId());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteNota(int id) {
        String query = "DELETE FROM notat WHERE id = ?";
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

    public double getAverageGradeForStudent(int studentId) {
        String query = "SELECT AVG(nota) AS average FROM notat WHERE nxenesi_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("average");
                    // Round to 2 decimal places
                    return Math.round(avg * 100.0) / 100.0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Nota mapResultSetToNota(ResultSet rs) throws SQLException {
        Nota n = new Nota();
        n.setId(rs.getInt("id"));
        n.setNxenesiId(rs.getInt("nxenesi_id"));
        n.setLendaId(rs.getInt("lenda_id"));
        
        int mesuesiId = rs.getInt("mesuesi_id");
        if (rs.wasNull()) {
            n.setMesuesiId(null);
        } else {
            n.setMesuesiId(mesuesiId);
        }
        
        n.setNota(rs.getDouble("nota"));
        n.setLloji(rs.getString("lloji"));
        n.setDataDhenies(rs.getDate("data_dhënies"));

        // Set helper display fields
        n.setEmriNxenesit(rs.getString("nx_em"));
        n.setMbiemriNxenesit(rs.getString("nx_mbiemri"));
        n.setEmriLendes(rs.getString("l_emri"));
        n.setEmriMesuesit(rs.getString("m_emri"));
        n.setMbiemriMesuesit(rs.getString("m_mbiemri"));
        return n;
    }
}
