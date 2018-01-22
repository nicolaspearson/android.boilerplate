package com.lupinemoon.boilerplate.data.storage.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import timber.log.Timber;
import com.lupinemoon.boilerplate.BuildConfig;
import com.lupinemoon.boilerplate.data.storage.interfaces.Storage;

public class SharedPreferencesStorage implements Storage {

    private SharedPreferences preferences;

    private LruCache<String, Object> cache;

    private int cacheRequest;
    private int cacheHit;

    public SharedPreferencesStorage(Context context) {
        Timber.d("init SharedPreferencesStorage");
        preferences = context.getSharedPreferences(BuildConfig.FLAVOR_STORAGE, Context.MODE_PRIVATE);
        cache = new LruCache<>(BuildConfig.STORAGE_MEM_CACHE_SIZE);
    }

    @Override
    public synchronized int countCacheHit() {
        return cacheHit;
    }

    @Override
    public synchronized int countCacheRequests() {
        return cacheRequest;
    }

    @Override
    public synchronized boolean contains(String key) {
        return (cache.get(key) != null) || preferences.contains(key);
    }

    @Override
    public synchronized void delCached(String key) {
        cache.remove(key);
    }

    @Override
    public synchronized void clearCache() {
        cache.evictAll();
    }

    @Override
    public synchronized Object getCached(final String key, final Class aClass) throws Exception {
        Object value = null;
        try {
            value = getCached(key);
            if (value != null) {
                return aClass.cast(value);
            } else {
                throw new CacheMissException();
            }
        } catch (Exception e) {
            Timber.w("cache miss: cache.get(%s), of type %s and value: %s", key, String.valueOf(aClass), value);
            if (value != null) {
                cacheHit = cacheHit--;
            }
            if (key != null) {
                cache.remove(key);
            }
            throw e;
        }

    }

    @Override
    public synchronized Object getCached(String key) {
        cacheRequest = cacheRequest++;
        Object val = cache.get(key);
        if (val != null) {
            cacheHit = cacheHit++;
        }
        return val;
    }

    @Override
    public synchronized void putCache(String key, Object val) {
        if ((key != null) && (val != null)) {
            cache.put(key, val);
        }
    }

    @Override
    public synchronized void putBoolean(String key, boolean val) {
        putCache(key, val);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, val);
        edit.apply();
    }

    @Override
    public synchronized void putInt(String key, int val) {
        putCache(key, val);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(key, val);
        edit.apply();
    }

    @Override
    public synchronized void putLong(String key, long val) {
        putCache(key, val);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(key, val);
        edit.apply();
    }

    @Override
    public synchronized void putFloat(String key, float val) {
        putCache(key, val);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putFloat(key, val);
        edit.apply();
    }

    @Override
    public synchronized void putDouble(String key, double val) {
        putCache(key, val);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(key, Double.doubleToLongBits(val));
        edit.apply();
    }

    @Override
    public synchronized void putObject(String key, Object val) {
        putCache(key, val);
        SharedPreferences.Editor edit = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(val);
        edit.putString(key, json);
        edit.apply();
    }

    @Override
    public synchronized void putString(String key, String val) {
        putCache(key, val);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, val);
        edit.apply();
    }

    @Override
    public synchronized boolean getBoolean(final String key, final boolean backupVal) {
        try {
            return (boolean) getCached(key, Boolean.class);
        } catch (Exception eCache) {
            try {
                if (preferences.contains(key)) {
                    boolean val = preferences.getBoolean(key, backupVal);
                    putCache(key, val);
                    return val;
                }
            } catch (Exception eDB) {
                Timber.w(eDB, "db.getBoolean(" + key + "), backup=" + String.valueOf(backupVal));
            }
        }
        return backupVal;
    }

    @Override
    public synchronized int getInt(final String key, final int backupVal) {
        try {
            return (int) getCached(key, Integer.class);
        } catch (Exception eCache) {
            try {
                if (preferences.contains(key)) {
                    int val = preferences.getInt(key, backupVal);
                    putCache(key, val);
                    return val;
                }
            } catch (Exception eDB) {
                Timber.w(eDB, "db.getInt(" + key + "), backup=" + String.valueOf(backupVal));
            }
        }
        return backupVal;
    }

    @Override
    public synchronized long getLong(final String key, final long backupVal) {
        try {
            return (long) getCached(key, Long.class);
        } catch (Exception eCache) {
            try {
                if (preferences.contains(key)) {
                    long val = preferences.getLong(key, backupVal);
                    putCache(key, val);
                    return val;
                }
            } catch (Exception eDB) {
                Timber.w(eDB, "db.getLong(" + key + "), backup=" + String.valueOf(backupVal));
            }
        }
        return backupVal;
    }

    @Override
    public synchronized float getFloat(final String key, final float backupVal) {
        try {
            return (float) getCached(key, Float.class);
        } catch (Exception eCache) {
            try {
                if (preferences.contains(key)) {
                    float val = preferences.getFloat(key, backupVal);
                    putCache(key, val);
                    return val;
                }
            } catch (Exception eDB) {
                Timber.w(eDB, "db.getFloat(" + key + "), backup=" + String.valueOf(backupVal));
            }
        }
        return backupVal;
    }

    @Override
    public synchronized double getDouble(final String key, final double backupVal) {
        try {
            return (double) getCached(key, Double.class);
        } catch (Exception eCache) {
            try {
                if (preferences.contains(key)) {
                    long backupLongBits = Double.doubleToLongBits(backupVal);
                    long longBits = preferences.getLong(key, backupLongBits);
                    double val = Double.longBitsToDouble(longBits);
                    putCache(key, val);
                    return val;
                }
            } catch (Exception eDB) {
                Timber.w(eDB, "db.getDouble(" + key + "), backup=" + String.valueOf(backupVal));
            }
        }
        return backupVal;
    }

    @Override
    public synchronized Object getObject(final String key, final Class objectClass) {
        try {
            return getCached(key, objectClass);
        } catch (Exception eCache) {
            try {
                if (preferences.contains(key)) {
                    String json = preferences.getString(key, null);
                    Timber.d("json: %s", json);
                    if (!TextUtils.isEmpty(json) && !json.equals("null")) {
                        Gson gson = new Gson();
                        Object val = gson.fromJson(json, objectClass);
                        Timber.d("getObject: k:" + key + ", val:" + val);
                        putCache(key, val);
                        return val;
                    }
                }
            } catch (Exception eDB) {
                Timber.w(eDB, "db.getObject(" + key + "), class=" + String.valueOf(objectClass));
            }
        }
        return null;
    }

    @Override
    public synchronized Object getObject(final String key, final Class objectClass, Type type) {
        try {
            return getCached(key, objectClass);
        } catch (Exception eCache) {
            try {
                if (preferences.contains(key)) {
                    String json = preferences.getString(key, null);
                    if (json != null) {
                        Gson gson = new Gson();
                        Object val = gson.fromJson(json, type);
                        Timber.d("getObject: k:" + key + ", val:" + val);
                        putCache(key, val);
                        return val;
                    }
                }
            } catch (Exception eDB) {
                Timber.w(eDB, "db.getObject(" + key + "), class=" + String.valueOf(objectClass));
            }
        }
        return null;
    }

    @Override
    public String getJsonString(String key) {
        return (String) getObject(key, String.class);
    }

    @Override
    public synchronized String getString(String key) {
        return getString(key, "");
    }

    @Override
    public String getString(String key, String backupVal) {
        try {
            return (String) getCached(key, String.class);
        } catch (Exception eCache) {
            try {
                if (preferences.contains(key)) {
                    String val = preferences.getString(key, backupVal);
                    putCache(key, val);
                    return val;
                }
            } catch (Exception eDB) {
                Timber.w(eDB, "db.getString(" + key + "), backup=" + String.valueOf(backupVal));
            }
        }
        return backupVal;
    }

    @Override
    public synchronized void del(String key) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(key);
        edit.apply();
        delCached(key);
    }

    @Override
    public synchronized void close() {
        Timber.d("close");
        clearCache();
        cache = null;
        preferences = null;
    }

    @Override
    public void finalize() {
        Timber.d("finalize");
        if (preferences != null) {
            close();
        }

    }

    public static class CacheMissException extends Exception {
    }
}
