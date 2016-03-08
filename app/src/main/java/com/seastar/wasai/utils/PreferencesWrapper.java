package com.seastar.wasai.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesWrapper implements SharedPreferences.OnSharedPreferenceChangeListener {

    //回调接口，改变成功后可以进行回调
    public interface PreferencesListener {
        void afterChanged(SharedPreferences sharedPreferences, String key);
    }

    private List<PreferencesListener> listeners = null;
    private boolean hasRegister = false;

    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;


    public PreferencesWrapper(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        listeners = new ArrayList<PreferencesListener>();
    }

    public void registerChangeListener() {
        if (!hasRegister) {
            preferences.registerOnSharedPreferenceChangeListener(this);
            hasRegister = true;
        }
    }

    public void unregisterChangeListener() {
        if (hasRegister) {
            preferences.unregisterOnSharedPreferenceChangeListener(this);
            hasRegister = false;
        }
    }

    public void addListener(PreferencesListener list) {
        if (list != null) {
            listeners.add(list);
        }
    }

    public String getStringValue(String keyValue, String defValue) {
        return preferences.getString(keyValue, defValue);
    }

    public boolean getBooleanValue(String keyValue, boolean defValue) {
        return preferences.getBoolean(keyValue, defValue);
    }

    public int getIntValue(String keyValue, int defValue) {
        return preferences.getInt(keyValue, defValue);
    }

    public long getLongValue(String keyValue, long defValue) {
        return preferences.getLong(keyValue, defValue);
    }

    public void setIntValue(String keyValue, int newValue) {
        if (editor == null)
            editor = preferences.edit();
        editor.putInt(keyValue, newValue);
    }

    public void setIntValueAndCommit(String keyValue, int newValue) {
        setIntValue(keyValue, newValue);
        editor.commit();
        editor = null;
    }

    public void setStringValue(String keyValue, String newValue) {
        if (editor == null)
            editor = preferences.edit();
        editor.putString(keyValue, newValue);
    }

    public void setStringValueAndCommit(String keyValue, String newValue) {
        setStringValue(keyValue, newValue);
        editor.commit();
        editor = null;
    }

    public void setLongValue(String keyValue, long newValue) {
        if (editor == null)
            editor = preferences.edit();
        editor.putLong(keyValue, newValue);
    }

    public void setLongValueAndCommit(String keyValue, long newValue) {
        setLongValue(keyValue, newValue);
        editor.commit();
        editor = null;
    }

    public void setBooleanValue(String keyValue, boolean newValue) {
        if (editor == null)
            editor = preferences.edit();
        editor.putBoolean(keyValue, newValue);
    }

    public void setBooleanValueAndCommit(String keyValue, boolean newValue) {
        setBooleanValue(keyValue, newValue);
        editor.commit();
        editor = null;
    }

    public void commit() {
        if (editor != null) {
            editor.commit();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (hasRegister && listeners != null) {
            for (int i = listeners.size() - 1; i >= 0; --i) {
                listeners.get(i).afterChanged(sharedPreferences, key);
            }
        }
    }

}
