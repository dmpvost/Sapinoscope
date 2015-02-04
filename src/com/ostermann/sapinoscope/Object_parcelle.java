package com.ostermann.sapinoscope;

public class Object_parcelle {

	private int id;
	private String name;
	private String description;
	private float coef;
	
	public Object_parcelle() {
		id=0;
		name="vide";
		description="vide";
		coef=1;
	}
	
	public Object_parcelle(int id, String name,String description,int coef) {
		this.id=id;
		this.name=name;
		this.description=description;
		this.coef=coef;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getCoef() {
		return coef;
	}

	public void setCoef(float coef) {
		this.coef = coef;
	}
	
	public String toString() {
        return this.id + ". " + this.name + " [" + this.description + "]";
    }
	
	
}
