package controllers;

import dao.MesuesiDAO;
import dao.MesuesiLendaDAO;
import models.Mesuesi;

import java.util.List;

public class MesuesiController {
    private final MesuesiDAO mesuesiDAO = new MesuesiDAO();
    private final MesuesiLendaDAO mesuesiLendaDAO = new MesuesiLendaDAO();

    public List<Mesuesi> getAllTeachers() {
        return mesuesiDAO.getAllMesuesit();
    }

    public Mesuesi getTeacherById(int id) {
        return mesuesiDAO.getMesuesiById(id);
    }

    public boolean addTeacher(Mesuesi teacher) {
        return mesuesiDAO.addMesuesi(teacher) != -1;
    }

    public boolean updateTeacher(Mesuesi teacher) {
        return mesuesiDAO.updateMesuesi(teacher);
    }

    public boolean deleteTeacher(int id) {
        return mesuesiDAO.deleteMesuesi(id);
    }

    public List<Integer> getAssignedSubjectIds(int teacherId) {
        return mesuesiLendaDAO.getLendaIdsForTeacher(teacherId);
    }

    public boolean assignSubjectsToTeacher(int teacherId, List<Integer> subjectIds) {
        return mesuesiLendaDAO.assignSubjectsToTeacher(teacherId, subjectIds);
    }
}
