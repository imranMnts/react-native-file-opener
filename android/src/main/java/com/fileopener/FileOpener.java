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

//  @ReactMethod
//  public void open(String fileArg, String contentType, Promise promise) throws JSONException {
//  		File file = new File(fileArg);
//
//  		if (file.exists()) {
//  			try {
//          Uri path = FileProvider.getUriForFile(getReactApplicationContext(), getReactApplicationContext().getPackageName() + ".fileprovider", file);
//  				Intent intent = new Intent(Intent.ACTION_VIEW);
//  				intent.setDataAndType(path, contentType);
//          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//  				getReactApplicationContext().startActivity(intent);
//
//                promise.resolve("Open success!!");
//  			} catch (android.content.ActivityNotFoundException e) {
//                promise.reject("Open error!!");
//  			}
//  		} else {
//            promise.reject("File not found");
//  		}
//  	}

    @ReactMethod
    public void open(String fileArg, String contentType, Promise promise) throws JSONException {
        File file = new File(fileArg);
        if (file.exists()) {
            Uri path;
            /*
            * Android不再允许在app中把file://Uri暴露给其他app，包括但不局限于通过Intent或ClipData 等方法。原因在于使用file://Uri会有一些风险，比如：
            * 文件是私有的，接收file://Uri的app无法访问该文件。
            * 在Android6.0之后引入运行时权限，如果接收file://Uri的app没有申请READ_EXTERNAL_STORAGE权限，在读取文件时会引发崩溃。
            * 因此，google提供了FileProvider，使用它可以生成content://Uri来替代file://Uri。
            * */
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    path = FileProvider.getUriForFile(getReactApplicationContext(), getReactApplicationContext().getPackageName()+".provider", file);
                } else {
                    path = Uri.fromFile(file);
                }
                intent.setDataAndType(path, contentType);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                if (intent.resolveActivity(getReactApplicationContext().getPackageManager()) == null) {
                    promise.reject("当前系统中没有可以打开" + contentType + "的软件，请先安装相应的App");
                    return;
                }
                getReactApplicationContext().startActivity(intent);

                promise.resolve("");
            } catch (android.content.ActivityNotFoundException e) {
                promise.reject("打开失败");
                Log.e("file-opener", e.toString());
            }
        } else {
            promise.reject("找不到您要打开的文件");
            Log.e("file-opener", "找不到您要打开的文件");
        }
    }

}
