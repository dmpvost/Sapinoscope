package com.ostermann.sapinoscope;

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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Message_alerte_activity extends Activity 
{

	
	private String log_name_activity ="MessageAlerte";
	private int sect_id = 0;
	private int parc_id = 0;
	private String parc_name = "";
	private String sect_name = "";
	
	Context contexte;
	
	private int secteurID;
	private int parcelleID;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Log.i(log_name_activity+"/onCreate", "----NOUVELLE ACTIVITE START----");
		setContentView(R.layout.message_alerte);
		
		contexte = this;
		
		Intent intent_alerte = getIntent();
		secteurID = intent_alerte.getIntExtra("sect_id", -1);
		parcelleID = intent_alerte.getIntExtra("parc_id", -1);
		if(secteurID == -1 || parcelleID == -1)
			Log.e("alerteActivity","Impossible de récupérer les informations, etat indetermine...");
		
		Button okButton = (Button) findViewById(R.id.bt_messageAlerte_ok);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentAjoutSapin = new Intent(contexte, Ajout_sapin.class);
				intentAjoutSapin.putExtra("sect_id", secteurID);
				intentAjoutSapin.putExtra("parc_id", parcelleID);
				startActivity(intentAjoutSapin);
				finish();
			}
		});
		
	}
}
