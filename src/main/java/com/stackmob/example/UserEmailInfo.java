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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.DataService;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.sdkapi.OrderingDirection;
import com.stackmob.sdkapi.ResultFilters;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMCondition;
import com.stackmob.sdkapi.SMEquals;
import com.stackmob.sdkapi.SMIn;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMOrdering;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMValue;


/** 
 * @author sson
 * useremail 로 username 을 가져오는 custom code. 
 */
// 

public class UserEmailInfo implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "user_email_info";
  }

  @Override
  public List<String> getParams() {
	  return Arrays.asList("useremail");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	LoggerService logger = serviceProvider.getLoggerService(UserEmailInfo.class);
	    
	    
    // useremail : user 가 입력한 가입했을 때의 email 입력값. 
    
    String useremail = request.getParams().get("useremail");
    
    
    if ( !Util.strCheck(useremail) ) {
    	useremail = "";
    }
    
   	    
   
    if (useremail == null || useremail.isEmpty()) {
        HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no-user email ");
        return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
      }
    
    
    // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
   
    
    List<SMCondition> query = new ArrayList<SMCondition>();
    query.add(new SMEquals("useremail", new SMString(useremail)));
    
    // execute the query
    List<SMObject> result;
   
    
    try {
    	   
		  result = dataService.readObjects("user",query);
		  logger.debug("result=="+result);
		  String username;
		  Map<String, Object> returnMap = new HashMap<String, Object>();
		  
		  // user was in the datastore, so check the score and update if necessary
		  if (result != null && result.size() == 1) {
			  
			  username = result.get(0).getValue().get("username").toString();
			  returnMap.put("username", username);
			  return new ResponseToProcess(HttpURLConnection.HTTP_OK, returnMap);
			  
		  } else if (result.size() > 1 ){ 
			  // 2개가 나오면 겁나 이상한 거임.. 원래 그렇게 되면 안 댐... 
			  // returnMap.put("response_body", result);
			  logger.error("duplicated user error");
			  HashMap<String, String> errMap = new HashMap<String, String>();
		      errMap.put("error", "double user");
		      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
		  } else {
			  logger.error("no exist user");
			  HashMap<String, String> errMap = new HashMap<String, String>();
		      errMap.put("error", "no exist user");
		      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
		  }
	   
   
      
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
