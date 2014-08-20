#This is my Grocery_List App

It is used to make simple lists of items, such as items you need from the grocery store. But you can also use
it as a quick ToDo List, or any other kind of simple list.

This app is very simple to use, and pretty basic. All options are done with the options menu, or a long click
on a list item will bring up a context menu.

#How to use this app

When you open the app you are presented on the main screen, where
you can create a new list or edit an existing list. If there are no existing lists, a Toast/Popup message will
tell you there is nothing in the database.

-Add Items to current list
Type in a new item in the edit text box and click the 'add' button to add new items to the current
list.

-"Naming" your list
You can name your lists before saving them in the database, but the only way to do it is by putting 
the name you want in the first position in your list(the first item of your list is used for the name). 
If you change it later, it will still have the first item in the list from the first time it was saved.

-Changing the name of your list
To change the name of your list enter the new name and add it to the list. Next, only select the new
names check box. Click options menu >> Group Items; This will being the new name to the top of the 
list. Next select the old name and click delete. Save your list.

-Delete items from list
Check the items check boxes and click delete to delete items from current
list. You can select as many items as you want in any order.

-Select/Deselect all items in the current list at once
You can select/de-select all items in the current list, long press on an item to 
show the context menu. Click select or de-select all to select or de-select
all items in the list respectively.

-Save the list to the sqlite database
To save the current list click options menu >> Save. The list will be added
to the database. You can also click the save icon in the action bar, and your list will be saved as well.

-View all saved lists in the database
To view all saved lists, click options menu >> Open Saved Lists; You will be taken 
to another screen with a list of all the lists that have been saved in the 
database. Click the list you want to view/edit and you will be taken back to the main screen with 
the chosen list items displayed.

-Delete entire list from database
To delete a list from the database; starting in the main screen click options menu >> Open Saved Lists. 
You are taken to another screen with a list of all the lists that have been saved in the database. 
Long click on the list you want to delete to bring up the context menu. 
Click 'Delete Saved List' to delete the list. You can delete one list at a time, in any order.

-Make a new list 
To make a new list, simply delete all the items in the current list. It will automatically start 
a new list when you delete all current list items.

-Group Items at the top of your list
To group some items at the top of the list, first click the check boxes of the items you want to move.
Click options menu >> 'Group Items' and all the checked items will move to the top of the list. 
All other items will be at the bottom. There is no way to undo the move, unless you do not save the 
changes and open the list from the database again. (This is assuming the list is saved in the 
database before grouping them together.) 

-Import multiple items to the current list
To import multiple items, either type in a comma separated list of items into the EditText box and go to Options >> Import List.
It will take the text from the EditText box, split up the list into a String[] and then add each individual item to the current list.
Does not matter if you have items in the list already or are building a new list the functionality is the same.

-Export your list to a comma separated string
To export your list to the edit text box, open or create your list. Next click options >> Export List; Once the list appears
as one string you can long click the text and it will automatically highlight all of it. Then click copy and you can paste it
into an e-mail application or text message to share your list easily.

=============================================================
TODO:

-Help menu; want an easy way for a user to learn how to use the app. Screen shots....or screen recording seems pretty easy [Google]; Or i can just
add screen shots to this readme.me file and put them with the instructions on the top.

=============================================================
BUGS:
1#Found a new bug....when you delete multiple lists from the database....it will crash with CUrsorIndexOutOfBoundsException:
	-DBHelper.getList(92)
	-ViewDatabaseListActivity.onContextItemSelected(166)
-It happens when you delete the first list, then the list moves, then delete the first list again, it will crash
-The ID's of the database lists do not update when you delete the first one....
-In the loop calling update list is not updating...because I am setting the list id and then calling update on an id that is not there...
FIXED:
-I added a method to the DBHelper class to update the IDs of each list in the database using the names of the saved lists to locate the right
list to update. 

2#When a user is making a list, there is nothing in the db, saves the list. Then edits the list without going to the db first, it will save a new list
because i did not set the listId variable to the current list.
Fixed:
called setListID after you save the list, so anytime you create a list you can save over and over and it will not create new lists every time.

In the dbHelper.getList method, if the id that is passed in is negative, set it to 0. It will never be negative.

3#When you press 'Save' multiple times while editing a list, white space will be added to the left side of the text, making them look indented.

Fixed:
used the .trim() method to trim the white space from the beginning and end of the item strings.
CreateModifyListActivity line=72

4#For some strange reason when you would create a new list, it would over write the first saved list in the db.

Fixed:
in createmodifylistactivity.onOptionsItemSelected()....set the listId to the size of the db items. This way the new list will get assigned the id
at the end of the db list and will not over write the first db list item each time.

5#When there are no items in the list, if the user presses 'save' the program will crash. 

Fixed: In createModifyActivity.java > onOptionsItemSelected() i have a string 'name' which gets initialized by the first item in the list.
It was positioned on top of the check to see if the list is empty; and if it is return true; SO it would try to set 'name' to nothing and was 
throwing an excrption. I just moved it below the check to see if the list is empty and it no longer crashes.