package de.lbader.apps.ekgr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GalleryViewer extends AppCompatActivity
        implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private SliderLayout sliderShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_viewer);

        sliderShow = (SliderLayout) findViewById(R.id.gallery_slider);

        Bundle args = getIntent().getExtras();
        for (String url : args.getStringArrayList("images")) {
            DefaultSliderView imgSliderView = new DefaultSliderView(this);
            imgSliderView.image(url);
            imgSliderView.setScaleType(BaseSliderView.ScaleType.CenterInside);
            ((ImageView) imgSliderView.getView().findViewById(R.id.daimajia_slider_image)).
                    setBackgroundColor(Color.WHITE);
            sliderShow.addSlider(imgSliderView);
        }

        setTitle(args.getString("title").trim());

        int position = args.getInt("position");
        // sliderShow.addOnPageChangeListener(this);
        sliderShow.setCurrentPosition(position, false);
        sliderShow.stopAutoCycle();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
}
