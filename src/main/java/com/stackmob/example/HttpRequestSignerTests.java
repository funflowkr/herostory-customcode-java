package com.stackmob.example;

import com.stackmob.sdkapi.http.request.*;
import org.junit.*;
import org.scribe.exceptions.OAuthException;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequestSignerTests {
    private static String urlString = "http://localhost";
    private static String apiKey = "k2l1_0LeGKSo";
    private static String apiSecret = "4FB5BC98lsKC3WNFHiY3";
    private static String appName = "test_app";
    private static int appVersion = 0;

    @Test public void testSignWithoutBody() throws MalformedURLException, OAuthException {
        HttpRequestSigner signer = new HttpRequestSigner(apiKey, apiSecret, appName, appVersion);
        GetRequest req = new GetRequest(urlString);
        HttpRequestWithoutBody retReq = signer.sign(req);
        Assert.assertEquals(retReq.getUrl(), new URL(urlString));
    }

    @Test public void testSignWithBody() throws MalformedURLException, OAuthException {
        HttpRequestSigner signer = new HttpRequestSigner(apiKey, apiSecret, appName, appVersion);
        String body = "testBody";
        PostRequest req = new PostRequest(urlString, body);
        HttpRequestWithBody retReq = signer.sign(req);
        Assert.assertEquals(retReq.getUrl(), new URL(urlString));
    }
}

