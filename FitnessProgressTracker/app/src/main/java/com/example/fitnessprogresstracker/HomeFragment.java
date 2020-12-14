package com.example.fitnessprogresstracker;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private WebView webView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        webView = (WebView) view.findViewById(R.id.wvCalorieCalc);

        webView.loadUrl("https://www.calculator.net/calorie-calculator.html");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());


        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:document.getElementsByTagName('input')[1].value = '" + 22 + "';" +
                        "document.getElementsByTagName('input')[2].checked = '" + true + "';" +
                        "document.getElementsByTagName('input')[4].value = '" + 5 + "';" +
                        "document.getElementsByTagName('input')[5].value = '" + 7 + "';" +
                        "document.getElementsByTagName('input')[6].value = '" + 141 + "';" +
                        "document.getElementById('cactivity').options.selectedIndex = '" + 4 + "';" +
                        "document.getElementsByTagName('input')[24].click();");

                super.onPageFinished(view, url);
                // By 'input' tag: Age: [1], Male: [2] as 'm', female: [3] as 'f', Feet: [4], Inches: [5], Weight: [6]
                // document.getElementsByTagName('input')[7].click();"
                //javascript:document.getElementsByName('cage').value = '"+age+"';
            }
        });




        // Inflate the layout for this fragment
        return view;
    }
}