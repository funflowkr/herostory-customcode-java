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

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.PushServiceException;
import com.stackmob.core.ServiceNotActivatedException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.http.Header;
import com.stackmob.sdkapi.http.HttpService;
import com.stackmob.sdkapi.http.exceptions.AccessDeniedException;
import com.stackmob.sdkapi.http.exceptions.TimeoutException;
import com.stackmob.sdkapi.http.request.GetRequest;
import com.stackmob.sdkapi.http.request.PostRequest;
import com.stackmob.sdkapi.http.response.HttpResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.json.JSONException;

public class HelloWorld implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "hello_world";
  }

  @Override
  public List<String> getParams() {
    return new ArrayList<String>();
  }

  final private static String CONSUMER_KEY = "_dnEqecv1JvW";
  final private static String CONSUMER_SECRET = "51B19EA04n9lo7by8KBs";
	
  final private static String REQUEST_TOKEN_ENDPOINT_URL = "https://nid.naver.com/naver.oauth?mode=req_req_token";
  final private static String ACCESS_TOKEN_ENDPOINT_URL = "https://nid.naver.com/naver.oauth?mode=req_acc_token";
  final private static String AUTHORIZE_WEBSITE_URL 	= "https://nid.naver.com/naver.oauth?mode=auth_req_token";

  private String AccessToken = "";
  private String AccessSecret = "";
	
  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    
	LoggerService logger = serviceProvider.getLoggerService(HelloWorld.class);
	   
	String url =  "http://whatismyipaddress.com";
	 
    Header accept = new Header("Accept-Charset", "utf-8");
    Header content = new Header("Content-Type", "application/x-www-form-urlencoded");
    
    Set<Header> set = new HashSet();
    set.add(accept);
    set.add(content);
    
    String responseBody = "";
    int responseCode;
	try {
      HttpService http = serviceProvider.getHttpService();
      GetRequest req = new GetRequest(url,set);
             
      HttpResponse resp = http.get(req);
      responseCode = resp.getCode();
      responseBody = resp.getBody();
    } catch(TimeoutException e) {
      logger.error(e.getMessage(), e);
      responseCode = HttpURLConnection.HTTP_BAD_GATEWAY;;
      responseBody = e.getMessage();
    } catch(AccessDeniedException e) {
      logger.error(e.getMessage(), e);
      responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;;
      responseBody = e.getMessage();
    } catch(MalformedURLException e) {
      logger.error(e.getMessage(), e);
      responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;;
      responseBody = e.getMessage();
    } catch(ServiceNotActivatedException e) {
      logger.error(e.getMessage(), e);
      responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;;
      responseBody = e.getMessage();
    }
      
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("response_body", responseBody);
     
    return new ResponseToProcess(responseCode, map);
  }

}
