package com.fileopener;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.Map;
import java.util.HashMap;

public class FileOpener extends ReactContextBaseJavaModule {

    public FileOpener(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "FileOpener";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    @ReactMethod
    public void open(String fileArg, String contentType, Promise promise) throws JSONException {
        File file = new File(fileArg);
        if (file.exists()) {
            Uri path;
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    path = FileProvider.getUriForFile(getReactApplicationContext(), getReactApplicationContext().getPackageName()+".fileprovider", file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    path = Uri.fromFile(file);
                }
                Log.d("path", path.toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setDataAndType(path, contentType);
                if (intent.resolveActivity(getReactApplicationContext().getPackageManager()) == null) {
                    promise.reject("No app to open a " + contentType + " file on your device");
                    return;
                }
                getReactApplicationContext().startActivity(intent);

                promise.resolve("");
            } catch (android.content.ActivityNotFoundException e) {
                promise.reject("Error to open");
                Log.e("file-opener", e.toString());
            }
        } else {
            promise.reject("Can't find the file");
            Log.e("file-opener", "Can't find the file");
        }
    }

}
