package com.ostermann.sapinoscope;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;

public class Message_alerte_activity extends Activity 
{

	
	private String log_name_activity ="MessageAlerte";
	private int sect_id = 0;
	private int parc_id = 0;
	private String parc_name = "";
	private String sect_name = "";
	
	private Object_parcelle parcelle ;
	private Object_secteur secteur;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Log.i(log_name_activity+"/onCreate", "----NOUVELLE ACTIVITE START----");
		setContentView(R.layout.message_alerte);
		
		// Reception INTENT
		/*Intent intent_mess_alerte = getIntent();
		sect_id = 	intent_mess_alerte.getIntExtra("sec_id", 1);
		parc_id = 	intent_mess_alerte.getIntExtra("parc_id", 1); 	
		Log.i(log_name_activity,"INTENT GET PARC_ID="+parc_id+" SECT_ID="+sect_id);
		
		parcelle = new Object_parcelle(parc_id);
		secteur = new Object_secteur(parc_id);

		// Initialisation
		init_windows();*/
		
	}

	
	public void init_windows()
	{
		TextView titre_parcelle = (TextView) findViewById(R.id.txt_addsap_parcelle_titre);
		titre_parcelle.setText("Parcelle"+parcelle.getName());
		TextView titre_secteur = (TextView) findViewById(R.id.txt_addsap_secteur_titre);
		titre_secteur.setText("Secteur"+secteur.getName());
		
		Spinner spin_variete = (Spinner) findViewById(R.id.spin_addsap_variete);
		//spin_variete.
	}
	
	
	
}
