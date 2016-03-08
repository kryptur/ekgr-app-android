package de.lbader.apps.ekgr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import de.lbader.apps.ekgr.imageviewer.ImageViewer;

/**
 * Created by lbader on 3/5/16.
 */
public class WebAppInterface {
    Context mContext;

    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void loadGallery(String images, int position, String title) {
        String[] ids = images.split(";");
        ArrayList<String> urls = new ArrayList<>();
        for (String id : ids) {
            urls.add("http://ekgr.de/gallery_system/system/" + id + "/large.JPG");
        }
        Intent intent = new Intent(mContext, ImageViewer.class);
        Bundle b = new Bundle();
        b.putInt("position", position);
        b.putStringArrayList("urls", urls);
        b.putString("title", title);
        intent.putExtras(b);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void gemeindegruss(String murls) {
        String[] urlArr = murls.split(";");
        ArrayList<String> urls = new ArrayList<>();
        for (String url : urlArr) {
            urls.add("http://ekgr.de/" + url);
        }
        Intent intent = new Intent(mContext, ImageViewer.class);
        Bundle b = new Bundle();
        b.putInt("position", 0);
        b.putStringArrayList("urls", urls);
        b.putString("title", "Gemeindegruss");
        intent.putExtras(b);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void shareNews(String title, String text, String created, String id) {
        String toShare = title.trim() + "\n" + text.trim() + "\n\n" + "http://ekgr.de/news_" + id + ".aspx";
        Intent send = new Intent();
        send.setAction(Intent.ACTION_SEND);
        send.putExtra(Intent.EXTRA_TEXT, toShare);
        send.setType("text/plain");
        mContext.startActivity(send);
    }

    @JavascriptInterface
    public void showImage(String src, String title) {
        if (title.equals("")) {
            title = mContext.getString(R.string.app_name);
        }
        ArrayList<String> urls = new ArrayList<>();
        urls.add(src);
        Intent intent = new Intent(mContext, ImageViewer.class);
        Bundle b = new Bundle();
        b.putInt("position", 0);
        b.putStringArrayList("urls", urls);
        b.putString("title", title);
        intent.putExtras(b);
        mContext.startActivity(intent);
    }
}
