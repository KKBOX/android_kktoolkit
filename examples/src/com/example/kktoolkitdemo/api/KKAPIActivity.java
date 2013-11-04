package com.example.kktoolkitdemo.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kktoolkitdemo.R;
import com.kkbox.toolkit.api.KKAPIListener;
import com.kkbox.toolkit.ui.KKActivity;

public class KKAPIActivity extends KKActivity {
    private TextView mTextView;
    private ExampleWeatherAPI mAPI;
    private EditText mInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apibase);
        Button btnSubmit = (Button) findViewById(R.id.submit);
        mTextView = (TextView) findViewById(R.id.output);
        mInput = (EditText) findViewById(R.id.input_url);
        setStatusText(" Status : Initialize");
        mAPI = new ExampleWeatherAPI();
        mAPI.setAPIListener(exampleAPIListener);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStatusText(" Status : Wait for response...");
                mAPI.start(mInput.getText().toString());
            }
        });
    }

    private void setStatusText(String status) {
        if (mTextView != null) {
            mTextView.setText(status);
        }
    }

    private KKAPIListener exampleAPIListener = new KKAPIListener() {
        @Override
        public void onAPIComplete() {
            ExampleWeatherAPI.WeatherData data = mAPI.getWeatherData();
            setStatusText(" Status : onAPIComplete \n [response] \n" +
                    mAPI.getResponseData() +
                    "\n [parse] \n" +
                    " Condition : " + data.main +
                    "\n Description : " + data.description +
                    "\n Temp : " + data.temp +
                    "\n Min temp : " + data.min_temp +
                    "\n Max temp : " + data.max_temp);
        }

        @Override
        public void onAPIError(int errorCode) {
            setStatusText(" Status : onAPIError");
        }
    };
}
