package com.pegasus.grocery_list;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewDatabaseListActivity extends ListActivity {
	
		//ArrayList to hold all of the string items
		ArrayList<String> items = new ArrayList<String>();
		
		//Array adapter to populate the ListView
		ArrayAdapter<String> adapter = null;
		
		//TAG
		private final String TAG = "ViewDatabaseListActivity class";
		
		//need a variable for storing the position of the clicked list item
		private int position;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_database_list);
		
		//Get references to widgets to use later
		final ListView lv = getListView();
		registerForContextMenu(lv);
		final DBHelper db = getDBHelperInstance();
		
		//Need all of the elements stored in the DB
		List<List_> dbLists = db.getAllLists();
		Log.v(TAG, "onCreate; dbLists = " + dbLists);
		
		//Now for each element in dbLists I want the .getData() to add the data to the items arrayList
		//to display in the ListView.
		for(int i=0; i < dbLists.size(); i++){
			Log.v(TAG, "onCreate(); dbLists.get(i).getData = " + dbLists.get(i).getData());
			Log.v(TAG, "onCreate(); dbLists.get(i).getID = " + dbLists.get(i).getID());
			
			//Added a "\n" to make the name: easier to read when viewing lists in the database.
			String info = "Name: " + dbLists.get(i).getName() + ";" + "\n" + "Items: " + 
					dbLists.get(i).getData(); 
			items.add(info);
		}
		
		//If Items.size = 0 then I can go back to the original application because there is 
		//nothing in the database.
		if(items.size() == 0){
			Log.v(TAG, "there is nothing in the database at this moment...");

			Intent i = new Intent(this, CreateModifyListActivity.class);
			startActivity(i);
		}

		//instantiate adapter...only need single choice mode....
			adapter = new ArrayAdapter<String>(this, 
					android.R.layout.simple_list_item_checked, 
					items);
			
			//set the list adapter
			this.setListAdapter(adapter);
			
			//set choice mode to single
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			
			//close the db reference
			db.close();
	}//end onCreate()
	
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}



	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.v(TAG, "onListItemClick(); position = " + position);
		Log.v(TAG, "id = " + id);
		
		//When a user clicks the DB list, the position and id are the same.
		//So once I know the position of the list I can send that information 
		//back to the main activity to open the correct list 
		setPosition(position);
		Log.v(TAG, "onListItemClick(); User just clicked a list item; setPosition(position); position = "
				+ position);
		
		//after the user selects the item call the finish() method to return the data to the 
		//main class.
		finish();

	}
	
	@Override
	public void finish() {
		// This is the method to use an intent to send the data back to the main activity
		//get all the information from the db and .putExtra all the data so the main 
		//method will have access to it
		Log.v(TAG, "finish(); postion =" + getPosition());
		
		//Db reference
		final DBHelper db = getDBHelperInstance();
		
		//Make a List_ object from the position chosen by the user.
		List_ l = db.getList(getPosition());
		Log.v(TAG, "finish(); just called db.getList(getPosition); getPosition = " + getPosition());

		String data = l.getData();
		int id = l.getID();
		Intent i = new Intent();
		//add the 'id' into the intent so i can use it to update lists that were chosen from the db
		i.putExtra("id", String.valueOf(id));
		//add the 'data' into the intent so it can be passed to createModifyListActivity
		i.putExtra("data",	data);
		setResult(RESULT_OK, i);
		
		//Close the db because were leaving to go back to the createModifyListActivity page
		db.close();
		
		super.finish();
	}
	
	private void setPosition(int p){
		this.position = p;
	}
	
	private int getPosition(){
		return position;
	}
	
	//Method that returns an instance of DBHelper class. This is just a wrapper method to aviod calling
	//it multiple times throughout this class.
	private DBHelper getDBHelperInstance(){
		DBHelper db = new DBHelper(this.getBaseContext());
		return db;
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		//It is asking for an AdapterView.AdapterContextMenuInfo object
		//it is casting the result of item.getMenuInfo() to an AdapterView.AdapterContextMenuInfo object
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		//info.position will give the index of selected item
		//I need to know the index to retrieve the correct list from the database 
		//in the createModifyListActivity
		int indexOfSelectedItem = info.position;
		Log.v(TAG, "onContextItemSelected(); info.position = " + info.position);
		
		if(item.getTitle() == "Delete Saved List"){
			//This one I have to send a List_ object to the DBHelper.deleteList() method
			Log.v(TAG, "onContextItemSelected(); indexOfSelectedItem = " + indexOfSelectedItem);
			//Get a reference to DBHelper class
			DBHelper db = getDBHelperInstance();
			//Create a List_ object 
			List_ l = db.getList(indexOfSelectedItem);
			//delete list
			
			Log.v(TAG, "onContextItemSelected(); about to delete the List_from db;"
					+ "l.getData() = " + l.getData());
			Log.v(TAG, "onContextItemSelected(); l.getID() = " + l.getID());
			db.deleteList(l);

			items.remove(indexOfSelectedItem);
			adapter.notifyDataSetChanged();
			
			//Need all of the elements stored in the DB; because i just removed an item
			List<List_> dbLists = db.getAllLists();
			
			//If there is no lists in the db anymore...it will take you to the 'CreateModifyListActivity'
			//if dbLists.length == 0 start CreateModifyActivity() so user can start over...
			if(dbLists.size() == 0){
				Log.v(TAG, "there is nothing in the database at this moment...");
				
				//close the db object
				db.close();
				
				Intent i = new Intent(this, CreateModifyListActivity.class);
				startActivity(i);
			}
			//I can use this for loop and the dbLists to loop through all of the current items in the list
			//FOr each item in the dbLists make a LIst_ object and call its setID() correctly
			//This way whenever a database is deleted, the id's are updated and put back in order
			//without missing numbers
			
			//Had to make a new method for updating the Id's of the list_ objects saved in the db.
			
			int count = 1;
			for(List_ list : dbLists){

				//send each list to the db.updateListId() method with the corresponding id number
				Log.v(TAG, "onCOntextMenuItemSelected(); sending list to updateLIstId with count = " + count);
				db.updateListId(list, count);
				count++;
			}

			db.close();
		}
		
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, "Delete Saved List");
	}

	
}
