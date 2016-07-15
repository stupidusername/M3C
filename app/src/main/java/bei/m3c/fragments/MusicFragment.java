package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import bei.m3c.R;

/**
 * Music fragment
 */
public class MusicFragment extends Fragment {

    private ListView radiosListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radiosListView = (ListView) view.findViewById(R.id.media_listview);

        // Load demo data to the radio list view
        String[] radios = {"Rock", "Pop", "Electrónica", "Clásicos",
                "Latino", "Reggae", "Baladas", "Jazz", "Hip-Hop", "Tango", "80s", "90s"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, radios);
        radiosListView.setAdapter(adapter);
    }
}
