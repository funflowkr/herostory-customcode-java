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

import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.SDKServiceProvider;

import com.stackmob.sdkapi.http.HttpService;
import com.stackmob.sdkapi.http.request.HttpRequest;
import com.stackmob.sdkapi.http.request.GetRequest;
import com.stackmob.sdkapi.http.response.HttpResponse;
import com.stackmob.core.ServiceNotActivatedException;
import com.stackmob.sdkapi.http.exceptions.AccessDeniedException;
import com.stackmob.sdkapi.http.exceptions.TimeoutException;
import java.net.MalformedURLException;
import com.stackmob.sdkapi.http.request.PostRequest;
import com.stackmob.sdkapi.http.Header;
import com.stackmob.sdkapi.LoggerService;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;

public class OAuthNaverConnect implements CustomCodeMethod {

  //Create your Twilio Acct at twilio.com and enter 
  //Your accountsid and accesstoken below.
  public static final String accountsid = "YOUR_ACCOUNTSID";
  public static final String accesstoken = "YOUR_ACCESSTOKEN";
    
  @Override
  public String getMethodName() {
    return "oauth_naver_connect";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("oauth_token","oauth_verifier");
    
  }  

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    int responseCode = 0;
    String responseBody = "";

	LoggerService logger = serviceProvider.getLoggerService(OAuthNaverConnect.class);
      
    // TO phonenumber should be YOUR cel phone
    String oauth_token = request.getParams().get("oauth_token");
    
    //  text message you want to send
    String oauth_verifier = request.getParams().get("oauth_verifier");
    
    
    oauth_token = "J5U0cqcDgAyIV9R3";
    oauth_verifier = "jmTCvj_0s2i2PXAtdOP1lSvLlkPJoi";
    
    /*if (toPhoneNumber == null || toPhoneNumber.isEmpty()) {
      logger.error("Missing phone number");
    }
      
    if (message == null || message.isEmpty()) {
      logger.error("Missing message");
    }

    StringBuilder body = new StringBuilder();

    body.append("To=");
    body.append(toPhoneNumber);
    body.append("&From=");
    body.append(fromPhoneNumber);
    body.append("&Body=");
    body.append(message);
*/
    String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountsid + "/SMS/Messages.json";
    url = "https://nid.naver.com/naver.oauth?oauth_token=J5U0cqcDgAyIV9R3&oauth_consumer_key=k2l1_0LeGKSo&oauth_nonce=EcRLyhB0&oauth_timestamp=1372127884&oauth_verifier=jmTCvj_0s2i2PXAtdOP1lSvLlkPJoi&oauth_signature_method=HAMC_SHA1&mode=req_acc_token&oauth_signature=X12%2BcWHshMOxivyPHn6w6ltgf5I%3D";
    
    
    /*String pair = accountsid + ":" + accesstoken;
      
    // Base 64 Encode the accountsid/accesstoken
    String encodedString = new String("utf-8");
    try {
      byte[] b =Base64.encodeBase64(pair.getBytes("utf-8"));
      encodedString = new String(b);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "the auth header threw an exception: " + e.getMessage());
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }
    
    Header accept = new Header("Accept-Charset", "utf-8");
    Header auth = new Header("Authorization","Basic " + encodedString);
    Header content = new Header("Content-Type", "application/x-www-form-urlencoded");

    Set<Header> set = new HashSet();
    set.add(accept);
    set.add(content);
    set.add(auth);*/
      
    try {
      HttpService http = serviceProvider.getHttpService();
      //PostRequest req = new PostRequest(url,set,body.toString());
      GetRequest req = new GetRequest(url);
      
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
