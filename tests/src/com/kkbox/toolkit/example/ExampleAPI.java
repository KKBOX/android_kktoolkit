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
 * ExampleAPIBase.java: This is an example of KKAPIBase.
 */
package com.kkbox.toolkit.example;

import com.kkbox.toolkit.api.KKAPIBase;
import com.kkbox.toolkit.api.KKAPIRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
public class ExampleAPI extends KKAPIBase {
	public static class ErrorCode extends KKAPIBase.ErrorCode {
		public static final int FAILED = -1;
	}

	private static final String APIUrl = "http://toolkit.allansun.dev5.test.kkcorp/apitest.php";
	protected String sessionId = "test";
	protected String errorMessage = "";
	protected String reponseText = "";

	public String getData() {
		return reponseText;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void start(String testString) {
		try {
			KKAPIRequest request = new KKAPIRequest(APIUrl, null);
			request.addGetParam("sessionId", URLEncoder.encode(sessionId, "UTF-8"));
			request.addPostParam("testString", testString);
			execute(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected int parse(String data) {
		try {

			final JSONObject jsonObject = new JSONObject(data);
			int status = jsonObject.getInt("status");

			if (status < 0) {
				errorMessage = jsonObject.optString("message");
				return status;
			}
			reponseText = jsonObject.optString("data");

		} catch (JSONException e) {
			return ErrorCode.INVALID_API_FORMAT;
		}
		return ErrorCode.NO_ERROR;
	}

}
