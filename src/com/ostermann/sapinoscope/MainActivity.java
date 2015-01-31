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

			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(contexte, Parcelle_activity.class);
				startActivity(intent);
			}
		});

		// Bouton Analyse (juste pour le test pour le moment)
		Button analyseButton = (Button) findViewById(R.id.button_analyse);
		analyseButton.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
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
		});

		// Bouton Analyse (juste pour le test pour le moment)
		Button syncButton = (Button) findViewById(R.id.button_syncrho);
		syncButton.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				// Exemple d'insert
				SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
				try{
					db.execSQL("INSERT INTO PARCELLE VALUES('2','colroy','colory desc','1')");
					Log.i("testDB", "test insert sans errors");
				}catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



}
