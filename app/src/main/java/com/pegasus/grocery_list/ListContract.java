package com.pegasus.grocery_list;

import android.provider.BaseColumns;

public class ListContract {
	static final int DB_VERSION = 1;
	static final String DB_NAME = "ListDB";
	
	//empty constructor
	public ListContract(){}
	
	/* Inner class that defines the table lists */
	public static abstract class List implements BaseColumns {
		//Table name
		 static final String TABLE_NAME = "Lists";
		 
		//Column names individual
		 static final String _ID = "id";
		 static final String _NAME = "name";
		 static final String _DATA = "jsonString";
		 
		 //Columns; String array of the column names; used for db.query...
		 static final String[] columns = new String[]{List._ID, List._NAME, List._DATA};
		 
		//Data types
		 static final String TYPE_TEXT = "STRING";
		 static final String TYPE_INT = "INTEGER";
		 
		 //selectAll
		 static final String selectAll = "SELECT  * FROM " + List.TABLE_NAME;
		
		//onCreate
		static final String SQL_CREATE_TABLE = 
				"CREATE TABLE " + List.TABLE_NAME + " ( "
				+ List._ID + " " + List.TYPE_INT + " PRIMARY KEY, "
				+ List._NAME + " " + List.TYPE_TEXT + ", "
				+ List._DATA + " " + List.TYPE_TEXT + " )";
		
		static final String SQL_DROP_TABLE =
				"DROP TABLE IF EXISTS" + List.TABLE_NAME;
	}

}
