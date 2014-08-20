package com.pegasus.grocery_list;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CreateModifyListActivity extends ListActivity {
	
	//ArrayList to hold all of the string items
	ArrayList<String> items = new ArrayList<String>();
	private static final String TAG = "CreateModifyListActivity";
	
	//variable for request_code; used to verify which request 'onActivityResult' is responding too
	private static final int REQUEST_CODE = 10;

	//Array adapter to populate the ListView
	ArrayAdapter<String> adapter = null;
	
	//int id = null; set it to the id of the List_ passed in...
	String listId;

    EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_modify_list);

		//Get references to ListView
		final ListView lv = getListView();
		
		registerForContextMenu(lv);
		
		//Reference for two buttons 'add' and 'delete' and EditText
		Button addButton = (Button)findViewById(R.id.add_to_list);
		Button deleteButton = (Button)findViewById(R.id.delete_from_list);
		editText = (EditText)findViewById(R.id.input);

		//This is checking the bundle being passed into 'onCreate' for a String of items sent from the
		//ViewDatabaseListActivity.
		if(savedInstanceState != null){
			Log.v(TAG, "OnCreate(); savedInstanceState != null; = true");
			//Looking for extra in Bundle named 'data' and 'id'
			if(savedInstanceState.get("data") != null && savedInstanceState.get("id") != null){
                //Store the 'data' in a String
				String dataForItems = savedInstanceState.get("data").toString();
				Log.v(TAG, "onCreate(); dataForItems.split(\",\")[0] = " + dataForItems.split(",")[0]);
				
				listId = savedInstanceState.get("id").toString();
				Log.v(TAG, "onCreate(); listId = " + listId);

			//This loop will go through all the items from 'dataForItems' and add each one to the 
			//'items' arrayList
			//Split the 'dataForItems' String into an array of items to be added to 'items' arrayList
			String[] fillItems = dataForItems.split(",");
			for(int i = 0; i < fillItems.length; i++){
				//Use .trim() method to trim off leading and trailing white space
				items.add(fillItems[i].trim());
			}//end for loop
		  }
		}//end if(savedInstanceState != null)
		
		//Reference to DBHelper class --Handles all of the database operations.
		DBHelper db = getDBHelperInstance();

		//Is there anything in the database? --Check
		final List<List_> currentLists = db.getAllLists();
		
		//If there is currently nothing in the database; Toast a message for the user so they know
		if(currentLists.size() == 0){
			Log.v(TAG, "onCreate(); Currently nothing in the database");
			Toast.makeText(getApplicationContext(), 
					"The DataBase is currently empty. Please create, and save a list first.", 
					Toast.LENGTH_LONG).show();
		}
		
		//instantiate adapter...
		adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_multiple_choice, 
				items);
		
		//set the list adapter
		this.setListAdapter(adapter);
		
		//set choice mode to multiple
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Log.v(TAG, "Just set list adapter and set choice mode");
		
		//the add button onClickListener
		addButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//Retrieve the value of the EditText widget when the add button is pressed
				//EditText editText = (EditText)findViewById(R.id.input);
				Editable userInput = editText.getText();
				Log.v(TAG, "addButton onCLickListener; userInput = " + userInput);
				
				//Add the userInput to the 'items' arrayList
				items.add(userInput.toString());
				Log.v(TAG, "addButton onClickListener; added userInput" + userInput + "to 'items' arrayList");
				
				//Notify adapter of changes to arrayList
				adapter.notifyDataSetChanged();
				
				//Set the EditText value to empty string
				editText.setText("");
			}
			
		}); //end addButtonOnClickListener
		
		//the delete button onClickListener
		deleteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// When the delete button is clicked i need to get a list of checked items;
				SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
				//TODO: if checkedItems == null or empty handle it
				//Right now when delete is pressed and no items are checked, nothing happens...
				
				/*
				 * 
				//Start the count from the highest number; because when you start from 0
				//and delete an item, all the other items position will change. which means
				//i would have to start over every time there is an item removed.
				 * 
				*/
				
				//This will loop through the current arrayList starting from the highest index and delete all checked items
				//Based on their [int]position in the arrayList
				for(int i = lv.getCount(); i >= 0; i--) {
					if(checkedItems.get(i)){ //If the current item is checked
						Log.d(TAG, "inside deleteButtOnClickListener; checkedItems.get(i) = " + checkedItems.get(i));

						//Delete the item at position(i)
						items.remove(i);
					}
					//inform adapter of changes to arrayList
					adapter.notifyDataSetChanged();
				}
				//clear checked Items
				checkedItems.clear();	
				
				//After the items are deleted check if items arrayLIst is empty
				//if it is empty, then set the listId to null
				//This way when the user saves a list after deleting the contents of another
				//The program will save it as a new list, instead of overriding the one you deleted.
				if(items.isEmpty()){
					Log.v(TAG, "inside deleteButtOnClickListener; items.isEmpty() == true");
					//there is nothing in the items arrayLIst....set listId to null if it is not already
					//If the user deleted everything from the items arrayLIst then start a new list....
					//to do that you need to set the listId to null
					if(listId != null){
						Log.v(TAG, "inside DeleteButtonOnClickListener; calling setListId(null)");
						setListId(null);
					}
				}//end if items.isempty()
				
			}//end onCLick()
			
		}); //end deleteButton.OnClickListener
		//close the database object
		db.close();
		
		
	} //end onCreate

	
	@Override
	protected void onPause() {

		super.onPause();

		//I want to save the current list when onPause is called, or update the list if it is saved in the
		//db already
		
	}
	
	//private method to set listId when all the items in the list are deleted
	private void setListId(String value){
		//I want to set the listId to whatever value is passed in
		this.listId = value;
		Log.v(TAG, "setListId(); listId was set too = " + listId);
	}
	
	//private method to retrieve the value of listId when a list is passed in
	private String getListId(){
		return listId;
	}
	
	//Method that returns an instance of DBHelper class. This is just a wrapper method to aviod calling
	//it multiple times throughout this class.
	private DBHelper getDBHelperInstance(){
		return new DBHelper(this.getBaseContext());
		//return db;
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		/*
		 * This was added to prevent the current 'items' from being lost on orientation change
		 */
		super.onSaveInstanceState(outState);
		//It saves the current 'items' to the bundle; so, when 'onCreate' gets called again
		//it is passed the current 'items'
		outState.putString("data", to_s(items));
		Log.v(TAG, "onSaveInstanceState(); used the to_s(items) method; result = " + to_s(items));
		
		//Also have to put the listId into the bundle...
		outState.putString("id", getListId());
	}
	
	//to_s method will accept the arrayList 'items' and replace the [,] with '',''
	//THis is also a method to avoid multiple uses of the .replace method all over the class
	private String to_s(ArrayList list){
		return list.toString().replace("[", "").replace("]", "");
	}

    //This method will take a comma separated list of strings, split them up into array,
    //then fill the current list with the items
    //This will be an options menu. Thought about doing it automatically from the 'add' button, but that is
    //for adding a single item to the list. Adding multiple items at once is its own functionality
    //and needs its own options item
	private void importList(){
        Log.v(TAG, "importList();");
		//Get reference to editText
        editText = (EditText)findViewById(R.id.input);
        //Check if editText is empty
        if(editText.getText().toString() == ""){
            //There is nothing in the editText box
        }

            //Make sure there are items in the editText box
            String importText = editText.getText().toString();
            if((importText.indexOf(',')) > 0){
                //There is at least one comma; split up the string into an array
                String[] importItems = importText.split(",");
                Log.v(TAG, "importList(); importItems = " + importItems);
                //Now I have importItems as a string[]; add them to the items arrayList
                for(int i = 0; i < importItems.length; i++){
                    //add each item to the arrayList
                    items.add(importItems[i]);
                }

                //Notify adapter of items added
                adapter.notifyDataSetChanged();

                //Set the editText box to ""
                editText.setText("");
            }else{
                Log.e(TAG, "importList(); importItems did not contain a comma");
            }

	}

    //This method will be used to copy all items in the current list and place them, as a comma separated
    //string of items, in the editText box [ highlighted ] so the user can copy the list to the clipboard
    //and paste it into an email or text application for sending/sharing lists easily.
    private void exportList(){
        Log.v(TAG, "exportList();");
        //Check if the current list is empty. Continue if its not empty; do nothing if it is
        if(items.isEmpty()){

        }else{
            //there are items in the current list
            int totalSizeOfList = items.size();
            String sep = ","; //Item separator
            StringBuilder sb = new StringBuilder(totalSizeOfList);
            for(String s : items){
                sb.append(s).append(sep);
            }
            Log.v(TAG, "exportList(); StringBuilder sb = " + sb);
            //Need reference to the editTextbox to input the StringBuilder sb
            editText = (EditText)findViewById(R.id.input);
            editText.setText(sb.toString());
            editText.selectAll();
        }

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_modify_list, menu);
		menu.add(0, 1, 0, "Save List");
		menu.add(0, 2, 0, "Open Saved Lists");
		menu.add(0, 3, 0, "Group Items");
        menu.add(0, 4, 0, "Import List");
        menu.add(0, 5, 0, "Export List");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 1 || item.getItemId() == R.id.action_save){
			Log.d(TAG, "onOptionsItemSelected() [Save List]");
			
			//Want to check if the current list is in the database already..
			//GOing to need a db reference, and the current List_ id
			
			//Also need to check if items.empty? because if there is nothing in the list
			//the program will crash when user presses save...
			if(items.isEmpty()){
				//items are empty and we cannot save
				return true;
			}

            //Set the first item of the list as the 'name' of the list
            String name = items.get(0);
			
			if(listId != null){
				Log.v(TAG, "onOptionsItemSelected(); actionBar save button pressed; listId = " + listId);
				
				//Need a database reference
				DBHelper db = getDBHelperInstance();
				
				//Need an int variable of listId to retrieve the right list_ object
				int id = Integer.parseInt(listId);
				
				//Need to get a reference of the current items arraylist to update the list_ in the db
				String data = to_s(items);
				
				//Get a reference to the LIst_ that was passed from the database
				List_ l = db.getList(id - 1);
				
				//Update the data to include current changes
				l.setData(data);
				
				//update the List_ in the db
				db.updateList(l);
				
				//Close the db and exit
				db.close();
				return true;
			}
						
			//Database reference
			DBHelper db = getDBHelperInstance();
			
			//Make a new List_ object to save in the db
			//Remove the brackets from the string...
			List_ list = new List_(name, to_s(items));
			Log.v(TAG, "items.toString() = " + items.toString());
			Log.v(TAG, "onOptionsItemSelected(); list id = " + list.getID());
			//Save the current list to the database
			db.addList(list);
			
			//Set the list id to the current list
			//listId = String.valueOf(list.getID() + 1);
			List<List_> currentLists = db.getAllLists();
			Log.v(TAG, "onOptionsItemSelected(); currentLists.size() = " + currentLists.size());
			setListId(String.valueOf(currentLists.size()));
			
			//CLose db reference
			db.close();
			
			return true;
			
		}else if(item.getItemId() == 2) {
			Log.d(TAG, "onOptionsItemSelected(); [Open Saved List]");
			//This is where I can start the intent to open the sub-activity

			//Clear the arrayList items before leaving the main screen
			if(items.size() != 0){
				items.clear();
			}

			//The intent to open the viewDatabaseListActivity
			Intent i = new Intent(this, ViewDatabaseListActivity.class);
			//onActivityResult will be called when viewDatabaseListActivity is done...
			startActivityForResult(i, REQUEST_CODE);
			return true;
						
		}else if(item.getItemId() == 3){
			Log.d(TAG, "onOptionsItemSelected() [Group Items]");
			
			//Going to need 2 arraylists to hold the items
			ArrayList<String> checked = new ArrayList<String>();
			ArrayList<String> unchecked = new ArrayList<String>();
			
			//get reference to all checked items
			SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
			//If checkedItems == 0 or null or whatever return true;
			
			//Loop through the checkedItems; separating out the checked items
			for(int i = 0; i < getListView().getCount(); i++){
				//if current item is checked
				if(checkedItems.get(i)){
					Log.v(TAG, "onOptionsItemSelected(); checkedItems.get = true; "
							+ "items.get(i) = " + items.get(i));
					checked.add(items.get(i));
				}else{
					//Current item is not checked
					Log.v(TAG, "onOptionsItemSelected(); checkItems.get = false; "
							+ "items.get(i) =" + items.get(i));
					unchecked.add(items.get(i));
				}
			}
			//Log both of the arraylist's 
			Log.v(TAG, "onOptionsItemSelected(); checked items = " + checked);
			Log.v(TAG, "onOptionsItemSelected(); unchecked items = " + unchecked);
			//Clear all items from 'items' arrayList
			items.clear();
			//Add checked, and unchecked back into 'items' arrayList with checked added first...
			for(int i = 0; i < checked.size(); i++){
				//FOr each item in checked arrayList; add to 'items' arrayList
				items.add(checked.get(i));
			}
			
			for(int i = 0; i < unchecked.size(); i++){
				//FOr each item in checked arrayList; add to 'items' arrayList
				items.add(unchecked.get(i));
			}
			//Tell adapter that data has changed...
			adapter.notifyDataSetChanged();
			Log.v(TAG, "onOptionsItemSelected(); grouped items list = " + items);
			
			//Uncheck all items
			checkedItems.clear();
			return true;
		}else if(item.getItemId() == 4){
            //Call importList()
            importList();
            return true;
        }else if(item.getItemId() == 5){
            //Call importList()
            exportList();
            return true;
        }else{
			return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		//I could use a switch; but i only need 2 so if/else works for now...
		if(item.getTitle() == "Select All"){
			//code to select all items in the list; need reference to listView
			ListView lv = getListView();
			
			//THis loop will go through all the items in the listAdapter and set the checked
			//value to true...
			for(int i = 0; i < getListAdapter().getCount(); i++){
				lv.setItemChecked(i, true);
			}
		}else if (item.getTitle() == "De-Select All"){
			//code to de-select all items in the list; need reference to listView
			ListView lv = getListView();
			
			//THis loop will go through all the items in the listAdapter and set the checked
			//value to false
			for(int i = 0; i < getListAdapter().getCount(); i++){
				lv.setItemChecked(i, false);
			}
		}else {
			return false;
		}
		return true;
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, "Select All");
		menu.add(0, v.getId(), 0, "De-Select All");
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// This method is called when the sub-activity (ViewDatabaseListActivity) returns
		
		//Check that result_code is ok; && the requestCode is what i expect...
		if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){
			//Check if the 'name' and 'data' are in the Intent...
			if (data.hasExtra("data") && data.hasExtra("id")){
				//Set the Id
				String id = data.getExtras().getString("id");
				Log.v(TAG, "onActivityResult(); id = " + id);
				//Set the listData String variable
				String listData = data.getExtras().getString("data");
				Log.v(TAG, "onActivityResult(); listData = " + listData);

				//Fill a Bundle object with the information retrieved from the other activity
				//and call onCreate(bundle)
				//onCreate will look for the 'data' and populate the 'items' arrayList with it.
				Bundle myBundle = new Bundle();
				myBundle.putString("id", id);
				myBundle.putString("data", listData);
				onCreate(myBundle);

			}
		}
	}

}
