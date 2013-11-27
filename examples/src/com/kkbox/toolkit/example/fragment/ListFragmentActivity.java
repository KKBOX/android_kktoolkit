package com.kkbox.toolkit.example.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.kkbox.toolkit.example.ExampleActivity;
import com.kkbox.toolkit.example.R;

public class ListFragmentActivity extends ExampleActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfragment_example);
		getSupportActionBar().setTitle("KKFragment Example");

		ExampleCityListFragment firstFragment = new ExampleCityListFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		firstFragment.setArguments(getIntent().getExtras());
		fragmentTransaction.add(R.id.sub_fragment, firstFragment);
		fragmentTransaction.commit();
	}

}
