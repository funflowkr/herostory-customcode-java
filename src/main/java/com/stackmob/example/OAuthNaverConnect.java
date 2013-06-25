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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import com.stackmob.sdkapi.http.request.PostRequest;
import com.stackmob.sdkapi.http.Header;
import com.stackmob.sdkapi.LoggerService;

import java.net.HttpURLConnection;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.oauth.OAuth;
import net.oauth.OAuthException;
import net.oauth.signature.*;

import org.apache.commons.codec.binary.Base64;


public class OAuthNaverConnect implements CustomCodeMethod {

		
	
	
  //Create your Twilio Acct at twilio.com and enter 
  //Your accountsid and accesstoken below.
  public static final String accountsid = "YOUR_ACCOUNTSID";
  public static final String accesstoken = "YOUR_ACCESSTOKEN";

  private static String urlString = "http://localhost";
  private static String apiKey = "k2l1_0LeGKSo";
  private static String apiSecret = "4FB5BC98lsKC3WNFHiY3";
  
  private static String oauth_consumer_key = "k2l1_0LeGKSo";
  private static String oauth_consumer_secret = "4FB5BC98lsKC3WNFHiY3";
  private static String oauth_token = "QkKpcz2Yvb2BA3w1";
  private static String oauth_token_secret = "hcFlDBd8WtAY71lRBAQBuZCemmcdPI";
  
  
  
  
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
    url = "https://nid.naver.com/naver.oauth?oauth_token=uGNocmbmLA1qXrh0&oauth_consumer_key=k2l1_0LeGKSo&oauth_nonce=1CLEjo90&oauth_timestamp=1372131694&oauth_verifier=yk8WQugXWTC_7852NTMuz1FsXdqc3D&oauth_signature_method=HAMC_SHA1&mode=req_acc_token&oauth_signature=QL1UwWxAhA1BwGn0qzX%2B9qb5woE%3D";
    
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

	Header auth 
	Authorization: 
	
    Set<Header> set = new HashSet();
    set.add(accept);
    set.add(content);
    set.add(auth);*/
    
   
    
    //Header auth = new Header("Authorization","OAuth realm=\"http://dev.apis.naver.com/apitest/nid/getUserId.xml\",oauth_token=\"8Be034_eE9fMAzhqL8Cxp7KRu3UBrb",oauth_consumer_key="k2l1_0LeGKSo",oauth_nonce="up_jv8fM",oauth_timestamp=\"1372131821\",oauth_version=\"1.0a\",oauth_signature_method=\"HMAC_SHA1\",oauth_signature=\"CndnfEhqG7xhJljRNzpNVQc0nac%3D\"";
    	
    
    try {
      HttpService http = serviceProvider.getHttpService();
      //PostRequest req = new PostRequest(url,set,body.toString());
      GetRequest req = new GetRequest(url);
      
      HttpResponse resp = http.get(req);
      responseCode = resp.getCode();
      responseBody = resp.getBody();
      
      logger.debug("resp.toString()"+ resp.toString());
      logger.debug("resp.getHeaders"+ resp.getHeaders());
      
      
      /*
      	{
	  	  "response_body": "oauth_token=nvOBH0crjrw5EmC1TOnPiWW_vnhenQ&oauth_token_secret=3aq2lzse7qVNQ6CdzjdveC_g6FRLjW&userid=wmrXtANKuDkK",
	  	  "response_code": 200  
	  	}
	  	*/
	  
      /*//config.log("obtaining access token from " + api.getAccessTokenEndpoint());
      OAuthRequest oAuthRequest = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
      oAuthRequest.addOAuthParameter(OAuthConstants.TOKEN, oauth_token);
      oAuthRequest.addOAuthParameter(OAuthConstants.VERIFIER, oauth_verifier);
      */
      
      url = "https://nid.naver.com/naver.oauth?" +
    		"mode=req_acc_token&" +
        	"oauth_consumer_key=k2l1_0LeGKSo&" +
    		"oauth_nonce=1CLEjo90&" +
    		"oauth_signature_method=HAMC_SHA1&" +
      		"oauth_timestamp=1372131694&" +
      		"oauth_token=uGNocmbmLA1qXrh0&" +
      		"oauth_verifier=yk8WQugXWTC_7852NTMuz1FsXdqc3D&" +
      		"oauth_signature=QL1UwWxAhA1BwGn0qzX%2B9qb5woE%3D";
      
      String baseString = "GET&https%3A%2F%2Fnid.naver.com%2Fnaver.oauth&mode%3Dreq_acc_token%26oauth_consumer_key%3Dk2l1_0LeGKSo%26oauth_nonce%3Dyc02xvit%26oauth_signature_method%3DHAMC_SHA1%26oauth_timestamp%3D1372165318%26oauth_token%3DQkKpcz2Yvb2BA3w1%26oauth_verifier%3D0BG7FB3Qq4QxenYXp4lOVpIGR67PZN";
      logger.debug(baseString);
      
      
      HttpRequestSignerNaverAPI api = null;
      long epoch = System.currentTimeMillis()/1000;
      String oauth_timestamp = String.valueOf(System.currentTimeMillis()/1000);
     	
      logger.debug("oauth_timestamp="+oauth_timestamp);
      
      Random rand = new Random();
      
      String oauth_nonce = oauth_timestamp + rand.nextInt(); // api.getTimestampService().getNonce();
      logger.debug("oauth_nonce="+oauth_nonce);
      String oauth_signature = getSignature(baseString);
      
      
      logger.debug("oauth_signature="+oauth_signature);
      // IWDxwGUbTWAK%2ByfiGyflCUSAU94%3D
      
      
      /*
      
      
      logger.debug("setting token to: " + oauth_token + " and verifier to: " + oauth_verifier);
        addOAuthParams(oAuthRequest, requestToken);
        appendSignature(request);
        Response response = request.send(tuner);
        return api.getAccessTokenExtractor().extract(response.getBody());
      }

      
      */
      
      
      
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
    } catch (OAuthException e) {
		// TODO Auto-generated catch block
    	logger.error(e.getMessage(), e);
    	responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;;
        responseBody = e.getMessage();
		//e.printStackTrace();
	}
      
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("response_code", responseCode);
    map.put("response_body", responseBody);
     
    return new ResponseToProcess(responseCode, map);
  }

  
  protected String getSignature(String baseString) throws OAuthException {
      try {
          String signature = OAuth.percentEncode(Base64.encodeBase64String(computeSignature(baseString)));
          return signature;
      } catch (GeneralSecurityException e) {
          throw new OAuthException(e);
      } catch (UnsupportedEncodingException e) {
          throw new OAuthException(e);
      }
  }

 
  private byte[] computeSignature(String baseString)
          throws GeneralSecurityException, UnsupportedEncodingException {
      SecretKey key = null;
      synchronized (this) {
          if (this.key == null) {
              String keyString = OAuth.percentEncode(oauth_consumer_secret)
                      + '&' + OAuth.percentEncode(oauth_token_secret);
              byte[] keyBytes = keyString.getBytes(ENCODING);
              this.key = new SecretKeySpec(keyBytes, MAC_NAME);
          }
          key = this.key;
      }
      Mac mac = Mac.getInstance(MAC_NAME);
      mac.init(key);
      byte[] text = baseString.getBytes(ENCODING);
      return mac.doFinal(text);
  }
  /** ISO-8859-1 or US-ASCII would work, too. */
  private static final String ENCODING = OAuth.ENCODING;

  private static final String MAC_NAME = "HmacSHA1";

  private SecretKey key = null;
}
