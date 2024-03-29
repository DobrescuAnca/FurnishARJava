package com.example.furnishar;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.api.GoogleApiClient;

public class SessionManager {

    private static final String PREFS_NAME = "furnish_ar";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String LOGIN_METHOD = "login_method";
    private static final String USER_EMAIL = "user_email";
    private static final String FIREBASE_USER_ID = "user_id";

    public static final String FACEBOOK = "facebook";
    public static final String GOOGLE = "google";

    private static SessionManager instance = null;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private GoogleApiClient googleApiClient;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void setAccessToken(String accessToken) {
        editor.putString(ACCESS_TOKEN, accessToken).apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN, "");
    }

    public void setUserEmail(String emailAddress) {
        editor.putString(USER_EMAIL, emailAddress).apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(USER_EMAIL, "");
    }

    public void setLoginMethod(String loginMethod) {
        editor.putString(LOGIN_METHOD, loginMethod).apply();
    }

    public void setUserID(String userID) {
        editor.putString(FIREBASE_USER_ID, userID).apply();
    }

    public String getFirebaseUserId() {
        return sharedPreferences.getString(FIREBASE_USER_ID, "");
    }



}
