package com.ostermann.sapinoscope;

import java.util.Vector;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Variete_sapin {

	private int var_id;
	private String var_nom;
	private float var_coef;
	private static String log_name_activity ="Object_Variété";
	
	public Variete_sapin()
	{
		var_id=0;
		var_nom="";
		var_coef=0;
	}
	
	public Variete_sapin(int var_id,String var_nom, float var_coef)
	{
		this.var_id = var_id;
		this.var_nom = var_nom;
		this.var_coef = var_coef;
	}

	public int getVar_id() {
		return var_id;
	}

	public void setVar_id(int var_id) {
		this.var_id = var_id;
	}

	public String getVar_nom() {
		return var_nom;
	}

	public void setVar_nom(String var_nom) {
		this.var_nom = var_nom;
	}

	public float getVar_coef() {
		return var_coef;
	}

	public void setVar_coef(float var_coef) {
		this.var_coef = var_coef;
	}
	
	
	public static Vector<Variete_sapin> createListOfAllParcelle()
	{
		Vector<Variete_sapin> liste = new Vector<Variete_sapin>();

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM VARIETE";
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>=0)
			{
	            do
	            {
	            	Variete_sapin variete= new Variete_sapin();
	            	variete.setVar_id(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
	            	variete.setVar_nom(c.getString(c.getColumnIndex("PARC_N")));
	            	variete.setVar_coef(Float.parseFloat(c.getString(c.getColumnIndex("PARC_COEF"))));
	            	liste.add(variete);
	                //Log.i(log_name_activity,"ID:"+parcelle.getId()+" nom:"+parcelle.getName()+" desc:"+parcelle.getDescription()+" coef:"+parcelle.getCoef());
	            }while(c.moveToNext());
	        }
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity, "Sortie en erreur");
		}
		return liste;
	}
	
	
}
