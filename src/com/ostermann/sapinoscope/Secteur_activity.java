package com.ostermann.sapinoscope;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;

public class Secteur_activity extends Activity {
	private Context contexte = this;
	private AlertDialog.Builder dialogBuilder;
	private ListView liste_secteur;
	
	private String[] mes_secteurs = null;
	private Object_secteur[] tab_secteur = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secteur);
		liste_secteur = (ListView) findViewById(R.id.listView_secteur);
		
		// Reception INTENT
		Intent intent_secteur = getIntent();
		final int parcelle_id = intent_secteur.getIntExtra("id", 1);
		String parcelle_select = intent_secteur.getStringExtra("name");
		
		TextView txt_parcelle = (TextView) findViewById(R.id.txt_secteur_select_parcelle_titre);
		txt_parcelle.setText("Parcelle : "+parcelle_select);

		secteur_listview(liste_secteur, parcelle_id);
		
		final Button add_secteur = (Button) findViewById(R.id.bt_add_secteur);
		add_secteur.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				EditText ed_secteur = (EditText) findViewById(R.id.editText_add_secteur);
				String secteur_name = ed_secteur.getText().toString();
				Log.i("DB-Secteur", "secteur:"+secteur_name);
				
				SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
				try
				{
					String req = "INSERT INTO SECTEUR ('PARC_ID','SEC_N','SEC_ANGLE','SEC_CROIS') VALUES ('"+parcelle_id+"','"+secteur_name+"',1,1)";
					Log.i("testDB", req);
					db.execSQL(req);
					Log.i("testDB", "test insert sans errors");
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				
				ed_secteur.setText(""); 
				secteur_listview( liste_secteur, parcelle_id);
				// fermer le clavier
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(add_secteur.getWindowToken(), 0);
			}
		});
		
	}
	
	
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
			Log.i("DB-Secteur", "OK:"+selectQuery);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i("DB-Secteur", "Sortie en erreur");
		}
		

		
	}

	
}
