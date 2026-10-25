
package app.experimento.kairou;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Button;
import android.widget.Toast;
import android.content.res.ColorStateList;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private Button backButton;

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
        if (url == null) return false;

        Uri uri = Uri.parse(url);
        String host = uri.getHost();

        if (host == null) return false;

        host = host.toLowerCase();

        for (String domain : adDomains) {
            if (host.equals(domain) || host.endsWith("." + domain)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKairou(String url) {
        if (url == null) return false;

        Uri uri = Uri.parse(url);
        String host = uri.getHost();

        return host != null &&
            (host.equalsIgnoreCase("kairou.space") ||
             host.toLowerCase().endsWith(".kairou.space"));
    }

    private void voltar() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            Toast.makeText(
                this,
                "Não há página anterior",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        FrameLayout layout = new FrameLayout(this);
        layout.setBackgroundColor(Color.BLACK);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.BLACK);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(false);
        settings.setTextZoom(120);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMediaPlaybackRequiresUserGesture(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {

                Uri uri = request.getUrl();
                String scheme = uri.getScheme();

                if (!"https".equalsIgnoreCase(scheme) &&
                    !"http".equalsIgnoreCase(scheme)) {
                    return true;
                }

                if (!request.isForMainFrame()) {
                    return false;
                }

                return !isKairou(uri.toString());
            }

            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    String url) {

                Uri uri = Uri.parse(url);
                String scheme = uri.getScheme();

                if (!"https".equalsIgnoreCase(scheme) &&
                    !"http".equalsIgnoreCase(scheme)) {
                    return true;
                }

                return !isKairou(url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(
                    WebView view,
                    WebResourceRequest request) {

                if (isAdUrl(request.getUrl().toString())) {
                    return new WebResourceResponse(
                        "text/plain",
                        "UTF-8",
                        new ByteArrayInputStream(new byte[0])
                    );
                }

                return super.shouldInterceptRequest(view, request);
            }
        });

        progressBar = new ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        );

        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setProgressTintList(
            ColorStateList.valueOf(Color.rgb(35, 145, 255))
        );

        FrameLayout.LayoutParams webParams =
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            );

        layout.addView(webView, webParams);

        FrameLayout.LayoutParams progressParams =
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                6,
                Gravity.TOP
            );

        layout.addView(progressBar, progressParams);

        backButton = new Button(this);
        backButton.setText("‹");
        backButton.setTextSize(36);
        backButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        backButton.setTextColor(Color.WHITE);
        backButton.setGravity(Gravity.CENTER);
        backButton.setPadding(0, 0, 0, 4);
        backButton.setAllCaps(false);
        backButton.setFocusable(true);
        backButton.setFocusableInTouchMode(true);
        backButton.setContentDescription("Voltar à página anterior");

        GradientDrawable buttonBackground = new GradientDrawable();
        buttonBackground.setShape(GradientDrawable.RECTANGLE);
        buttonBackground.setColor(Color.rgb(25, 110, 190));
        buttonBackground.setCornerRadius(
            14 * getResources().getDisplayMetrics().density
        );

        backButton.setBackground(buttonBackground);
        backButton.setElevation(10f);

        backButton.setOnFocusChangeListener((view, hasFocus) -> {
            view.setAlpha(hasFocus ? 1.0f : 0.85f);
            view.setScaleX(hasFocus ? 1.08f : 1.0f);
            view.setScaleY(hasFocus ? 1.08f : 1.0f);
        });

        backButton.setOnClickListener(v -> {
            voltar();
            webView.requestFocus();
        });

        float density = getResources().getDisplayMetrics().density;
        int buttonSize = (int) (60 * density);
        int margin = (int) (24 * density);

        FrameLayout.LayoutParams buttonParams =
            new FrameLayout.LayoutParams(
                buttonSize,
                buttonSize,
                Gravity.BOTTOM | Gravity.START
            );

        buttonParams.setMargins(margin, 0, 0, margin);
        layout.addView(backButton, buttonParams);

        setContentView(layout);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                progressBar.setVisibility(
                    progress >= 100 ? View.GONE : View.VISIBLE
                );
            }
        });

        webView.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP &&
                keyCode == KeyEvent.KEYCODE_BACK) {
                voltar();
                return true;
            }

            return false;
        });

        backButton.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP &&
                keyCode == KeyEvent.KEYCODE_BACK) {
                voltar();
                return true;
            }

            return false;
        });

        webView.requestFocus();
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

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }
}
