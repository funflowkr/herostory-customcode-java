package com.stackmob.example;

import org.scribe.services.TimestampService;
import org.scribe.services.TimestampServiceImpl;
import org.scribe.model.Token;
import org.scribe.builder.api.DefaultApi10a;

public class HttpRequestSignerNaverAPI extends DefaultApi10a {
	public static class TimeService extends TimestampServiceImpl {
        @Override
        public String getTimestampInSeconds() {
			return String.valueOf(System.currentTimeMillis());
        }
    }

    @Override
    public String getRequestTokenEndpoint() {
    //    return null;
        return "https://nid.naver.com/naver.oauth?mode=req_req_token";
    }

    @Override
    public String getAccessTokenEndpoint() {
    //    return null;
        return "https://nid.naver.com/naver.oauth?mode=req_acc_token";
    }

    @Override
    public String getAuthorizationUrl(Token token) {
    //    return null;
        return "https://nid.naver.com/naver.oauth?mode=auth_req_token&oauth_token="+token.getToken();
    }

    @Override
    public TimestampService getTimestampService()
    {
        return new TimeService();
    }
}