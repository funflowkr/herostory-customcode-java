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

public class UserSelfFeed implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "user_self_feed";
  }

//  max_id : timestamp ( createdate ) 가져와야 하는 최고 값 
//  since_id : 가져와야 하는 최저값 
//  
//  since_id < id < max_id 이어야 함. 
//  https://dev.twitter.com/docs/working-with-timelines
  
  @Override
  public List<String> getParams() {
	  return Arrays.asList("max_id","since_id","limit");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	LoggerService logger = serviceProvider.getLoggerService(UserSelfFeed.class);
	//Log the JSON object passed to the StackMob Logs
	logger.debug(request.getBody());
	    
	    
    String loginname = request.getLoggedInUser();
    long  max_id = 0 ;
    long  since_id = 0 ;
    
    int limit = 0 ;  
    
    String strMaxId = request.getParams().get("max_id");
    String strSinceId = request.getParams().get("since_id");
    
    String strLimit = request.getParams().get("limit");

    if ( !Util.strCheck(strMaxId) ) {
    	strMaxId = "0";
    }
    if ( !Util.strCheck(strSinceId) ) {
    	strSinceId = "0";
    }
    if ( !Util.strCheck(strLimit) ) {
	  strLimit = "100";
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

    logger.debug("--max_id="+ max_id+"");
    logger.debug("--since_id="+ since_id+"");
    
    if (loginname == null || loginname.isEmpty()) {
        HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no user is logged in!!");
        return new ResponseToProcess(HttpURLConnection.HTTP_UNAUTHORIZED, errParams); // http 401 - unauthorized
      }
    
    // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
   
    // user 가 가지고 있는 팔로워들의 character 들을 찾아낸다음. 그 팔로워들의 character 로 글을 가져온다. 
    
    List<SMCondition> userQuery = new ArrayList<SMCondition>();
    List<SMObject> userResult;
    
    userQuery.add(new SMEquals("username", new SMString(loginname)));
    
    ResultFilters userFilters = new ResultFilters(0, -1, null, Arrays.asList("username", "gameauths", "gameauths.characters","gameauths.characters.follows"));
    //ResultFilters userFilters = new ResultFilters(0, -1, null, Arrays.asList("username", "gameauths.characters.follows"));
    
    SMObject userObject;
    
    List<SMValue> followers = new ArrayList<SMValue>();
    
    try {
    	   
    	userResult = dataService.readObjects("user",userQuery,2,userFilters);
        
    	logger.debug("user result=" + userResult);
    	
    	//userObject = result.get(0);
        //to = userObject.getValue().get("email").toString();
        //toname = userObject.getValue().get("name").toString();
        
     	  // user was in the datastore, so check the score and update if necessary
        if (userResult != null && userResult.size() == 1) {
        	userObject = userResult.get(0);
        	
        	logger.debug("user followers==" + userObject);
        	
        	//List<SMObject> gameauths ;
        	
        	JSONArray jArr = new JSONArray(userObject.getValue().get("gameauths").toString());
        	JSONObject jObj = jArr.getJSONObject(0);
        	JSONArray characterArray = jObj.getJSONArray("characters");
        	
        	logger.debug("gameauths1 =" + jObj + "");
        	
        	for (int i=1;i<characterArray.length();i++) {
        		logger.debug("gameauths characters=" + i + " - " + characterArray.getJSONObject(i).getJSONArray("follows"));
        		JSONArray follows = characterArray.getJSONObject(i).getJSONArray("follows");
        		for (int j=1;j<follows.length();j++) {
        			
        			logger.debug("String " + j + "- "+ follows.getJSONObject(0).toString()+ " ");
        		}
        	}
        	
        	
        	Map<String, SMValue> userMap = new HashMap<String, SMValue>();
        	userMap.put("username", userObject.getValue().get("gameauths"));
            SMObject gameauths = new SMObject(userMap);
            
        	
        	
        	
       /*
        	logger.debug("gameauths =" + gameauths);
        	
        	SMObject gameauthsObject;
        	gameauthsObject = (SMObject) gameauths.getValue().get("characters");
        	logger.debug("gameauths Obj =" + gameauthsObject);
       */ 	
        	
        	
        	followers.add(new SMString("8d6116b56fc34ee69132a493fb81a2fd"));
        	followers.add(new SMString("4c3ac97db716474aa66118854e2f9b96"));
        	followers.add(new SMString("8442544a42394cc3b4a800599ff964a3"));
        	
        	  
        	logger.debug("user followers==" + followers);
        	//logger.debug("user followers==" + followers);
        	//
        } else {
        //  Map<String, SMValue> userMap = new HashMap<String, SMValue>();
        //  userMap.put("username", new SMString(username));
        //  userMap.put("score", new SMInt(0L));
        //  newUser = true;
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
    
    
    query.add(new SMIn("character",followers));
    logger.debug("max_id="+ max_id+"");
    if (max_id > 0) {
    	query.add(new SMLess("createddate",new SMInt(max_id)));
    }
    logger.debug("since_id="+ since_id);
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
    	  returnMap.put("data", postObject);
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
