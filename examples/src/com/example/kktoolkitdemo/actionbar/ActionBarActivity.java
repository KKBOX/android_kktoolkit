package com.example.kktoolkitdemo.actionbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kktoolkitdemo.R;
import com.kkbox.toolkit.ui.KKActionBar;
import com.kkbox.toolkit.ui.KKActivity;

/**
 * Created by gigichien on 13/10/18.
 */
public class ActionBarActivity extends KKActivity {
    private KKActionBar actionBar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actionbar);
        setAcionBarUsage();
    }

    private void setAcionBarUsage(){
        actionBar = getKKActionBar();
        Button btnSetHome, btnSetLogo, btnSetSubTitle, btnSetTitle;
        btnSetHome = (Button) findViewById(R.id.set__home);
        btnSetLogo = (Button) findViewById(R.id.set_logo);
        btnSetSubTitle = (Button) findViewById(R.id.set_sub_title);
        btnSetTitle = (Button) findViewById(R.id.set_title);
        btnSetHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actionBar != null){
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
            }
        });
        btnSetLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actionBar != null){
                    actionBar.setLogo(getBaseContext().getResources().getDrawable(R.drawable.ic_launcher));
                }

            }
        });
        btnSetSubTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actionBar != null){
                    actionBar.setSubtitle("This is SubTitle");
                }
            }
        });
        btnSetTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actionBar != null){
                    actionBar.setTitle("This is Title");
                }
            }
        });

    }

}
