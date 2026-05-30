package models;

import java.util.Objects;

public class Lenda {
    private int id;
    private String emriLendes;
    private String pershkrimi;
    private int kreditet;

    public Lenda() {}

    public Lenda(int id, String emriLendes, String pershkrimi, int kreditet) {
        this.id = id;
        this.emriLendes = emriLendes;
        this.pershkrimi = pershkrimi;
        this.kreditet = kreditet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmriLendes() {
        return emriLendes;
    }

    public void setEmriLendes(String emriLendes) {
        this.emriLendes = emriLendes;
    }

    public String getPershkrimi() {
        return pershkrimi;
    }

    public void setPershkrimi(String pershkrimi) {
        this.pershkrimi = pershkrimi;
    }

    public int getKreditet() {
        return kreditet;
    }

    public void setKreditet(int kreditet) {
        this.kreditet = kreditet;
    }

    @Override
    public String toString() {
        return emriLendes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lenda lenda = (Lenda) o;
        return id == lenda.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
