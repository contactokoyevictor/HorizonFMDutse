package ng.horizonfm.horizonfm;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LinkedInFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    WebView htmlWebView;
    ProgressBar spinner;
    View view;
    private String mParam1;
    private String mParam2;

    public LinkedInFragment() {
        // Required empty public constructor
    }

    public static LinkedInFragment newInstance(String param1, String param2) {
        LinkedInFragment fragment = new LinkedInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (!MainActivity.getInstance().chechConnection()) {
            MainActivity.getInstance().snackbar = Snackbar.make(MainActivity.getInstance().findViewById(R.id.fab),
                    MainActivity.getInstance().message = "Sorry! Not connected to internet", Snackbar.LENGTH_LONG);
            MainActivity.getInstance().sbView = MainActivity.getInstance().snackbar.getView();

            MainActivity.getInstance().textView = (TextView) MainActivity.getInstance().sbView.findViewById(android.support.design.R.id.snackbar_text);
            MainActivity.getInstance().showSnack(false, MainActivity.getInstance().message = "Sorry! Not connected to internet");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_linked_in, container, false);
        htmlWebView = (WebView) view.findViewById(R.id.linkedinview);
        spinner = (ProgressBar) view.findViewById(R.id.progressBarlinkedin);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (htmlWebView.canGoBack()) {
                        htmlWebView.goBack();
                        return false;
                    } else {
                        return true;
                    }
                }
                return false;
            }
        });


        spinner.setVisibility(View.VISIBLE);
        htmlWebView.setVisibility(View.INVISIBLE);
        //other references
        htmlWebView.setWebViewClient(new LinkedInFragment.CustomWebViewClient());
        WebSettings webSetting = htmlWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);
        htmlWebView.loadUrl("https://www.linkedin.com");
        return view;
    }

    private void showCustomError(WebView view, int errorCode) {
        view.getSettings().setAppCacheEnabled(true);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setDisplayZoomControls(true);
        view.getSettings().setDomStorageEnabled(true);

        if (errorCode == 400) {
            view.loadUrl("file:///android_asset/web/pages-error-400.html");
        }
        else if (errorCode == 403) {
            view.loadUrl("file:///android_asset/web/pages-error-403.html");
        }
        else if (errorCode == 404) {
            view.loadUrl("file:///android_asset/web/pages-error-404.html");
        }
        else if (errorCode == 500) {
            view.loadUrl("file:///android_asset/web/pages-error-500.html");
        }
        else if (errorCode == 503) {
            view.loadUrl("file:///android_asset/web/pages-error-503.html");
        }
        else {
            view.loadUrl("file:///android_asset/web/pages-error-400.html");
        }

    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            showCustomError(view, errorCode);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            spinner.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }

        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && htmlWebView.canGoBack()) {
                htmlWebView.goBack();
                return true;
            }
            return false;
        }
    }


}