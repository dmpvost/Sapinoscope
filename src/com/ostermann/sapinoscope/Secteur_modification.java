package com.ostermann.sapinoscope;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ostermann.sapinoscope.Object_secteur.Source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Secteur_modification extends Activity 
{

	private Context contexte = this;

	private String log_name_activity = "SECTEUR_MODIFICATION";
	private Object_secteur secteur;

	private TextView txt_sect;
	private EditText ed_sect_desc = null;
	private Spinner spin_sect_crois=null;
	private Spinner spin_sect_gel=null;
	private Spinner spin_sect_annee=null;

	private int sect_add;

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secteur_modification);
		Log.i(log_name_activity, "----NOUVELLE ACTIVITE START----");

		txt_sect = (TextView) findViewById(R.id.txt_secteur_modif_title);
		ed_sect_desc = (EditText) findViewById(R.id.editText_secteur_modif_nom);
		spin_sect_crois = (Spinner) findViewById(R.id.spinner_coef_croissance);
		spin_sect_gel = (Spinner) findViewById(R.id.spinner_coef_gel);
		spin_sect_annee = (Spinner) findViewById(R.id.spinner_annee);

		// Reception INTENT
		Intent intent_sect_modif = getIntent();
		int sec_id = 	intent_sect_modif.getIntExtra("sec_id", -1);
		int parc_id =  	intent_sect_modif.getIntExtra("parc_id", -1);
		String name =  	intent_sect_modif.getStringExtra("name");
		sect_add = 		intent_sect_modif.getIntExtra("add", -1); 		// bool = 1 : ADD  // 0 : update


		// on recupere le texte de l'activite si c'est une nouvelle parcelle
		if ( sect_add == -1)
		{
			Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
		}
		else if ( sect_add == 1) // INSERT (si on cree un nouveau secteur)
		{
			if( parc_id == -1)
				Log.e(log_name_activity, "Impossible d'initialiser via parc_id");

			Log.i(log_name_activity,"->Ajout d'un nouveau secteur");
			secteur = new Object_secteur(0, parc_id, name, 0, 0);
		}
		else // sinon SELECT (quand on modifie un secteur existant)
		{
			Log.i(log_name_activity,"->Modification d'un secteur");
			secteur = new Object_secteur(sec_id, Source.sec_id);
		}


		Log.i(log_name_activity,"INTENT GET : PARC_ID:"+secteur.getId_parc()+"  SECT_N:"+secteur.getName()+"  SECT_ID:"+secteur.getId());


		// INITIALISAION
		txt_sect.setText("Secteur : "+secteur.getName());
		ed_sect_desc.setText(secteur.getName());		
		init_spinner_coef_croissance();
		init_spinner_annee();
		init_spinner_coef_gel();
		Select_spinner_annee();
		Select_spinner_coef_gel();
		
		//---------------------------------------------------------
		// INSERT UPDATE PARCELLE -- ON CLIC     (Bouton "VALIDER")
		//---------------------------------------------------------
		Button bt_add_secteur = (Button) findViewById(R.id.bt_secteur_modif_add);
		bt_add_secteur.setOnClickListener(new OnClickListener() 
		{

			public void onClick(View v) 
			{

				Log.i(log_name_activity, "Clic - VALIDER");
				String sect_name = ed_sect_desc.getText().toString();
				String spin_annee = spin_sect_annee.getSelectedItem().toString();
				float spin_crois = Float.parseFloat(spin_sect_crois.getSelectedItem().toString());
				float spin_gel =   Float.parseFloat(spin_sect_gel.getSelectedItem().toString());

				if ( sect_add == 0 ) // Update
				{
					// UPDATE SECTEUR
					Log.i(log_name_activity, "UPDATE Secteur");
					SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
					try
					{
						String req_secteur = "UPDATE SECTEUR SET SEC_N='"+sect_name+"' , SEC_CROIS='"+spin_crois+"' WHERE SEC_ID="+secteur.getId() ;
						db.execSQL(req_secteur);
						Log.i(log_name_activity, "req_secteur");
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					// INSERT SECTEUR
					Log.i(log_name_activity, "INSERT SECTEUR");
					SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
					try
					{
						String req_secteur = "INSERT into SECTEUR (PARC_ID,SEC_N,SEC_ANGLE,SEC_CROIS)  VALUES ( "+secteur.getId_parc()+",'"+sect_name+"', 0 , "+spin_crois+" ) ;" ;
						db.execSQL(req_secteur);
						Log.i(log_name_activity, "req_secteur");
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}

					// GET LAST ID ADDED
					int max_id = select_max_id("SECTEUR", "SEC_ID");
					if (max_id != 0)
					{
						// INSERT INFO_SECTEUR
						db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
						try
						{
							String req_info_seq = "INSERT into INFO_SECTEUR (SEC_ID,ANN_ID,INF_SEC_COEF_GEL) VALUES (  "+max_id+", "+spin_annee+", "+spin_gel+");";
							db.execSQL(req_info_seq);
							Log.i(log_name_activity, req_info_seq);
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
					}
				}

				Intent intent_secteur_liste = new Intent(contexte, Secteur_activity.class);
				intent_secteur_liste.putExtra("id", secteur.getId_parc());
				Log.i(log_name_activity,"INTENT SET : PARC_ID:"+secteur.getId_parc()+" SECT_N:"+sect_name+"SECT_ID:"+secteur.getId());
				startActivity(intent_secteur_liste);
				finish();

			}
		});
	}

	
	
	//**************************************************************************//	
	public int select_max_id(String table, String colonne)
	{
		int value=0;

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT MAX("+colonne+") FROM "+table;
			Log.i("requete",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				value=c.getInt(0);
				Log.i("DB requete","ID:"+value+" nb_row="+nb_row);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return value;
	}

	
	//*********************************************************************************
		public void init_spinner_annee()
		{
			//String year = (String) android.text.format.DateFormat.format("yyyy", date);
			List<String> list_annee = new ArrayList<String>();

			//On recupere l'annee actuelle
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			int DateActuelle = Integer.parseInt(sdf.format(c.getTime()));	
			
			if ( sect_add == -1)	
			{
				Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
			}
			else if (sect_add == 1) //bool = 1 : nouveau secteur
			{				
				//on n'affiche que l'annee actuelle
				list_annee.add(""+DateActuelle);			
			}
			else // bool = 0 : modification d'un secteur
			{
				//on affiche l'annee actuelle avec 1 avant et 2 apres
				for(int i = DateActuelle -1 ; i<DateActuelle+3 ; i++)
				{
					list_annee.add(""+i);
				}
			}
			
			ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_annee);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_sect_annee.setAdapter(adapter2);
		}
	
	//*********************************************************************************
	public void init_spinner_coef_gel()
	{
		// Creation du spinner coef_gel
		List<String> list_coef_gel = new ArrayList<String>();

		//Remplit la liste de 1 a 0.1
		double i=1.5;	
		while ( i >= 0.1 )
		{
			list_coef_gel.add(""+i);
			i = (double)Math.round((i - 0.1)*100)/100 ;			
		}
		
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_coef_gel);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_sect_gel.setAdapter(adapter3);
	}
	
	//*********************************************************************************
	public void Select_spinner_annee()
	{
		int index_spinner_annee = 0;
		
		if ( sect_add == -1)	
		{
			Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
		}
		else //nouveau secteur ou modification existant : meme combat !
		{
			//On recupere l'annee actuelle
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			int DateActuelle = Integer.parseInt(sdf.format(c.getTime()));	
					
			for (int i=0;i<spin_sect_annee.getCount();i++)
			{
				//On check chaque item de la liste
				int index_annee = Integer.parseInt(spin_sect_annee.getItemAtPosition(i).toString());
				if (index_annee == DateActuelle)
				{
					index_spinner_annee = i;	//On retourne l'index correspondant a la date du jour
					break;
				}
			}
		}
		//On selectionne le bon item dans la liste deroulante annee
		spin_sect_annee.setSelection(index_spinner_annee);
	}
	
	
	public void Select_spinner_coef_gel()
	{
		int index_spinner_gel = 0;
		
		if ( sect_add == -1)	
		{
			Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
		}
		else if (sect_add == 1) //bool = 1 : nouveau secteur
		{				
			//On ne fait rien (1.0 par defaut)
		}
		else // bool = 0 : modification d'un secteur
		{
			//On recupere le coef de gel du secteur en fonction de l'annee
			int annee_en_cours = Integer.parseInt(spin_sect_annee.getSelectedItem().toString());
			double coeff_BDD = Get_Gel_Fonction_Annee(annee_en_cours);
			
			Log.i(log_name_activity, "Annee_en_cours  (Select_spinner_coef_gel) : "+annee_en_cours);
			Log.i(log_name_activity, "Coeff_BDD  (Select_spinner_coef_gel) : "+coeff_BDD);
			
			for (int i=0;i<spin_sect_gel.getCount();i++)
			{
				//On check chaque item de la liste
				double index_gel = Double.parseDouble(spin_sect_gel.getItemAtPosition(i).toString());
				if (index_gel == coeff_BDD)
				{
					index_spinner_gel = i;	//On retourne l'index correspondant a la date du jour
					break;
				}
			}
		}
		//On selectionne le bon item dans la liste deroulante annee
		spin_sect_gel.setSelection(index_spinner_gel);
	}
	
	public double Get_Gel_Fonction_Annee(int annee_voulue)
	{
		double value=0;

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			//String selectQuery = "SELECT INF_SEC_COEF_GEL FROM INFO_SECTEUR WHERE ANN_ID=strftime('%YYYY', '"+annee_voulue+"') AND SEC_ID="+secteur.getId();
			String selectQuery = "SELECT INF_SEC_COEF_GEL FROM INFO_SECTEUR WHERE ((ANN_ID="+annee_voulue +") AND (SEC_ID="+secteur.getId()+"))";
			Log.i("requete",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				value=c.getDouble(0);
				Log.i("DB requete","ID:"+value+" nb_row="+nb_row);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return value;
		
	}
	
	
	
	
	
	
	
	
	//***********************************************************************************/	
	public void init_spinner_coef_croissance()
	{
		// Creation du spinner croissance
		List<String> list_coef_crois = new ArrayList<String>();
		if ( secteur.getCoef_croissance() != 0)
		{
			list_coef_crois.add(""+secteur.getCoef_croissance());
		}
		double i=1.6;
		while ( i > 0.1 )
		{
			//if (i != sect_coef_crois)
			{
				list_coef_crois.add(""+i);
			}
			i = (double)Math.round((i - 0.1)*100)/100 ;			
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_coef_crois);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_sect_crois.setAdapter(adapter);
	}

}
