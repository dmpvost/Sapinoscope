package com.ostermann.sapinoscope;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Varietes_Listview extends Activity {

	private Context contexte = this;
	private AlertDialog.Builder dialogBuilder;
	private Vector<Object_variete> tab_variete = null;
	private ListView liste_variete;
	private String log_name_activity = "Variete_Listview";
	private int item_listview_selected = 0;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_varietes_listview);
		liste_variete = (ListView) findViewById(R.id.listview_varietes);
		Log.i(log_name_activity, "----NOUVELLE ACTIVITE----");
		registerForContextMenu(liste_variete);	//CA PLANTE ICI BORDEL DE MERDE ...

		//Initialisation
		varietes_listview(liste_variete);	
		variete_ClickCallBack(liste_variete);
	
		liste_variete.requestFocus();
		
		// Bouton modif infos variete
		Button modif_infos = (Button) findViewById(R.id.button_modif_variete);
		modif_infos.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{

			}
		});
	}

	
	
	//**************************************************************************//
	private void varietes_listview(ListView liste_variete) 
	{
		tab_variete =  Object_variete.createListOfAllVariete();
		ArrayAdapter<Object_variete> adapter = new ArrayAdapter<Object_variete>(this,R.layout.parcelle_texte,tab_variete); 
		liste_variete.setAdapter(adapter);
	}
	
	

	//**************************************************************************//	
	public boolean onContextItemSelected(MenuItem item) 
	{
		   if (item.getTitle() == "Supprimer") 
		   {
			   variete_delete(tab_variete.get(item_listview_selected).getVar_id(),tab_variete.get(item_listview_selected).getVar_nom());
		   }
		   else {
		      return false;
		   }
		   return true;
	}
	
	
	private void variete_delete(final int id,final String name)
	{
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("ATTENTION");
		dialogBuilder.setMessage("Voulez vous supprimer toutes les données de "+name+"?");

		// Bouton positif
		dialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
				try
				{
					db.execSQL("DELETE FROM VARIETE WHERE VAR_ID="+id);
					Toast.makeText(getApplicationContext(), "Suppression de "+name, Toast.LENGTH_SHORT).show();
					varietes_listview(liste_variete);
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
		});
		// Bouton negatif
		dialogBuilder.setNegativeButton("Non", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
			}
		});
		
		// Output
		AlertDialog popup_parcelle = dialogBuilder.create();
		popup_parcelle.show();
	}
	
	
	
	
	
	//**************************************************************************//
	// Gestion du clic dans la listview
	private void variete_ClickCallBack(ListView liste_variete) 
	{	
		liste_variete.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) 
			{

			}
		});
	}
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.varietes__listview, menu);
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
}
