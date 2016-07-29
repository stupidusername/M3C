package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import bei.m3c.R;
import bei.m3c.adapters.RadioAdapterBase;
import bei.m3c.events.GetRadiosEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.jobs.GetRadiosJob;
import bei.m3c.models.Radio;
import bei.m3c.players.MusicPlayer;

/**
 * Music fragment
 */
public class MusicFragment extends Fragment {

    public static final int DEFAULT_RADIO_POSITION = 0;

    // views
    private ListView radiosListView;
    private ImageButton playPauseButton;
    private ImageButton previousButton;
    private ImageButton stopButton;
    private ImageButton nextButton;
    private ImageButton volumeButton;
    private TextView timeElapsedTextView;
    private TextView timeRemainingTextView;
    private SeekBar timeSeekbar;
    private SeekBar volumeSeekbar;
    // adapters
    private RadioAdapterBase radioAdapter;

    @Override
    public void onDestroyView() {
        JobManagerHelper.cancelJobsInBackground(GetRadiosJob.TAG);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radiosListView = (ListView) view.findViewById(R.id.music_listview);
        playPauseButton = (ImageButton) view.findViewById(R.id.music_play_pause_button);
        previousButton = (ImageButton) view.findViewById(R.id.music_previous_button);
        stopButton = (ImageButton) view.findViewById(R.id.music_stop_button);
        nextButton = (ImageButton) view.findViewById(R.id.music_next_button);
        volumeButton = (ImageButton) view.findViewById(R.id.music_volume_button);
        timeElapsedTextView = (TextView) view.findViewById(R.id.music_time_elapsed_textview);
        timeRemainingTextView = (TextView) view.findViewById(R.id.music_time_remaining_textview);
        timeSeekbar = (SeekBar) view.findViewById(R.id.music_time_seekbar);
        volumeSeekbar = (SeekBar) view.findViewById(R.id.music_volume_seekbar);

        // Set default texts
        timeElapsedTextView.setText(getString(R.string.time_default));
        timeRemainingTextView.setText(getString(R.string.time_default));

        // Set radio adapter
        radioAdapter = new RadioAdapterBase(getLayoutInflater(savedInstanceState));
        radiosListView.setAdapter(radioAdapter);
        radiosListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ThemeHelper.setImageButtonTheme(playPauseButton);
        ThemeHelper.setImageButtonTheme(previousButton);
        ThemeHelper.setImageButtonTheme(stopButton);
        ThemeHelper.setImageButtonTheme(nextButton);
        ThemeHelper.setImageButtonTheme(volumeButton);
        ThemeHelper.setSeekBarTheme(timeSeekbar);
        ThemeHelper.setSeekBarTheme(volumeSeekbar);

        EventBus.getDefault().register(this);
        JobManagerHelper.getJobManager().addJobInBackground(new GetRadiosJob());

        // Set UI listeners
        radiosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Test", "SARASA");
                // Change selected radio if no previous one was selected or if the radio id is different
                Radio selectedRadio = radioAdapter.getItem(position);
                Radio currentRadio = MusicPlayer.getInstance().getRadio();
                if (currentRadio == null || selectedRadio.id != currentRadio.id) {
                    MusicPlayer.getInstance().selectRadio(selectedRadio);
                }
            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Play current selected radio or choose the first one if none of them was selected
                Radio currentRadio = MusicPlayer.getInstance().getRadio();
                if (currentRadio == null) {
                    if(!radioAdapter.isEmpty()) {
                        Radio newRadio = radioAdapter.getItem(DEFAULT_RADIO_POSITION);
                        radiosListView.setItemChecked(DEFAULT_RADIO_POSITION, true);
                        MusicPlayer.getInstance().selectRadio(newRadio);
                    }
                } else {
                    MusicPlayer.getInstance().play();
                }
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playPrevious();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().clearSelectedRadio();
                radiosListView.clearChoices();
                radioAdapter.notifyDataSetChanged();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playNext();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetRadiosEvent event) {
        radioAdapter.replaceList(event.radios);
    }
}
