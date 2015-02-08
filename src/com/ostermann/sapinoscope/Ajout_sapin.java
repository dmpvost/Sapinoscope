package com.ostermann.sapinoscope;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ostermann.sapinoscope.Object_sapin.Status_sapin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	private int nbSapinLigne=0;
	
	boolean zigZag=true;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajout_sapin);
		
		Log.i("ajoutSapinAct","Initialisation de l'ajout de sapin...");
		
		Intent intentAjoutSapin = getIntent();
		int secteurID  = intentAjoutSapin.getIntExtra("sect_id", -1);
		if(secteurID == -1 )
			Log.e("ajoutSapinAct","Impossible de recuperer les informations de l'intent, etat indetermine...");

		
		secteurActuel = new Object_secteur(secteurID);
		parcelleActuel = new Object_parcelle(secteurActuel.getId_parc());
		//zigZag=secteurActuel.getZigzag();
		
		Log.i("ajoutSapinAct","Ajout prevue pour le secteur "+ secteurID +" de la parcelle "+secteurActuel.getId_parc());
		
		int newSecteur = intentAjoutSapin.getIntExtra("new_secteur", -1);//TODO CHANGER CA POUR QUE CA MARCHE
		if(newSecteur == 0)
		{
			int xDepart = intentAjoutSapin.getIntExtra("x", -1);//TODO CHANGER CA POUR QUE CA MARCHE
			int yDepart = intentAjoutSapin.getIntExtra("y", -1);//TODO CHANGER CA POUR QUE CA MARCHE
			
			Log.i("ajoutSapinAct","Reprise de l'enregistrement a partir de x="+xDepart +" et y="+yDepart);
			
			Vector<Etat_sapin> etatSapin0 = Etat_sapin.createListOfInfoSapinFromXY(secteurID, xDepart, yDepart);
			
			int x1 = getNextStepX(xDepart, yDepart, secteurActuel.getZigzag()); 
			Vector<Etat_sapin> etatSapin1 = Etat_sapin.createListOfInfoSapinFromXY(secteurID, x1, yDepart);
			
			int x2 = getNextStepX(x1, yDepart, secteurActuel.getZigzag()); 
			Vector<Etat_sapin> etatSapin2 = Etat_sapin.createListOfInfoSapinFromXY(secteurID, x2, yDepart);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Add the buttons
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User clicked OK button
			           }
			       });
						
			CharSequence[] infoOfLast3Sapins = new CharSequence[3];
			
			infoOfLast3Sapins[0] = etatSapin0!=null ? "Sapin de depart : "+(Object_variete.getVarieteName(etatSapin0.get(0).sapin.var_id) +" " +etatSapin0.get(0).infoSapin.taille +"m\n"
														+new SimpleDateFormat("(dd/MM/yyyy)").format(etatSapin0.get(0).infoSapin.date)) : "";
			infoOfLast3Sapins[1] = etatSapin1!=null ? "Sapin suivant 2 : "+(Object_variete.getVarieteName(etatSapin1.get(0).sapin.var_id) +" " +etatSapin1.get(0).infoSapin.taille +"m\n"
														+new SimpleDateFormat("(dd/MM/yyyy)").format(etatSapin1.get(0).infoSapin.date)) : "";
			infoOfLast3Sapins[2] = etatSapin2!=null ? "Sapin suivant 3 : "+(Object_variete.getVarieteName(etatSapin2.get(0).sapin.var_id) +" " +etatSapin2.get(0).infoSapin.taille +"m\n"
														+new SimpleDateFormat("(dd/MM/yyyy)").format(etatSapin2.get(0).infoSapin.date)) : "";
			
			builder.setItems(infoOfLast3Sapins, null);

			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();
			setAndShowActuelPositionX(xDepart);
			setAndShowActuelPositionX(yDepart);
			
			nbSapinLigne = getNbSapinOnY(secteurID,xDepart,yDepart);
			setTextView_NbSapin_ligne(nbSapinLigne);
		}
		else
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
				goToNextPositionY();
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
	
	protected void onPause()
	{
		super.onPause();
		Sapinoscope.getLocationHelper().stopRecherche();
	}
	
	protected void onStop()
	{
		super.onStop();
		Sapinoscope.getLocationHelper().stopRecherche();
		setPositionsSecteur();
	}
	
	protected void onResume()
	{
		super.onResume();
		Sapinoscope.getLocationHelper().startRecherche();
	}
	
	protected void onStart()
	{
		super.onStart();
		Sapinoscope.getLocationHelper().startRecherche();
	}
	
	protected void onDestroy()
	{
		super.onStart();
		Sapinoscope.getLocationHelper().stopRecherche();
		setPositionsSecteur();
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
		Vector<Point2D> points = new Vector<Point2D>(N);
		
		for(int i=0;i<N;i++)
		{
			int x = random.nextInt(canvas.getWidth());
			int y = random.nextInt(canvas.getHeight());
			points.add(new Point2D(x, y));
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
			throw new Exception("Erreur lors de l'insertion des coordonn�es :"+position.x()+", "+position.y());
		}
		else
		{
			c.moveToFirst();
    		return c.getInt(c.getColumnIndex("COORD_ID"));
		}
    }
    
    private int getNbSapinOnY(int idSecteur, int x, int y)
    {
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
    	String reqMinSelectX = "SELECT MIN(SAP_LIG) FROM SAPIN WHERE SEC_ID='"+idSecteur+"' AND SAP_COL='"+positionY+"'";
    	Cursor cursorX = db.rawQuery(reqMinSelectX, null);
    	cursorX.moveToFirst();
    	int minX = cursorX.getInt(0);
    	
    	return x-minX;
    }
    
    private boolean getMaxXYsapinPosFromDB(final int idSecteur)
    {
    	//On commence par recuperer Y pour savoir s'il faut prendre le maximum ou le minimum de X
    	
    	String reqSelectMaxY = "SELECT MAX(SAP_COL) FROM SAPIN WHERE SEC_ID='"+idSecteur+"'";
    	
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
    	
    	Cursor cursorY = db.rawQuery(reqSelectMaxY, null);
    	cursorY.moveToFirst();
    	positionY = cursorY.getInt(0);
    	
    	String reqMinSelectX = "SELECT MIN(SAP_LIG) FROM SAPIN WHERE SEC_ID='"+idSecteur+"' AND SAP_COL='"+positionY+"'";
    	Cursor cursorX = db.rawQuery(reqMinSelectX, null);
    	cursorX.moveToFirst();
    	int minX = cursorX.getInt(0);
    	
    	String reqMaxSelectX = "SELECT MAX(SAP_LIG) FROM SAPIN WHERE SEC_ID='"+idSecteur+"' AND SAP_COL='"+positionY+"'";
    	cursorX = db.rawQuery(reqMaxSelectX, null);
    	cursorX.moveToFirst();
    	int maxX = cursorX.getInt(0);
    	
    	if(zigZag && positionY%2 == 1) // Y est impaire et on est en mode zigzag, donc on decremente X 
    	{
	    	positionX = (minX==0) ? minX : minX-1;
    		
    	}else // Y est paire ou on est pas en mode zigzag, donc on incremente X
    	{
	    	positionX = (maxX==0) ? maxX : maxX+1;
    	}
    	
    	nbSapinLigne = maxX - minX;
    	setTextView_NbSapin_ligne(nbSapinLigne);
    	
    	if(positionX == 0 && positionY == 0)
    	{
    		Log.i("ajoutSapinAct","pas encore de sapin enregistr� pour cette parcelle, debut � 0,0");
    		return false;
    	}
    	else
			Log.i("ajoutSapinAct","Sapins trouves pour ce secteur, debut � "+ positionX +","+positionY);
    	
    	return true;
    }
    
    private void addSapinAtCurrentPosInDb(Object_sapin.Status_sapin status, int nbIdentique)
    {
    	for(int i=0; i<nbIdentique; ++i)
    	{
	    	Object_sapin sapin = new Object_sapin();
	    	sapin.sec_id = secteurActuel.getId();
	    	sapin.var_id = varieteActuel.getVar_id();
	    	if(zigZag && positionY%2 == 1)//Les colonnes impaires sont d�cr�ment�, les colonnes paire sont incr�ment�
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
	    	
	    	Object_infoSapin infoSapin = new Object_infoSapin(sapin.getSapId(), Calendar.getInstance().getTime());
	    	infoSapin.status= status;
	    	infoSapin.taille= tailleActuel;
	    	
	    	infoSapin.saveInDb();
    	}
    }
    
    // Donne quel serait le point suivant a entrer : 
    public static int getNextStepX(int x, int y, boolean _zigzag)
    {
    	if(_zigzag && y%2 == 1)
			return x-1;
		else
			return x+1;
    }
    
    private void goToNextPositionX()
    {
   	 	nbSapinLigne++;
    	if(zigZag && positionY%2 == 1)
			setAndShowActuelPositionX(positionX-=nbIdentiqueActuel);
		else
			setAndShowActuelPositionX(positionX+=nbIdentiqueActuel);
    }
    
    private void goToNextPositionY()
    {
        nbSapinLigne=0;
    	setAndShowActuelPositionY(positionY+1);
    	if(zigZag)
    	{
    		if( positionY%2 == 0)
    			setAndShowActuelPositionX(positionX+1);
    		else
    			setAndShowActuelPositionX(positionX-1);
    	}else
    		setAndShowActuelPositionX(0);
    }
    
    private void setAndShowActuelPositionX(int x)
    {
    	 positionX = x;
    	 setTextView_NbSapin_ligne(nbSapinLigne);
        
    }
    
    private void setAndShowActuelPositionY(int y)
    {
        // Affiche 0 lors d une nouvelle ligne
        setTextView_NbSapin_ligne(nbSapinLigne);
    	positionY = y;
   	 	TextView txtView_getY = (TextView) findViewById(R.id.txt_addsapin_getY);
   	 	txtView_getY.setText("Ligne :"+y);
    }
    
    private void fillGui()
    {
    	TextView textViewParcelle = (TextView) findViewById(R.id.txt_addsap_parcelle_titre);
    	textViewParcelle.setText("Parcelle : "+parcelleActuel.getName());
    	
    	TextView textViewSecteur = (TextView) findViewById(R.id.txt_addsap_secteur_titre);
    	textViewSecteur.setText("Secteur : "+secteurActuel.getName());
    	
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
    

    private void removeAllCoordForSecteur(int secteurID)
    {
    	String requette = "DELETE FROM SEC_COORD WHERE SEC_ID="+secteurID;
    	
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	db.execSQL(requette);
    }
    
    private void insertPointSecteur(Point2D p, int secteurID)
    {
    	String requette = "SELECT COORD_ID FROM COORDONNEE WHERE COORD_LAT="+p.x()+" AND COORD_LON="+p.y();
    	
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	
    	Cursor c = db.rawQuery(requette, null);
    	
    	if(c.getCount()==0)
    	{
    		Log.e("ajoutSapinAct","Impossible de retrouver le point : ("+p.x()+" "+p.y()+") dans la base!");
    		return;
    	}
    	
    	if(c.getCount()>1)
    		Log.w("ajoutSapinAct","Le point : ("+p.x()+" "+p.y()+") est present 2 fois dans la base!");
    	
    	c.moveToFirst();
    	
    	int coordID = c.getInt(0);
    	
    	requette = "INSERT INTO SAPIN (	'SEC_ID',			'COORD_ID')"
			   				+"VALUES(	'"+secteurID +"',	'"+coordID+"')";
    	
    	db.execSQL(requette);
    }
    
    private void setPositionsSecteur()
    {
    	Vector<Object_sapin> sapinList = Object_sapin.createListOfSapin(secteurActuel.getId());
    	Vector<Point2D> positionsSapinConnus = new Vector<Point2D>();
    	    	
    	for(int i=0; i<sapinList.size(); ++i)
    	{
    		if(sapinList.get(i).coordonne != null)
    			positionsSapinConnus.add(sapinList.get(i).coordonne);
    	}
    	
    	if(positionsSapinConnus.size() <3)
    	{
    		Log.e("ajoutSapinAct","Pas assez de points de sapin pour definir un secteur, le secteur n'est pas defini.");
    		return;
    	}
    		
    	removeAllCoordForSecteur(secteurActuel.getId());
    	
    	GrahamScan graham = new GrahamScan(positionsSapinConnus);
    	for(Point2D p : graham.hull())
    	{
    		insertPointSecteur(p,secteurActuel.getId());
    	}
    }

    private void setTextView_NbSapin_ligne(int x)
    {
   	 TextView txtView_getX = (TextView) findViewById(R.id.txt_addsapin_getX);
   	 txtView_getX.setText("Sapin N° :"+x);

    }
}
