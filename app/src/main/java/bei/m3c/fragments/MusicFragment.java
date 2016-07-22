package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;

import bei.m3c.R;
import bei.m3c.adapters.RadioAdapter;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.Radio;

/**
 * Music fragment
 */
public class MusicFragment extends Fragment {

    private ListView radiosListView;
    private ImageButton playPauseButton;
    private ImageButton previousButton;
    private ImageButton stopButton;
    private ImageButton nextButton;
    private ImageButton volumeButton;
    private SeekBar timeSeekbar;
    private SeekBar volumeSeekbar;

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
        timeSeekbar = (SeekBar) view.findViewById(R.id.music_time_seekbar);
        volumeSeekbar = (SeekBar) view.findViewById(R.id.music_volume_seekbar);

        // Load demo data to the radio list view
        ArrayList<Radio> radios = new ArrayList<>();
        radios.add(new Radio(1, "Rock"));
        radios.add(new Radio(1, "Pop"));
        radios.add(new Radio(1, "Electrónica"));
        radios.add(new Radio(1, "Clásicos"));
        radios.add(new Radio(1, "Latino"));
        radios.add(new Radio(1, "Reggae"));
        radios.add(new Radio(1, "Baladas"));
        radios.add(new Radio(1, "Jazz"));
        radios.add(new Radio(1, "Hip-Hop"));
        radios.add(new Radio(1, "Tango"));
        radios.add(new Radio(1, "Blues"));
        radios.add(new Radio(1, "80s"));
        radios.add(new Radio(1, "90s"));
        RadioAdapter adapter = new RadioAdapter(getContext(), R.layout.listview_row, radios);
        radiosListView.setAdapter(adapter);
        radiosListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ThemeHelper.setImageButtonTheme(playPauseButton);
        ThemeHelper.setImageButtonTheme(previousButton);
        ThemeHelper.setImageButtonTheme(stopButton);
        ThemeHelper.setImageButtonTheme(nextButton);
        ThemeHelper.setImageButtonTheme(volumeButton);
        ThemeHelper.setSeekBarTheme(timeSeekbar);
        ThemeHelper.setSeekBarTheme(volumeSeekbar);
    }
}
