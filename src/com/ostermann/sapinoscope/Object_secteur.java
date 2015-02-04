package com.ostermann.sapinoscope;

public class Object_secteur {

	private int id;
	private int id_parc;
	private String name;
	private float angle;
	private float coef_croissance;
	
	public Object_secteur() {
		id=0;
		id_parc=0;
		name="vide";
		angle=0;
		coef_croissance=1;
	}
	
	public Object_secteur(int id, int id_parc,String name,float angle, float coef_croissance) {
		this.id=id;
		this.id_parc=id_parc;
		this.name=name;
		this.angle=angle;
		this.coef_croissance=coef_croissance;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_parc() {
		return id_parc;
	}

	public void setId_parc(int id_parc) {
		this.id_parc = id_parc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getCoef_croissance() {
		return coef_croissance;
	}

	public void setCoef_croissance(float coef_croissance) {
		this.coef_croissance = coef_croissance;
	}
	
	
	public String toString() {
        return this.name + " [" + this.coef_croissance + "]";
    }
	
}
