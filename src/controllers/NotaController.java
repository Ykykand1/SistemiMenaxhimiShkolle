package controllers;

import dao.NotaDAO;
import models.Nota;

import java.util.List;

public class NotaController {
    private final NotaDAO notaDAO = new NotaDAO();

    public List<Nota> getAllGrades() {
        return notaDAO.getAllNotat();
    }

    public List<Nota> getGradesWithFilters(Integer studentId, Integer subjectId, String lloji) {
        return notaDAO.getNotatWithFilters(studentId, subjectId, lloji);
    }

    public List<Nota> getGradesByStudentId(int studentId) {
        return notaDAO.getNotatByStudentId(studentId);
    }

    public List<Nota> getGradesByTeacherAndSubject(int teacherId, int subjectId) {
        return notaDAO.getNotatByTeacherAndSubject(teacherId, subjectId);
    }

    public boolean addGrade(Nota grade) {
        validateGradeValue(grade.getNota());
        return notaDAO.addNota(grade);
    }

    public boolean updateGrade(Nota grade) {
        validateGradeValue(grade.getNota());
        return notaDAO.updateNota(grade);
    }

    public boolean deleteGrade(int id) {
        return notaDAO.deleteNota(id);
    }

    public double getAverageGradeForStudent(int studentId) {
        return notaDAO.getAverageGradeForStudent(studentId);
    }

    private void validateGradeValue(double nota) {
        if (nota < 1.0 || nota > 10.0) {
            throw new IllegalArgumentException("Nota duhet te jete midis 1.0 dhe 10.0!");
        }
    }
}
