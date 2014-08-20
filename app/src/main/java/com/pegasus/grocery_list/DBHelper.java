package com.pegasus.grocery_list;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DBHelper.java";
	
	public DBHelper(Context context) {
		super(context, ListContract.DB_NAME, null, ListContract.DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(ListContract.List.SQL_CREATE_TABLE);
		//Creates a new DB table with id, name, and data columns
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(ListContract.List.SQL_DROP_TABLE);
		onCreate(db);
		//This removes the current table and calls the onCreate() method to create a new one with updated columns or whatever...
	}
	
	//All CRUD operations
	
	//Adding a new list
	public void addList(List_ list) {
		//Pass in a List_ object to add to the database
		SQLiteDatabase db = this.getWritableDatabase();
		//Get a writable instance of that database
		
		ContentValues cv = new ContentValues();
		cv.put(ListContract.List._NAME, list.getName());
		cv.put(ListContract.List._DATA, list.getData().toString());
		//populate a contentValues object with the name and data you want to store in the DB
		
		//insert rows into the proper rows in the DB
		db.insert(ListContract.List.TABLE_NAME, null, cv);
		db.close();
		//close the DB
	}
	
	// Getting single list
	public List_ getList(int id) {
		//the [int]id of a List_ object is passed in to identify the saved list you want
		Log.v(TAG, "getList(); id = " + id);
		//The id can not be less than 0
		if(id < 0) {
			id = 0;
		}
		SQLiteDatabase db = this.getReadableDatabase();
		//get a readable instance of the database
		Log.v(TAG, "getList(); db = " + db);
		//A Cursor object is what is returned from a DB query
		//Need the tableName, the columns, and the id that was passed in to find the List_ object
		Cursor cursor = db.query(ListContract.List.TABLE_NAME, 
				null, //null will return all columns 
				ListContract.List._ID + "=?",
				new String[] { String.valueOf(id + 1) },//This is the id passed in; and the value will replace '=?' on the previous line 
				null, null, null, null); //THis use to have (id + 1)....find out why... because it is off when you select a list to use
		
		Log.v(TAG, "String.valueOf(id + 1) = " + String.valueOf(id + 1));
		cursor.moveToFirst();
		Log.v(TAG, "getList(); just called cursor.moveToFirst()");
		if(cursor.getCount() != 0){
		
		Log.v(TAG, "getList(); cursor.getCount() = " + cursor.getCount());
		Log.v(TAG, "getList(); cursor.getString(0) = " + cursor.getString(0));
		Log.v(TAG, "getList(); cursor.getString(1) = " + cursor.getString(1));
		Log.v(TAG, "getList(); cursor.getString(2) = " + cursor.getString(2));
		}else {
			//Cursor.getCount() == 0
			Log.v(TAG, "getList(); else; cursor.getCount() = " + cursor.getCount());
			//If the cursor is out of bounds....
			
			//It is crashing here because the cursor doesnt have anything in it...
		}
		//When i choose a list they are numbered 0 -> list.length(); but the cursor is numbered
		//1 -> list.length()...so when I choose a list the number passed back is not the same
		//Log.d(TAG, "getList(); cursor.getInt(0) = " + cursor.getInt(0));
		
		//if (cursor != null)//If the id passed in matched any ids in the database table
	    //  cursor.moveToFirst();//move to the first position of the matched 
		//List_ objects [which there should only be one]
		List_ list = new List_(Integer.parseInt(cursor.getString(0)), 
				cursor.getString(1), cursor.getString(2));
		//Create a new List_ object 'list' with the id, name, and data retrieved from the cursor.
		db.close();
		//close the DB connection
		cursor.close();	
		return list;
		//return the List_ object
		}
	
	//setListData
	/*If i made a set listdata method how would it work....
	 * */
	
	// Getting All Contacts
	 public List<List_> getAllLists() {
		 //GOing to return a List object with all the saved List_ objects in the DB
	    List<List_> arrayList = new ArrayList<List_>();
	    //Create a new List<List_> object 'arrayList
	    
	    SQLiteDatabase db = this.getWritableDatabase();
	    //Get an instance of the DB
	    Cursor cursor = db.rawQuery(ListContract.List.selectAll, null);
	    //QUery the DB for all of the List_ objects saved...
	    
	 // looping through all rows and adding to list
	    //if (cursor != null)
	    if (cursor.moveToFirst()) {
	        do {
	            List_ l = new List_();
	            l.setID(Integer.parseInt(cursor.getString(0)));
	            l.setName(cursor.getString(1));
	            l.setData(cursor.getString(2));
	            // Adding contact to list
	            arrayList.add(l);
	        } while (cursor.moveToNext());
	    }
	    //Loop through all the items in the cursor
	    //create a new List_ object 'l'; set all variables to the current cursor position List_ object
	    //add the List_ object to the arrayList
	    db.close();
	    cursor.close();
	    //close DB and Cursor objects
	    return arrayList;
	    //return the arrayList
	 }
	 
	// Getting contacts Count; returns the number of lists in the DB
		public int getListsCount() {
			//String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.rawQuery(ListContract.List.selectAll, null);
	        cursor.close();
	        db.close();
	        // return count
	        return cursor.getCount();
		}
		
	// Updating single List_
	public int updateList(List_ list) {
		//pass in the List_ object you want to update 
		SQLiteDatabase db = this.getWritableDatabase();
		//Get a writable instance of the DB 
		 
	    ContentValues values = new ContentValues();
	    values.put(ListContract.List._ID, list.getID());
	    values.put(ListContract.List._NAME, list.getName());
	    values.put(ListContract.List._DATA, list.getData());
	    //Populate the LIst_ object with the new name, and data values
	    Log.v("DBHelper()", "updateList(); list.getID() = " + list.getID());
	    Log.v("DBHelper()", "updateList(); list.getData() = " + list.getData());

	    int number = db.update(ListContract.List.TABLE_NAME, 
	    		values, 
	    		ListContract.List._ID + " =? ",//ListContract.List._NAME + " =? AND " + ListContract.List._DATA + " = ?",
	            new String[] { String.valueOf(list.getID()) });
	     Log.v("DBHelper()", "updateList(); list.getData() = " + list.getData());
		 Log.v("DBHelper()", "updateList(); number returned from db.update() = " + number);
	    db.close();
	    // updating row
	    return number;
		}//end updateLIst
	
	//method for updating the id's
	public int updateListId(List_ list, int id){
		//This is going to update lists based on the data values. 
		//It will be used when deleting List_ objects from the database to keep the id numbers in order.
		Log.v(TAG, "updateListId(); id = " + id);
		
		//Get db reference
		SQLiteDatabase db = this.getWritableDatabase();
		
		//New values
		ContentValues values = new ContentValues();
		values.put(ListContract.List._ID, id);
		values.put(ListContract.List._NAME, list.getName());
	    values.put(ListContract.List._DATA, list.getData());
	    
	    Log.v(TAG, "updateListId(); just set the ContentValues!");
	    
	    int number = db.update(ListContract.List.TABLE_NAME, 
	    		values, 
	    		ListContract.List._NAME + " =? ", 
	    		new String[] { list.getName().toString() });
	    Log.v(TAG, "updateListId(); just called db.update; number = " + number);
	    
	    db.close();
	    
	    return number;
		
	}//end updateListId()
	
	// Deleting single contact
		public void deleteList(List_ list) {
			SQLiteDatabase db = this.getWritableDatabase();
			try{
				int count = db.delete(ListContract.List.TABLE_NAME, 
		    		ListContract.List._ID + " = ?",
		            new String[] { String.valueOf(list.getID()) });
				Log.v(TAG, "deleteList(); just deleted list.getData() " 
		            + list.getData());
				Log.v(TAG, "deleteList(); String.valueOf(list.getID()) = " 
		            + String.valueOf(list.getID()));
				Log.v(TAG, "deleteList(); list.getId() = " + list.getID());
				Log.v(TAG, "count = " + count);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				db.close();
			}
		}

}
