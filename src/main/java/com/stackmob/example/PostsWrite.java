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
import com.stackmob.sdkapi.BulkResult;
import com.stackmob.sdkapi.DataService;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMIncrement;
import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMUpdate;
import com.stackmob.sdkapi.SMValue;

public class PostsWrite implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "posts_write";
  }

  
  @Override
  public List<String> getParams() {
	  return Arrays.asList("characters_id","post_text","imageurl","share_posts_id","posts_id");
  }

 
  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	  
	
	  
	LoggerService logger = serviceProvider.getLoggerService(PostsWrite.class);
	//Log the JSON object passed to the StackMob Logs
	//logger.debug(request.getBody());
	
    Map<String, Object> map = new HashMap<String, Object>();
    String verb = request.getVerb().toString();
    
    
   
    
 // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
    SMObject resultinc = null;
    
    String characters_id = null;
    String share_posts_id = null ;
    
    if (!Util.strCheck(characters_id) ) {
    	HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no characters_id - exception");
        return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
  	}
	
    
    try {
	    
    	// this is where we handle the special case for `POST` and `PUT` requests
	    if (verb.equalsIgnoreCase("post")) {
	    	logger.debug("GET ACTION ==== POST");
	    	
	    	String post_text = null;
	        String imageurl = null;
	        
	    	// 새로운 글을 쓰고. 
	    	// 원본 글에는 shares 와 share_count + 1 해주고. 
	    	
	    	if (!request.getBody().isEmpty()) {
	            try {
	              JSONObject jsonObj = new JSONObject(request.getBody());
	              if (!jsonObj.isNull("characters_id")) characters_id = jsonObj.getString("characters_id");
	              if (!jsonObj.isNull("post_text")) post_text = jsonObj.getString("post_text");
	              if (!jsonObj.isNull("imageurl")) imageurl = jsonObj.getString("imageurl");
	              if (!jsonObj.isNull("share_posts_id")) share_posts_id = jsonObj.getString("share_posts_id");
	              
	            } catch (JSONException e) {
	            	logger.debug("Caught JSON Exception");
	              e.printStackTrace();
	            }
	          } else logger.debug("Request body is empty");
	    	
	    	if (!Util.strCheck(post_text) ) {
	        	HashMap<String, String> errParams = new HashMap<String, String>();
	            errParams.put("error", "no post_text - exception");
	            return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
	      	}
	    	
	    	
	    	Map<String, SMValue> objMap = new HashMap<String, SMValue>();
	    	objMap.put("character", new SMString(characters_id));
	    	objMap.put("text", new SMString(post_text));
	    	objMap.put("comment_count", new SMInt((long) 0));
	    	objMap.put("like_count", new SMInt((long) 0));
	    	objMap.put("share_count", new SMInt((long) 0));
	    	
	    	objMap.put("imageurl", new SMString(imageurl));
	    	objMap.put("share_post", new SMString(share_posts_id));
	    	
	    	
	    	
	    	// insert comment
	    	List<SMObject> objectsToCreate = Arrays.asList();
	    	SMObject result = dataService.createObject("posts", new SMObject(objMap));
	    	
	    	SMObject resultshare = null ;
	    	SMObject resultshareinc = null ;
	    	
	    	// share 한 포스트가 있는 경우는 shares 와 share_count+1 에 넣어준다. 
	    	
	    	if (!Util.strCheck(share_posts_id)) {
	    		
	    		// shares 한 사람 입력
		    	List<SMString> valuesToAppend = Arrays.asList(new SMString(characters_id));
		    	resultshare = dataService.addRelatedObjects("posts", new SMString(share_posts_id), "shares", valuesToAppend);
		    	
		    	// share 한 Count Update 
		    	List<SMUpdate> update = new ArrayList<SMUpdate>();
		    	update.add(new SMIncrement("share_count", new SMInt((long) 1)));
		    	resultshareinc = dataService.updateObject("posts", new SMString(share_posts_id), update);	
	    		
	    		
	    	}
	    	
	    	logger.debug("post result="+result + ", share result=" + resultshare + ",result share inc=" + resultshareinc);
	    	
	    // this is where we handle the case for `DELETE` requests
	    } else if (verb.equalsIgnoreCase("delete") ) {
	    	
	    	logger.debug("GET ACTION ==== DELETE");
	    	
	    	// posts_id 를 가지고 posts 를 지운다. 
	    	// share 를 한 원글이 있다면 그 글을 찾아가서 shares 에서 삭제하고 
	    	// share_count 도 -1 
	    	
	    	String posts_id = request.getParams().get("posts_id");
	        
	        if (!Util.strCheck(posts_id) ) {
	        	HashMap<String, String> errParams = new HashMap<String, String>();
	            errParams.put("error", "no posts_id - exception");
	            return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
	      	}
	        
	        boolean result = dataService.deleteObject("posts", posts_id);
	    	
	    	SMObject resultshare = null ;
	    	SMObject resultshareinc = null ;
	    	
	    	if (!Util.strCheck(share_posts_id)) {
	    		
	    		// shares 한 사람 입력
		    	List<SMString> valuesToRemove = Arrays.asList(new SMString(characters_id));
		    	dataService.removeRelatedObjects("posts", new SMString(share_posts_id), "shares", valuesToRemove,true);
		    	
		    	// share 한 Count Update 
		    	List<SMUpdate> update = new ArrayList<SMUpdate>();
		    	update.add(new SMIncrement("share_count", new SMInt((long) -1)));
		    	resultshareinc = dataService.updateObject("posts", new SMString(share_posts_id), update);	
	    		
	    		
	    	}
	    	
	    	logger.debug("update result="+ result + ", increment result=" + resultshareinc);
	    	
	    
	    	// this is where we handle the case for `GET` 
	    } else {
	    	logger.debug("GET ACTION ==== GET");
	        	
	    }
	 
	
          
      Map<String, Object> returnMap = new HashMap<String, Object>();
      
      returnMap.put("code", HttpURLConnection.HTTP_OK);
      returnMap.put("data", resultinc);
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
