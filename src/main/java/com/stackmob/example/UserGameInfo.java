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
 * User 가 해당 게임에 로그인을 한 후 해당 로그인 키값을 이용하여 
 * 게임 캐릭터 정보를 가져온다. 
 * 아직 API 가 만들어지지 않았기 때문에 임시로 캐릭터를 가져온다고 가정한다. 
 * 
 */
// 

public class UserGameInfo implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "user_game_info";
  }

  @Override
  public List<String> getParams() {
	  return Arrays.asList("gameid","gamename");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	LoggerService logger = serviceProvider.getLoggerService(UserSelfFeed.class);
	//Log the JSON object passed to the StackMob Logs
	logger.debug(request.getBody());
	    
	    
    String loginname = request.getLoggedInUser();
    
    
    // game id : hangame 에서 보내준 user 의 auth key id 
    // game name : "KRITIKA" 
    
    String gameid = request.getParams().get("gameid");
    String gamename = request.getParams().get("gamename");
    
    
    if ( !Util.strCheck(gameid) ) {
    	gameid = "";
    }
    if ( !Util.strCheck(gamename) ) {
    	gamename = "KRITIKA";
	}
    
   
    
    /**
    if (loginname == null || loginname.isEmpty()) {
        HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no user is logged in!!");
        return new ResponseToProcess(HttpURLConnection.HTTP_UNAUTHORIZED, errParams); // http 401 - unauthorized
      }
    **/
    
    /**
     * http:// 연결로 게임 서버로 캐릭터 정보를 쿼리 한다. 
     * 
     * 지금은 임시 방편으로 characters 안에 들어있는 것으로 형태만 만들어놓았음. 
     * 
     */
    
    
    // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
   
    
    List<SMCondition> query = new ArrayList<SMCondition>();
    
    // execute the query
    List<SMObject> result;
   
    List<SMOrdering> orderings = Arrays.asList(
	  new SMOrdering("level", OrderingDirection.DESCENDING),
	  new SMOrdering("exp", OrderingDirection.DESCENDING)
	);
    
    // com.stackmob.sdkapi.ResultFilters.ResultFilters(long start, long end, List<SMOrdering> orderings, List<String> fields)
    // 시작, 끝, 정렬, 가져올 필드 값. 
	ResultFilters filters = new ResultFilters(0, 3 , orderings, Arrays.asList("charactername","servername","level","exp","avatarimageurl"));
	
	// cQuery.add(new SMEquals("username", new SMString(loginname)));
    
    try {
    	   
			result = dataService.readObjects("characters",query,1,filters);
			SMObject postObject;
		  
		  Map<String, Object> returnMap = new HashMap<String, Object>();
		  
		  // user was in the datastore, so check the score and update if necessary
		  if (result != null && result.size() == 1) {
			  postObject = result.get(0);
			  returnMap.put("response_body", postObject);
			  //  logger.debug("result=="+result);
		  } else if (result.size() > 1 ){
			  returnMap.put("response_body", result);
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
