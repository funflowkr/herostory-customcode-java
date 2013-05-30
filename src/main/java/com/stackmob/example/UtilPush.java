package com.stackmob.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.PushServiceException;
import com.stackmob.core.ServiceNotActivatedException;
import com.stackmob.sdkapi.DataService;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.sdkapi.PushService;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMList;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMValue;


/**
 * Created with IntelliJ IDEA.
 * User: sid
 * Date: 3/12/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */



public class UtilPush {

	public static final String PUSH_CONTENT_TYPE_POST  = "POST";
	public static final String PUSH_CONTENT_TYPE_PICTURE = "PICTURE";
	public static final String PUSH_CONTENT_TYPE_MOVIE  = "MOVIE";
	public static final String PUSH_CONTENT_TYPE_LINK  = "LINK";
	
	
	
	// username : 푸쉬를 받는 사람의 username 
	// characters_id : 푸쉬를 보내는 사람의 characters_id / 주로 friend characters_id 
	
	public static boolean sendPush(String username,String characters_id,String codeName,List<SMString> args, SDKServiceProvider serviceProvider) throws ServiceNotActivatedException,JSONException, InvalidSchemaException, DatastoreException, PushServiceException, Exception {
		
    	
  		LoggerService logger = serviceProvider.getLoggerService(UtilPush.class);
  		DataService dataService = serviceProvider.getDataService();
  		PushService pushService = serviceProvider.getPushService();
  		
  		
  			 
  		// Database 에 넣기
  		// 히스토리 처럼 app 에서 푸쉬를 보여줄 수 있게 하기 위하여 
  		Map<String, SMValue> objMap = new HashMap<String, SMValue>();
    	objMap.put("sm_owner", new SMString(username));
    	objMap.put("character", new SMString(characters_id));
    	objMap.put("msg_args", new SMList<SMString>(args));
    	objMap.put("codename", new SMString(codeName));
    	
  		SMObject toCreate = new SMObject(objMap);
  		// logger.debug("toCreate="+ toCreate.toString());
  		SMObject createResult = dataService.createObject("pushes", toCreate);
  		
  		// logger.debug("createResult="+ createResult);
	
  		
  		// push 보내기 
  		//get all tokens for John Doe
	  	List<String> users = new ArrayList<String>();
	  	users.add(Util.getUsername(username));
	  	
	  	// make push message 
	  	JSONArray jsonArray = new JSONArray(args);
  		JSONObject jsonObject = new JSONObject();
  		jsonObject.put("loc-key", codeName);
		jsonObject.put("loc-args",jsonArray);
		
  		
  		String pushMessage = jsonObject.toString();
  		
	  	Map<String, String> payload = new HashMap<String, String>();
	  	payload.put("badge", "1");
	  	payload.put("sound", "customsound.wav");
	  	payload.put("alert", pushMessage);
	  	payload.put("other", "stuff");
	  	
	  	logger.debug("pushMessage="+ pushMessage);
	  	
	    //send a push notification to all of John Doe's devices
	  	pushService.sendPushToUsers(users, payload);
	  	
  	    return true;
  		
  	
    }
}


