package com.ostermann.sapinoscope;

import java.util.Vector;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;



public class Object_sapin {
	public enum Status_sapin {
		INDEFINI(-1),
		VIDE(0),
		NOUVEAU(1),
		OK(2),
		TOC(3);
	    private final int code;

	    private Status_sapin(int code) {
	        this.code = code;
	    }

	    public int toInt() {
	        return code;
	    }

	    public String toString() {
	        return String.valueOf(code);
	    }
	    
	    static public Status_sapin fromInt(int s)
	    {
	    	switch(s)
	    	{
	    	case 0:
	    		return VIDE;
	    	case 1:
	    		return NOUVEAU;
	    	case 2:
	    		return OK;
	    	case 3: 
	    		return TOC;
    		default:
    			return INDEFINI;
	    	}
	    }
	}
	private int sap_id;
	public int var_id;
	public int sec_id;
	public int xLigne;
	public int yColonne;
	public Bitmap photo;
	public Status_sapin status;
	public int coord_id;
	public Point2D coordonne;
	
	Object_sapin()
	{
		sap_id=-1;
		status=Status_sapin.INDEFINI;
		sec_id=-1;
		var_id=-1;
		xLigne=0;
		yColonne=0;
		coord_id=-1;
		coordonne= new Point2D(0,0);
	}
		
	Object_sapin(Status_sapin s, int secteurID, int varieteID, int x, int y)
	{
		sap_id=-1;
		status=s;
		sec_id=secteurID;
		var_id=varieteID;
		xLigne=x;
		yColonne=y;
		coord_id=-1;
		coordonne= new Point2D(0,0);
	}
	
	Object_sapin(Status_sapin s, int secteurID, int varieteID, int x, int y, int coordID, Point2D coord)
	{
		sap_id=-1;
		status=s;
		sec_id=secteurID;
		var_id=varieteID;
		xLigne=x;
		yColonne=y;
		coord_id=-coordID;
		coordonne=coord;
	}
	
	public void saveInDb(boolean saveCoordId)
	{
		String requette;
		if(saveCoordId && coord_id == -1)
		{
			Log.e("ObjectSapin","Demande d'enregistrement dans la base d'une coordonnee non renseigne, sapin sauvegarde sans coordonne");
			saveCoordId = false;
		}
		
		if(sap_id==-1)//sapin inconnu dans la base, ajout
		{
			if(saveCoordId)// on enregistre l'id des coordonnes
			{
				requette = "INSERT INTO SAPIN ('VAR_ID',			'SEC_ID',			'SAP_LIG',		'SAP_COL',		'SAP_STATUS',	'COORD_ID')"
									+"VALUES(		'"+var_id +"',		'"+sec_id+"',		'"+xLigne+"',	'"+yColonne+"',	'"+status+"',	'"+coord_id+"');";
			}
	    	else// on enregistre pas l'id des coordonnes
	    	{
	    		requette = "INSERT INTO SAPIN ('VAR_ID',			'SEC_ID',			'SAP_LIG',		'SAP_COL',		'SAP_STATUS')"
	    							+"VALUES(		'"+var_id +"',		'"+sec_id+"',		'"+xLigne+"',	'"+yColonne+"',	'"+status+"');";
	    	}
		}else//le sapin vient de la base, il faut donc mettre a jour ses infos
		{
			requette = 			  "UPDATE SAPIN SET "
								+ "VAR_ID=		'"+ var_id   +"',"
								+ "SEC_ID=		'"+ sec_id   +"',"
								+ "SAP_LIG=		'"+ xLigne   +"',"
								+ "SAP_COL=	   	'"+ yColonne +"',"
								+ "SAP_COL=	   	'"+ yColonne +"',"
							    + "SAP_STATUS= 	'"+ status 	 +"'";
	    if(saveCoordId)requette	+=",COORD_ID= 	'"+ coord_id +"'";
					   requette +="WHERE SAP_ID="+sap_id;
		}
		
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	db.execSQL(requette);
	}
	
	public static Vector<Object_sapin> createListOfSapin(int secteurID)
	{
		Vector<Object_sapin> liste = new Vector<Object_sapin>();

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			//LEFT JOIN pour selectionner tous les sapin, meme ceux qui n'ont pas de coordonne.
			String selectQuery = "SELECT * FROM SAPIN LEFT JOIN COORDONNEE USING(COORD_ID) WHERE SEC_ID="+secteurID;
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
	            do{
	            	Object_sapin sapin= new Object_sapin();
	            	sapin.sap_id = c.getInt(c.getColumnIndex("SAP_ID"));
	            	sapin.var_id = c.getInt(c.getColumnIndex("VAR_ID"));
	            	sapin.sec_id = c.getInt(c.getColumnIndex("SEC_ID"));
	            	sapin.xLigne = c.getInt(c.getColumnIndex("SAP_LIG"));
	            	sapin.yColonne = c.getInt(c.getColumnIndex("SAP_COL"));
	            	sapin.status = Status_sapin.fromInt((c.getInt(c.getColumnIndex("SAP_STATUS"))));
	            	try
	            	{
	            		sapin.coord_id = c.getInt(c.getColumnIndex("COORD_ID"));
	            		double lat = c.getDouble(c.getColumnIndex("COORD_LAT"));
	            		double lon = c.getDouble(c.getColumnIndex("COORD_LON"));
	            		sapin.coordonne = new Point2D(lat,lon);
	            	}
	            	catch(Exception e)
	            	{
	            		//Le sapin n'a pas de coordonne (COORD_ID==null)
	            	}
	            	Log.i("ObjectSapin", "ID:"+sapin.sap_id+" VAR_ID:"+sapin.var_id+" COORD:"+sapin.coordonne.toString());
	            	liste.add(sapin);
	            }while(c.moveToNext());
	        }
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.e("ObjectSapin", "Sortie en erreur");
		}
		return liste;
	}
}