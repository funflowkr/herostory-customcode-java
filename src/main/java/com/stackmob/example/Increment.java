package com.stackmob.example;
/**
 * Copyright 2013 StackMob
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

import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.*;

import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.DatastoreException;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.example.Util;

import java.lang.Integer;
import java.lang.String;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class Increment implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "increment";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("number");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    int responseCode = 0;
    String responseBody = "";
    int intNumber = 0;

    LoggerService logger = serviceProvider.getLoggerService(Increment.class);  //Log to the StackMob Custom Code Console

    String strNumber = request.getParams().get("number");

    if ( Util.strCheck(strNumber) ) {
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "the number passed was null or empty.");
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }

    try {
      intNumber = Integer.parseInt(strNumber);
    } catch (NumberFormatException e) {
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "number format exception");
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }

    DataService dataService = serviceProvider.getDataService();   // get the StackMob datastore service and assemble the query

    try {
      List<SMUpdate> update = new ArrayList<SMUpdate>();
      update.add(new SMIncrement("num_likes", intNumber));
      SMObject incrementResult = dataService.updateObject("todo", "todo1", update); // todo schema with todo_id = todo1
      responseBody = incrementResult.toString();
    } catch (InvalidSchemaException e) {
      HashMap<String, String> errMap = new HashMap<String, String>();
      errMap.put("error", "invalid_schema");
      errMap.put("detail", e.toString());
      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
    } catch (DatastoreException e) {
      HashMap<String, String> errMap = new HashMap<String, String>();
      errMap.put("error", "datastore_exception");
      errMap.put("detail", e.toString());
      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
    } catch(Exception e) {
      HashMap<String, String> errMap = new HashMap<String, String>();
      errMap.put("error", "unknown");
      errMap.put("detail", e.toString());
      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
    }

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("response_body", responseBody);

    return new ResponseToProcess(responseCode, map);
  }
}