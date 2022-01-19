package com.org.trophy.admin.Helper;

public interface HttpListener {
    void onResponse(String response, int code, Object... objects);
}
