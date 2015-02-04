package com.ostermann.sapinoscope;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Secteur_activity extends Activity {
	private Context contexte = this;
	private AlertDialog.Builder dialogBuilder;
	private ListView liste_secteur;
	
	private String[] mes_secteurs = null;
	private Object_secteur[] tab_secteur = null;
	
	private int item_listview_select = 0;
	private String parcelle_select="";
	
	private String log_name_activity ="SecteurListview";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secteur);
		liste_secteur = (ListView) findViewById(R.id.listView_secteur);
		Log.i(log_name_activity, "----NOUVELLE ACTIVITE----");
		
		// Reception INTENT
		Intent intent_secteur = getIntent();
		final int parcelle_id = intent_secteur.getIntExtra("id", 1);
		
		
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM PARCELLE WHERE PARC_ID="+parcelle_id;
			Log.i(log_name_activity,selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				do{
					parcelle_select = c.getString(c.getColumnIndex("PARC_N"));
					Log.i(log_name_activity,"Name : parcelle_select");
				}while(c.moveToNext());
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		Log.i(log_name_activity,"INTENT GET : PARC_ID:"+parcelle_id);
		
		
		TextView txt_parcelle = (TextView) findViewById(R.id.txt_secteur_select_parcelle_titre);
		txt_parcelle.setText("Parcelle : "+parcelle_select);

		// initialisation
		registerForContextMenu(liste_secteur);
		secteur_listview(liste_secteur, parcelle_id);
		secteur_ClickCallBack(liste_secteur);
		
		// bouton ajout secteur
		Button add_secteur = (Button) findViewById(R.id.bt_add_secteur);
		add_secteur.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Log.i(log_name_activity, "clic boutton ajout secteur");
				EditText edt_add_secteur = (EditText) findViewById(R.id.editText_add_secteur);
				String name_secteur = edt_add_secteur.getText().toString();
				edt_add_secteur.setText(""); //vide le champ de saisie
				Object_secteur secteur = new Object_secteur(1, parcelle_id, name_secteur, 0, 1);
				start_activity_secteur_modification(secteur, 1);
			}
		});
	}
	
	//**************************************************************************//	
	private void secteur_listview(ListView liste_secteur,int parcelle_id) 
	{
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM SECTEUR WHERE PARC_ID="+parcelle_id;
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			mes_secteurs = new String[nb_row];
			tab_secteur = new Object_secteur[nb_row];
			if(c.moveToFirst() && nb_row>0)
			{
				int i=0;
	            do{
	               //assing values 
	            	tab_secteur[i] = new Object_secteur();
	            	tab_secteur[i].setId(Integer.parseInt(c.getString(c.getColumnIndex("SEC_ID"))));
	            	tab_secteur[i].setId_parc(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
	            	tab_secteur[i].setName(c.getString(c.getColumnIndex("SEC_N")));
	            	mes_secteurs[i]=tab_secteur[i].getName();
	            	tab_secteur[i].setAngle(Float.parseFloat(c.getString(c.getColumnIndex("SEC_ANGLE"))));
	            	tab_secteur[i].setCoef_croissance(Float.parseFloat(c.getString(c.getColumnIndex("SEC_CROIS"))));
	                i++;
	            }while(c.moveToNext());
	            
	    		// crée une liste d'item
	    		ArrayAdapter<Object_secteur> adapter_secteur = new ArrayAdapter<Object_secteur>(this,R.layout.secteur_texte,tab_secteur); 
	    		liste_secteur.setAdapter(adapter_secteur);
	        }
			Log.i(log_name_activity, "OK:"+selectQuery);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity, "Sortie en erreur");
		}
		
	}

	
	//**************************************************************************//
	// Gestion du clic
	private void secteur_ClickCallBack(ListView liste_secteur) 
	{	
		liste_secteur.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) 
			{
				// Selection d'un SECTEUR -- GO to ADD SAPIN / put parc_id,sect_id
				//TextView ma_parcelle = (TextView) viewClicked;
				Intent intent_addsapin = new Intent(contexte, Message_alerte_activity.class);
				intent_addsapin.putExtra("sect_id", tab_secteur[position].getId());
				intent_addsapin.putExtra("parc_id", tab_secteur[position].getId_parc());
				Log.i(log_name_activity,"INTENT SET : PARC_ID:"+tab_secteur[position].getId_parc()+" SECT_N:"+tab_secteur[position].getId());
				startActivity(intent_addsapin);
			}
		});
	}
	
	//**************************************************************************//
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		 if (v.getId() == R.id.listView_secteur) 
	     {
	    	    AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
	    	    item_listview_select = acmi.position;
	    	    menu.add("Edition");
	    	    menu.add("Supprimer");
    	}
			     
	}

	
	//**************************************************************************//	
		public boolean onContextItemSelected(MenuItem item) 
		{
			   if (item.getTitle() == "Edition") 
			   {
				   start_activity_secteur_modification(tab_secteur[item_listview_select], 0);
			   }
			   else if (item.getTitle() == "Supprimer") 
			   {
				   secteur_delete(tab_secteur[item_listview_select]);
			   }
			   else 
			   {
			      return false;
			   }
			   return true;
		}
		
		

	//**************************************************************************//
		private void secteur_delete(final Object_secteur secteur)
		{
			dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("ATTENTION");
			dialogBuilder.setMessage("Voulez vous supprimer toutes les données de "+secteur.getName()+"?");

			// Bouton positif
			dialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
					try
					{
						String req = "DELETE FROM SECTEUR WHERE SEC_ID="+secteur.getId();
						db.execSQL(req);
						Toast.makeText(getApplicationContext(), "Suppression de "+secteur.getName(), Toast.LENGTH_SHORT).show();
						secteur_listview(liste_secteur,secteur.getId_parc());
						Log.i(log_name_activity,req);
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
				}
			});
			// Bouton négatif
			dialogBuilder.setNegativeButton("Non", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					// ne rien faire
				}
			});
			
			// Output
			AlertDialog popup_parcelle = dialogBuilder.create();
			popup_parcelle.show();
		}
		
		//**************************************************************************//
	
	
	
	//**************************************************************************//
	public void start_activity_secteur_modification(Object_secteur secteur, int add_or_modify)
	{
		//  add_or_modify
		//  1 : ADD  
		//  0 : UPDATE
		Log.i(log_name_activity, "start_activity_secteur_modification");
		Intent intent_sect_add = new Intent(contexte, Secteur_modification.class);
		intent_sect_add.putExtra("sec_id", secteur.getId());
		intent_sect_add.putExtra("parc_id", secteur.getId_parc());
		intent_sect_add.putExtra("name", secteur.getName());
		intent_sect_add.putExtra("add", add_or_modify);
		Log.i(log_name_activity,"INTENT GET : PARC_ID:"+secteur.getId_parc()+" SECT_N:"+secteur.getName()+"SECT_ID:"+secteur.getId());
		startActivity(intent_sect_add);
		finish();
	}
	
}
