package models;

public class Mesuesi {
    private int id;
    private String emri;
    private String mbiemri;
    private String email;
    private String telefon;

    public Mesuesi() {}

    public Mesuesi(int id, String emri, String mbiemri, String email, String telefon) {
        this.id = id;
        this.emri = emri;
        this.mbiemri = mbiemri;
        this.email = email;
        this.telefon = telefon;
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

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    @Override
    public String toString() {
        return emri + " " + mbiemri;
    }
}
