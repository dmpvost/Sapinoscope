package com.ostermann.sapinoscope;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
 
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
 
 
 
public class DataBaseHelper extends SQLiteOpenHelper {
 
    //The Android's default system path of your application database.
    private static String DB_PATH = Sapinoscope.getAppContext().getPackageName()+"/databases/";
    //private static String DB_PATH = "/data/data/votre_package_name/databases/";
      
    private static String DB_NAME = "Sapin_DB";
     
    private static String path = DB_PATH+DB_NAME;
     
    private SQLiteDatabase myDataBase;
      
    private final Context myContext;
     
    /**
      * Constructor
      * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
      * @param context
      */
    public DataBaseHelper(Context context) {
      
    super(context, DB_NAME, null, 1);
    this.myContext = context;
    }  
     
     /**
      * Creates a empty database on the system and rewrites it with your own database.
      * */
    public void createDataBase() throws IOException{
         
        boolean isdbExist = checkDataBase();
         
        if(!isdbExist){
            try{
                this.getReadableDatabase();
                copyDataBase();
            }
            catch(IOException e){
                throw new Error("Probleme de copie de la base de données");
            }
        }
         
    }
      
 
    /**
      * Check if the database already exist to avoid re-copying the file each time you open the application.
      * @return true if it exists, false if it doesn't
      */
    private boolean checkDataBase() {
        // TODO Auto-generated method stub
        SQLiteDatabase db = null;
        try{
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
            if (db != null)
                db.close();
        }
        catch(SQLiteException e){
            e.printStackTrace();
        }
         
        return db!=null?true:false;
    }
     
    /**
      * Copies your database from your local assets-folder to the just created empty database in the
      * system folder, from where it can be accessed and handled.
      * This is done by transfering bytestream.
      * */
     private void copyDataBase() throws IOException{
        // TODO Auto-generated method stub
        InputStream inputStream = myContext.getAssets().open(DB_NAME);
        OutputStream outputStream = new FileOutputStream(path);
         
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer))>0){
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }
      
     public void openDataBase() throws SQLException{
         myDataBase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
     }
      
     @Override
     public synchronized void close(){
          
         if(myDataBase !=null)
             myDataBase.close();
         super.close();
     }
     
      
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
 
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
 
    }
 
}