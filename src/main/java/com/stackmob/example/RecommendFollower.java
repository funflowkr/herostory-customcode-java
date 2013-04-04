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

public class RecommendFollower implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "recommend_follower";
  }

  @Override
  public List<String> getParams() {
	  return Arrays.asList("page","limit","characters_id");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	LoggerService logger = serviceProvider.getLoggerService(UserSelfFeed.class);
	//Log the JSON object passed to the StackMob Logs
	logger.debug(request.getBody());
	    
	    
    String loginname = request.getLoggedInUser();
    int page = 0 ;
    int limit = 0 ;  
    
    String strPage = request.getParams().get("page");
    String strLimit = request.getParams().get("limit");
    String strCharactersID = request.getParams().get("characters_id");

    if ( !Util.strCheck(strPage) ) {
      strPage = "0";
    }
    if ( !Util.strCheck(strLimit) ) {
	  strLimit = "0";
	}

    /**
    if (loginname == null || loginname.isEmpty()) {
        HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no user is logged in!!");
        return new ResponseToProcess(HttpURLConnection.HTTP_UNAUTHORIZED, errParams); // http 401 - unauthorized
      }
    **/
    
    /**
     * recommend follower 
     * 
     * 1. top 10 heropoint characters.
     * 2. 카테고리별로 나눠서 추천한다.  
     * 3. 팔로우 한 사람을 기반으로 추천.
     * 
     */
    
    
    // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
   
    
    List<SMCondition> query = new ArrayList<SMCondition>();
    
    // execute the query
    List<SMObject> result;
   
    List<SMOrdering> orderings = Arrays.asList(
	  new SMOrdering("heropoint", OrderingDirection.DESCENDING),
	  new SMOrdering("level", OrderingDirection.DESCENDING)
	);
    
    // com.stackmob.sdkapi.ResultFilters.ResultFilters(long start, long end, List<SMOrdering> orderings, List<String> fields)
	ResultFilters filters = new ResultFilters(0, 10 , orderings, Arrays.asList("characters_id", "charactername","level","avatarimageurl","heropoint"));
	
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
