package br.mackenzie.webapp.Secretaria;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="secretarias")
public class Secretaria {

    @Id @GeneratedValue
    private long id;

    private String local;
    private String criterios;
    private String arquivo;

    public Secretaria() {
        super();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getLocal() {
        return local;
    }
    public void setLocal(String local) {
        this.local = local;
    }

    public String getCriterios() {
        return criterios;
    }
    public void setCriterios(String criterios) {
        this.criterios = criterios;
    }

    public String getArquivo() {
        return arquivo;
    }
    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }
}
