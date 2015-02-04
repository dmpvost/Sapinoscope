package com.ostermann.sapinoscope;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.R.bool;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Parcelle_modification extends Activity {

	private Context contexte = this;
	private String log_name_activity = "ParcelleModification";
	private int parcelle_id=0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parcelle_modification);
		Log.i(log_name_activity, "NOUVELLE ACTIVITE START");
		
		String parcelle_name = "";
		String parcelle_desc = "";
		float parcelle_coef = 0;
		
		// Reception INTENT
		Intent intent_parc_modif = getIntent();
		parcelle_id = intent_parc_modif.getIntExtra("id", 1);

		final int parcelle_add = intent_parc_modif.getIntExtra("add", 1); 		// bool = 1 : ADD  // 0 : update
		
		// on récupère le txte de l'activite si c'est une nouvelle parcelle
		if ( parcelle_add == 1)
		{
			Log.i(log_name_activity,"->INSERT Parcelle");
			parcelle_name = intent_parc_modif.getStringExtra("name");
		}
		else // on récupère le nom si c'est un UPDATE
		{
			Log.i(log_name_activity,"->UPDATE Parcelle");
			SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
			try
			{
				String selectQuery = "SELECT PARC_N as NAME, PARC_DESC as DESC, PARC_COEF as COEF FROM PARCELLE WHERE PARC_ID="+parcelle_id;
				Log.i("requette",selectQuery);
				Cursor c = db.rawQuery(selectQuery, null);
				int nb_row = c.getCount();
				Log.i("requette",selectQuery + " / COUNT = "+nb_row);
				if(c.moveToFirst() && nb_row>0)
				{
		            do{
		        		parcelle_name = c.getString(c.getColumnIndex("NAME"));
		            	parcelle_desc = c.getString(c.getColumnIndex("DESC"));
		            	parcelle_coef = Float.parseFloat(c.getString(c.getColumnIndex("COEF")));
		                
		            }while(c.moveToNext());
		        }
				
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		
		TextView txt_parcelle = (TextView) findViewById(R.id.txt_parcelle_modif_title);
		final Spinner spin_parc_coef = (Spinner) findViewById(R.id.spin_parcelle_modif);
		final EditText ed_parc_desc = (EditText) findViewById(R.id.editText_parcelle_modif_desc);
		final EditText ed_parc_name = (EditText) findViewById(R.id.editText_parcelle_modif_nom);
		ed_parc_name.setText(parcelle_name);
		ed_parc_desc.setText(parcelle_desc);
		txt_parcelle.setText("Parcelle : "+parcelle_name);
		
		List<String> list_coef = new ArrayList<String>();
		if ( parcelle_coef != 0 )
		{
			list_coef.add(""+parcelle_coef);
		}
		double i=1;
		while ( i > 0.1 )
		{
			if (i != parcelle_coef)
			{
				list_coef.add(""+i);
			}
			i = (double)Math.round((i - 0.1)*100)/100 ;			
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_coef);
		//Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_parc_coef.setAdapter(adapter);
		
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
			finish();
			
		}
		});
		
		
		
		
		
		
	}

	
	
	

}
