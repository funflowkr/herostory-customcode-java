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

import org.json.JSONException;
import org.json.JSONObject;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.DataService;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMIncrement;
import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMUpdate;

public class PostsLike implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "posts_like";
  }

  
  @Override
  public List<String> getParams() {
	  return Arrays.asList("posts_id","characters_id","m");
  }

  
  
  
  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	LoggerService logger = serviceProvider.getLoggerService(PostsLike.class);
	//Log the JSON object passed to the StackMob Logs
	//logger.debug(request.getBody());
	
	
	HashMap<String, String> maptest = Util.addHeroPoint(0, "8442544a42394cc3b4a800599ff964a3", serviceProvider);
	
	
    Map<String, Object> map = new HashMap<String, Object>();
    String verb = request.getVerb().toString();
    String posts_id = null ;
    String characters_id = null;
    String m = null;
    
    if (verb.equalsIgnoreCase("post") || verb.equalsIgnoreCase("put")) {
    	logger.debug("GET ACTION ==== POST or PUT");
    	
    	if (!request.getBody().isEmpty()) {
            try {
              JSONObject jsonObj = new JSONObject(request.getBody());
              if (!jsonObj.isNull("posts_id")) posts_id = jsonObj.getString("posts_id");
              if (!jsonObj.isNull("characters_id")) characters_id = jsonObj.getString("characters_id");
            } catch (JSONException e) {
            	logger.debug("Caught JSON Exception");
              e.printStackTrace();
            }
          } else logger.debug("Request body is empty");
    } else { 
    	posts_id = request.getParams().get("posts_id");
        characters_id = request.getParams().get("characters_id");
        
        // m = DELETE 이면 삭제하라는 것이다. 
        // custom code 에서 DELETE Verb 를 쓸 수 있게 android code 를 짤수가 없다. -0- 
        m = request.getParams().get("m");
    	
    }
    	
    
    if (!Util.strCheck(posts_id) ) {
    	HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no posts_id - exception");
        return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
  	}
    
    if (!Util.strCheck(characters_id) ) {
    	HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no characters_id - exception");
        return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
  	}
    
    
 // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
	
    try {
	    
    	// this is where we handle the special case for `POST` and `PUT` requests
	    if (verb.equalsIgnoreCase("post") || verb.equalsIgnoreCase("put")) {
	    	logger.debug("GET ACTION ==== POST or PUT");
	    	
	    	// like 한 사람 입력
	    	List<SMString> valuesToAppend = Arrays.asList(new SMString(characters_id));
	    	SMObject result = dataService.addRelatedObjects("posts", new SMString(posts_id), "likes", valuesToAppend);
	    	
	    	// like 한 Count Update 
	    	List<SMUpdate> update = new ArrayList<SMUpdate>();
	    	update.add(new SMIncrement("like_count", new SMInt((long) 1)));
	    	SMObject resultinc = dataService.updateObject("posts", new SMString(posts_id), update);
	    	
	    	logger.debug("update result="+result + ", increment result=" + resultinc + ",,update=" + update);
	    	
	    // this is where we handle the case for `DELETE` requests
	    } else if (verb.equalsIgnoreCase("delete") || verb.equalsIgnoreCase("get")) {
	    	if (m.equalsIgnoreCase("delete")) {
	    		
	    		logger.debug("GET ACTION ==== DELETE");
		    	// like 한 사람 delete 
		    	List<SMString> valuesToRemove = Arrays.asList(new SMString(characters_id));
		    	dataService.removeRelatedObjects("posts", new SMString(posts_id),"likes", valuesToRemove, false);
		    	
		    	// like 한 Count-1 update  
		    	List<SMUpdate> update = new ArrayList<SMUpdate>();
		    	update.add(new SMIncrement("like_count", new SMInt((long) -1)));
		    	SMObject resultinc = dataService.updateObject("posts", new SMString(posts_id), update);
		    	
		    	logger.debug("update result="+ ", increment result=" + resultinc + "update=" + update);
	    	}
	    
	    	// this is where we handle the case for `GET` 
	    } else {
	    	// logger.debug("GET ACTION ==== GET");
	        	
	    }
	 
	
          
      Map<String, Object> returnMap = new HashMap<String, Object>();
      
      returnMap.put("code", "200");
      
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
