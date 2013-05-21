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

import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.DataService;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.sdkapi.ResultFilters;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMCondition;
import com.stackmob.sdkapi.SMEquals;
import com.stackmob.sdkapi.SMIn;
import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMValue;

public class CharacterInfo implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "character_info";
  }

//  max_id : timestamp ( createdate ) 가져와야 하는 최고 값 
//  since_id : 가져와야 하는 최저값 
//  
//  since_id < id < max_id 이어야 함. 
//  https://dev.twitter.com/docs/working-with-timelines
  
  @Override
  public List<String> getParams() {
	  return Arrays.asList("characters_id");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	LoggerService logger = serviceProvider.getLoggerService(CharacterInfo.class);
	//Log the JSON object passed to the StackMob Logs
	//logger.debug(request.getBody());
	    
	    
    String characters_id = request.getParams().get("characters_id");

    if ( !Util.strCheck(characters_id) ) {
    	HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no characters_id - exception");
        return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
  	}
    
    
    // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
   
    // build a query
    List<SMCondition> query  = new ArrayList<SMCondition>();
    
    query.add(new SMEquals("characters_id", new SMString(characters_id)));
    
 // execute the query
    List<SMObject> result;
    SMObject resultObj = null;
    
    int resultFollowingCount = 0;
    try {
        result = dataService.readObjects("characters",query,1);
	    
	    if (result != null) {
	    	try {
		    	// JSON parsing 하는데 : , / , = 이 섞여서 제대로 못한다. 
	    		// 완전 땜빵 짜증 
	    		JSONArray jArr = new JSONArray(result.get(0).getValue().get("follows").toString().replace(":","").replace("/","").replaceAll("size=",""));
		    	
	    		
		    	resultFollowingCount = jArr.length();
		    	
	    	} catch (JSONException e){
	    		resultFollowingCount = 0;
	    		logger.debug(e.toString());
	    		
	    	}
	    	logger.debug("result="+result+"/following="+ resultFollowingCount);
	    	resultObj = result.get(0);
	    }
	    
	    
    } catch(Exception e) {
	    HashMap<String, String> errMap = new HashMap<String, String>();
	    errMap.put("error", "unknown");
	    errMap.put("detail", e.toString());
	    return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
	}
    
    // count like feed // 
    List<SMCondition> queryLike = new ArrayList<SMCondition>();
    
    queryLike.add(new SMIn("likes", Arrays.asList(new SMString(characters_id))));
    
    ResultFilters filters = new ResultFilters(0, -1, null, Arrays.asList("posts_id"));
    
    // execute the query
    List<SMObject> resultLike;
    int resultLikeCount;
    try {
    	
    	resultLike = dataService.readObjects("posts",queryLike,1,filters);
    	if (resultLike != null) {
    		resultLikeCount = resultLike.size();
    	} else {
    		resultLikeCount = 0 ;
    	}
    	logger.debug("resultLike="+resultLike+ "///Count="+resultLikeCount);
    	
    } catch(Exception e) {
	    HashMap<String, String> errMap = new HashMap<String, String>();
	    errMap.put("error", "unknown");
	    errMap.put("detail", e.toString());
	    return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
	}
    
    // count followers // 
    List<SMCondition> queryFollowers = new ArrayList<SMCondition>();
    
    queryFollowers.add(new SMIn("follows", Arrays.asList(new SMString(characters_id))));
    
    
    // execute the query
    List<SMObject> resultFollowers;
    int resultFollowersCount;
    try {
    	
    	resultFollowers = dataService.readObjects("characters",queryFollowers,0,null);
    	
    	if (resultFollowers != null && resultFollowers.size() >= 1 ) {
    		resultFollowersCount = resultFollowers.size();
    		logger.debug("resultFollowers="+resultFollowers+ "///Count="+resultFollowersCount + "////" + resultFollowers.get(0).getValue().get("follows"));
    		// resultObj.getValue().put("followers", resultFollowers.get(0).getValue().get("follows"));
    		resultObj.getValue().put("followers", (SMValue) resultFollowers); 
    	} else {
    		resultFollowersCount = 0 ;
    	}
    	
    } catch(Exception e) {
	    HashMap<String, String> errMap = new HashMap<String, String>();
	    errMap.put("error", "unknown");
	    errMap.put("detail", e.toString());
	    return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
	}
    
    resultObj.getValue().put("like_count",new SMInt((long) resultLikeCount));
    resultObj.getValue().put("follows_count",new SMInt((long) resultFollowingCount));
    resultObj.getValue().put("followers_count",new SMInt((long) resultFollowersCount));
    
    
    // execute the query
    List<SMObject> resultTotal;
    
      Map<String, Object> returnMap = new HashMap<String, Object>();
      
   	  // user was in the datastore, so check the score and update if necessary
      if (result != null && result.size() == 1) {
    	  returnMap.put("data", result);
      //  logger.debug("result=="+result);
      } else if (result.size() > 1 ){
    	  returnMap.put("data", result);
      } else {
      //  Map<String, SMValue> userMap = new HashMap<String, SMValue>();
      //  userMap.put("username", new SMString(username));
      //  userMap.put("score", new SMInt(0L));
      //  newUser = true;
      //  postObject = null ; // new SMObject(userMap);
      //  logger.debug("result size=" + result.size());
      }
     
      return new ResponseToProcess(HttpURLConnection.HTTP_OK, returnMap);
   
  }
}
