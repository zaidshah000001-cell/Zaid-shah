package com.nawab.ride;
import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.webkit.JavascriptInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.content.Intent;
import android.net.Uri;
public class MainActivity extends Activity {
 private WebView web; private ValueCallback<Uri[]> upload;
 private static final int PICK=102;
 @Override public void onCreate(Bundle state){super.onCreate(state);web=new WebView(this);setContentView(web);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(true);web.setWebViewClient(new WebViewClient());web.setWebChromeClient(new WebChromeClient(){@Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams params){if(upload!=null)upload.onReceiveValue(null);upload=cb;try{startActivityForResult(params.createIntent(),PICK);return true;}catch(Exception e){upload=null;return false;}}});web.addJavascriptInterface(new SmsBridge(),"NawabSms");web.loadUrl("file:///android_asset/index.html");}
 public class SmsBridge {
  @JavascriptInterface public void enable(){runOnUiThread(()->{if(android.os.Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},200);}else{getSharedPreferences("sms",MODE_PRIVATE).edit().putBoolean("enabled",true).apply();}});}
  @JavascriptInterface public void disable(){getSharedPreferences("sms",MODE_PRIVATE).edit().putBoolean("enabled",false).apply();}
  @JavascriptInterface public String status(){return (getSharedPreferences("sms",MODE_PRIVATE).getBoolean("enabled",false) && checkSelfPermission(Manifest.permission.RECEIVE_SMS)==PackageManager.PERMISSION_GRANTED)?"enabled":"disabled";}
  @JavascriptInterface public String pending(){SharedPreferences p=getSharedPreferences("sms",MODE_PRIVATE);return p.getString("pending","[]");}
  @JavascriptInterface public void setPending(String json){try{org.json.JSONArray arr=new org.json.JSONArray(json);getSharedPreferences("sms",MODE_PRIVATE).edit().putString("pending",arr.toString()).apply();}catch(Exception ignored){}}
  @JavascriptInterface public void clearPending(){getSharedPreferences("sms",MODE_PRIVATE).edit().putString("pending","[]").apply();}
 }
 @Override public void onRequestPermissionsResult(int code,String[] perms,int[] results){super.onRequestPermissionsResult(code,perms,results);if(code==200&&results.length>0&&results[0]==PackageManager.PERMISSION_GRANTED)getSharedPreferences("sms",MODE_PRIVATE).edit().putBoolean("enabled",true).apply();}
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==PICK&&upload!=null){upload.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result,data));upload=null;}}
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}
