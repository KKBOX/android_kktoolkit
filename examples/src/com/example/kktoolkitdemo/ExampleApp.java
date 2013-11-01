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
 * ExampleApp.java: This is the entry point of the example APP.
 * This starts KKService, and then go to the main activity.
 */
package com.example.kktoolkitdemo;

import android.app.Application;
import android.content.Intent;

import com.example.kktoolkitdemo.notification.ExampleService;
import com.kkbox.toolkit.utils.KKDebug;

public class ExampleApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
        Intent intent = new Intent(this, ExampleService.class);
		KKDebug.setDebugEnabled(true);
        startService(intent);
    }
}