package app.experimento.kairou;

import android.app.Activity;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.Toast;
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
        "adservice.google.com",
        "adform.net",
        "taboola.com",
        "outbrain.com",
        "popads.net",
        "propellerads.com"
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

    private void voltar() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            Toast.makeText(this, "Não há página anterior", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout layout = new FrameLayout(this);
        layout.setBackgroundColor(Color.BLACK);

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
settings.setSupportMultipleWindows(false);

                webView.setWebViewClient(new WebViewClient() {
            private boolean isKairou(String url) {
                String host = Uri.parse(url).getHost();
                return host != null &&
                    (host.equalsIgnoreCase("kairou.space") ||
                     host.toLowerCase().endsWith(".kairou.space"));
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (!"https".equalsIgnoreCase(uri.getScheme()) &&
                    !"http".equalsIgnoreCase(uri.getScheme())) {
                    return true;
                }
                if (!request.isForMainFrame()) return false;
                return !isKairou(uri.toString());
            }

            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (!"https".equalsIgnoreCase(uri.getScheme()) &&
                    !"http".equalsIgnoreCase(uri.getScheme())) {
                    return true;
                }
                return !isKairou(url);
            }

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

        layout.addView(webView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));

        FrameLayout.LayoutParams progressParams =
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, 8, Gravity.TOP);
        layout.addView(progressBar, progressParams);

                Button backButton = new Button(this);
        backButton.setText("‹");
        backButton.setTextSize(32);
        backButton.setTextColor(Color.WHITE);
        backButton.setPadding(0, 0, 0, 5);
        backButton.setAllCaps(false);
        backButton.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(
                Color.argb(175, 25, 110, 190)));
        backButton.setElevation(8f);
        backButton.setOnClickListener(v -> voltar());

        int buttonSize = (int) (44 * getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams buttonParams =
            new FrameLayout.LayoutParams(
                buttonSize, buttonSize, Gravity.BOTTOM | Gravity.START);
        buttonParams.setMargins(
            (int)(10 * getResources().getDisplayMetrics().density), 0, 0,
            (int)(18 * getResources().getDisplayMetrics().density));
        layout.addView(backButton, buttonParams);

        setContentView(layout);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                progressBar.setVisibility(
                    progress >= 100 ? View.GONE : View.VISIBLE);
            }
        });

        webView.loadUrl("https://www.kairou.space/");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
