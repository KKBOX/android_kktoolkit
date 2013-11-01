package com.example.kktoolkitdemo.resizableview;

import android.os.Bundle;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.kktoolkitdemo.R;
import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.ui.ResizableView;

/**
 * Created by gigichien on 13/10/21.
 */
public class ResizableViewActivity extends KKActivity {
    Button btnL, btnM, btnS;
    ResizableView mResizableView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_resizable_view);

        btnL = (Button) findViewById(R.id.btn_large);
        btnM = (Button) findViewById(R.id.btn_middle);
        btnS = (Button) findViewById(R.id.btn_small);
        mResizableView = (ResizableView) findViewById(R.id.resizable_view);

        setCtrlButton();
        btnS.callOnClick();
        mResizableView.setBackgroundResource(R.drawable.ic_launcher);
    }

    int mDisplayHeight = 0;
    private void setCtrlButton(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDisplayHeight = dm.heightPixels;
        btnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mResizableView.setLayoutParams(new LinearLayout.LayoutParams(
                        mDisplayHeight/2, mDisplayHeight/2));
            }
        });
        btnM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mResizableView.setLayoutParams(new LinearLayout.LayoutParams(
                        mDisplayHeight/3, mDisplayHeight/3));
            }
        });

        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mResizableView.setLayoutParams(new LinearLayout.LayoutParams(
                        mDisplayHeight/4, mDisplayHeight/4));
            }
        });
    }
}
