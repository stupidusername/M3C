package bei.m3c.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import bei.m3c.R;
import bei.m3c.adapters.ViewPagerAdapter;
import bei.m3c.fragments.BarFragment;
import bei.m3c.fragments.InfoFragment;
import bei.m3c.fragments.LightsACFragment;
import bei.m3c.fragments.MusicFragment;
import bei.m3c.fragments.TVFragment;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.helpers.ThemeHelper;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.appcompat.BuildConfig;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button preferencesButton;

    private AlertDialog preferencesAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        preferencesButton = (Button) findViewById(R.id.preferences_button);

        setupViewPager(viewPager);

        ThemeHelper.setTabLayoutTheme(tabLayout);

        // Add tabs
        tabLayout.setupWithViewPager(viewPager);

        // Set up preferences alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.version) + " " + PreferencesHelper.getAppVersion(this);
        builder.setTitle(title);
        builder.setView(R.layout.dialog_preferences);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Launch preferences activity if password is correct
                EditText passwordEditText = (EditText) ((AlertDialog) dialog).findViewById(R.id.preferences_dialog_password_edittext);
                if (passwordEditText.getText().toString().equals(PreferencesHelper.PASSWORD)) {
                    Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.wrong_password),
                            Toast.LENGTH_SHORT).show();
                }
                passwordEditText.getText().clear();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        preferencesAlertDialog = builder.create();

        // Set up preferences button
        preferencesButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                preferencesAlertDialog.show();
                return false;
            }
        });
    }


    // Use immersive mode
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
        adapter.addFragment(new LightsACFragment(), getString(R.string.lights_ac_title));
        adapter.addFragment(new BarFragment(), getString(R.string.bar_title));
        adapter.addFragment(new InfoFragment(), getString(R.string.info_title));
        viewPager.setAdapter(adapter);
    }

}
