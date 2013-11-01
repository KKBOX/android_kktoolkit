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
 * ExampleService.java: This is an example of using KKService.
 */
package com.example.kktoolkitdemo.notification;

import android.os.Handler;

import com.kkbox.toolkit.KKService;
import com.kkbox.toolkit.dialog.KKDialogFactory;
import com.kkbox.toolkit.dialog.KKDialogPostExecutionListener;

public class ExampleService extends KKService {

    private static Handler handler;

    @Override
    public void initServiceComponent() {
        handler = new Handler() {
        };
    }

    @Override
    public void finalize() {
    }


    public static void postAlertDialogInThreeSec() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDialogNotificationManager().addDialog(
                        KKDialogFactory.createAlertDialog(
                                0,
                                "KKBOX Reminder",
                                "This is a test message",
                                "Confirm",
                                new KKDialogPostExecutionListener() {
                                }));
            }
        }, 3000);

    }

    public static void postYesNoDialogInThreeSec() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ExampleService.getDialogNotificationManager().addDialog(
                        KKDialogFactory.createYesOrNoDialog(
                                2,
                                "KKBOX Reminder",
                                "This is a test message",
                                "Yes",
                                "No",
                                new KKDialogPostExecutionListener() {
                                }));
            }
        }, 3000);
    }

    public static void postChoiseDialogInThreeSec() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ExampleService.getDialogNotificationManager().addDialog(
                        KKDialogFactory.createThreeChoiceDialog(
                                1,
                                "KKBOX Reminder",
                                "This is a test message",
                                "Retry",
                                "Ignore",
                                "Abort",
                                new KKDialogPostExecutionListener() {
                                }));
            }
        }, 3000);

    }


    public static void postProcessingDialogInThreeSec() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ExampleService.getDialogNotificationManager().addDialog(
                        KKDialogFactory.createProgressingDialog(
                                3,
                                "Processing",
                                new KKDialogPostExecutionListener() {
                                }));
            }
        }, 3000);
    }

}
