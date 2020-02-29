package com.example.recyclerview_listadapter_example;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ListAdapter класс работает асинхронно с получаемыми данными. Он хранит старый лист значений
 * у себя, поэтому для сравнения не надо вызывать ничего кроме
 * submitList( new ArrayList(list)); Здесь new ArrayList нужен чтобы данные
 * обновились. для Его работы в конструкторе нужен DiffUtil.ItemCallback<Contact> DIFF_CALLBACK
 *
 * https://guides.codepath.com/android/using-the-recyclerview#notifying-the-adapter
 */
public class ContactsAdapter extends ListAdapter<Contact, ContactsAdapter.ViewHolder>
        implements TouchAnimation.MyItemTouchHelperAdapter {

    private Contact undoContact;
    private int undoContactPosition;

    // Store a member variable for the contacts
    private List<Contact> mContacts = new ArrayList<Contact>();


    // constructor
    public ContactsAdapter() {
        super(DIFF_CALLBACK);
    }


    public static final DiffUtil.ItemCallback<Contact> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Contact>() {
                @Override
                public boolean areItemsTheSame(Contact oldItem, Contact newItem) {
                    return  oldItem.getId()== newItem.getId() ;
                }
                @Override
                public boolean areContentsTheSame(Contact oldItem, Contact newItem) {
                    return    (oldItem.getName().equals (newItem.getName()) && (oldItem.isOnline() == newItem.isOnline()));
                }
            };


    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Contact contact = getItem(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(contact.getName());
         Button button = viewHolder.messageButton;
         button.setText(contact.isOnline() ? "Message" : "Offline");
         button.setEnabled(contact.isOnline());
         viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
    }

//Work with data. Example methods

    public void addMoreContacts(List<Contact> newContacts) {
        mContacts.addAll(newContacts);
        submitList( new ArrayList(mContacts)); // DiffUtil takes care of the check
    }


    public void renameSecondContact( ) {
        if (mContacts.size() > 2) {
            mContacts.set(2, new Contact(mContacts.get(2).getId(), "EDITED", false));
            submitList(new ArrayList(mContacts)); // DiffUtil takes care of the check
        }
    }

    public void deleteContact( ) {
        if (mContacts.size() > 0) {
            mContacts.remove(0);
            submitList(new ArrayList(mContacts)); // DiffUtil takes care of the check
        }
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mContacts, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mContacts, i, i - 1);
            }
        }
        submitList( new ArrayList(mContacts));
        return true;
    }


    @Override
    public void onItemDelete(int position) {
        undoContact = mContacts.get(position);
        undoContactPosition = position;
        mContacts.remove(position);
        submitList(new ArrayList(mContacts));
    }

    @Override
    public void onItemUndo(){
        if (undoContact != null) {
            mContacts.add(undoContactPosition, undoContact );
            submitList( new ArrayList(mContacts));
            undoContact = null;

        }
    }




    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
         public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            int position = getAdapterPosition();


            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            messageButton = (Button) itemView.findViewById(R.id.message_button);
            messageButton.setOnClickListener(new ButtonClick(this));
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Contact user = mContacts.get(position);
                // We can access the data within the views

                if (itemClicker != null){
                    itemClicker.onItemClick(position,user);
                }
            }
        }


    }

    class ButtonClick implements View.OnClickListener{
        private  ViewHolder vh;

        public ButtonClick(ViewHolder vhh){
            this.vh = vhh;
        }
        @Override
        public void onClick(View v) {
            int pos  = vh.getAdapterPosition();
            if ((buttonClicker != null) && (pos != RecyclerView.NO_POSITION))
             {
                 buttonClicker.onButtonClick(pos,mContacts.get(pos));
             }
        }
    }

    private ButtonListener buttonClicker;
    interface ButtonListener{
        void onButtonClick(int position, Contact contact);
    }

    public void setButtonListener(ButtonListener buttonListener){
        this.buttonClicker = buttonListener;
    }


    public void setItemListener(MyItemClick listenerListener){
        this.itemClicker = listenerListener;
    }
    private MyItemClick itemClicker;
    interface MyItemClick{
        void onItemClick(int position, Contact contact);
    }

}