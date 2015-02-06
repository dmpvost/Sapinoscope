package com.ostermann.sapinoscope;

import java.util.List;
import java.util.Random;
import java.util.Vector;

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
import android.widget.Button;

public class Ajout_sapin extends Activity {

	private int positionX;
	private int positionY;
	
	private int secteurID;
	private int parcelleID;
	
	private Vector<Location> registredLocations;
	
	public enum STATUS_SAPIN {
		VIDE(0),
		NOUVEAU(1),
		OK(2),
		TOC(3);
	    private final int code;

	    private STATUS_SAPIN(int code) {
	        this.code = code;
	    }

	    public int toInt() {
	        return code;
	    }

	    public String toString() {
	        return String.valueOf(code);
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajout_sapin);
		
		Log.i("ajoutSapinAct","Initialisation de l'ajout de sapin...");
		
		Intent intentAjoutSapin = getIntent();
		secteurID  = intentAjoutSapin.getIntExtra("sect_id", -1);
		parcelleID = intentAjoutSapin.getIntExtra("parc_id", -1);
		if(secteurID == -1 || parcelleID == -1)
			Log.e("ajoutSapinAct","Impossible de récupérer les informations de l'intent, etat indetermine...");
		
		Log.i("ajoutSapinAct","Ajout prévue pour le secteur "+ secteurID +" de la parcelle "+parcelleID);
		
		getMaxXYsapinPosFromDB(positionX, positionY, secteurID);
		
		registredLocations = new Vector<Location>();
		
		Sapinoscope.getLocationHelper().startRecherche();
		
		Button addSapinBtn = (Button) findViewById(R.id.bt_addsap_sapin_existant);
		addSapinBtn.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				positionX++;
			}
		});
		
		Button newLineBtn = (Button) findViewById(R.id.bt_addsap_nouvelle_ligne);
		newLineBtn.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				positionY++;
			}
		});
		
		Button newSapinBtn = (Button) findViewById(R.id.bt_addsap_nouveau_sapin);
		newSapinBtn.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				
			}
		});
		
		Button newSoucheBtn = (Button) findViewById(R.id.bt_addsap_souche);
		newSoucheBtn.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				
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
    private int insertCoord(Location location) throws Exception
    {
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	
    	String reqSelectID = "SELECT COORD_ID FROM COORDONNEE WHERE(COORD_LAT= '"+location.getLatitude()+"' AND COORD_LON ='"+location.getLongitude()+";";
    	Cursor c = db.rawQuery(reqSelectID, null);
    	if (c.getCount() != 0)
    	{
    		c.moveToFirst();
    		return c.getInt(c.getColumnIndex("COORD_ID"));
    	}
    	
    	String reqInsert = "INSERT INTO COORDONNEE ('COORD_LAT','COORD_LON') VALUES ('"+location.getLatitude()+"','"+location.getLongitude()+"')";
		Log.i("testDB", reqInsert);
		db.execSQL(reqInsert);
		
		c = db.rawQuery(reqSelectID, null);
		if (c.getCount() == 0)
		{
			throw new Exception("Erreur lors de l'insertion des coordonnées :"+location.getLatitude()+", "+location.getLongitude());
		}
		else
		{
			c.moveToFirst();
    		return c.getInt(c.getColumnIndex("COORD_ID"));
		}
    }
    
    private boolean getMaxXYsapinPosFromDB(int x, int y, final int idSecteur)
    {
    	String reqSelectMaxX = "SELECT MAX(SAP_LIG) FROM SAPIN WHERE SEC_ID='"+idSecteur+"';";
    	String reqSelectMaxY = "SELECT MAX(SAP_COL) FROM SAPIN WHERE SEC_ID='"+idSecteur+"';";
    	
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
    	
    	Cursor cursorX = db.rawQuery(reqSelectMaxX, null);
    	Cursor cursorY = db.rawQuery(reqSelectMaxY, null);
    	
    	if(cursorX.getCount() == 0 && cursorY.getCount() == 0)
    	{
    		Log.i("ajoutSapinAct","pas encore de sapin enregistré pour cette parcelle, debut à 0,0");
    		x=0;
    		y=0;
    		return false;
    	}
    	cursorX.moveToFirst();
    	cursorY.moveToFirst();
    	x = cursorX.getInt(0);
    	y = cursorY.getInt(0);
    	Log.i("ajoutSapinAct","Sapins trouves pour ce secteur, debut à "+ x +","+y);
    	
    	return true;
    }
    
    private void addSapinAtCurrentPos(final STATUS_SAPIN status, final int secteurID, final int x, final int y, final int variete)
    {
    	int idCoord = -1;
    	try 
    	{
    		idCoord = insertCoord(Sapinoscope.getLocationHelper().getLocation());
		} catch (Exception e) 
    	{
			e.printStackTrace();
			Log.w("ajoutSapinAct", "Impossible de sauvegarder la position, sauvegarde du sapin sans position...");
		}
    	String requetteAjout;
    	
    	if(idCoord == -1)// on enregistre pas les coordonnes
    		requetteAjout = "INSERT INTO SAPIN ('VAR_ID',			'SEC_ID',			'SAP_LIG',	'SAP_COL',	'SAP_STATUS')"
		+"VALUES(								'"+ variete +"',	'"+secteurID+"',	'"+x+"',	'"+y+"',	'"+status.toInt()+");";
    	else// on enregistre les coordonnés du sapin
    		requetteAjout = "INSERT INTO SAPIN ('VAR_ID',		'SEC_ID',		'SAP_LIG',	'SAP_COL',	'SAP_STATUS', 			'COORD_ID')"
    								+"VALUES(	'"+ variete +"','"+secteurID+"','"+x+"',	'"+y+"',	'"+status.toInt()+"',	'"+idCoord+"');";
    	
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
    	db.execSQL(requetteAjout);
    }
}
