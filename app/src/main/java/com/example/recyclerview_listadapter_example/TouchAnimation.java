package com.example.recyclerview_listadapter_example;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TouchAnimation extends  ItemTouchHelper.Callback {

    private MyItemTouchHelperAdapter mAdapter;
    boolean canSwipe = true;
    private UndoOperation undoOperation;

   interface MyItemTouchHelperAdapter {
        void onItemUndo();
        boolean onItemMove(int from, int to);
        void onItemDelete(int pos);
    }

    interface UndoOperation{
        void showUndoSnackbar();
    }


    public void setUndoOperationListener(UndoOperation undoOper){
        this.undoOperation = undoOper;
    }



    public TouchAnimation(MyItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
    }

    //To reorder items in the list
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        canSwipe = false;

        //https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter#submitList(java.util.List%3CT%3E,%20java.lang.Runnable)
        //submitList method form ListAdapter works in separate thread and can cause IndexOutOfBoundsException
        //when user clicks swipe very fast. The solution is to prohibit swiping until timer run out;
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    try {
                        wait(1000); //wait for second and allow user to swipe again
                        canSwipe =true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

           //Delete an item and show snackbar
            mAdapter.onItemDelete(viewHolder.getAdapterPosition());
            if (undoOperation != null) {
                undoOperation.showUndoSnackbar();
            }

    }


    //Allow dragging
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    //Allow user to swipe again when timer run out.
    @Override
    public boolean isItemViewSwipeEnabled() {
        return canSwipe;
    }
}
