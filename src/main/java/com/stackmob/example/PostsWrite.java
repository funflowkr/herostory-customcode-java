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
import com.stackmob.sdkapi.SMCondition;
import com.stackmob.sdkapi.SMDouble;
import com.stackmob.sdkapi.SMEquals;
import com.stackmob.sdkapi.SMIncrement;
import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMList;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMSet;
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
	  return Arrays.asList("characters_id","post_text","imageurl","share_posts_id","share_characters_id","posts_id","m");
  }

 
  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	  
	LoggerService logger = serviceProvider.getLoggerService(PostsWrite.class);
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
    SMObject resultinc = null;
    
    String characters_id = "";
    String share_posts_id = "" ;
    String share_characters_id = "";
    String m = null;
    
    List<SMObject> resultdata = null;
    
    try {
	    
    	// this is where we handle the special case for `POST` and `PUT` requests
	    if (verb.equalsIgnoreCase("post")) {
	    	logger.debug("GET ACTION ==== POST");
	    	
	    	String post_text = "";
	        String imageurl = "";
	        
	    	// 새로운 글을 쓰고. 
	    	// 원본 글에는 shares 와 share_count + 1 해주고. 
	    	
	    	if (!request.getBody().isEmpty()) {
	            try {
	              JSONObject jsonObj = new JSONObject(request.getBody());
	              if (!jsonObj.isNull("characters_id")) characters_id = jsonObj.getString("characters_id");
	              if (!jsonObj.isNull("post_text")) post_text = jsonObj.getString("post_text");
	              if (!jsonObj.isNull("imageurl")) imageurl = jsonObj.getString("imageurl");
	              if (!jsonObj.isNull("share_posts_id")) share_posts_id = jsonObj.getString("share_posts_id");
	              if (!jsonObj.isNull("share_characters_id")) share_characters_id = jsonObj.getString("share_characters_id");
	              
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
	    	if (!Util.strCheck(characters_id) ) {
	        	HashMap<String, String> errParams = new HashMap<String, String>();
	            errParams.put("error", "no characters_id - exception");
	            return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
	      	}
	    	
	    	
	    	Map<String, SMValue> objMap = new HashMap<String, SMValue>();
	    	objMap.put("sm_owner", new SMString(loginname));
	    	objMap.put("character", new SMString(characters_id));
	    	objMap.put("text", new SMString(post_text));
	    	objMap.put("comment_count", new SMInt((long) 0));
	    	objMap.put("like_count", new SMInt((long) 0));
	    	objMap.put("share_count", new SMInt((long) 0));
	    	
	    	objMap.put("imageurl", new SMString(imageurl));
	    	objMap.put("share_post", new SMString(share_posts_id));
	    	objMap.put("share_post_character", new SMString(share_characters_id));
	    	
	    	
	    	// insert comment
	    	List<SMObject> objectsToCreate = Arrays.asList();
	    	SMObject result = dataService.createObject("posts", new SMObject(objMap));
	    	
	    	SMObject resultshare = null ;
	    	SMObject resultshareinc = null ;
	    	String posts_id = null;
	    	
	    	posts_id = result.getValue().get("posts_id").toString();
	    	
	    	// share 한 포스트가 있는 경우는 shares 와 share_count+1 에 넣어준다. 
	    	
	    	if (Util.strCheck(share_posts_id)) {
	    		
	    		// shares 한 사람 입력
		    	List<SMString> valuesToAppend = Arrays.asList(new SMString(characters_id));
		    	resultshare = dataService.addRelatedObjects("posts", new SMString(share_posts_id), "shares", valuesToAppend);
		    	
		    	// share 한 Count Update 
		    	List<SMUpdate> update = new ArrayList<SMUpdate>();
		    	update.add(new SMIncrement("share_count", new SMInt((long) 1)));
		    	resultshareinc = dataService.updateObject("posts", new SMString(share_posts_id), update);	
	    		
		    	// 원본글 글쓴이 파악해서 영웅지수 업데이트 
		    	String post_characters_id = resultshareinc.getValue().get("character").toString();
		    	
		    	if (!Util.setHeroPoint(Util.HEROPOINT_CAT_SHARE, 1, post_characters_id, serviceProvider)) {
		    		logger.debug("HERO POINT ERR: category="+ Util.HEROPOINT_CAT_LIKE + ",posts_id="+posts_id+",characters_id="+ post_characters_id);
		    	}
		    	
	    		
	    	}
	    	
	    	logger.debug("post result="+result + ", share result=" + resultshare + ",result share inc=" + resultshareinc);
	    	
	    	List<SMCondition> query  = new ArrayList<SMCondition>();
	        query.add(new SMEquals("posts_id", new SMString(posts_id)));
	        
	        // execute the query
	        List<SMObject> resultPost;
	        String WriterID = null;
	        
	        resultdata = dataService.readObjects("posts",query,1);
	         
	        
	    	
	    	
	    // this is where we handle the case for `DELETE` requests
	    } else if (verb.equalsIgnoreCase("delete") || verb.equalsIgnoreCase("get") ) {
	    	
	    	// m = DELETE 이면 삭제하라는 것이다. 
	        // custom code 에서 DELETE Verb 를 쓸 수 있게 android code 를 짤수가 없다. -0- 
	        m = request.getParams().get("m");
	        
	    	if (m.equalsIgnoreCase("delete")) {
	    		
	    		logger.debug("GET ACTION ==== DELETE");
		    	
		    	String posts_id = request.getParams().get("posts_id");
		    	characters_id = request.getParams().get("characters_id");
		    	share_posts_id = request.getParams().get("share_posts_id");
		        
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
		    	
		        
		    	// posts 를 실제로 쓴 사람이 정말 맞는지 확인 작업 필요. 
		    	// build a query
		        List<SMCondition> query  = new ArrayList<SMCondition>();
		        
		        query.add(new SMEquals("posts_id", new SMString(posts_id)));
		        
		        // execute the query
		        List<SMObject> result;
		        String WriterID = null;
		        
	            result = dataService.readObjects("posts",query);
	    	    
	    	    if (result != null) {
	    	   		WriterID = (result.get(0).getValue().get("sm_owner").toString());
	    	    		
	    	    	logger.debug("WriterID="+WriterID+"/LoginName="+ loginname); 
	    	    }
	    	    
	    	    if (WriterID.equalsIgnoreCase(loginname))
	    	    {
			    	// posts_id 를 가지고 posts 를 지운다. 
			    	// share 를 한 원글이 있다면 그 글을 찾아가서 shares 에서 삭제하고 
			    	// share_count 도 -1 
			    	
			    	
			        boolean resultb = dataService.deleteObject("posts", posts_id);
			    	
			    	SMObject resultshare = null ;
			    	SMObject resultshareinc = null ;
			    	
			    	if (Util.strCheck(share_posts_id)) {
			    		
			    		// shares 한 사람 입력
				    	List<SMString> valuesToRemove = Arrays.asList(new SMString(characters_id));
				    	dataService.removeRelatedObjects("posts", new SMString(share_posts_id), "shares", valuesToRemove,false);
				    	
				    	// share 한 Count Update 
				    	List<SMUpdate> update = new ArrayList<SMUpdate>();
				    	update.add(new SMIncrement("share_count", new SMInt((long) -1)));
				    	resultshareinc = dataService.updateObject("posts", new SMString(share_posts_id), update);	
			    		
				    	// 원본글 글쓴이 파악해서 영웅지수 업데이트 
				    	String post_characters_id = resultshareinc.getValue().get("character").toString();
				    	
				    	if (!Util.setHeroPoint(Util.HEROPOINT_CAT_SHARE, -1, post_characters_id, serviceProvider)) {
				    		logger.debug("HERO POINT ERR: category="+ Util.HEROPOINT_CAT_LIKE + ",posts_id="+posts_id+",characters_id="+ post_characters_id);
				    	}
			    	}
			    	
			    	logger.debug("update result="+ resultb + ", increment result=" + resultshareinc);
			    	
			    	resultdata = Arrays.asList(resultshareinc);
	    	    } else {
	    	    	HashMap<String, Object> errParams = new HashMap<String, Object>();
	    	        errParams.put("error", "Login user is not writer - exception");
	    	        errParams.put("code", 601);
	    	        return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errParams); // http 500 - Internal Error
	    	        
	    	    }	
			    
	    	}
	    	// this is where we handle the case for `GET` 
	    } else {
	    	// logger.debug("GET ACTION ==== GET");
	        	
	    }
	 
	
          
      Map<String, Object> returnMap = new HashMap<String, Object>();
      returnMap.put("code", HttpURLConnection.HTTP_OK);
      returnMap.put("data", resultdata);
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
