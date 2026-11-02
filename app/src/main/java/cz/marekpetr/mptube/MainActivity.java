package cz.marekpetr.mptube;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.Context;
import android.view.View;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    private WebView browser;
    private EditText address;
    private Switch filterSwitch;
    private volatile boolean filtering = true;
    private FrameLayout fullscreenFrame;
    private View customVideo;
    private WebChromeClient.CustomViewCallback videoCallback;
    private final Set<String> blocked = new HashSet<>(Arrays.asList(
        "doubleclick.net", "googlesyndication.com", "googleadservices.com",
        "adservice.google.com", "googletagservices.com", "scorecardresearch.com",
        "taboola.com", "outbrain.com", "adsrvr.org"
    ));
    private int dp(float n) { return (int)(getResources().getDisplayMetrics().density*n + .5f); }
    private TextView action(String txt, LinearLayout bar, Runnable tap) {
        TextView t = new TextView(this); t.setText(txt); t.setTextColor(Color.WHITE);
        t.setTextSize(19); t.setGravity(Gravity.CENTER);
        bar.addView(t, new LinearLayout.LayoutParams(dp(46),dp(50)));
        t.setOnClickListener(v->tap.run()); return t;
    }
    @Override public void onCreate(Bundle b) {
        super.onCreate(b); getWindow().setStatusBarColor(0xff0b1020);getWindow().setNavigationBarColor(0xff0b1020);
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(0xff0b1020);
        TextView brand=new TextView(this);brand.setText("▶  MP TUBE     •     Created by Marek Petr");brand.setTextColor(0xfff3ca65);
        brand.setTypeface(null,Typeface.BOLD);brand.setTextSize(15);brand.setPadding(dp(14),dp(9),dp(8),dp(9));root.addView(brand);
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);root.addView(bar);
        action("⌂",bar,()->browser.loadUrl("https://m.youtube.com/"));
        action("‹",bar,()->{if(browser.canGoBack())browser.goBack();});
        action("›",bar,()->{if(browser.canGoForward())browser.goForward();});
        address=new EditText(this);address.setSingleLine(true);address.setTextColor(Color.WHITE);address.setHintTextColor(0xff9aa4bc);
        address.setHint("YouTube nebo adresa");address.setTextSize(13);address.setSelectAllOnFocus(true);
        address.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
        bar.addView(address,new LinearLayout.LayoutParams(0,dp(47),1));
        address.setOnEditorActionListener((v,id,event)->{navigate(address.getText().toString());return true;});
        action("↻",bar,()->browser.reload());
        LinearLayout controls=new LinearLayout(this);controls.setGravity(Gravity.CENTER_VERTICAL);controls.setPadding(dp(12),0,dp(8),0);root.addView(controls);
        filterSwitch=new Switch(this);filterSwitch.setText("Filtr reklamních domén");filterSwitch.setTextColor(0xffc7d3e7);filterSwitch.setTextSize(12);
        filtering=getPreferences(MODE_PRIVATE).getBoolean("filter_enabled",true);filterSwitch.setChecked(filtering);filterSwitch.setOnCheckedChangeListener((button,checked)->{filtering=checked;getPreferences(MODE_PRIVATE).edit().putBoolean("filter_enabled",checked).apply();browser.reload();});controls.addView(filterSwitch,new LinearLayout.LayoutParams(0,dp(40),1));
        TextView info=new TextView(this);info.setText("Filtr ≠ 100 % bez reklam");info.setTextSize(10);info.setTextColor(0xff9aa4bc);controls.addView(info);
        fullscreenFrame=new FrameLayout(this);root.addView(fullscreenFrame,new LinearLayout.LayoutParams(-1,0,1));
        browser=new WebView(this);fullscreenFrame.addView(browser,new FrameLayout.LayoutParams(-1,-1));
        browser.setBackgroundColor(Color.BLACK);browser.getSettings().setJavaScriptEnabled(true);browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setMediaPlaybackRequiresUserGesture(true);
        browser.getSettings().setSupportMultipleWindows(false);browser.getSettings().setAllowFileAccess(false);browser.getSettings().setAllowContentAccess(false);
        CookieManager.getInstance().setAcceptCookie(true);
        browser.setWebViewClient(new WebViewClient(){
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest req) {
                if(!filtering)return null;
                String host=req.getUrl().getHost();if(host==null)return null;
                host=host.toLowerCase(java.util.Locale.ROOT);
                for(String d:blocked)if(host.equals(d)||host.endsWith("."+d)) {
                    return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));
                }
                return null;
            }
            @Override public void onPageFinished(WebView view,String url){address.setText(url);}
        });
        browser.setWebChromeClient(new WebChromeClient(){
            @Override public void onShowCustomView(View view,CustomViewCallback cb){
                if(customVideo!=null){cb.onCustomViewHidden();return;}
                customVideo=view;videoCallback=cb;browser.setVisibility(View.GONE);
                fullscreenFrame.addView(view,new FrameLayout.LayoutParams(-1,-1));
                getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
            }
            @Override public void onHideCustomView(){closeVideo();}
        });
        setContentView(root);browser.loadUrl("https://m.youtube.com/");
    }
    private void closeVideo(){if(customVideo==null)return;fullscreenFrame.removeView(customVideo);customVideo=null;
       browser.setVisibility(View.VISIBLE);getWindow().getDecorView().setSystemUiVisibility(0);
       if(videoCallback!=null){videoCallback.onCustomViewHidden();videoCallback=null;}}
    private void navigate(String raw){String s=raw.trim();if(s.isEmpty())return;
        if(!s.startsWith("https://")&&!s.startsWith("http://")){
           if(s.contains(".")&&!s.contains(" "))s="https://"+s;
           else s="https://m.youtube.com/results?search_query="+android.net.Uri.encode(s);
        }
        if(s.startsWith("https://")||s.startsWith("http://"))browser.loadUrl(s);
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(address.getWindowToken(),0);
        address.clearFocus();
    }
    @Override public void onBackPressed(){if(customVideo!=null){closeVideo();return;}if(browser.canGoBack()){browser.goBack();return;}super.onBackPressed();}
    @Override protected void onDestroy(){if(browser!=null){browser.destroy();}super.onDestroy();}
}
