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
}


