/**
 * Copyright 2012-2013 StackMob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stackmob.example;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.PushServiceException;
import com.stackmob.core.ServiceNotActivatedException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMString;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

public class HelloWorld implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "hello_world";
  }

  @Override
  public List<String> getParams() {
    return new ArrayList<String>();
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    
	  LoggerService logger = serviceProvider.getLoggerService(HelloWorld.class);
	   
	  Map<String, Object> map = new HashMap<String, Object>();
    String username = "sohnkh@gmail.com";
    String characters_id = "asdfasdlfja;sdfjkasd;f";
    String codeName = "F_USER";
    List<SMString> args = Arrays.asList(new SMString("aasdf"),new SMString("as"),new SMString("vvvv"));
     try {
    	 logger.debug("start push");
		UtilPush.sendPush(username, characters_id, codeName, args, serviceProvider);
	} catch (ServiceNotActivatedException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (InvalidSchemaException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (DatastoreException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (PushServiceException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	}
    map.put("msg", "Hello, world!!!");
    return new ResponseToProcess(HttpURLConnection.HTTP_OK, map);
  }

}
