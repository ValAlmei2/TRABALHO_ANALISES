package br.mackenzie.webapp.Coordenacao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Coordenacao")
public class Coordenacao {

	@Id @GeneratedValue
	private long id;

	private int id_coordenacao;
	private String area;
		
	public Coordenacao() {
		super();
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getid_coordenacao() {
		return id_coordenacao;
	}
	public void seid_coordenacao(int id_coordenacao) {
		this.id_coordenacao = id_coordenacao;
	}
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}


}