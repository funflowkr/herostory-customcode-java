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
import com.stackmob.sdkapi.SMDouble;
import com.stackmob.sdkapi.SMEquals;
import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMList;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMSet;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMUpdate;


/**
 * Created with IntelliJ IDEA.
 * User: sid
 * Date: 3/12/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */



public class Util {

	public static final int HEROPOINT_CAT_LIKE = 0;
	public static final int HEROPOINT_CAT_SHARE = 1;
	public static final int HEROPOINT_CAT_COMMENT = 2;
	public static final int HEROPOINT_CAT_FOLLOW = 3;
	
	public static final int HEROPOINT_CAT_LIKE_POINT = 1;
	public static final int HEROPOINT_CAT_SHARE_POINT = 3;
	public static final int HEROPOINT_CAT_COMMENT_POINT = 2;
	public static final int HEROPOINT_CAT_FOLLOW_POINT = 30;
	
	static public Boolean strCheck(String str) {
	    boolean bool = true;
	
	    if (str == null || str.isEmpty() ) {
	      bool = false;
	    }
	
	   return bool;
	}
    
	
  	static public List<SMDouble> setHeroPointCount(int category,String arrHeroPointCount,int incrementCnt ) {
  		
  		
  		List<SMDouble> heroPointCount = new ArrayList<SMDouble>();
  		
  		String[] HeroPointCounts = arrHeroPointCount.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
  		
  		for (int i = 0; i < HeroPointCounts.length; i++) {
  		    try {
  		    	if (category == i ) {
  		    		heroPointCount.add(new SMDouble((double) (Float.parseFloat(HeroPointCounts[i])+incrementCnt)));
  		    		
  		    	} else {
  		    		heroPointCount.add(new SMDouble((double) Float.parseFloat(HeroPointCounts[i])));
  		    	}
  		    //	logger.debug("HeroPointCounts=="+heroPointCount.toString());
  		    } catch (NumberFormatException nfe) {
  		    //	logger.debug(nfe.toString());
  		    	
  		    };
  		    
  		}
  		//returnHeroPointCount = Arrays.toString(HeroPointCounts);
  		return heroPointCount;
  	}
  	
  	static public int getHeroPoint(List<SMDouble> heroPointCount) {
  		int totalPoint = 0 ;
  		for (int i=0;i<heroPointCount.size();i++) {
  			switch (i) {
		    	case HEROPOINT_CAT_LIKE    : 
		    		totalPoint = totalPoint + HEROPOINT_CAT_LIKE_POINT * heroPointCount.get(i).getValue().intValue();
		    		break;
		    	case HEROPOINT_CAT_SHARE   : 
		    		totalPoint = totalPoint + HEROPOINT_CAT_SHARE_POINT * heroPointCount.get(i).getValue().intValue();
		    		break;
		       	case HEROPOINT_CAT_COMMENT  : 
		       		totalPoint = totalPoint + HEROPOINT_CAT_COMMENT_POINT * heroPointCount.get(i).getValue().intValue();
		    		break;
		        case HEROPOINT_CAT_FOLLOW  : 
		        	totalPoint = totalPoint + HEROPOINT_CAT_FOLLOW_POINT * heroPointCount.get(i).getValue().intValue();
		    		break;
		        default    :
		            break;
  			}
  		}
		  
		
		
		return totalPoint;
		
	  
  	}
  	
    public static boolean setHeroPoint(int category, int incrementCnt, String characters_id, SDKServiceProvider serviceProvider) {
		
  		LoggerService logger = serviceProvider.getLoggerService(PostsComment.class);
  		DataService dataService = serviceProvider.getDataService();
  		
  		// build a query
  	    List<SMCondition> query  = new ArrayList<SMCondition>();
  	    query.add(new SMEquals("characters_id", new SMString(characters_id)));
  	    
  	 // execute the query
  	    List<SMObject> result;
  	    
  	    String arrHeroPointCount = "0,0,0,0";
  	    List<SMDouble> heroPointCount = new ArrayList<SMDouble>();
  	    
  	    int oldHeroPoint = 0;
  	    int newHeroPoint;
  	    
  	    if (incrementCnt>0) {
  	    	incrementCnt = 1;
  	    } else {
  	    	incrementCnt = -1;
  	    }
  	    
  	    try {
  	    
  	    	result = dataService.readObjects("characters",query);
  	    	
  	    	if (result != null) {
  	    		// logger.debug("result="+result.get(0));
  	    		try {
  	    			oldHeroPoint = Integer.parseInt(result.get(0).getValue().get("heropoint").toString());
  	    		} catch (Exception e) {
  	    		//	logger.debug("result.get(0).getValue().get(heropoint)"+e.toString());
  	    		}
  	    		
  	    		try { 
  	    			arrHeroPointCount = result.get(0).getValue().get("heropoint_count").toString();
  	    		} catch (Exception e) {
  	    		//	logger.debug("result.get(0).getValue().get(heropoint_count)"+e.toString());
  	    		}
  		    	logger.debug("old HeroPoint="+oldHeroPoint+"/old arrHeroPointCount="+ arrHeroPointCount);
  		    	
  		    	heroPointCount = Util.setHeroPointCount(category,arrHeroPointCount,incrementCnt);
  			    newHeroPoint = Util.getHeroPoint(heroPointCount);
  			    
  			    // logger.debug("newHeroPoint="+newHeroPoint+"/newArrHeroPointCount="+ heroPointCount.toString());
  			    
  			    List<SMUpdate> update = new ArrayList<SMUpdate>();
  				update.add(new SMSet("heropoint", new SMInt((long) newHeroPoint)));
  				update.add(new SMSet("heropoint_count", new SMList(heroPointCount)));
  				SMObject resultUpdate = dataService.updateObject("characters", new SMString(characters_id), update);;
  				logger.debug("resultUpdate="+resultUpdate);
  				return true;
  		    } 
  		    
  		} catch (InvalidSchemaException e) {
  		  /*HashMap<String, String> errMap = new HashMap<String, String>();
  	      errMap.put("error", "invalid_schema");
  	      errMap.put("detail", e.toString());
  	      logger.debug("error="+e.toString());*/
  	      //  return errMap;
  	      //  return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
  	    } catch (DatastoreException e) {
  	      /*HashMap<String, String> errMap = new HashMap<String, String>();
  	      errMap.put("error", "datastore_exception");
  	      errMap.put("detail", e.toString());
  	      logger.debug("error"+e.toString());
  	      return errMap;*/
  		  //  return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
  	    } catch(Exception e) {
  	      /*HashMap<String, String> errMap = new HashMap<String, String>();
  	      errMap.put("error", "unknown");
  	      errMap.put("detail", e.toString());
  	      logger.debug("error"+e.toString());
  	      return errMap;*/
  		  //return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
  	    }    
  	    
  	    /*HashMap<String, String> returnMap = new HashMap<String, String>();
  	    returnMap.put("success", "true");
  	    //returnMap.put("HeroPoint", HeroPoint+"");
  	    //returnMap.put("HeroPoint", HeroPoint+"");
  	     */	    		
  	    return false;
  		
  	
    }
}


