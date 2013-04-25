package com.stackmob.example;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.DataService;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMCondition;
import com.stackmob.sdkapi.SMEquals;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMString;


/**
 * Created with IntelliJ IDEA.
 * User: sid
 * Date: 3/12/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */



public class Util {

	public static final int HEROPOINT_CAT_LIKE = 1;
	public static final int HEROPOINT_CAT_SHARE = 2;
	public static final int HEROPOINT_CAT_COMMENT = 3;
	public static final int HEROPOINT_CAT_FOLLOW = 4;
	
	
	static public Boolean strCheck(String str) {
	    boolean bool = true;
	
	    if (str == null || str.isEmpty() ) {
	      bool = false;
	    }
	
	   return bool;
	}
    
	static public HashMap<String, String> addHeroPoint(int category, String characters_id) {
		
		/*LoggerService logger = serviceProvider.getLoggerService(Util.class);
		DataService dataService = serviceProvider.getDataService();
		
		// build a query
	    List<SMCondition> query  = new ArrayList<SMCondition>();
	    
	    query.add(new SMEquals("characters_id", new SMString(characters_id)));
	    
	 // execute the query
	    List<SMObject> result;
	    
	    String arrHeroPointCount = "0,0,0,0";
	    
	    int oldHeroPoint = 0;
	    int newHeroPoint;
	    
	    try {
	    
	    	result = dataService.readObjects("characters",query);
		    
		    if (result != null) {
		    	try {
		    		oldHeroPoint = Integer.parseInt(result.get(0).getValue().get("heropoint").toString());
		    		arrHeroPointCount = result.get(0).getValue().get("heropoint_count").toString();
			    } catch (NumberFormatException nfe) {
			    	HashMap<String, String> errMap = new HashMap<String, String>();
				    errMap.put("error", "heropoint is not integer.");
				    errMap.put("detail", nfe.toString());
				    return errMap;
			    	
			    }; 
		    	logger.debug("HeroPoint="+oldHeroPoint+"/arrHeroPointCount="+ arrHeroPointCount);
		    }
		    
		    newHeroPoint = getHeroPoint(oldHeroPoint,category);
		    arrHeroPointCount = setHeroPointCount(category,arrHeroPointCount);
		    
		} catch (InvalidSchemaException e) {
	      HashMap<String, String> errMap = new HashMap<String, String>();
	      errMap.put("error", "invalid_schema");
	      errMap.put("detail", e.toString());
	      return errMap;
	      //  return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
	    } catch (DatastoreException e) {
	      HashMap<String, String> errMap = new HashMap<String, String>();
	      errMap.put("error", "datastore_exception");
	      errMap.put("detail", e.toString());
	      return errMap;
		  //  return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
	    } catch(Exception e) {
	      HashMap<String, String> errMap = new HashMap<String, String>();
	      errMap.put("error", "unknown");
	      errMap.put("detail", e.toString());
	      return errMap;
		  //return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
	    }    
	    */
	    HashMap<String, String> returnMap = new HashMap<String, String>();
	    returnMap.put("success", "true");
	    //returnMap.put("HeroPoint", HeroPoint+"");
	    //returnMap.put("HeroPoint", HeroPoint+"");
	    		
	    return returnMap;
		
	}
	
  
  	static public String setHeroPointCount(int category,String arrHeroPointCount ) {
  		
  		
  		String[] HeroPointCounts = arrHeroPointCount.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
  		String returnHeroPointCount = "";
  		
  		
  		for (int i = 0; i < HeroPointCounts.length; i++) {
  		    try {
  		    	if (category == i+1 ) {
  		    		HeroPointCounts[i] = Integer.parseInt(HeroPointCounts[i])+1+"";
  		    	} 
  		    } catch (NumberFormatException nfe) {};
  		}
  		returnHeroPointCount = Arrays.toString(HeroPointCounts);
  		return returnHeroPointCount;
  	}
  	
  	static public int getHeroPoint(int oldHeroPoint,int category) {
	  
  		int point=0;
		  
		switch (category) {
	    	case HEROPOINT_CAT_LIKE    : 
	    		point = 1;
	    		break;
	    	case HEROPOINT_CAT_SHARE   : 
				point = 3;
	    		break;
	       	case HEROPOINT_CAT_COMMENT  : 
		       	point = 2;
	    		break;
	        case HEROPOINT_CAT_FOLLOW  : 
	           	point = 30;
	    		break;
	        default    :
	            break;
	    }
		
		return oldHeroPoint + point ;
		
	  
  	}
}


