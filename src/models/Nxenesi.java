package models;

import java.sql.Date;

public class Nxenesi {
    private int id;
    private String emri;
    private String mbiemri;
    private String email;
    private Date dataLindjes;
    private String klasa;
    private Date dataRegjistrimit;

    public Nxenesi() {}

    public Nxenesi(int id, String emri, String mbiemri, String email, Date dataLindjes, String klasa, Date dataRegjistrimit) {
        this.id = id;
        this.emri = emri;
        this.mbiemri = mbiemri;
        this.email = email;
        this.dataLindjes = dataLindjes;
        this.klasa = klasa;
        this.dataRegjistrimit = dataRegjistrimit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmri() {
        return emri;
    }

    public void setEmri(String emri) {
        this.emri = emri;
    }

    public String getMbiemri() {
        return mbiemri;
    }

    public void setMbiemri(String mbiemri) {
        this.mbiemri = mbiemri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataLindjes() {
        return dataLindjes;
    }

    public void setDataLindjes(Date dataLindjes) {
        this.dataLindjes = dataLindjes;
    }

    public String getKlasa() {
        return klasa;
    }

    public void setKlasa(String klasa) {
        this.klasa = klasa;
    }

    public Date getDataRegjistrimit() {
        return dataRegjistrimit;
    }

    public void setDataRegjistrimit(Date dataRegjistrimit) {
        this.dataRegjistrimit = dataRegjistrimit;
    }

    @Override
    public String toString() {
        return emri + " " + mbiemri + " (" + klasa + ")";
    }
}
