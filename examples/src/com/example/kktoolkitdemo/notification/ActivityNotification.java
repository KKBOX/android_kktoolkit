/* Copyright (C) 2013 KKBOX Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * ​http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * ActivityNotification.java: This activity demonstrates the usage of KKDialogs (com.kkbox.toolkit.notification).
 */
package com.example.kktoolkitdemo.notification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.kktoolkitdemo.R;
import com.kkbox.toolkit.dialog.KKDialogFactory;
import com.kkbox.toolkit.dialog.KKDialogPostExecutionListener;
import com.kkbox.toolkit.ui.KKServiceActivity;

public class ActivityNotification extends KKServiceActivity {

    private final OnClickListener btnNotifyOne = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ActivityNotification.this, EmptyActivity.class);
            startActivity(intent);
            ExampleService.postAlertDialogInThreeSec();

        }
    };

    private final OnClickListener btnNotifyTwo =    new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ActivityNotification.this, EmptyActivity.class);
            startActivity(intent);
            ExampleService.postYesNoDialogInThreeSec();
        }
    };

    private final OnClickListener btnNotifyThree = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ActivityNotification.this, EmptyActivity.class);
            startActivity(intent);
            ExampleService.postChoiseDialogInThreeSec();
        }
    };

    private final OnClickListener btnNotifyFour = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ActivityNotification.this, EmptyActivity.class);
            startActivity(intent);
            ExampleService.postProcessingDialogInThreeSec();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_example);
        getKKActionBar().setTitle("Notification Example");

        Button btnNotify1 = (Button) findViewById(R.id.btnNotify1);
        btnNotify1.setOnClickListener(btnNotifyOne);
        Button btnNotify2 = (Button) findViewById(R.id.btnNotify2);
        btnNotify2.setOnClickListener(btnNotifyTwo);
        Button btnNotify3 = (Button) findViewById(R.id.btnNotify3);
        btnNotify3.setOnClickListener(btnNotifyThree);
        Button btnNotify4 = (Button) findViewById(R.id.btnNotify4);
        btnNotify4.setOnClickListener(btnNotifyFour);

    }
}
