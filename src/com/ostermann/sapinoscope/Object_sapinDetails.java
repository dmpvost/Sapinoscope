package com.ostermann.sapinoscope;

import java.util.Vector;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class Object_sapinDetails {

	
	int id;
	int ligne;
	int colonne;
	int status;
	String variete;
	int taille;
	int numero;
	
	
	public Object_sapinDetails()
	{
		id=0;
		ligne=0;
		colonne=0;
		status=0;
		variete="null";
		taille=0;
		numero=0;
	}


	public int getNumero() {
		return numero;
	}


	public void setNumero(int numero) {
		this.numero = numero;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getLigne() {
		return ligne;
	}


	public void setLigne(int ligne) {
		this.ligne = ligne;
	}


	public int getColonne() {
		return colonne;
	}


	public void setColonne(int colonne) {
		this.colonne = colonne;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public String getVariete() {
		return variete;
	}


	public void setVariete(String variete) {
		this.variete = variete;
	}


	public int getTaille() {
		return taille;
	}


	public void setTaille(int taille) {
		this.taille = taille;
	}


	@Override
	public String toString() {
		
		String s = "";
		if( status == 0)
		{
			s = numero+": Erreur "+status+" l:"+ligne+" c;"+colonne;
		}
		else if ( status == 1)
		{
			s = numero+": Jeune plant - "+variete;
		}
		else if ( status == 2)
		{
			s = numero+": "+taille+"m "+variete;
		}
		else if (status ==3)
		{
			s = numero+": Souche";
		}
		return s;
	}
	 
	 
	public static Vector<Object_sapinDetails> createListOfSapin_Y(int secteur_id, int y, int limit)
	{
		Vector<Object_sapinDetails> L_sapinD = new Vector<Object_sapinDetails>();

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			 
			String selectQuery =  " SELECT						"
								+ "   S.SAP_ID 			AS ID 	"
								+ "	 ,S.SAP_LIG 		AS X 	"
								+ "  ,S.SAP_COL  		AS Y 	"
								+ "	 ,I.INF_SAP_STATUS 	AS ST	"
								+ "	 ,V.VAR_NOM 		AS VAR 	"
								+ "	 ,(SELECT 					"
								+ "			INF.INF_SAP_TAIL 	"
								+ "		FROM					"
								+ "			INFO_SAPIN INF 		"
								+ "		ORDER BY 				"
								+ "			INF.INF_SAP_DATE DESC "
								+ "		LIMIT 1					"
								+ "	  ) AS TAILLE	 			"				
								+ "	FROM						"
								+ "		SAPIN S 				"
								+ "		INNER JOIN VARIETE V 	"
								+ "			ON S.VAR_ID=V.VAR_ID"
								+ "		INNER JOIN INFO_SAPIN I "
								+ " 		ON I.SAP_ID=S.SAP_ID"
								+ " WHERE 						"
								+ "		SEC_ID="+secteur_id
								+ "	AND							"
								+ "		SAP_COL="+y
								+ "	ORDER BY					"
								+ "		S.SAP_LIG ASC";
			if (limit!=0)
				selectQuery = selectQuery+ "LIMIT "+limit;
			
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			int i=1;
			if(c.moveToFirst() && nb_row>=0)
			{
				do
				{
					Object_sapinDetails sapin= new Object_sapinDetails();
					sapin.setId(c.getInt(c.getColumnIndex("ID")));
					sapin.setLigne(c.getInt(c.getColumnIndex("X")));
					sapin.setColonne(c.getInt(c.getColumnIndex("Y")));
					sapin.setStatus(c.getInt(c.getColumnIndex("ST")));
					sapin.setVariete(c.getString(c.getColumnIndex("VAR")));
					sapin.setTaille(c.getInt(c.getColumnIndex("TAILLE")));
					sapin.setNumero(i);
					L_sapinD.add(sapin);
					i++;
					Log.i("Obj_sapinDetails/createListOfAllParcelle","ID:"+sapin.getId()+" X:"+sapin.getLigne()+" Y:"
							+sapin.getColonne()+" Status:"+sapin.getStatus()+" Variété:"+sapin.getVariete()+" Taille:"+sapin.getTaille());
				}while(c.moveToNext());
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return L_sapinD;
	}

	
	
}
