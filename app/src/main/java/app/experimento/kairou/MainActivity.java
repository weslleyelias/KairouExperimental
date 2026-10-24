package app.experimento.kairou;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Color;
import android.view.View;
import android.view.Gravity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.net.Uri;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    private WebView webView;
    private ProgressBar progressBar;

    private final List<String> adDomains = Arrays.asList(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "googletagservices.com",
        "adnxs.com",
        "amazon-adsystem.com",
        "adservice.google.com"
    );

    private boolean isAdUrl(String url) {
        String host = Uri.parse(url).getHost();
        if (host == null) return false;
        host = host.toLowerCase();
        for (String domain : adDomains) {
            if (host.equals(domain) || host.endsWith("." + domain)) {
                return true;
            }
        }
        return false;
    }

    private void goBackOrExit() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout layout = new FrameLayout(this);
        layout.setBackgroundColor(Color.WHITE);

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(
                    WebView view, WebResourceRequest request) {
                if (isAdUrl(request.getUrl().toString())) {
                    return new WebResourceResponse(
                        "text/plain", "UTF-8",
                        new ByteArrayInputStream(new byte[0]));
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

        progressBar = new ProgressBar(
            this, null, android.R.attr.progressBarStyleHorizontal);

        FrameLayout.LayoutParams progressParams =
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, 8, Gravity.TOP);

        layout.addView(webView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        layout.addView(progressBar, progressParams);
        setContentView(layout);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                progressBar.setVisibility(
                    progress >= 100 ? View.GONE : View.VISIBLE);
            }
        });

        if (Build.VERSION.SDK_INT >= 33) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                new OnBackInvokedCallback() {
                    @Override
                    public void onBackInvoked() {
                        goBackOrExit();
                    }
                });
        }

        webView.loadUrl("https://www.kairou.space/");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        goBackOrExit();
    }
}
