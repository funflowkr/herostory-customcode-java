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

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.DataService;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMCondition;
import com.stackmob.sdkapi.SMEquals;
import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMSet;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMUpdate;
import com.stackmob.sdkapi.SMValue;

public class HighScore implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "set_high_score";
  }

  @Override
  public List<String> getParams() {
    return new ArrayList<String>();
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    String username = request.getParams().get("username");
    Long score = Long.parseLong(request.getParams().get("score"));
   
    if (username == null || username.isEmpty() || score == null) {
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "one or both the username or score was empty or null");
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }
   
    // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
   
    // build a query
    List<SMCondition> query = new ArrayList<SMCondition>();
    query.add(new SMEquals("username", new SMString(username)));
   
    // execute the query
    List<SMObject> result;
    try {
      boolean newUser = false;
      boolean updated = false;
   
      result = dataService.readObjects("users", query);
   
      SMObject userObject;
   
      // user was in the datastore, so check the score and update if necessary
      if (result != null && result.size() == 1) {
        userObject = result.get(0);
      } else {
        Map<String, SMValue> userMap = new HashMap<String, SMValue>();
        userMap.put("username", new SMString(username));
        userMap.put("score", new SMInt(0L));
        newUser = true;
        userObject = new SMObject(userMap);
      }
   
      SMValue oldScore = userObject.getValue().get("score");
   
      // if it was a high score, update the datastore
      List<SMUpdate> update = new ArrayList<SMUpdate>();
      if (oldScore == null || ((SMInt)oldScore).getValue() < score) {
        update.add(new SMSet("score", new SMInt(score)));
        updated = true;
      }
   
      if(newUser) {
        dataService.createObject("users", userObject);
      } else if(updated) {
        dataService.updateObject("users", username, update);
      }
   
      Map<String, Object> returnMap = new HashMap<String, Object>();
      returnMap.put("updated", updated);
      returnMap.put("newUser", newUser);
      returnMap.put("username", username); 
      return new ResponseToProcess(HttpURLConnection.HTTP_OK, returnMap);
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
   
  }
}
