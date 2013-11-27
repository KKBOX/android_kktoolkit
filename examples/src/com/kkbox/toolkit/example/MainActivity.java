package com.kkbox.toolkit.example;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kkbox.toolkit.example.api.KKAPIActivity;
import com.kkbox.toolkit.example.circleImageView.CircleImageViewActivity;
import com.kkbox.toolkit.example.eventqueue.EventQueueActivity;
import com.kkbox.toolkit.example.fragment.ListFragmentActivity;
import com.kkbox.toolkit.example.image.ImageManagerActivity;
import com.kkbox.toolkit.example.listview.KKDragAndDropListViewActivity;
import com.kkbox.toolkit.example.listview.KKListViewActivity;
import com.kkbox.toolkit.example.messageview.KKMessageViewActivity;
import com.kkbox.toolkit.example.notification.ActivityNotification;
import com.kkbox.toolkit.example.resizableview.ResizableViewActivity;
import com.kkbox.toolkit.example.tabfragment.ActivityTabFragment;
import com.kkbox.toolkit.example.viewpager.InfiniteViewPagerActivity;

public class MainActivity extends ListActivity {
	private String[] mStrings = {
			"KKMessageView",
			"InfiniteViewPager",
			"ResizableView",
			"KKEventQueue",
			"KKListView",
			"KKDragAndDropListView",
			"KKTabFragment",
			"KKImageManager",
			"KKAPIBase",
			"KKFragment",
			"KKDialog",
			"CircleImageView"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, mStrings));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = null;
		switch (position) {
			case 0:
				intent = new Intent(MainActivity.this, KKMessageViewActivity.class);
				break;
			case 1:
				intent = new Intent(MainActivity.this, InfiniteViewPagerActivity.class);
				break;
			case 2:
				intent = new Intent(MainActivity.this, ResizableViewActivity.class);
				break;
			case 3:
				intent = new Intent(MainActivity.this, EventQueueActivity.class);
				break;
			case 4:
				intent = new Intent(MainActivity.this, KKListViewActivity.class);
				break;
			case 5:
				intent = new Intent(MainActivity.this, KKDragAndDropListViewActivity.class);
				break;
			case 6:
				intent = new Intent(MainActivity.this, ActivityTabFragment.class);
				break;
			case 7:
				intent = new Intent(MainActivity.this, ImageManagerActivity.class);
				break;
			case 8:
				intent = new Intent(MainActivity.this, KKAPIActivity.class);
				break;
			case 9:
				intent = new Intent(MainActivity.this, ListFragmentActivity.class);
				break;
			case 10:
				intent = new Intent(MainActivity.this, ActivityNotification.class);
				break;
			case 11:
				intent = new Intent(MainActivity.this, CircleImageViewActivity.class);
				break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}
}
