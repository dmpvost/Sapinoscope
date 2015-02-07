package com.ostermann.sapinoscope;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class Choix_depart_sapin extends Activity {

	
	private String log_name_activity ="ChoixDepartSapin";
	private Object_secteur secteur = null;
	
	private Spinner spin_colonneY = (Spinner) findViewById(R.id.spin_choixSapin_colonneY);
	private Spinner spin_ligneX = (Spinner) findViewById(R.id.spin_choixSapin_ligneX);
	private Vector<Object_sapin> sapin;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choix_depart_sapin);
		
		Log.i(log_name_activity+"/onCreate", "----NOUVELLE ACTIVITE----");
		// Reception INTENT
		Intent intent_depart = getIntent();
		int secteur_id = intent_depart.getIntExtra("id", -1);

		if ( secteur_id == -1)
		{
			Log.e(log_name_activity+"/onCreate","Impossible de récupéré secteur ID");
		}
	
		
		// Initialisation
		initialisation(secteur_id);
		
		
	}

	public void initialisation(int secteur_id)
	{
		secteur = new Object_secteur(secteur_id);
		sapin = Object_sapin.createListOfSapin(secteur_id); 
		int max_y = Object_sapin.selectMaxNbSapins("SAP_COL", "SEC_ID", secteur.getId());
		init_spinner_colonneY(max_y);
		init_spinner_ligneX(max_y);
	}
	
	public void init_spinner_colonneY(int max_y)
	{
		
		List<String> liste_colonneY = new ArrayList<String>();
		for (int j = 0; j < max_y; j++) 
		{
			liste_colonneY.add(""+j+1);
		}	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, liste_colonneY);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_colonneY.setAdapter(adapter);
	}
	
	public void init_spinner_ligneX(int y)
	{
		
	}
	
	

	
}
