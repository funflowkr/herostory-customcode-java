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
import com.stackmob.sdkapi.SMBoolean;
import com.stackmob.sdkapi.SMCondition;
import com.stackmob.sdkapi.SMEquals;
import com.stackmob.sdkapi.SMIncrement;
import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMIsNull;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMSet;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMUpdate;
import com.stackmob.sdkapi.SMValue;

public class EventGetCoupon implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "event_get_coupon";
  }

  
  @Override
  public List<String> getParams() {
	  return new ArrayList<String>();
  }

 
  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	  
	LoggerService logger = serviceProvider.getLoggerService(EventGetCoupon.class);
	//Log the JSON object passed to the StackMob Logs
	//logger.debug(request.getBody());
	
	String loginname = request.getLoggedInUser();
	
	if (loginname == null || loginname.isEmpty()) {
        HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no user is logged in!!");
        return new ResponseToProcess(HttpURLConnection.HTTP_UNAUTHORIZED, errParams); // http 401 - unauthorized
    }
	
	
	
	
	
    Map<String, Object> map = new HashMap<String, Object>();
    String verb = request.getVerb().toString();
    
     
 // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
    
    try {
	    
    	// 이벤트 데이터 확인 
    	// build a query
        
    	List<SMCondition> query  = new ArrayList<SMCondition>();
        query.add(new SMEquals("eventstart_id", new SMString(loginname)));
        
        // execute the query
        List<SMObject> result = null;
        result = dataService.readObjects("eventstart",query);
        
        String m1,m2,m3,m4,m5,m6;
        String couponcode = null;
        String eventcoupon_id = null;
		
        if (result != null) {
	   		m1 = result.get(0).getValue().get("m1_follow").toString();
	   		m2 = result.get(0).getValue().get("m2_post").toString();
	   		m3 = result.get(0).getValue().get("m3_comment").toString();
	   		m4 = result.get(0).getValue().get("m4_like").toString();
	   		m5 = result.get(0).getValue().get("m5_attend").toString();
	   		m6 = result.get(0).getValue().get("m6_push").toString();
	   		
	   		if (m1.equalsIgnoreCase("true") || 
	   				m2.equalsIgnoreCase("true")|| 
	   				m3.equalsIgnoreCase("true")|| 
	   				m4.equalsIgnoreCase("true")|| 
	   				Integer.parseInt(m5) >= 3 ||
	   				Integer.parseInt(m6) >= 10 ) {
	   			// event 조건 성공 
	   			
	   			
	   			
	   			List<SMCondition> query_coupon  = new ArrayList<SMCondition>();
	   	        query_coupon.add(new SMEquals("eventname", new SMString("start")));
	   	        query_coupon.add(new SMIsNull("r_user", new SMBoolean(true)));
	   	        
	   	        // execute the query
	   	        List<SMObject> result_coupon = null;
	   	        result_coupon = dataService.readObjects("eventcoupon",query);
	   	        
	   	        if (result_coupon != null) {
	   	        	eventcoupon_id = result_coupon.get(0).getValue().get("eventcoupon_id").toString();
	   	        	couponcode = result_coupon.get(0).getValue().get("couponcode").toString();
	   	        	
		   	        List<SMUpdate> update = new ArrayList<SMUpdate>();
		   	        update.add(new SMSet("r_user", new SMString(loginname)));
		   	        update.add(new SMSet("sm_owner", new SMString("user/"+loginname)));
		   	         
	   	        	dataService.updateObject("eventcoupon", eventcoupon_id, update);
	   	        	
	   	        } else {
		   			
	  	   		  Map<String, Object> returnMap = new HashMap<String, Object>();
	  	   	      returnMap.put("code", 604);
	  	   	      returnMap.put("error", "NO MORE COUPON");
	  	   	      logger.error("code=604");
	  	   	      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, returnMap);
	  	   		}
	   	        
	   		
	   			
	   		} else {
	   			
	   		  Map<String, Object> returnMap = new HashMap<String, Object>();
	   	      returnMap.put("code", 602);
	   	      returnMap.put("error", "NOT CONDITIONED");
	   	   logger.error("code=602");
	   	      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, returnMap);
	   	      
	   		}
        } else {
	   		Map<String, Object> returnMap = new HashMap<String, Object>();
	   	    returnMap.put("code", 605);
	   	    returnMap.put("error", "NO USER");
	   	    logger.error("code=605");
	   	    return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, returnMap);
	   		
	    }
        
      Map<String, Object> returnMap = new HashMap<String, Object>();
      returnMap.put("code", HttpURLConnection.HTTP_OK);
      returnMap.put("couponcode", couponcode);
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
