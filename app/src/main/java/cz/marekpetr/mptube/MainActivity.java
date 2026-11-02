package cz.marekpetr.mptube;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.Context;
import android.view.View;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import android.net.Uri;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    private WebView browser;
    private EditText address;
    private boolean filtering;
    private FrameLayout fullscreenFrame;
    private View customVideo;
    private WebChromeClient.CustomViewCallback videoCallback;
    private final Set<String> blocked = new HashSet<>(Arrays.asList(
        "doubleclick.net", "googlesyndication.com", "googleadservices.com",
        "adservice.google.com", "googletagservices.com", "scorecardresearch.com",
        "taboola.com", "outbrain.com", "adsrvr.org"
    ));

    private int dp(float value) {
        return (int)(getResources().getDisplayMetrics().density * value + .5f);
    }

    private TextView button(String text, LinearLayout parent, Runnable action) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(0xffffd36a);
        view.setTextSize(23);
        view.setGravity(Gravity.CENTER);
        parent.addView(view, new LinearLayout.LayoutParams(dp(44), dp(52)));
        view.setOnClickListener(v -> action.run());
        return view;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(0xff0b1020);
        getWindow().setNavigationBarColor(0xff0b1020);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xff0b1020);

        TextView brand = new TextView(this);
        brand.setText("▶  MP-TUBE   •   Created by Marek Petr");
        brand.setTextColor(0xffffd36a);
        brand.setTypeface(null, Typeface.BOLD);
        brand.setTextSize(15);
        brand.setPadding(dp(14), dp(12), dp(8), dp(12));
        root.addView(brand);

        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(bar);
        button("⌂", bar, () -> browser.loadUrl("https://m.youtube.com/"));
        button("‹", bar, () -> { if (browser.canGoBack()) browser.goBack(); });
        button("›", bar, () -> { if (browser.canGoForward()) browser.goForward(); });

        address = new EditText(this);
        address.setSingleLine(true);
        address.setTextColor(Color.WHITE);
        address.setHintTextColor(0xff9aa4bc);
        address.setHint("Hledat video nebo vložit adresu");
        address.setTextSize(13);
        address.setSelectAllOnFocus(true);
        address.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
            android.text.InputType.TYPE_TEXT_VARIATION_URI);
        bar.addView(address, new LinearLayout.LayoutParams(0, dp(48), 1));
        address.setOnEditorActionListener((v, actionId, event) -> {
            navigate(address.getText().toString());
            return true;
        });
        button("↻", bar, () -> browser.reload());

        LinearLayout controls = new LinearLayout(this);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(dp(12), 0, dp(10), 0);
        root.addView(controls);
        filtering = getPreferences(MODE_PRIVATE).getBoolean("filter_enabled", true);
        Switch filterSwitch = new Switch(this);
        filterSwitch.setText("Filtr reklamních domén");
        filterSwitch.setTextColor(0xffc7d3e7);
        filterSwitch.setTextSize(12);
        filterSwitch.setChecked(filtering);
        controls.addView(filterSwitch, new LinearLayout.LayoutParams(0, dp(42), 1));
        TextView note = new TextView(this);
        note.setText("Bez záruky");
        note.setTextSize(10);
        note.setTextColor(0xff9aa4bc);
        controls.addView(note);
        filterSwitch.setOnCheckedChangeListener((v, enabled) -> {
            filtering = enabled;
            getPreferences(MODE_PRIVATE).edit().putBoolean("filter_enabled", enabled).apply();
            if (browser != null) browser.reload();
        });

        fullscreenFrame = new FrameLayout(this);
        root.addView(fullscreenFrame, new LinearLayout.LayoutParams(-1, 0, 1));
        browser = new WebView(this);
        fullscreenFrame.addView(browser, new FrameLayout.LayoutParams(-1, -1));
        browser.setBackgroundColor(Color.BLACK);
        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setSupportMultipleWindows(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        CookieManager.getInstance().setAcceptCookie(true);

        browser.setWebViewClient(new WebViewClient() {
            @Override public WebResourceResponse shouldInterceptRequest(
                WebView view, WebResourceRequest request) {
                if (!filtering) return null;
                String host = request.getUrl().getHost();
                if (host == null) return null;
                host = host.toLowerCase(java.util.Locale.ROOT);
                for (String domain : blocked) {
                    if (host.equals(domain) || host.endsWith("." + domain)) {
                        return new WebResourceResponse("text/plain", "UTF-8",
                            new ByteArrayInputStream(new byte[0]));
                    }
                }
                return null;
            }
            @Override public void onPageFinished(WebView view, String url) {
                address.setText(url);
            }
        });
        browser.setWebChromeClient(new WebChromeClient() {
            @Override public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customVideo != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                customVideo = view;
                videoCallback = callback;
                browser.setVisibility(View.GONE);
                fullscreenFrame.addView(view, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
            }
            @Override public void onHideCustomView() { closeVideo(); }
        });

        FrameLayout screen = new FrameLayout(this);
        screen.addView(root, new FrameLayout.LayoutParams(-1, -1));
        FrameLayout splash = new FrameLayout(this);
        splash.setBackgroundColor(0xff080b13);
        TextView signature = new TextView(this);
        signature.setText("Created by\nMarek Petr");
        signature.setTextColor(0xffffd36a);
        signature.setTextSize(31);
        signature.setTypeface(null, Typeface.BOLD);
        signature.setGravity(Gravity.CENTER);
        signature.setShadowLayer(dp(18), 0, 0, 0xffffb300);
        splash.addView(signature, new FrameLayout.LayoutParams(-1, -1));
        screen.addView(splash, new FrameLayout.LayoutParams(-1, -1));
        setContentView(screen);

        signature.setAlpha(0f);
        signature.setScaleX(.72f);
        signature.setScaleY(.72f);
        signature.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(850).start();
        splash.postDelayed(() -> splash.animate().alpha(0f).setDuration(550)
            .withEndAction(() -> screen.removeView(splash)).start(), 2100);
        browser.loadUrl("https://m.youtube.com/");
    }

    private void closeVideo() {
        if (customVideo == null) return;
        fullscreenFrame.removeView(customVideo);
        customVideo = null;
        browser.setVisibility(View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(0);
        if (videoCallback != null) {
            videoCallback.onCustomViewHidden();
            videoCallback = null;
        }
    }

    private void navigate(String raw) {
        String value = raw.trim();
        if (value.isEmpty()) return;
        if (!value.startsWith("https://") && !value.startsWith("http://")) {
            if (value.contains(".") && !value.contains(" "))
                value = "https://" + value;
            else
                value = "https://m.youtube.com/results?search_query=" + Uri.encode(value);
        }
        Uri uri = Uri.parse(value);
        if ("https".equalsIgnoreCase(uri.getScheme())) browser.loadUrl(value);
        InputMethodManager keyboard =
            (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (keyboard != null) keyboard.hideSoftInputFromWindow(address.getWindowToken(), 0);
        address.clearFocus();
    }

    @Override public void onBackPressed() {
        if (customVideo != null) { closeVideo(); return; }
        if (browser.canGoBack()) { browser.goBack(); return; }
        super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (browser != null) {
            browser.stopLoading();
            browser.destroy();
        }
        super.onDestroy();
    }
}
