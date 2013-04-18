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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.stackmob.sdkapi.SMGreater;
import com.stackmob.sdkapi.SMIn;
import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMLess;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMOrdering;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMValue;

public class CharacterSelfFeed implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "character_self_feed";
  }

//  max_id : timestamp ( createdate ) 가져와야 하는 최고 값 
//  since_id : 가져와야 하는 최저값 
//  
//  since_id < id < max_id 이어야 함. 
//  https://dev.twitter.com/docs/working-with-timelines
  
  @Override
  public List<String> getParams() {
	  return Arrays.asList("max_id","since_id","limit","characters_id");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	LoggerService logger = serviceProvider.getLoggerService(CharacterSelfFeed.class);
	//Log the JSON object passed to the StackMob Logs
	//logger.debug(request.getBody());
	    
	    
    String loginname = request.getLoggedInUser();
    long  max_id = 0 ;
    long  since_id = 0 ;
    
    int limit = 0 ;  
    
    String strMaxId = request.getParams().get("max_id");
    String strSinceId = request.getParams().get("since_id");
    
    String strLimit = request.getParams().get("limit");
    String characters_id = request.getParams().get("characters_id");

    if ( !Util.strCheck(strMaxId) ) {
    	strMaxId = "0";
    }
    if ( !Util.strCheck(strSinceId) ) {
    	strSinceId = "0";
    }
    if ( !Util.strCheck(strLimit) ) {
	  strLimit = "100";
	}
    if ( !Util.strCheck(characters_id) ) {
    	HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no characters_id - exception");
        return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
  	}
      

    try {
    	max_id = Long.parseLong(strMaxId);
    } catch (NumberFormatException e) {
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "max_id - number format exception");
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }
	try {
		since_id = Long.parseLong(strSinceId);
	} catch (NumberFormatException e) {
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "since_id - number format exception");
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }
	
    try {
    	limit = Integer.parseInt(strLimit);
    } catch (NumberFormatException e) {
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "limit - number format exception");
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }

    logger.debug("--max_id="+ max_id+""+"--since_id="+ since_id+""+"--characters_id="+characters_id);
    
    
    // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
   
    
    // 해당 캐릭터가 팔로우 하는 사람을 가져와서. 
    // 그 팔로우들이 쓴 글을 찾아야 한다. 
    
    List<SMCondition> userQuery = new ArrayList<SMCondition>();
    List<SMObject> userResult;
    
    userQuery.add(new SMEquals("characters_id", new SMString(characters_id)));
    
    SMObject userObject;
    
    
    List<SMValue> followers = new ArrayList<SMValue>();
    
    
    
    
    try {
 	   
    	userResult = dataService.readObjects("characters",userQuery,0,null);
    	
    	followers.add(new SMString(characters_id));
    	
    	
//    	followers.
    	logger.debug("user result=" + userResult);
    	
    	//to = userObject.getValue().get("email").toString();
        //toname = userObject.getValue().get("name").toString();
        
     	  // user was in the datastore, so check the score and update if necessary
        if (userResult != null && userResult.size() == 1) {
        	userObject = userResult.get(0);
        	
        	// logger.debug("user followers==" + userObject);
        	
        	//List<SMObject> gameauths ;
        	
        	try {
        		
        		/*    
        	    String a [] = userObject.getValue().get("follows").toString().split(",");
        	    
        	    for (String string : a) {
        	    	followers.add(new SMString(string));
        		}
        		*/
        		SMValue follows = userObject.getValue().get("follows");
        		
        		if (follows != null) {
        			JSONArray characterArray = new JSONArray(userObject.getValue().get("follows").toString());
        			logger.debug("characterArray =" + characterArray + "");
		        	
		        	for (int i=0;i<characterArray.length();i++) {
		        		followers.add(new SMString(characterArray.getString(i)));
		    		}
        		}
        	} catch (JSONException e){
    			// follows 가 없다면 걍 넘어간다.
    			logger.debug("Json error=" + e.toString());
        		
    		}
        	
        	logger.debug("user followers=" + followers);
        	//logger.debug("user followers==" + followers);
        	//
        } else {
        
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
    
    // build a query
    List<SMCondition> query  = new ArrayList<SMCondition>();
    
    if (followers != null && followers.size() >= 1) { 
        query.add(new SMIn("character",followers));
    }
    
    // logger.debug("max_id="+ max_id+"");
    if (max_id > 0) {
    	query.add(new SMLess("createddate",new SMInt(max_id)));
    }
    // logger.debug("since_id="+ since_id);
    if (since_id > 0) {
    	query.add(new SMGreater("createddate",new SMInt(since_id)));
    }
    logger.debug("query="+ query);
    
    List<SMOrdering> orderings = Arrays.asList(
	  new SMOrdering("createddate", OrderingDirection.DESCENDING)
	);
    
    // logger.debug("limit="+ limit+"");
    // limit 
    if ( limit > 0 ) {
    	limit = limit-1;  
    } else {
    	limit = 99; // default value = 100  
    }
    
    ResultFilters filters = new ResultFilters(0, limit, orderings, null);
    
    // execute the query
    List<SMObject> result;
    try {
    	
	/***
    	readObjects(
    		String schema, 
    		List<SMCondition> conditions, 
    		int expandDepth, 
    		ResultFilters resultFilters
		) throws InvalidSchemaException, DatastoreException
	 */

      result = dataService.readObjects("posts",query,1,filters);
      SMObject postObject;
      
      Map<String, Object> returnMap = new HashMap<String, Object>();
      
   	  // user was in the datastore, so check the score and update if necessary
      if (result != null && result.size() == 1) {
    	  postObject = result.get(0);
      //  returnMap.put("data", postObject);
    	  returnMap.put("data", result);
      //  logger.debug("result=="+result);
      } else if (result.size() > 1 ){
    	  returnMap.put("data", result);
      } else {
      //  Map<String, SMValue> userMap = new HashMap<String, SMValue>();
      //  userMap.put("username", new SMString(username));
      //  userMap.put("score", new SMInt(0L));
      //  newUser = true;
        postObject = null ; // new SMObject(userMap);
        logger.debug("result size=" + result.size());
      }
   
   
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
