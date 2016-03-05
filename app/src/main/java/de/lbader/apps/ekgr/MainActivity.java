package de.lbader.apps.ekgr;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public WebView myWebView;
    private SwipeRefreshLayout swipeContainer;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    public JSONArray navigation;
    private int lvl1, lvl2, level;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        myWebView = (WebView) findViewById(R.id.webView);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        myWebView.addJavascriptInterface(new WebAppInterface(this), "App");

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("EKGR-App");
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.setWebViewClient(new WebViewClient(){
            private int running = 0;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                running++;
                myWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                running = Math.max(running, 1);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (--running == 0) {
                    swipeContainer.setRefreshing(true);
                }
            }
        });

        myWebView.loadUrl("http://ekgr.de/app.aspx");

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myWebView.reload();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setEnabled(false);
        /*
        myWebView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (myWebView.getScrollY() == 0) {
                    swipeContainer.setEnabled(true);
                } else {
                    swipeContainer.setEnabled(false);
                }
            }
        });
         */

        navigation = null;
        new RequestTask("navi", new RequestTask.RequestCallback() {
            @Override
            public void callback(JSONObject res) {
                if (res != null) {
                    try {
                        navigation = res.getJSONArray("data");
                        lvl1 = 0;
                        lvl2 = 0;
                        level = 0;
                        showNavi();
                    } catch (JSONException ex) {
                        Log.e("JSON", ex.getMessage());
                    }
                }
            }
        }).execute("");

    }

    private void showNavi() {
        String[] entries;
        ArrayList<String> list = new ArrayList<>();
        JSONArray elements = navigation;
        try {
            switch (level) {
                case 0:
                    elements = navigation;
                    break;
                case 1:
                    elements = navigation.getJSONObject(lvl1).getJSONArray("children");
                    break;
                case 2:
                    elements = navigation.getJSONObject(lvl1).getJSONArray("children").getJSONObject(lvl2).getJSONArray("children");
            }

            for (int i = 0; i < elements.length(); ++i) {
                list.add(elements.getJSONObject(i).getString("title").replace("<br />", "\n"));
            }
            if (level > 0) {
                list.add(0, "Zurück");
            }
        } catch (JSONException ex) {
            Log.e("JSON", ex.getMessage());
        }


        entries = list.toArray(new String[list.size()]);

        mNavigationDrawerFragment.mDrawerListView.setAdapter(new ArrayAdapter<String>(
                mNavigationDrawerFragment.themedContext,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                entries
        ));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (navigation == null) {
            return;
        }

        boolean close = false;
        JSONObject elem = null;
        try {
            switch (level) {
                case 0:
                    elem = navigation.getJSONObject(position);
                    lvl1 = position;
                    break;
                case 1:
                    if (position == 0) {
                        elem = null;
                        level = 0;
                    } else {
                        elem = navigation.getJSONObject(lvl1).getJSONArray("children").getJSONObject(position - 1);
                        lvl2 = position - 1;
                    }
                    break;
                case 2:
                    if (position == 0) {
                        elem = null;
                        level = 1;
                    } else {
                        elem = navigation.getJSONObject(lvl1).getJSONArray("children").getJSONObject(lvl2).getJSONArray("children").getJSONObject(position - 1);
                    }
                    break;
            }

            if (elem != null) {
                String link = elem.getString("link");
                if (!link.equals("#")) {
                    myWebView.loadUrl("http://ekgr.de/" + link);

                    if (mNavigationDrawerFragment.mDrawerLayout != null) {
                        mNavigationDrawerFragment.mDrawerLayout.closeDrawer(mNavigationDrawerFragment.mFragmentContainerView);
                    }
                } else {
                    level++;
                }
            }
            showNavi();

        } catch (JSONException ex) {
            Log.e("JSON", ex.getMessage());
        }



    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("App beenden?")
                    .setMessage("Wollen Sie die App wirklich beenden?")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Nein", null)
                    .show();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }



}
