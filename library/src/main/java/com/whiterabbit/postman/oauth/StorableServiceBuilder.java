package com.whiterabbit.postman.oauth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.whiterabbit.postman.utils.Constants;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.SignatureType;
import org.scribe.oauth.OAuthService;

/**
 * Created with IntelliJ IDEA.
 * User: fedepaol
 * Date: 1/5/13
 * Time: 2:34 PM
 */
public class StorableServiceBuilder {
    private static final String API_KEY = "com.whiterabbit.apikey";
    private static final String API_SECRET = "com.whiterabbit.apisecret";
    private static final String CALLBACK = "com.whiterabbit.callback";
    private static final String API_SCOPE = "com.whiterabbit.scope";
    private static final String SIGNATURE_TYPE = "com.whiterabbit.signaturetype";
    private static final String API = "com.whiterabbit.api";
    private static final String SERVICES = "com.whiterabbit.services";

    private String apiKey;
    private String apiSecret;
    private String callback;
    private String scope;
    private SignatureType signatureType;
    private String mServiceName;
    private Class api;
    private ServiceBuilder mServiceBuilder;

    public StorableServiceBuilder(String name){
        mServiceName = name;
        mServiceBuilder = new ServiceBuilder();
        signatureType = SignatureType.Header;
    }

    public String getName(){
        return mServiceName;
    }


    public StorableServiceBuilder provider(Class<? extends Api> apiClass)
    {
        this.api = apiClass;
        mServiceBuilder.provider(apiClass);
        return this;
    }


    public StorableServiceBuilder callback(String callback)
    {
        this.callback = callback;
        mServiceBuilder.callback(callback);
        return this;
    }

    public StorableServiceBuilder apiKey(String apiKey)
    {
        this.apiKey = apiKey;
        mServiceBuilder.apiKey(apiKey);
        return this;
    }

    public StorableServiceBuilder apiSecret(String apiSecret)
    {
        this.apiSecret = apiSecret;
        mServiceBuilder.apiSecret(apiSecret);
        return this;
    }

    public StorableServiceBuilder scope(String scope)
    {
        this.scope = scope;
        mServiceBuilder.scope(scope);
        return this;
    }

    public StorableServiceBuilder signatureType(SignatureType type)
    {
        this.signatureType = type;
        mServiceBuilder.signatureType(type);
        return this;
    }


    public OAuthService build(Context c)
    {
        return mServiceBuilder.build();
    }



    void storeToPreferences(String serviceName, Context c){
        SharedPreferences mySharedPreferences = c.getSharedPreferences(serviceName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(API_KEY, apiKey);
        editor.putString(API_SECRET, apiSecret);
        editor.putString(CALLBACK, callback);
        editor.putString(API_SCOPE, scope);
        editor.putInt(SIGNATURE_TYPE, signatureType.ordinal());
        editor.putString(API, api.getName());
        editor.commit();


        SharedPreferences listPreferences = c.getSharedPreferences(Constants.ALL_SERVICES, Activity.MODE_PRIVATE);

        StringBuilder builder = new StringBuilder(listPreferences.getString(SERVICES, ""));
        builder.append(serviceName).append(";");

        SharedPreferences.Editor listEditor = mySharedPreferences.edit();
        listEditor.putString(SERVICES, builder.toString());
        listEditor.commit();
    }

    public static String[] getAllServices(Context c){
        SharedPreferences listPreferences = c.getSharedPreferences(Constants.ALL_SERVICES, Activity.MODE_PRIVATE);
        String[] res = listPreferences.getString(SERVICES, "").split(";");
        return res;
    }

    StorableServiceBuilder(String serviceName, Context c){
        SharedPreferences mySharedPreferences = c.getSharedPreferences(serviceName, Activity.MODE_PRIVATE);
        mServiceName = serviceName;
        apiKey = mySharedPreferences.getString(API_KEY, "");
        apiSecret = mySharedPreferences.getString(API_SECRET, "");
        callback = mySharedPreferences.getString(CALLBACK, "");
        scope = mySharedPreferences.getString(API_SCOPE, "");
        signatureType = SignatureType.values()[mySharedPreferences.getInt(SIGNATURE_TYPE, -1)];


        String className = mySharedPreferences.getString(API, "");
        try {
            api = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Log.e(Constants.LOG_TAG, String.format("Class %s not found", className));
        }

        mServiceBuilder = new ServiceBuilder();
        mServiceBuilder.provider(api).apiKey(apiKey).apiSecret(apiSecret);  // mandatory
        mServiceBuilder.signatureType(signatureType);

        if(!callback.equals("")){
            mServiceBuilder.callback(callback);
        }
        if(!scope.equals("")){
            mServiceBuilder.scope(scope);
        }
    }


}