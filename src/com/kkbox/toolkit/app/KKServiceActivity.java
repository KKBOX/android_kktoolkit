/* Copyright (C) 2014 KKBOX Inc.
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
package com.kkbox.toolkit.app;

import android.content.Intent;
import android.os.Bundle;

public interface KKServiceActivity {
	public void activateSubFragment(KKFragment fragment);

	public void deactivateSubFragment(KKFragment fragment);

	public void onServiceStarted();

	public void sendMessageToActiveSubFragments(Bundle arguments);

	public void finishAllKKActivity();
	
	public void startActivityIfNoDialog(Intent intent);
	
	public void finishIfNoDialog();
	
	public void startActivityForResultIfNoDialog(Intent intent, int requestCode);
}
