package models;

import java.sql.Date;

public class Nota {
    private int id;
    private int nxenesiId;
    private int lendaId;
    private Integer mesuesiId; // Can be null if teacher is deleted (ON DELETE SET NULL)
    private double nota;
    private String lloji;
    private Date dataDhenies;

    // Helper display fields
    private String emriNxenesit;
    private String mbiemriNxenesit;
    private String emriLendes;
    private String emriMesuesit;
    private String mbiemriMesuesit;

    public Nota() {}

    public Nota(int id, int nxenesiId, int lendaId, Integer mesuesiId, double nota, String lloji, Date dataDhenies) {
        this.id = id;
        this.nxenesiId = nxenesiId;
        this.lendaId = lendaId;
        this.mesuesiId = mesuesiId;
        this.nota = nota;
        this.lloji = lloji;
        this.dataDhenies = dataDhenies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNxenesiId() {
        return nxenesiId;
    }

    public void setNxenesiId(int nxenesiId) {
        this.nxenesiId = nxenesiId;
    }

    public int getLendaId() {
        return lendaId;
    }

    public void setLendaId(int lendaId) {
        this.lendaId = lendaId;
    }

    public Integer getMesuesiId() {
        return mesuesiId;
    }

    public void setMesuesiId(Integer mesuesiId) {
        this.mesuesiId = mesuesiId;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getLloji() {
        return lloji;
    }

    public void setLloji(String lloji) {
        this.lloji = lloji;
    }

    public Date getDataDhenies() {
        return dataDhenies;
    }

    public void setDataDhenies(Date dataDhenies) {
        this.dataDhenies = dataDhenies;
    }

    public String getEmriNxenesit() {
        return emriNxenesit;
    }

    public void setEmriNxenesit(String emriNxenesit) {
        this.emriNxenesit = emriNxenesit;
    }

    public String getMbiemriNxenesit() {
        return mbiemriNxenesit;
    }

    public void setMbiemriNxenesit(String mbiemriNxenesit) {
        this.mbiemriNxenesit = mbiemriNxenesit;
    }

    public String getEmriLendes() {
        return emriLendes;
    }

    public void setEmriLendes(String emriLendes) {
        this.emriLendes = emriLendes;
    }

    public String getEmriMesuesit() {
        return emriMesuesit;
    }

    public void setEmriMesuesit(String emriMesuesit) {
        this.emriMesuesit = emriMesuesit;
    }

    public String getMbiemriMesuesit() {
        return mbiemriMesuesit;
    }

    public void setMbiemriMesuesit(String mbiemriMesuesit) {
        this.mbiemriMesuesit = mbiemriMesuesit;
    }

    public String getStudentFullName() {
        if (emriNxenesit != null && mbiemriNxenesit != null) {
            return emriNxenesit + " " + mbiemriNxenesit;
        }
        return "N/A";
    }

    public String getTeacherFullName() {
        if (emriMesuesit != null && mbiemriMesuesit != null) {
            return emriMesuesit + " " + mbiemriMesuesit;
        }
        return "Deleted Teacher";
    }
}
