package bei.m3c.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import bei.m3c.R;
import bei.m3c.adapters.ViewPagerAdapter;
import bei.m3c.fragments.ACFragment;
import bei.m3c.fragments.BarFragment;
import bei.m3c.fragments.BillFragment;
import bei.m3c.fragments.LightsFragment;
import bei.m3c.fragments.MusicFragment;
import bei.m3c.fragments.TVFragment;
import bei.m3c.helpers.ThemeHelper;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // Set tab layout according to the color theme
        tabLayout.setSelectedTabIndicatorColor(ThemeHelper.getAccentColor());
        LayerDrawable layerDrawable = (LayerDrawable) getDrawable(R.drawable.tablayout_background);
        Drawable tabLayoutUnderline = layerDrawable.getDrawable(0);
        tabLayoutUnderline.setColorFilter(ThemeHelper.getDarkAccentColor(), PorterDuff.Mode.SRC);
        tabLayout.setBackground(layerDrawable);
        // Add tabs
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MusicFragment(), getString(R.string.music_title));
        adapter.addFragment(new TVFragment(), getString(R.string.tv_title));
        adapter.addFragment(new LightsFragment(), getString(R.string.lights_title));
        adapter.addFragment(new ACFragment(), getString(R.string.ac_title));
        adapter.addFragment(new BarFragment(), getString(R.string.bar_title));
        adapter.addFragment(new BillFragment(), getString(R.string.bill_title));
        viewPager.setAdapter(adapter);
    }

}
