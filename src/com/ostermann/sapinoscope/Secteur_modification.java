package com.ostermann.sapinoscope;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	private int parc_id = 0;
	private int sect_id = 0;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secteur_modification);
		Log.i(log_name_activity, "----NOUVELLE ACTIVITE START----");

		String sect_name="";
		final String parc_name ="";
		float sect_coef_crois =0;
		float sect_coef_gel  =0;		

		// Reception INTENT
		Intent intent_sect_modif = getIntent();
		sect_id = 	intent_sect_modif.getIntExtra("sec_id", 1);
		final int sect_add = 	intent_sect_modif.getIntExtra("add", 1); 		// bool = 1 : ADD  // 0 : update

		// on récupère le texte de l'activite si c'est une nouvelle parcelle
		if ( sect_add == 1)
		{
			Log.i(log_name_activity,"->Ajout d'un nouveau secteur");
			sect_name =intent_sect_modif.getStringExtra("name");
			parc_id = 	intent_sect_modif.getIntExtra("parc_id", 1);
		}
		else // sinon SELECT
		{
			Log.i(log_name_activity,"->Modification d'un secteur");
			SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
			try
			{
				String selectQuery = "SELECT * FROM SECTEUR WHERE SEC_ID="+sect_id;
				Log.i("requette",selectQuery);
				Cursor c = db.rawQuery(selectQuery, null);
				int nb_row = c.getCount();
				if(c.moveToFirst() && nb_row>0)
				{
					do{
						parc_id = Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID")));
						sect_name = c.getString(c.getColumnIndex("SEC_N"));
						sect_coef_crois = Float.parseFloat(c.getString(c.getColumnIndex("SEC_CROIS")));
						Log.i("DB requette","ID:");
					}while(c.moveToNext());
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}

		Log.i(log_name_activity,"INTENT GET : PARC_ID:"+parc_id+" SECT_N:"+sect_name+"SECT_ID:"+sect_id);
		
		TextView txt_sect = (TextView) findViewById(R.id.txt_secteur_modif_title);
		final EditText ed_sect_desc = (EditText) findViewById(R.id.editText_secteur_modif_nom);
		final Spinner spin_sect_crois = (Spinner) findViewById(R.id.spinner_coef_croissance);
		final Spinner spin_sect_gel = (Spinner) findViewById(R.id.spinner_coef_gel);
		final Spinner spin_sect_annee = (Spinner) findViewById(R.id.spinner_annee);

		txt_sect.setText("Secteur : "+sect_name);
		ed_sect_desc.setText(sect_name);		

		// Création du spinner croissance
		List<String> list_coef_crois = new ArrayList<String>();
		if ( sect_coef_crois != 0)
		{
			list_coef_crois.add(""+sect_coef_crois);
		}

		double i=1;
		while ( i > 0.1 )
		{
			if (i != sect_coef_crois)
			{
				list_coef_crois.add(""+i);
			}
			i = (double)Math.round((i - 0.1)*100)/100 ;			
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_coef_crois);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_sect_crois.setAdapter(adapter);

		//String year = (String) android.text.format.DateFormat.format("yyyy", date);

		// Création du spinner année
		List<String> list_annee = new ArrayList<String>();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String strDate = sdf.format(c.getTime());
		list_annee.add(""+strDate);
		int a=2014;
		while ( a < 2025 )
		{
			if( Integer.parseInt(strDate) != a )
			{
				list_annee.add(""+a);
			}
			a++;
		}
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_annee);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_sect_annee.setAdapter(adapter2);

		// Création du spinner coef_gel
		List<String> list_coef_gel = new ArrayList<String>();
		if ( sect_coef_crois != 0)
		{
			list_coef_gel.add(""+sect_coef_gel);
		}
		i=1;
		while ( i > 0.1 )
		{
			if (i != sect_coef_gel)
			{
				list_coef_gel.add(""+i);
			}
			i = (double)Math.round((i - 0.1)*100)/100 ;			
		}
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_coef_gel);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_sect_gel.setAdapter(adapter3);




		// INSERT UPDATE PARCELLE -- ON CLIC
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
					// INSERT SECTEUR
					Log.i(log_name_activity, "UPDATE Secteur");
					SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
					try
					{
						String req_secteur = "UPDATE SECTEUR SET SEC_N='"+sect_name+"' , SEC_CROIS='"+spin_crois+"' WHERE SEC_ID="+sect_id ;
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
						String req_secteur = "INSERT into SECTEUR (PARC_ID,SEC_N,SEC_ANGLE,SEC_CROIS)  VALUES ( "+parc_id+",'"+sect_name+"', 0 , "+spin_crois+" ) ;" ;
						db.execSQL(req_secteur);
						Log.i(log_name_activity, "req_secteur");
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}

					// GET LAST ID ADDED
					sect_id = select_max_id("SECTEUR", "SEC_ID");
					if (sect_id != 0)
					{
						// INSERT INFO_SECTEUR
						db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
						try
						{
							String req_info_seq = "INSERT into INFO_SECTEUR (SEC_ID,ANN_ID,INF_SEC_COEF_GEL) VALUES (  "+sect_id+", "+spin_annee+", "+spin_gel+");";
							db.execSQL(req_info_seq);
							Log.i(log_name_activity, "req_info_seq");
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
					}
				}

				Intent intent_secteur_liste = new Intent(contexte, Secteur_activity.class);
				intent_secteur_liste.putExtra("id", parc_id);
				Log.i(log_name_activity,"INTENT SET : PARC_ID:"+parc_id+" SECT_N:"+sect_name+"SECT_ID:"+sect_id);
				startActivity(intent_secteur_liste);
				finish();

			}
		});
	}

	//**************************************************************************//	
	public int select_max_id(String table, String colonne)
	{
		int value=0;
		String txt="";

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT MAX("+colonne+") FROM "+table;
			Log.i("requette",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				do{
					txt = c.getString(0);
					Log.i("DB requette","ID:"+txt);
				}while(c.moveToNext());
			}
			else
			{
				txt = "0";
			}

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		value = Integer.valueOf(txt);
		return value;
	}

}
