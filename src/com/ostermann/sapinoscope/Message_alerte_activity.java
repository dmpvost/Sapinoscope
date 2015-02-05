package com.ostermann.sapinoscope;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Message_alerte_activity extends Activity {

	private Object_parcelle parcelle ;
	private Object_secteur secteur;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_alerte);
		
		final Context contexte = this;
		
		Intent intent_alerte = getIntent();
		final int secteurID = intent_alerte.getIntExtra("sect_id", -1);
		final int parcelleID = intent_alerte.getIntExtra("parc_id", -1);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message_alerte, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	public void init_windows()
	{
		TextView titre_parcelle = (TextView) findViewById(R.id.txt_addsap_parcelle_titre);
		titre_parcelle.setText("Parcelle"+parcelle.getName()); //parcelle.getName() fait un nullPointerException car non initialise actuelement
		TextView titre_secteur = (TextView) findViewById(R.id.txt_addsap_secteur_titre);
		titre_secteur.setText("Secteur"+secteur.getName());
		
		Spinner spin_variete = (Spinner) findViewById(R.id.spin_addsap_variete);
		//spin_variete.
	}
	*/
}
