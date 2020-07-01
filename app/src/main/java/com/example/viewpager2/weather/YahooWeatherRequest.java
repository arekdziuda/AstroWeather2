package com.example.viewpager2.weather;
// Copyright 2019 Oath Inc. Licensed under the terms of the zLib license see https://opensource.org/licenses/Zlib for terms.

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.JsonSyntaxException;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

//https://developer.yahoo.com/weather/documentation.html#oauth-android
public class YahooWeatherRequest<T> extends JsonRequest<T> {
    private final String appId = "95SW2e36";
    private final String CONSUMER_KEY = "dj0yJmk9eHVNTHh5ME1qNWVkJmQ9WVdrOU9UVlRWekpsTXpZbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PTZj";
    private final String CONSUMER_SECRET = "8bae383a5889c1f9e115b75fe7f56919d56035df";
    private final String baseUrl = "https://weather-ydn-yql.media.yahoo.com/forecastrss";
    private String location = "lodz,pl";
    private String longitude;
    private String latitude;
    private boolean isCoordinateRequest;

    public YahooWeatherRequest(int method, String url, String requestBody, String location, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        if (location != null)
            this.location = location;

        this.isCoordinateRequest = false;
    }

    public YahooWeatherRequest(int method, String url, String requestBody, String longitude, String latitude, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        if (longitude != null)
            this.longitude = longitude;

        if (latitude != null)
            this.latitude = latitude;

        this.isCoordinateRequest = true;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        OAuthConsumer consumer = new OAuthConsumer(null, CONSUMER_KEY, CONSUMER_SECRET, null);
        consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        try {
            OAuthMessage request = accessor.newRequestMessage(OAuthMessage.GET, getUrl(), null);
            String authorization = request.getAuthorizationHeader(null);
            headers.put("Authorization", authorization);
        } catch (OAuthException | IOException | URISyntaxException e) {
            throw new AuthFailureError(e.getMessage());
        }

        headers.put("X-Yahoo-App-Id", appId);
        headers.put("Content-Type", "application/json");
        return headers;
    }

	@Override
	public String getUrl() {
		String url =  this.isCoordinateRequest ? getCoordinateUrl() : getLocalizationUrl();
		return url;
	}

	private String getCoordinateUrl() {
		return baseUrl + "?lat=" + this.latitude +  "&lon=" + this.longitude + "&format=json&u=c";
	}

	private String getLocalizationUrl() {
		return baseUrl + "?location="+ location + "&format=json&u=c";
	}

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            T parsedResponse = parseResponse(json);
            return Response.success(
                    parsedResponse,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JsonSyntaxException | JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    private T parseResponse(String jsonObject) throws JSONException {
        JSONObject jsonObject1 = new JSONObject(jsonObject);
        return (T) jsonObject1;
    }
}