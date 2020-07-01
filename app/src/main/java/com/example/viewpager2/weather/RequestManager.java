package com.example.viewpager2.weather;
// Copyright 2019 Oath Inc. Licensed under the terms of the zLib license see https://opensource.org/licenses/Zlib for terms.

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestManager {
    private static RequestManager sInstance;

    Context mContext;
    RequestQueue mRequestQueue;

    public static synchronized RequestManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RequestManager(context);
        }
        return sInstance;
    }

    private RequestManager(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    public <T> void addToRequestQueue(Request<T> request) {
        mRequestQueue.add(request);
    }
}