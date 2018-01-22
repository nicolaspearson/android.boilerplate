package com.lupinemoon.boilerplate.data.storage.interfaces;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
public interface Storage {

    int countCacheHit();

    int countCacheRequests();

    boolean contains(String key);

    Object getCached(String key);

    Object getCached(String key, Class aClass) throws Exception;

    void putCache(String key, Object value);

    boolean getBoolean(String key, boolean backupVal);

    void putBoolean(String key, boolean val);

    long getLong(String key, long backupVal);

    void putLong(String key, long val);

    float getFloat(String key, float backupVal);

    void putFloat(String key, float val);

    double getDouble(String key, double backupVal);

    void putDouble(String key, double val);

    Object getObject(String key, Class objectClass);

    Object getObject(String key, Class objectClass, Type type);

    void putObject(String key, Object val);

    String getJsonString(String key);

    String getString(String key);

    String getString(String key, String backupVal);

    void putString(String key, String val);

    int getInt(String key, int backupVal);

    void putInt(String key, int val);

    void del(String key);

    void delCached(String key);

    void clearCache();

    void close();
}
