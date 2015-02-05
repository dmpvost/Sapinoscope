package com.ostermann.sapinoscope;

import java.util.Vector;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;

public class Object_secteur {

	private int id;
	private int id_parc;
	private String name;
	private float angle;
	private float coef_croissance;
	private static String log_name_activity = "Object_Secteur";

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

	public Object_secteur(int parcelle_id)
	{
		Object_secteur secteur = new Object_secteur();
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{

			String selectQuery = "SELECT * FROM SECTEUR WHERE PARC_ID="+parcelle_id;
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				do{
					secteur.setId(Integer.parseInt(c.getString(c.getColumnIndex("SEC_ID"))));
					secteur.setId_parc(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
					secteur.setName(c.getString(c.getColumnIndex("SEC_N")));
					secteur.setAngle(Float.parseFloat(c.getString(c.getColumnIndex("SEC_ANGLE"))));
					secteur.setCoef_croissance(Float.parseFloat(c.getString(c.getColumnIndex("SEC_CROIS"))));
				}while(c.moveToNext());
			}
			Log.i(log_name_activity+"/Object_secteur(int parcelle_id)", "Create : "+selectQuery);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity+"/Object_secteur(int parcelle_id)", "Sortie en erreur");
		}



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
	
	public static Vector<Object_secteur> createListOfSecteur(int parc_id)
	{
		Vector<Object_secteur> liste = new Vector<Object_secteur>();

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM SECTEUR WHERE PARC_ID="+parc_id;
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
	            do{
	            	Object_secteur secteur= new Object_secteur();
	            	secteur.setId(Integer.parseInt(c.getString(c.getColumnIndex("SEC_ID"))));
	            	secteur.setId_parc(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
	            	secteur.setName(c.getString(c.getColumnIndex("SEC_N")));
	            	secteur.setAngle(Float.parseFloat(c.getString(c.getColumnIndex("SEC_ANGLE"))));
	            	secteur.setCoef_croissance(Float.parseFloat(c.getString(c.getColumnIndex("SEC_CROIS"))));
	            	Log.i(log_name_activity+"/createListOfSecteur", "ID:"+secteur.getId()+" PARC_ID:"+secteur.getId_parc()+" NAME:"+secteur.getName());
	            	liste.add(secteur);
	            }while(c.moveToNext());
	        }
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity+"/createListOfSecteur", "Sortie en erreur");
		}
		return liste;
	}

	
}
