package com.example.kktoolkitdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.kktoolkitdemo.R;
import com.example.kktoolkitdemo.SampleUtil;
import com.example.kktoolkitdemo.api.ExampleWeatherAPI;
import com.kkbox.toolkit.api.KKAPIRequest;
import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.ui.KKListFragment;

public class ExampleCityListFragment extends KKListFragment {

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle bundle = new Bundle();
            if (position == (SampleUtil.test_item.length - 1)) {
                bundle.putString("City", "");
            } else {
                bundle.putString("City", SampleUtil.test_item[position]);
            }
            switchToFragment(new ExampleWeatherFragment(), bundle);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);
        initView(view);
        getKKListView().setAdapter(new ArrayAdapter<String>(getKKActivity(), android.R.layout.simple_list_item_1, SampleUtil.test_item));
        getKKListView().setOnItemClickListener(onItemClickListener);
        return view;
    }

}
