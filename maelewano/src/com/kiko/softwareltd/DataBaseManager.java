package com.kiko.softwareltd;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseManager extends SQLiteOpenHelper{

    private static String DB_PATH = "/data/data/com.kiko.softwareltd/databases/";
 
    //the name of my database
    private static String DB_NAME = "maelewanodb";
    //general table
    public String TAB_GENERAL = "general";
    
    private static SQLiteDatabase mDataBase;
 
    //public static SQLiteDatabase mDataBase;
    private static DataBaseManager sInstance = null;
    
    // database version   
    private static final int DATABASE_VERSION = 2;
 
    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     */
    private DataBaseManager() {
        super(ApplicationContextProvider.getContext(), DB_NAME, null, DATABASE_VERSION);
 
        try {
            createDataBase();
            openDataBase();
            //Toast.makeText(ApplicationContextProvider.getContext(), "Database working", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }
 
    /**
     * Singleton for DataBase
     *
     * @return singleton instance
     */
    public static DataBaseManager instance() {
 
        if (sInstance == null) {
            sInstance = new DataBaseManager();
        }
        return sInstance;
    }
 
 
    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     *
     * @throws java.io.IOException io exception
     */
    private void createDataBase() throws IOException {
 
        boolean dbExist = checkDataBase();
 
        if (dbExist) {
            // do nothing - database already exist
        	copyDataBase();
        } else {
 
            // By calling this method an empty database will be created into
            // the default system path
            // of my application so we are going to be able to overwrite that
            // database with our database.
            this.getReadableDatabase();
 
            try {
 
                copyDataBase();
 
            } catch (IOException e) {
 
                throw new Error("Error copying database");
            }
        }
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
 
        SQLiteDatabase checkDB = null;
 
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Log.e("DataBase", "Databse Checked");
        } catch (SQLiteException e) {
 
            // database doesn't exist yet.
        	Log.d("DataBase", e.toString());
 
        }
 
        if (checkDB != null) {
        	 Log.e("DataBase", "Database is there");
            checkDB.close();
 
        }
 
        return checkDB != null;
        
    }
 
    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transferring bytestream.
     *
     * @throws java.io.IOException io exception
     */
    public void copyDataBase() throws IOException {
 
        // Open your local db as the input stream
        InputStream myInput = ApplicationContextProvider.getContext().getAssets().open(DB_NAME);
 
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
 
        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
 
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
 
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
 
    }
 
    private void openDataBase() throws SQLException {
 
        // Open the database
        String myPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        
        Log.e("DataBase", "Databse Openned");
    }
 
    /**
     * Select method
     *
     * @param query select query
     * @return - Cursor with the results
     * @throws android.database.SQLException sql exception
     */
    public Cursor select(String query) throws SQLException {
        return mDataBase.rawQuery(query, null);
    }
 
   
    /**
     * Insert method
     *
     * @param table  - name of the table
     * @param values values to insert
     * @throws android.database.SQLException sql exception
     */
    public void insert(String table, ContentValues values) throws SQLException {
        mDataBase.insert(table, null, values);
        Log.e("Databasehealper", "Values inserted********************************");
    }
 
    /**
     * Delete method
     *
     * @param table - table name
     * @param where WHERE clause, if pass null, all the rows will be deleted
     * @throws android.database.SQLException sql exception
     */
    public void delete(String table, String where) throws SQLException {
 
        mDataBase.delete(table, where, null);
        Log.e("Databasehealper", "Values deleted********************************");
    }
 
    /**
     * Update method
     *
     * @param table  - table name
     * @param values - values to update
     * @param where  - WHERE clause, if pass null, all rows will be updated
     */
    public void update(String table, ContentValues values, String where) {
 
        mDataBase.update(table, values, where, null);
 
    }
 
    /**
     * Let you make a raw query
     *
     * @param command - the sql comand you want to run
     */
    public void sqlCommand(String command) {
        mDataBase.execSQL(command);
    }
 
    @Override
    public synchronized void close() {
 
        if (mDataBase != null)
            mDataBase.close();
 
        super.close();
 
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
 
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
    }
}
