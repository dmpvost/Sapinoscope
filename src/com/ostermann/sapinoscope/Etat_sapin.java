package com.ostermann.sapinoscope;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Etat_sapin{
	public Object_sapin sapin;
	public Object_infoSapin infoSapin;
	
	Etat_sapin(Object_sapin s, Object_infoSapin is)
	{
		sapin =s;
		infoSapin = is;
	}
	// retourne la liste de toutes les infos connues pour ce point donne de ce secteur
	public static Vector<Etat_sapin> createListOfInfoSapinFromXY(int secteurID, int x, int y)
	{
		Vector<Etat_sapin> liste = new Vector<Etat_sapin>();
		
		String requette = "SELECT * FROM SAPIN INNER JOIN INFO_SAPIN USING(SAP_ID)"
								+ " WHERE SAPIN.SEC_ID="+secteurID
								+ " AND SAPIN.SAP_LIG="+x
								+ " AND SAPIN.SAP_COL="+y
								+ " ORDER BY INF_SAP_DATE DESC";
	
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		
		Cursor c = db.rawQuery(requette, null);
		if(c.getCount() >0)
		{
			c.moveToFirst();
			
			do{
				Object_sapin sapin = new Object_sapin(c);
				Object_infoSapin info = new Object_infoSapin(c);
            	liste.add(new Etat_sapin(sapin, info));
            }while(c.moveToNext());
			
			return liste;
		}
		return null;
	}
}
