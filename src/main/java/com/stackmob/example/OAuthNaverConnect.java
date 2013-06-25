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

  private static String oauth_consumer_key = "k2l1_0LeGKSo";
  private static String oauth_consumer_secret = "4FB5BC98lsKC3WNFHiY3";
  
  String oauth_token;
  String oauth_verifier;
  String oauth_token_secret ;
  
  @Override
  public String getMethodName() {
    return "oauth_naver_connect";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("oauth_token","oauth_verifier","oauth_token_secret");
    
  }  

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    int responseCode = 0;
    String responseBody = "";

	LoggerService logger = serviceProvider.getLoggerService(OAuthNaverConnect.class);
      
    oauth_token = request.getParams().get("oauth_token");
    oauth_verifier = request.getParams().get("oauth_verifier");
    oauth_token_secret = request.getParams().get("oauth_token_secret");
    
    if (oauth_token == null || oauth_token.isEmpty()) {
        logger.error("Missing oauth_token");
      }
    if (oauth_verifier == null || oauth_verifier.isEmpty()) {
        logger.error("Missing oauth_verifier");
      }
    if (oauth_token_secret == null || oauth_token_secret.isEmpty()) {
        logger.error("Missing oauth_token_secret");
      }

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
      
      
	  
      /*//config.log("obtaining access token from " + api.getAccessTokenEndpoint());
      OAuthRequest oAuthRequest = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
      oAuthRequest.addOAuthParameter(OAuthConstants.TOKEN, oauth_token);
      oAuthRequest.addOAuthParameter(OAuthConstants.VERIFIER, oauth_verifier);
      */
      /*
       
      
      String oauth_timestamp = String.valueOf(getMilis()/1000);
      logger.debug("oauth_timestamp="+oauth_timestamp);
      
      Random rand = new Random();
      
      String oauth_nonce = OAuth.percentEncode(String.valueOf(getMilis()/1000 + rand.nextInt())); // api.getTimestampService().getNonce();
      logger.debug("oauth_nonce="+oauth_nonce);
      
      
      
      String baseString = "GET&https%3A%2F%2Fnid.naver.com%2Fnaver.oauth&" +
    		  "mode%3Dreq_acc_token%26oauth_consumer_key%3D" + oauth_consumer_key + "" +
      		"%26oauth_nonce%3D" + oauth_nonce +
      		"%26oauth_signature_method%3DHAMC_SHA1" +
      		"%26oauth_timestamp%3D"+ oauth_timestamp + 
      		"%26oauth_token%3D" + oauth_token + 
      		"%26oauth_verifier%3D" + oauth_verifier ;
      logger.debug(baseString);
      
      String oauth_signature = getSignature(baseString);
      logger.debug("oauth_signature="+oauth_signature);
      
      
      
      url = "https://nid.naver.com/naver.oauth?" +
      		"mode=req_acc_token&" +
          	"oauth_consumer_key=" + oauth_consumer_key +"&" +
      		"oauth_nonce=" + oauth_nonce + "&" +
      		"oauth_signature_method=HAMC_SHA1&" +
    		"oauth_timestamp="+oauth_timestamp+ "&" +
    		"oauth_token="+oauth_token+"&" +
    		"oauth_verifier="+oauth_verifier+"&" +
    		"oauth_signature="+oauth_signature;
      
      
      logger.debug("url="+url);
      
      
      HttpService http = serviceProvider.getHttpService();
      
      //PostRequest req = new PostRequest(url,set,body.toString());
      
      GetRequest req = new GetRequest(url);
      
      HttpResponse resp = http.get(req);
      responseCode = resp.getCode();
      responseBody = resp.getBody();
      
      logger.debug("resp.toString()"+ resp.toString());
      logger.debug("resp.getHeaders"+ resp.getHeaders());
      
      
      if (responseCode== HttpURLConnection.HTTP_OK)
      {
	      String[] params = responseBody.split("&");  
		  for (String param : params)  
	  	    {  
	  	    	if ( param.split("=").length > 1 ) { 
	  	    		String name = param.split("=")[0];  
	  		        String value = param.split("=")[1];  
	  		        if (name.equalsIgnoreCase("oauth_token")) {
	  		        	oauth_token = value;
	  		        }
	  		        if (name.equalsIgnoreCase("oauth_token_secret")) {
	  		        	oauth_token_secret = value;
	  		        }
	  	    	}
	  	    }  
		  */
		  logger.debug("oauth_token=" +oauth_token + " oauth_token_secret=" + oauth_token_secret);
		  
	      String oauth_timestamp2 = String.valueOf(getMilis()/1000);
	      String oauth_nonce2 = OAuth.percentEncode(String.valueOf(getMilis()/1000 + rand.nextInt()));
	      
	      String baseString2 = "GET&http%3A%2F%2Fdev.apis.naver.com%2Fapitest%2Fnid%2FgetUserId.xml&" +
	        		"oauth_consumer_key%3D" + oauth_consumer_key + 
	        		"%26oauth_nonce%3D" + oauth_nonce2 +
	        		"%26oauth_signature_method%3DHMAC_SHA1" +
	        		"%26oauth_timestamp%3D"+ oauth_timestamp2 + 
	        		"%26oauth_token%3D" + oauth_token + 
	        		"%26oauth_version%3D1.0a";
	      logger.debug(baseString2);
	      
	      String oauth_signature2 = getSignature(baseString2);
	      logger.debug("oauth_signature="+oauth_signature2);
	       
	      String AuthHeader = "OAuth realm=\"http://dev.apis.naver.com/apitest/nid/getUserId.xml" +"\"," +
	        		"oauth_token=\"" + oauth_token+ "\"," +
	        		"oauth_consumer_key=\""+oauth_consumer_key+ "\"," +
	        		"oauth_nonce=\""+oauth_nonce2+ "\"," +
	        		"oauth_timestamp=\""+oauth_timestamp2+ "\"," +
	        		"oauth_version=\"1.0a\"," +
	        		"oauth_signature_method=\"HMAC_SHA1\",oauth_signature=\""+oauth_signature2+"\"";
	      	  
	        	
	      url="http://dev.apis.naver.com/apitest/nid/getUserId.xml";
	      
	      logger.debug(AuthHeader);
	      
	      Header auth = new Header("Authorization",AuthHeader);
	      
	  		
	      HttpService http2 = serviceProvider.getHttpService();
	      
	      Set<Header> set = new HashSet();
	      set.add(auth);
	      
	      GetRequest req2 = new GetRequest(url,set);  
	      HttpResponse resp2 = http2.get(req2);
	      
	      responseCode = resp2.getCode();
	      responseBody = resp2.getBody();
	      logger.debug("resp.toString()"+ resp2.toString());
	      logger.debug("resp.getHeaders"+ resp2.getHeaders());
	      
	      /*
	    	{
		  	  "response_body": "oauth_token=nvOBH0crjrw5EmC1TOnPiWW_vnhenQ&oauth_token_secret=3aq2lzse7qVNQ6CdzjdveC_g6FRLjW&userid=wmrXtANKuDkK",
		  	  "response_code": 200  
		  	}
		  	*/
/*      } else {
    	
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
  
  private final Random rand = new Random();
	Long getMilis() {
	  return System.currentTimeMillis();
	}

    Integer getRandomInteger() {
      return rand.nextInt();
    }
  
}
