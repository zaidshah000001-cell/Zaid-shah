package com.nawab.ride;
import android.app.Activity;
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
 @Override public void onCreate(Bundle state){super.onCreate(state);web=new WebView(this);setContentView(web);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(true);web.setWebViewClient(new WebViewClient());web.setWebChromeClient(new WebChromeClient(){@Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams params){if(upload!=null)upload.onReceiveValue(null);upload=cb;try{startActivityForResult(params.createIntent(),PICK);return true;}catch(Exception e){upload=null;return false;}}});web.loadUrl("file:///android_asset/index.html");}
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==PICK&&upload!=null){upload.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result,data));upload=null;}}
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}
