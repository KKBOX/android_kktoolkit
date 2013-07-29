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
package com.kkbox.toolkit.ui;

import android.content.res.XmlResourceParser;
import android.view.InflateException;
import android.view.MenuInflater;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class KKMenuInflaterCompat extends MenuInflater {

	private KKActivity activity;

	public KKMenuInflaterCompat(KKActivity activity) {
		super(activity);
		this.activity = activity;
	}

	public void inflate(int menuRes, KKMenuCompat menuCompat) {
		super.inflate(menuRes, menuCompat.getRawMenu());
		menuCompat.initMenu(menuRes);
		XmlResourceParser parser = null;
		try {
			parser = activity.getResources().getLayout(menuRes);
			parseMenu(parser, menuCompat);
		} catch (Exception e) {
			throw new InflateException("Error inflating menu XML", e);
		} finally {
			if (parser != null)
				parser.close();
		}
	}

	private void parseMenu(XmlPullParser parser, KKMenuCompat menuCompat) throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		String tagName;
		do {
			if (eventType == XmlPullParser.START_TAG) {
				tagName = parser.getName();
				if (tagName.equals("menu")) {
					eventType = parser.next();
					break;
				}
				throw new RuntimeException("Expecting menu, got " + tagName);
			}
			eventType = parser.next();
		} while (eventType != XmlPullParser.END_DOCUMENT);

		boolean reachedEndOfMenu = false;
		KKMenuItemCompat currentMenuItem = null;
		while (!reachedEndOfMenu) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					tagName = parser.getName();
					if (tagName.equals("item")) {
						for (int i = 0; i < parser.getAttributeCount(); i++) {
							//FIXME: what if id column is below showasaction or actionviewclass
							if (parser.getAttributeName(i).toLowerCase().equals("id")) {
								currentMenuItem = menuCompat.findItem(Integer.parseInt(parser.getAttributeValue(i).substring(1)));
							}
							if (currentMenuItem != null) {
								if (parser.getAttributeName(i).toLowerCase().equals("showasaction")) {
									if (parser.getAttributeValue(i).equals("0x2")
											|| parser.getAttributeValue(i).toLowerCase().equals("always")) {
										currentMenuItem.setShowAsActionFlags(KKMenuItemCompat.SHOW_AS_ACTION_ALWAYS);
									} else if (parser.getAttributeValue(i).equals("0x1")
											|| parser.getAttributeValue(i).toLowerCase().equals("ifroom")) {
										currentMenuItem.setShowAsActionFlags(KKMenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
									} else {
										currentMenuItem.setShowAsActionFlags(KKMenuItemCompat.SHOW_AS_ACTION_NEVER);
									}
								}
								if (parser.getAttributeName(i).toLowerCase().equals("actionviewclass")
										&& parser.getAttributeValue(i).toLowerCase().equals("com.kkbox.toolkit.ui.kksearchviewcompat")) {
									currentMenuItem.setCompatSearchView(new KKSearchViewCompat(activity));
									currentMenuItem.setActionView(null);
								}
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					if (tagName.equals("item")) {

					} else if (tagName.equals("menu")) {
						reachedEndOfMenu = true;
					}
					break;
				case XmlPullParser.END_DOCUMENT:
					throw new RuntimeException("Unexpected end of document");
			}
			eventType = parser.next();
		}
	}
}
