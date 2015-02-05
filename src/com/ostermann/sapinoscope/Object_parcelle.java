package com.ostermann.sapinoscope;

import java.util.Vector;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;

public class Object_parcelle {

	private int id;
	private String name;
	private String description;
	private float coef;

	private static String log_name_activity = "Object_Parcelle";

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

	public Object_parcelle(int parc_id)
	{
		Object_parcelle parcelle = new Object_parcelle();

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM PARCELLE WHERE PARC_ID="+parc_id;
			Log.i(log_name_activity+"/Object_parcelle(int parc_id)",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				do{
					parcelle.setId(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
					parcelle.setName(c.getString(c.getColumnIndex("PARC_N")));
					parcelle.setDescription(c.getString(c.getColumnIndex("PARC_DESC")));
					parcelle.setCoef(Float.parseFloat(c.getString(c.getColumnIndex("PARC_COEF"))));
					Log.i(log_name_activity+"/Object_parcelle(int parc_id)","ID:"+parcelle.getId()+" nom:"+parcelle.getName()+" desc:"+parcelle.getDescription()+" coef:"+parcelle.getCoef());
				}while(c.moveToNext());
			}
			Log.i(log_name_activity+"/Object_parcelle(int parc_id)", "Create : "+selectQuery);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity+"/Object_parcelle(int parc_id)", "Sortie en erreur");
		}

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
		return this.name + " [" + this.description + "]";
	}

	public static Vector<Object_parcelle> createListOfAllParcelle()
	{
		Vector<Object_parcelle> liste = new Vector<Object_parcelle>();

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM PARCELLE";
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>=0)
			{
	            do
	            {
	            	Object_parcelle parcelle= new Object_parcelle();
	            	parcelle.setId(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
	            	parcelle.setName(c.getString(c.getColumnIndex("PARC_N")));
	            	parcelle.setDescription(c.getString(c.getColumnIndex("PARC_DESC")));
	            	parcelle.setCoef(Float.parseFloat(c.getString(c.getColumnIndex("PARC_COEF"))));
	            	liste.add(parcelle);
	                Log.i(log_name_activity+"/createListOfAllParcelle","ID:"+parcelle.getId()+" nom:"+parcelle.getName()+" desc:"+parcelle.getDescription()+" coef:"+parcelle.getCoef());
	            }while(c.moveToNext());
	        }
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity+"/createListOfAllParcelle", "Sortie en erreur");
		}
		return liste;
	}


}
