package com.example.kktoolkitdemo.messageview;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.ui.KKMessageView;

/**
 * Created by gigichien on 13/10/21.
 */
public class KKMessageViewActivity extends KKActivity {
    private KKMessageView mMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMsg = new KKMessageView(this);
        this.setContentView(mMsg);
        mMsg.setSingleTextView("Loading...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "setSingleText");
        menu.add(0, 1, 1, "setMutipleText");
        menu.add(0, 2, 2, "setCustomView");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch(item.getItemId()){
            case 0:
                mMsg.setSingleTextView("Loading...");
                break;
            case 1:
                mMsg.setMultiTextView("KKBOX Message", "Loading...");
                break;
           case 2:
               TextView v = new TextView(this);
               v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
               v.setText("Customize message");
               v.setBackgroundColor(Color.WHITE);
               v.setGravity(Gravity.CENTER);
               mMsg.setCustomView(v);
               break;
        }
        return super.onOptionsItemSelected(item);
    }
}
