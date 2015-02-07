package com.ostermann.sapinoscope;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ostermann.sapinoscope.Object_sapin.Status_sapin;

public class Object_infoSapin {
	private int inf_sap_id;
	private int annee;
	private int sap_id;
	public float taille;
	public Status_sapin status;
	
	public int getInfoSapinID()
	{
		return inf_sap_id;
	}
	
	public Object_infoSapin(int id_sapin, int an)
	{
		String requetteSelect = "SELECT * FROM INFO_SAPIN WHERE SAP_ID="+id_sapin+" AND ANN_ID="+an;
		
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		Cursor c = db.rawQuery(requetteSelect, null);
		
		if(c.getCount()>1)
			Log.w("Object_infoSapin", "multiple entrees trouvees dans la table info sapin pour les valeurs : sapinID:"+id_sapin+" annee:"+an);
		
		if(c.getCount()!=0)
		{
			c.moveToFirst();
			inf_sap_id = c.getInt(c.getColumnIndex("INF_SAP_ID"));
			annee = an;
			sap_id = id_sapin;
			taille = c.getInt(c.getColumnIndex("INF_SAP_TAIL"));
			status = Status_sapin.fromInt(c.getInt(c.getColumnIndex("INF_SAP_STATUS")));
		}
		else// Nouvel object, on lui donne les valeurs par default
		{
			inf_sap_id=-1;
			annee=an;
			sap_id=id_sapin;
			taille=-1;
			status = status.INDEFINI;
		}
	}
	
	public void saveInDb()
	{
		if(annee == -1 || sap_id == -1 || taille == -1 || status == Status_sapin.INDEFINI)
			Log.w("Object_infoSapin", "Enregistrement d'un infoSapin contenant des erreurs dans la base : annee=="+annee+" sap_id=="+sap_id+" taille=="+taille+" status=="+status);
		
		String requette;
		
		if(inf_sap_id==-1)//infoSapin inconnu dans la base, ajout
		{
    		requette = "INSERT INTO INFO_SAPIN ('ANN_ID',			'SAP_ID',			'INF_SAP_TAIL',		'INF_SAP_STATUS')"
    						+"VALUES(	   		'"+annee +"',		'"+sap_id+"',		'"+taille+"',		'"+status+"');";
	    
		}else//l' infoSapin vient de la base, il faut donc mettre a jour ses infos
		{
			requette = 			  "UPDATE INFO_SAPIN SET "
								+ "ANN_ID=			'"+ annee  	+"',"
								+ "SAP_ID=			'"+ sap_id  +"',"
								+ "INF_SAP_TAIL=	'"+ taille  +"',"
								+ "INF_SAP_STATUS=	'"+ status 	+"'"
								+ "WHERE INF_SAP_ID="+inf_sap_id;
		}
		
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	db.execSQL(requette);
    	
    	if(inf_sap_id==-1)// L'infoSapin vient d'etre ajoute dans la base. On va maintenant recupere son id
    	{
	    		requette= "SELECT SAP_ID FROM INFO_SAPIN WHERE "
	    				+ "ANN_ID=			'"+ annee  	+"' AND "
						+ "SAP_ID=			'"+ sap_id  +"' AND "
						+ "INF_SAP_TAIL=	'"+ taille  +"' AND "
						+ "INF_SAP_STATUS=	'"+ status 	+"'";

			Cursor c = db.rawQuery(requette, null);
			c.moveToFirst();
			sap_id = c.getInt(0);
    	}
	}
}
