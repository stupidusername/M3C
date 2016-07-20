package bei.m3c.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        radios.add(new Radio(1, "80s"));
        radios.add(new Radio(1, "90s"));
        RadioAdapter adapter = new RadioAdapter(getContext(), R.layout.listview_row, radios);
        radiosListView.setAdapter(adapter);
        radiosListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        timeSeekbar.setProgressTintList(ColorStateList.valueOf(ThemeHelper.getAccentColor()));
        timeSeekbar.setThumbTintList(ColorStateList.valueOf(ThemeHelper.getAccentColor()));
        volumeSeekbar.setProgressTintList(ColorStateList.valueOf(ThemeHelper.getAccentColor()));
        volumeSeekbar.setThumbTintList(ColorStateList.valueOf(ThemeHelper.getAccentColor()));
    }
}
