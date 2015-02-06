package com.ostermann.sapinoscope;

import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ostermann.sapinoscope.Object_sapin.Status_sapin;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Ajout_sapin extends Activity {

	private int positionX;
	private int positionY;
		
	private Object_secteur secteurActuel;
	private Object_parcelle parcelleActuel;
	private Object_variete varieteActuel;
	private float tailleActuel;
	private int nbIdentiqueActuel;
	
	boolean zigZag=true;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajout_sapin);
		
		Log.i("ajoutSapinAct","Initialisation de l'ajout de sapin...");
		
		Intent intentAjoutSapin = getIntent();
		int secteurID  = intentAjoutSapin.getIntExtra("sect_id", -1);
		int parcelleID = intentAjoutSapin.getIntExtra("parc_id", -1);
		if(secteurID == -1 || parcelleID == -1)
			Log.e("ajoutSapinAct","Impossible de récupérer les informations de l'intent, etat indetermine...");
		
		secteurActuel = new Object_secteur(secteurID);
		parcelleActuel = new Object_parcelle(parcelleID);
		//zigZag=secteurActuel.getZigzag();
		
		Log.i("ajoutSapinAct","Ajout prévue pour le secteur "+ secteurID +" de la parcelle "+parcelleID);
		
		getMaxXYsapinPosFromDB(secteurID);
		
		fillGui();
		
		Sapinoscope.getLocationHelper().startRecherche();
		
		//Listener de spinner
		Spinner varieteSpin 	= (Spinner) findViewById(R.id.spin_addsap_variete);
		varieteSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,int position, long id) {
				varieteActuel = (Object_variete) adapter.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Spinner tailleSpin 		= (Spinner) findViewById(R.id.spin_addsap_taille);
		tailleSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,int position, long id) {
				//transformation de la string "1,2m" en 1,2 lisible par un float :
				String  tailleString = (String) adapter.getItemAtPosition(position);
				Pattern p = Pattern.compile("(\\d+[,.]?\\d*)");
				Matcher m = p.matcher(tailleString);
				if(!m.find())
					Log.e("ajoutSapinAct", "impossible de lire le spinner de taille!");
				tailleActuel = Float.parseFloat(m.group(1).replace(",", "."));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Spinner nbItentiqueSpin = (Spinner) findViewById(R.id.spin_addsap_sap_identique);
		nbItentiqueSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,int position, long id) {
				nbIdentiqueActuel = Integer.parseInt((String)adapter.getItemAtPosition(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//Listener de bouton
		Button addSapinBtn = (Button) findViewById(R.id.bt_addsap_sapin_existant);
		addSapinBtn.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				addSapinAtCurrentPosInDb(Status_sapin.OK, nbIdentiqueActuel);
				goToNextPositionX();
			}
		});
		
		Button newLineBtn = (Button) findViewById(R.id.bt_addsap_nouvelle_ligne);
		newLineBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				positionY++;
				if(!zigZag)
					positionX=0;
			}
		});
		
		Button newSapinBtn = (Button) findViewById(R.id.bt_addsap_nouveau_sapin);
		newSapinBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				addSapinAtCurrentPosInDb(Status_sapin.NOUVEAU, nbIdentiqueActuel);
				goToNextPositionX();
			}
		});
		
		Button newSoucheBtn = (Button) findViewById(R.id.bt_addsap_souche);
		newSoucheBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				addSapinAtCurrentPosInDb(Status_sapin.TOC, nbIdentiqueActuel);
				goToNextPositionX();
			}
		});
	}
	
	private void tryDrawing(SurfaceHolder holder) {
        Log.i("Draw", "Trying to draw...");

        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            Log.e("Draw", "Cannot draw onto the canvas as it's null");
        } else {
            drawMyStuff(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawMyStuff(final Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		canvas.drawColor(Color.BLACK);
		Random random = new Random();
		
		int N = 50;
		Point2D[] points = new Point2D[N];
		
		for(int i=0;i<N;i++)
		{
			int x = random.nextInt(canvas.getWidth());
			int y = random.nextInt(canvas.getHeight());
			points[i] = new Point2D(x, y);
			canvas.drawCircle(x, y, 10, paint);
		}
		GrahamScan graham = new GrahamScan(points);
		
		Point2D start = null;
		Point2D stop = null;
		Point2D firstPoint = null;
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(5);
		for (Point2D p : graham.hull())
		{
			if(start == null)
			{
				firstPoint=p;
				start=p;
			}
			else
			{		
				stop=p;	
				canvas.drawLine((float)start.x(), (float)start.y(), (float)stop.x(), (float)stop.y(), paint);
				start=p;
			}
		}
		canvas.drawLine((float)start.x(), (float)start.y(), (float)firstPoint.x(), (float)firstPoint.y(), paint);
    }

    // return l'id de la coordonne insere dans la base 
    private int insertCoord(Point2D position) throws Exception
    {
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	
    	String reqSelectID = "SELECT COORD_ID FROM COORDONNEE WHERE(COORD_LAT= "+position.x()+" AND COORD_LON ="+position.y()+")";
    	Cursor c = db.rawQuery(reqSelectID, null);
    	if (c.getCount() != 0)
    	{
    		c.moveToFirst();
    		return c.getInt(c.getColumnIndex("COORD_ID"));
    	}
    	
    	String reqInsert = "INSERT INTO COORDONNEE ('COORD_LAT','COORD_LON') VALUES ("+position.x()+","+position.y()+")";
		Log.i("testDB", reqInsert);
		db.execSQL(reqInsert);
		
		c = db.rawQuery(reqSelectID, null);
		if (c.getCount() == 0)
		{
			throw new Exception("Erreur lors de l'insertion des coordonnées :"+position.x()+", "+position.y());
		}
		else
		{
			c.moveToFirst();
    		return c.getInt(c.getColumnIndex("COORD_ID"));
		}
    }
    
    private boolean getMaxXYsapinPosFromDB(final int idSecteur)
    {
    	//On commence par recuperer Y pour savoir s'il faut prendre le maximum ou le minimum de X
    	
    	String reqSelectMaxY = "SELECT MAX(SAP_COL) FROM SAPIN WHERE SEC_ID='"+idSecteur+"'";
    	
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
    	
    	Cursor cursorY = db.rawQuery(reqSelectMaxY, null);
    	cursorY.moveToFirst();
    	positionY = cursorY.getInt(0);
    	
    	if(zigZag && positionY%2 == 1) // Y est impaire et on est en mode zigzag, donc on decremente X 
    	{
    		String reqSelectX = "SELECT MIN(SAP_LIG) FROM SAPIN WHERE SEC_ID='"+idSecteur+"' AND SAP_COL='"+positionY+"'";
	    	Cursor cursorX = db.rawQuery(reqSelectX, null);
	    	cursorX.moveToFirst();
	    	int x = cursorX.getInt(0);
	    	positionX = (x==0) ? x : x-1;
    		
    	}else // Y est paire ou on est pas en mode zigzag, donc on incremente X
    	{
    		String reqSelectX = "SELECT MAX(SAP_LIG) FROM SAPIN WHERE SEC_ID='"+idSecteur+"' AND SAP_COL='"+positionY+"'";
	    	Cursor cursorX = db.rawQuery(reqSelectX, null);
	    	cursorX.moveToFirst();
	    	int x = cursorX.getInt(0);
	    	positionX = (x==0) ? x : x+1;
    	}
    	
    	if(positionX == 0 && positionY == 0)
    	{
    		Log.i("ajoutSapinAct","pas encore de sapin enregistré pour cette parcelle, debut à 0,0");
    		return false;
    	}
    	else
			Log.i("ajoutSapinAct","Sapins trouves pour ce secteur, debut à "+ positionX +","+positionY);
    	
    	return true;
    }
    
    private void addSapinAtCurrentPosInDb(Object_sapin.Status_sapin status, int nbIdentique)
    {
    	for(int i=0; i<nbIdentique; ++i)
    	{
	    	Object_sapin sapin = new Object_sapin();
	    	sapin.sec_id = secteurActuel.getId();
	    	sapin.status = status;
	    	sapin.var_id = varieteActuel.getVar_id();
	    	if(zigZag && positionY%2 == 1)//Les colonnes impaires sont décrémenté, les colonnes paire sont incrémenté
	    		sapin.xLigne = positionX - i;
	    	else
	    		sapin.xLigne = positionX + i;
	    	sapin.yColonne = positionY;
	    	
	    	boolean positionTrouve=false;
	    	try 
	    	{
	    		int idCoord = insertCoord(Sapinoscope.getLocationHelper().getLocation());
	    		sapin.coord_id=idCoord;
	    		positionTrouve=true;
			} catch (Exception e) 
	    	{
				Log.w("ajoutSapinAct", "Impossible de sauvegarder la position, sauvegarde du sapin sans position...");
			}
	    	sapin.saveInDb(positionTrouve);
    	}
    }
    
    private void goToNextPositionX()
    {
    	if(zigZag && positionY%2 == 1)
			setAndShowActuelPositionX(positionX-=nbIdentiqueActuel);
		else
			setAndShowActuelPositionX(positionX+=nbIdentiqueActuel);
    }
    
    private void setAndShowActuelPositionX(int x)
    {
    	 positionX = x;
    }
    
    private void setAndShowActuelPositionY(int y)
    {
    	positionY = y;
    }
    
    private void fillGui()
    {
    	TextView textViewParcelle = (TextView) findViewById(R.id.txt_addsap_parcelle_titre);
    	textViewParcelle.setText(parcelleActuel.getName());
    	
    	TextView textViewSecteur = (TextView) findViewById(R.id.txt_addsap_secteur_titre);
    	textViewSecteur.setText(secteurActuel.getName());
    	
    	Vector<Object_variete> varietes = Object_variete.createListOfAllVariete();
    	Spinner varieteSpinner = (Spinner) findViewById(R.id.spin_addsap_variete);
		ArrayAdapter<Object_variete> adapterVariete = new ArrayAdapter<Object_variete>(this,R.layout.secteur_texte,varietes); 
		varieteSpinner.setAdapter(adapterVariete);	
		
		Spinner tailleSpinner = (Spinner) findViewById(R.id.spin_addsap_taille);
		String[] tailles = getResources().getStringArray(R.array.taille_predefini);
		ArrayAdapter<String> adapterTaille = new ArrayAdapter<String>(this, R.layout.secteur_texte,tailles);
		tailleSpinner.setAdapter(adapterTaille);
		
		Spinner nbIdentiqueSpinner = (Spinner) findViewById(R.id.spin_addsap_sap_identique);
		String[] nbIdentiques = getResources().getStringArray(R.array.nbIdentiques);
		ArrayAdapter<String> nbIdentiquesAdapter = new ArrayAdapter<String>(this, R.layout.secteur_texte,nbIdentiques);
		nbIdentiqueSpinner.setAdapter(nbIdentiquesAdapter);
    }
}
