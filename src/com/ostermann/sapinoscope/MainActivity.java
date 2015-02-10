package com.ostermann.sapinoscope;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	private Context contexte = this; 

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{  
		super.onCreate(savedInstanceState);
		Log.i("Activity", "lancement du menu principal");

		setContentView(R.layout.activity_main);

		// Bouton capture de sapin
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent(contexte, Parcelle_listView.class);
				startActivity(intent);
			}
		});

		
		// Bouton Analyse (juste pour le test pour le moment)
		Button analyseButton = (Button) findViewById(R.id.button_analyse);
		analyseButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent(contexte, Ajout_sapin.class);
				startActivity(intent);
			}
		});
		
		// Bouton modifier varietes
			Button verietesBoutton = (Button) findViewById(R.id.button_varietes);
			verietesBoutton.setOnClickListener(new OnClickListener() 
			{
				public void onClick(View v) 
				{
					Intent intent = new Intent(contexte, Varietes_Listview.class);
					startActivity(intent);
				}
			});
		
		// Bouton Analyse (juste pour le test pour le moment)
		/*Button analyseButton = (Button) findViewById(R.id.button_analyse);
		analyseButton.setOnClickListener(new OnClickListener() 
		{

			/*
			@Override
			public void onClick(View v) 
			{
				// test de recuperation de donnee GPS :
				Log.d("LocTEST", "Tentative de wait good location");
				
				
				class LocationProgressDialog extends AsyncTask<Context, Void, Void>
				{
				    private final ProgressDialog dialog = new ProgressDialog();

				    protected void onPreExecute()
				    {
				        this.dialog.setMessage("Determining your location...");
				        this.dialog.show();
				    }

				    protected Void doInBackground(Context... params)
				    {
				        LocationHelper location = new LocationHelper();

				        location.getLocation(params[0], locationResult);

				        return null;
				    }

				    protected void onPostExecute(final Void unused)
				    {
				        if(this.dialog.isShowing())
				        {
				            this.dialog.dismiss();
				        }

				        useLocation(); //does the stuff that requires current location
				    }

				}
				
				
				
				ProgressDialog dialog = ProgressDialog.show(v.getContext(), "","Calcul de la meilleur position...", true);
				dialog.show();
				Sapinoscope.getLocationHelper().setGoodLocationListerner(3, new GoodLocationListener() {
					
					@Override
					public void goodLocationFound(Location goodLocation) {
						Log.i("LocTEST", "");
						Log.i("LocTEST", "Position trouvee :" + goodLocation.getLatitude() + " " + goodLocation.getLongitude());
						Toast.makeText(contexte, "Position trouvee :" + goodLocation.getLatitude() + " " + goodLocation.getLongitude(), Toast.LENGTH_SHORT).show();
					}
				});
				Location goodLocation = Sapinoscope.getLocationHelper().waitGoodLocation(3);
				
				Log.d("LocTEST", "good location : " +goodLocation.getLatitude()+ " " + goodLocation.getLongitude()  );
			}

			*/
			
			/*public void onClick(View v) 
			{
				// Exemple de select avec plusieur ligne :
				SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
				try
				{
					String selectQuery = "SELECT * FROM PARCELLE";
					Cursor c = db.rawQuery(selectQuery, null);
					if(c.moveToFirst())
					{
			            do{
			               //assing values 
			               String column0 = c.getString(c.getColumnIndex("PARC_ID"));
			               String column1 = c.getString(c.getColumnIndex("PARC_N"));
			               String column2 = c.getString(c.getColumnIndex("PARC_DESC"));
			               String column3 = c.getString(c.getColumnIndex("PARC_COEF")); 
			               Log.i("DB print","ID:"+column0+" n:"+column1+" desc:"+column2+" coef:"+column3);

			            }while(c.moveToNext());
			        }
					Log.i("testDB", "test select sans errors");
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}	
			}
		});*/

		// Bouton Analyse (juste pour le test pour le moment)
		Button syncButton = (Button) findViewById(R.id.button_syncrho);
		syncButton.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(contexte, Graham_graphe.class);
				startActivity(intent);
			}
		});
	}


	


}
