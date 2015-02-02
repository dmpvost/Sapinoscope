package com.ostermann.sapinoscope;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

public class Parcelle_listView extends Activity {

	private Context contexte = this;
	private AlertDialog.Builder dialogBuilder;
	private ListView liste_parcelle;
	
	private String[] mes_parcelles = null;
	private Object_parcelle[] tab_parcelle = null;
	
	// Creation de l'interface
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parcelle_listview);
		liste_parcelle = (ListView) findViewById(R.id.listView_parcelle);
		
		registerForContextMenu(liste_parcelle);
		//Initialisation
		parcelle_listview(liste_parcelle);		
		parcelle_ClickCallBack(liste_parcelle);
		
		
		Button add_parcelle = (Button) findViewById(R.id.bt_add_parcelle);
		add_parcelle.setOnClickListener(new OnClickListener() 
		{

			public void onClick(View v) 
			{
				
				//popup_parcelle();
				
				EditText add_parcelle = (EditText) findViewById(R.id.editText_add_parcelle);
				String name_parcelle = add_parcelle.getText().toString();
				Log.i("testDB", "parcelle:"+name_parcelle);
				
				SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
				try
				{
					String req = "INSERT INTO PARCELLE ('PARC_N','PARC_DESC','PARC_COEF') VALUES ('"+name_parcelle+"','"+name_parcelle+"',1)";
					Log.i("testDB", req);
					db.execSQL(req);
					Log.i("testDB", "test insert sans errors");
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				
				
				add_parcelle.setText(""); //vide le champ de saisie
				parcelle_listview(liste_parcelle) ; // refresh de l'affichage
				// fermer le clavier
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(add_parcelle.getWindowToken(), 0);
			}
		});
		
		
	}
	
	//**************************************************************************//
	private void parcelle_listview(ListView liste_parcelle) 
	{
		
		
		// Exemple de select avec plusieur ligne :
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM PARCELLE";
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			mes_parcelles = new String[nb_row];
			tab_parcelle = new Object_parcelle[nb_row];
			if(c.moveToFirst() && nb_row>0)
			{
				int i=0;
	            do{
	               //assing values 
	            	tab_parcelle[i] = new Object_parcelle();
	            	tab_parcelle[i].setId(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
	            	tab_parcelle[i].setName(c.getString(c.getColumnIndex("PARC_N")));
	            	mes_parcelles[i]=tab_parcelle[i].getName();
	            	tab_parcelle[i].setDescription(c.getString(c.getColumnIndex("PARC_DESC")));
	            	tab_parcelle[i].setCoef(Integer.parseInt(c.getString(c.getColumnIndex("PARC_COEF"))));
	                Log.i("DB print","ID:"+tab_parcelle[i].getId()+" nom:"+tab_parcelle[i].getName()+" desc:"+tab_parcelle[i].getDescription()+" coef:"+tab_parcelle[i].getCoef());
	                i++;
	            }while(c.moveToNext());
	            
	    		// crée une liste d'item
	    		// Build adapter
	    		ArrayAdapter<Object_parcelle> adapter = new ArrayAdapter<Object_parcelle>(this,R.layout.parcelle_texte,tab_parcelle); 
	    		// Configure the listview
	    		liste_parcelle.setAdapter(adapter);
	        }
			Log.i("testDB", "test select sans errors");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		

		
	}

	
	//**************************************************************************//
	// Gestion du clic
	private void parcelle_ClickCallBack(ListView liste_parcelle) 
	{	
		liste_parcelle.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) 
			{
				TextView ma_parcelle = (TextView) viewClicked;
				//String message = "Clic sur #" + position + " texte:" + ma_parcelle.getText().toString();
				//Toast.makeText(Parcelle_listView.this, message, Toast.LENGTH_SHORT).show();	
				
				Intent intent_secteur = new Intent(contexte, Secteur_activity.class);
				intent_secteur.putExtra("id", tab_parcelle[position].getId());
				intent_secteur.putExtra("name", tab_parcelle[position].getName());
				startActivity(intent_secteur);
			}
		});
	}
	
	
	//**************************************************************************//
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		 if (v.getId() == R.id.listView_parcelle) 
	     {
	    	    ListView lv = (ListView) v;
	    	    AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
	    	    Object obj = lv.getItemAtPosition(acmi.position);
	    	    int b = acmi.position;

	    	    menu.add("One");
	    	    menu.add("Two");
	    	    menu.add("Three");
	    	    menu.add(b+" "+obj.toString());
    	}
			     
	}

	//**************************************************************************//	
	public boolean onContextItemSelected(MenuItem item) 
	{
		   if (item.getTitle() == "One") {
		      Toast.makeText(this, "Action 1 invoked", Toast.LENGTH_SHORT).show();
		   }
		   else if (item.getTitle() == "Two") {
		      Toast.makeText(this, "Action 2 invoked", Toast.LENGTH_SHORT).show();
		   }
		   else if (item.getTitle() == "Three") {
		      Toast.makeText(this, "Action 3 invoked", Toast.LENGTH_SHORT).show();
		   } 
		   else {
		      return false;
		   }
		   return true;
	}
	
	
	//**************************************************************************//	
	/*public int select_max_id(String table, String colonne)
	{
		int value=0;
		String txt="";
		
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT MAX("+colonne+") FROM "+table;
			Log.i("requette",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			if(c.moveToFirst())
			{
	            do{
	               //assing values 
	               txt = c.getString(c.getColumnIndex(colonne));
	               Log.i("DB print","ID:"+txt);
	            }while(c.moveToNext());
	        }
			Log.i("testDB", "test select sans errors");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		value = Integer.valueOf(txt);
		return value;
	}*/
	
//**************************************************************************//
	private void popup_parcelle()
	{
		//variable
		dialogBuilder = new AlertDialog.Builder(this);
		final EditText edt_name_parcelle = new EditText(this);
		final EditText edt_desc_parcelle = new EditText(this);
		final EditText edt_coef_parcelle = new EditText(this);

		String strName = "Nouvelle Parcelle";
		
		//process
		dialogBuilder.setTitle(strName);
		dialogBuilder.setMessage("Nom de la percelle?");
		dialogBuilder.setView(edt_name_parcelle);

		//___________________//
		// Bouton positif
		//dialogBuilder.setMultiChoiceItems(itemsId, checkedItems, listener)
		dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				Toast.makeText(getApplicationContext(), edt_name_parcelle.getText().toString(), Toast.LENGTH_LONG).show();
				
			}
		});
		//___________________//
		// Bouton négatif
		dialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				Toast.makeText(getApplicationContext(), "Annuler", Toast.LENGTH_LONG).show();
				
			}
		});
		//___________________//
		
		// Output
		AlertDialog popup_parcelle = dialogBuilder.create();
		popup_parcelle.show();
	}
	
	
}
