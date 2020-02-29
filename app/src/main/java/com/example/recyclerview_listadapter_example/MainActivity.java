package com.example.recyclerview_listadapter_example;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * This is RecyclerView with ListAdapter example
 * @author Kotikov Alexander
 * This code based on the article
 * https://guides.codepath.com/android/using-the-recyclerview
 *
 *
 * In this example you can:
 * Add item(s)
 * drag items
 * rename item
 * delete item
 * undo for deleted item
 *
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView rvContacts;
    ContactsAdapter adapter;

    View parentLayout; //for snackbar
    int itemID; //id used for sample Contacts data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(android.R.id.content);


        // Lookup the recyclerview in activity layout
        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);

        // Initialize adapter
        adapter = new ContactsAdapter();


        // Attach the adapter to the recyclerview
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items

        rvContacts.setLayoutManager(new LinearLayoutManager(this));

          //Every row in RecyclerView contains a button. Let's add a listener to it
        adapter.setButtonListener(new ContactsAdapter.ButtonListener(){
            @Override
            public void onButtonClick(int position, Contact contact) {
                Toast.makeText(getApplicationContext(), "BUTTON position="+ position+"  " + contact.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        //Listener for clicks on rows in RecyclerView
        adapter.setItemListener(new ContactsAdapter.MyItemClick() {
            @Override
            public void onItemClick(int position, Contact contact) {
                Toast.makeText(getApplicationContext(), "position="+ position+"  " + contact.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        //This is to enable swipe and move animations for recyclerview
        TouchAnimation callback = new TouchAnimation(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvContacts);

        //Add undo operation for cases when user swipe an item
        callback.setUndoOperationListener(new TouchAnimation.UndoOperation() {
            @Override
            public void showUndoSnackbar() {

               Snackbar snackbar =
                       Snackbar.make(parentLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.onItemUndo();
                    }
                });
                 snackbar.show();
            }
        });
    }


     //Button insert
    public void insertNewContact(View view) {
        ArrayList<Contact> contactsAddd = new ArrayList<Contact>();
        ++itemID;
        if (itemID % 2 == 0)
            contactsAddd.add(new Contact(itemID,"Item " + itemID, true));
        else
            contactsAddd.add(new Contact(itemID,"Item " + itemID, false));

        adapter.addMoreContacts(contactsAddd);

    }

  // Button delete
    public void deleteFirstContact(View view) {
        adapter.deleteContact();

    }

    //Button rename
    public void renameSecondContact(View view) {
        adapter.renameSecondContact();

    }

    //Button add 10 contacts
    public void add10Contacts(View view) {
        ArrayList<Contact> newContacts = new ArrayList<>();
        for (int i=0; i<10; i++) {
            ++itemID;
            if (itemID % 2 == 0)
                newContacts.add(new Contact(itemID, "Item " + itemID, true));
            else
                newContacts.add(new Contact(itemID, "Item " + itemID, false));
        }
        adapter.addMoreContacts(newContacts);

    }
}
