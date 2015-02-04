package com.ostermann.sapinoscope;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Parcelle_modification extends Activity {

	private Context contexte = this;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parcelle_modification);
		
		// Reception INTENT
		Intent intent_parc_modif = getIntent();
		final int parcelle_id = intent_parc_modif.getIntExtra("id", 1);
		String parcelle_name = intent_parc_modif.getStringExtra("name");
		String parcelle_desc = intent_parc_modif.getStringExtra("desc");
		float parcelle_coef = intent_parc_modif.getFloatExtra("coef", 1);
		
		// bool = 1 : ADD  // 0 : update
		final int parcelle_add = intent_parc_modif.getIntExtra("add", 1);
		
		TextView txt_parcelle = (TextView) findViewById(R.id.txt_parcelle_modif_title);
		txt_parcelle.setText("Parcelle : "+parcelle_name);
		
		final Spinner spin_parc_coef = (Spinner) findViewById(R.id.spin_parcelle_modif);
		final EditText ed_parc_desc = (EditText) findViewById(R.id.editText_parcelle_modif_desc);
		final EditText ed_parc_name = (EditText) findViewById(R.id.editText_parcelle_modif_nom);
		ed_parc_name.setText(parcelle_name);
		ed_parc_desc.setText(parcelle_desc);
		
		
		// INSERT UPDATE PARCELLE -- ON CLIC
		Button bt_add_parcelle = (Button) findViewById(R.id.bt_parcelle_modif_add);
		bt_add_parcelle.setOnClickListener(new OnClickListener() 
		{
			
			public void onClick(View v) 
			{
				
					SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
					try
					{
						String req ="";
						float spin = Float.parseFloat(spin_parc_coef.getSelectedItem().toString());
						
						if ( parcelle_add == 0 ) // Update
						{
							req = "UPDATE PARCELLE SET PARC_N='"+ed_parc_name.getText().toString()+"',PARC_DESC='"+ed_parc_desc.getText().toString()+"',PARC_COEF="+spin+" WHERE PARC_ID="+parcelle_id;
							Log.i("DB-UPT-Parcelle", req);
						}
						else
						{
							req = "INSERT INTO PARCELLE ('PARC_N','PARC_DESC','PARC_COEF') VALUES ('"+ed_parc_name.getText().toString()+"','"+ed_parc_desc.getText().toString()+"',"+spin+")";
							Log.i("DB-ADD-Parcelle", req);
						}
						
						
						db.execSQL(req);
						Log.i("DB-New-Parcelle", "test insert sans errors");
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
					
				
				ed_parc_desc.setText("");
				ed_parc_name.setText("");
				Intent intent_parcelle_liste = new Intent(contexte, Parcelle_listView.class);
				startActivity(intent_parcelle_liste);
				
			}
		});
		
		
		
		
		
		
	}

	
	
	

}
