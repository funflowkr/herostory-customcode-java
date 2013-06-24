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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	   
	Map<String, Object> map = new HashMap<String, Object>();
    String username = "sohnkh@gmail.com";
    String characters_id = "asdfasdlfja;sdfjkasd;f";
    String codeName = "F_USER";
    List<SMString> args = Arrays.asList(new SMString("aasdf"),new SMString("as"),new SMString("vvvv"));
    
    
    OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    
    /*OAuthConsumer consumer = new DefaultOAuthConsumer(
            "iIlNngv1KdV6XzNYkoLA",
            "exQ94pBpLXFcyttvLoxU2nrktThrlsj580zjYzmoM",
            SignatureMethod.HMAC_SHA1);
*/
    OAuthProvider provider = new DefaultOAuthProvider(
            REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZE_WEBSITE_URL);
    
    try {
		provider.retrieveAccessToken(consumer, "8tcgvBbIYS1EhOpVkgYvk_VHPLxTcY");
		logger.debug("retrieve Access Token Success");
	} catch (OAuthMessageSignerException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	} catch (OAuthNotAuthorizedException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	} catch (OAuthExpectationFailedException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	} catch (OAuthCommunicationException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
    
    AccessToken = consumer.getToken();
    AccessSecret = consumer.getTokenSecret();
    
    logger.debug("Access token: " + consumer.getToken());
    logger.debug("Token secret: " + consumer.getTokenSecret());
    
    
    // fetches a request token from the service provider and builds
    // a url based on AUTHORIZE_WEBSITE_URL and CALLBACK_URL to
    // which your app must now send the user
    //String url = provider.retrieveRequestToken(consumer, CALLBACK_URL);
    
    // create a consumer object and configure it with the access
    // token and token secret obtained from the service provider
    
    consumer.setTokenWithSecret(AccessToken, AccessSecret);

    // create an HTTP request to a protected resource
    URL url2;
	try {
		url2 = new URL("http://example.com/protected");
		HttpURLConnection request2 = (HttpURLConnection) url2.openConnection();
		// sign the request
	    consumer.sign(request2);
	 // send the request
	    request2.connect();
	    
	} catch (MalformedURLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (OAuthMessageSignerException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (OAuthExpectationFailedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (OAuthCommunicationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    try {
    	 logger.debug("start push");
		UtilPush.sendPush(username, characters_id, codeName, args, serviceProvider);
	} catch (ServiceNotActivatedException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (InvalidSchemaException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (DatastoreException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (PushServiceException e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.debug(e.toString());
	}
    map.put("msg", "Hello, world!!!");
    return new ResponseToProcess(HttpURLConnection.HTTP_OK, map);
  }

}
