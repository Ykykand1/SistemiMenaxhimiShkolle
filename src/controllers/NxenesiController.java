package controllers;

import dao.NxenesiDAO;
import models.Nxenesi;

import java.util.List;

public class NxenesiController {
    private final NxenesiDAO nxenesiDAO = new NxenesiDAO();

    public List<Nxenesi> getAllStudents() {
        return nxenesiDAO.getAllNxenesit();
    }

    public Nxenesi getStudentById(int id) {
        return nxenesiDAO.getNxenesiById(id);
    }

    public List<Nxenesi> searchStudents(String searchText) {
        return nxenesiDAO.searchNxenesit(searchText);
    }

    public boolean addStudent(Nxenesi student) {
        return nxenesiDAO.addNxenesi(student) != -1;
    }

    public boolean updateStudent(Nxenesi student) {
        return nxenesiDAO.updateNxenesi(student);
    }

    public boolean deleteStudent(int id) {
        return nxenesiDAO.deleteNxenesi(id);
    }
}
