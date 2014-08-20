package com.pegasus.grocery_list;

import java.util.ArrayList;

public class List_ {
	//private variables
    private int _id;
    private String _name;
    private String _data;
    
 // Empty constructor
    public List_(){
         
    }
    // constructor 
    //Create an instance of List_ class with id, name, and data provided
    public List_(int id, String name, String data){
        this._id = id;
        this._name = name;
        this._data = data;
    }
     
    // constructor
    //Create an instance of List_ class with name, and data provided
    public List_(String name, String data){
        this._name = name;
        this._data= data;
    }

    
 // getting ID; return the id of List_ object
    public int getID(){
        return this._id;
    }
     
    // setting id; set the id of the List_ object
    public void setID(int id){
        this._id = id;
    }
 
    //getting Name; return the name of List_ object
    public String getName(){
    	return this._name;
    }
    //setting Name; set the name of List_ object
    public void setName(String name){
    	this._name = name;
    }
    
    //getting Data; return the data of List_ object
    public String getData(){
    	return this._data;
    }
    //setting Data; set the data of the List_ object
    public void setData(String data){
    	this._data = data;
    }

}
