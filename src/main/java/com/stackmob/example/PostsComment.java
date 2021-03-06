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

public class PostsComment implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "posts_comment";
  }

  
  @Override
  public List<String> getParams() {
	  return Arrays.asList("posts_id","characters_id","comment_text","comments_id","m","charactername");
  }

 
  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
	
	LoggerService logger = serviceProvider.getLoggerService(PostsComment.class);
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
    String posts_id = null ;
    String characters_id = null;
    String comment_text = null;
    String comments_id = null;
    String m = null;
    String charactername = "캐릭터이름";
    
    if (verb.equalsIgnoreCase("post")) {
    	
    	if (!request.getBody().isEmpty()) {
            try {
              JSONObject jsonObj = new JSONObject(request.getBody());
              if (!jsonObj.isNull("posts_id")) posts_id = jsonObj.getString("posts_id");
              if (!jsonObj.isNull("characters_id")) characters_id = jsonObj.getString("characters_id");
              if (!jsonObj.isNull("comment_text")) comment_text = jsonObj.getString("comment_text");
              if (!jsonObj.isNull("charactername")) charactername = jsonObj.getString("charactername");
              
            } catch (JSONException e) {
            	logger.debug("Caught JSON Exception");
              e.printStackTrace();
            }
          } else logger.debug("Request body is empty");
    	
    	if (!Util.strCheck(characters_id) ) {
        	HashMap<String, String> errParams = new HashMap<String, String>();
            errParams.put("error", "no characters_id - exception");
            return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
      	}
    	if (!Util.strCheck(comment_text) ) {
        	HashMap<String, String> errParams = new HashMap<String, String>();
            errParams.put("error", "no comment_text - exception");
            return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
      	}
    
    } else { 
    	
    	posts_id = request.getParams().get("posts_id");
        comments_id = request.getParams().get("comments_id");
    	
        if (!Util.strCheck(comments_id) ) {
        	HashMap<String, String> errParams = new HashMap<String, String>();
            errParams.put("error", "no comments_id - exception");
            return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
      	}
        // m = DELETE 이면 삭제하라는 것이다. 
        // custom code 에서 DELETE Verb 를 쓸 수 있게 android code 를 짤수가 없다. -0- 
        m = request.getParams().get("m");
    }
    	
    
    if (!Util.strCheck(posts_id) ) {
    	HashMap<String, String> errParams = new HashMap<String, String>();
        errParams.put("error", "no posts_id - exception");
        return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
  	}
    
    
    
    
 // get the datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
    SMObject resultinc = null;
    List<SMObject> resultData = null;
    
    try {
	    
    	// this is where we handle the special case for `POST` and `PUT` requests
	    if (verb.equalsIgnoreCase("post")) {
	    	logger.debug("GET ACTION ==== POST");
	    	
	    	
	    	Map<String, SMValue> objMap = new HashMap<String, SMValue>();
	    	objMap.put("sm_owner", new SMString(Util.getSMOwner(loginname)));
	    	objMap.put("character", new SMString(characters_id));
	    	objMap.put("text", new SMString(comment_text));
	    	
	    	// insert comment
	    	List<SMObject> objectsToCreate = Arrays.asList(new SMObject(objMap));
	    	BulkResult result = dataService.createRelatedObjects("posts", new SMString(posts_id), "comments", objectsToCreate);
	    	comments_id = result.getSuccessIds().get(0).toString();
	    	
	    	// comment 한 Count Update 
	    	List<SMUpdate> update = new ArrayList<SMUpdate>();
	    	update.add(new SMIncrement("comment_count", new SMInt((long) 1)));
	    	resultinc = dataService.updateObject("posts", new SMString(posts_id), update);
	    	
	    	// 완료된 후 comment info 를 리턴한다. 
	        List<SMCondition> query  = new ArrayList<SMCondition>();
	        query.add(new SMEquals("comments_id", new SMString(comments_id)));
	        resultData = dataService.readObjects("comments",query,1);
            
	     // 원본글 글쓴이 파악해서 영웅지수 업데이트 
	    	String post_characters_id = resultinc.getValue().get("character").toString();
	    	
	    	if (!Util.setHeroPoint(Util.HEROPOINT_CAT_COMMENT, 1, post_characters_id, serviceProvider)) {
	    		logger.debug("HERO POINT ERR: category="+ Util.HEROPOINT_CAT_LIKE + ",posts_id="+posts_id+",characters_id="+ post_characters_id);
	    	}
	    	
	    	// 원본글 글쓴이에게 Push 해준다. 
	    	String post_username = resultinc.getValue().get("sm_owner").toString();
	    	String content_type = UtilPush.PUSH_CONTENT_TYPE_POST ;
	    	
	    	try {
		    	if (Util.strCheck(resultinc.getValue().get("imageurl").toString())) {
		    		content_type = UtilPush.PUSH_CONTENT_TYPE_PICTURE;
		    	}
	    	} catch (Exception e){ }
	    	
	    	List<SMString> pushArgs = new ArrayList<SMString>();
	    	pushArgs.add(new SMString(charactername));
	    	pushArgs.add(new SMString(content_type));
	    	pushArgs.add(new SMString(comment_text));
	    	pushArgs.add(new SMString(posts_id));
	    	
	    	
	    	UtilPush.sendPush(post_username, characters_id, "MY_COMMENT", pushArgs , serviceProvider);
	    	
	    	
	    	logger.debug("update result="+result + "//"+ result.getSuccessIds() + ", increment result=" + resultinc + ",,resultData=" + resultData);
	    	
	    // this is where we handle the case for `DELETE` requests
	    } else if (verb.equalsIgnoreCase("delete") || verb.equalsIgnoreCase("get") ) {
	    	
	    	if (m.equalsIgnoreCase("delete")) {
	    	
		    	logger.debug("GET ACTION ==== DELETE");
		    	
		    	// comment 를 실제로 쓴 사람이 정말 맞는지 확인 작업 필요. 
		    	// build a query
		        List<SMCondition> query  = new ArrayList<SMCondition>();
		        
		        query.add(new SMEquals("comments_id", new SMString(comments_id)));
		        
		        // execute the query
		        List<SMObject> result;
		        String WriterID = null;
		        
	            result = dataService.readObjects("comments",query);
	    	    
	    	    if (result != null) {
	    	   		WriterID = (result.get(0).getValue().get("sm_owner").toString());
	    	    		
	    	    	logger.debug("WriterID="+WriterID+"/LoginName="+ loginname); 
	    	    }
	    	    if (WriterID.equalsIgnoreCase("user/"+loginname))
	    	    {
	    	    	// comment 한 사람 delete 
			    	List<SMString> valuesToRemove = Arrays.asList(new SMString(comments_id));
			    	dataService.removeRelatedObjects("posts", new SMString(posts_id),"comments", valuesToRemove, true);
			    	
			    	// comment 한 Count-1 update  
			    	List<SMUpdate> update = new ArrayList<SMUpdate>();
			    	update.add(new SMIncrement("comment_count", new SMInt((long) -1)));
			    	resultinc = dataService.updateObject("posts", new SMString(posts_id), update);
			    	
			    	 // 원본글 글쓴이 파악해서 영웅지수 업데이트 
			    	String post_characters_id = resultinc.getValue().get("character").toString();
			    	
			    	if (!Util.setHeroPoint(Util.HEROPOINT_CAT_COMMENT, -1, post_characters_id, serviceProvider)) {
			    		logger.debug("HERO POINT ERR: category="+ Util.HEROPOINT_CAT_LIKE + ",posts_id="+posts_id+",characters_id="+ post_characters_id);
			    	}
			    	
			    	logger.debug("update result="+ ", increment result=" + resultinc + "update=" + update);
	    	    	
	    	    	
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
      returnMap.put("data", resultData);
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
