package com.example.kktoolkitdemo.listview;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.example.kktoolkitdemo.R;
import com.kkbox.toolkit.listview.adapter.ReorderListAdapter;
import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.ui.KKDragAndDropListView;

import java.util.ArrayList;
import java.util.List;

public class KKDragAndDropListViewActivity extends KKActivity {
    private KKDragAndDropListView mListView;
    private String[] mString = {"item 1", "item 2", "item 3", "item 4", "item 5", "item 6" };
    private ArrayList<String> mStrings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_draganddrop);
        mListView = (KKDragAndDropListView) findViewById(R.id.drag_listview);

        mStrings = new ArrayList<String>();
        mStrings.add("item 1");
        mStrings.add("item 2");
        mStrings.add("item 3");
        mStrings.add("item 4");
        mStrings.add("item 5");
        mStrings.add("item 6");

        mListView.setAdapter(new DragAdapter(
                this,
                R.layout.layout_list_item,
                R.id.item_text,
                mStrings));
        mListView.setGrabberId(R.id.item_grab);

    }
    private class DragAdapter extends ArrayAdapter implements ReorderListAdapter{
        ArrayList<String> data;
        public DragAdapter(Context context, int resource, int textViewResourceId, ArrayList<String> objects) {
            super(context, resource, textViewResourceId, objects);
            data = objects;
        }

        @Override
        public Object removeAtPosition(int position) {
            String temp = data.remove(position);
            notifyDataSetChanged();
            return temp;
        }

        @Override
        public void addAtPosition(int position, Object object) {
            data.add(position, (String)object);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

    }
}
