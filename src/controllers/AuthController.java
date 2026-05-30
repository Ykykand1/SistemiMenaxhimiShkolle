package controllers;

import dao.UserDAO;
import dao.NxenesiDAO;
import dao.MesuesiDAO;
import models.User;
import models.Nxenesi;
import models.Mesuesi;
import db.DBConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;

public class AuthController {
    private final UserDAO userDAO = new UserDAO();
    private final NxenesiDAO nxenesiDAO = new NxenesiDAO();
    private final MesuesiDAO mesuesiDAO = new MesuesiDAO();

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("SHA-256 algorithm not found", ex);
        }
    }

    public User login(String username, String password) {
        String hashedPassword = hashPassword(password);
        return userDAO.authenticate(username, hashedPassword);
    }

    public boolean isUsernameTaken(String username) {
        return userDAO.isUsernameExists(username);
    }

    public boolean signUpStudent(String username, String password, Nxenesi nxenesi) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Add student using the same connection so both inserts are transactional
            int studentId = nxenesiDAO.addNxenesi(conn, nxenesi);
            if (studentId == -1) {
                conn.rollback();
                return false;
            }

            // 2. Add user account using the same connection
            String hashedPassword = hashPassword(password);
            boolean userCreated = userDAO.createUser(conn, username, hashedPassword, "student", studentId);
            if (!userCreated) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
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

    public boolean signUpTeacher(String username, String password, Mesuesi mesuesi) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Add teacher using the same connection so both inserts are transactional
            int teacherId = mesuesiDAO.addMesuesi(conn, mesuesi);
            if (teacherId == -1) {
                conn.rollback();
                return false;
            }

            // 2. Add user account using the same connection
            String hashedPassword = hashPassword(password);
            boolean userCreated = userDAO.createUser(conn, username, hashedPassword, "teacher", teacherId);
            if (!userCreated) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
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
