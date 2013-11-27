package com.example.kktoolkitdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.kktoolkitdemo.ExampleActivity;
import com.example.kktoolkitdemo.R;

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
