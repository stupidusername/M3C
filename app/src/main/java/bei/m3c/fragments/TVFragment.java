package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import bei.m3c.R;
import bei.m3c.adapters.ChannelCategoryAdapter;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.ChannelCategory;

/**
 * TV fragment
 */
public class TVFragment extends Fragment {

    // Max number in number grid layout
    private static final int NUMBER_MAX = 9;

    private ListView listView;
    private ImageButton powerButton;
    private ImageButton plusButton;
    private ImageButton upButton;
    private ImageButton minusButton;
    private ImageButton downButton;
    private ImageButton muteButton;
    private Button infoButton;
    private GridLayout numberGridLayout;
    private Button[] numberButtons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tv, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.tv_listview);
        powerButton = (ImageButton) view.findViewById(R.id.tv_power_button);
        plusButton = (ImageButton) view.findViewById(R.id.tv_plus_button);
        upButton = (ImageButton) view.findViewById(R.id.tv_up_button);
        minusButton = (ImageButton) view.findViewById(R.id.tv_minus_button);
        downButton = (ImageButton) view.findViewById(R.id.tv_down_button);
        muteButton = (ImageButton) view.findViewById(R.id.tv_mute_button);
        infoButton = (Button) view.findViewById(R.id.tv_info_button);
        numberGridLayout = (GridLayout) view.findViewById(R.id.tv_number_gridlayout);

        // Set up number buttons
        numberButtons = new Button[NUMBER_MAX + 1];
        for (int number = 0; number <= NUMBER_MAX; number++) {
            String viewName = "tv_" + number + "_button";
            int viewId = getResources().getIdentifier(viewName, "id", getContext().getPackageName());
            numberButtons[number] = (Button) view.findViewById(viewId);
            ThemeHelper.setButtonTheme(numberButtons[number]);
        }

        // Load demo data to the channel category list view
        ArrayList<ChannelCategory> categories = new ArrayList<>();
        categories.add(new ChannelCategory(1, "Adultos"));
        categories.add(new ChannelCategory(1, "Nacionales"));
        categories.add(new ChannelCategory(1, "Noticias"));
        categories.add(new ChannelCategory(1, "Deportes"));
        categories.add(new ChannelCategory(1, "Infantiles"));
        categories.add(new ChannelCategory(1, "Cine"));
        categories.add(new ChannelCategory(1, "Variedades"));
        categories.add(new ChannelCategory(1, "Series"));
        ChannelCategoryAdapter adapter = new ChannelCategoryAdapter(getContext(), R.layout.listview_row, categories);
        listView.setAdapter(adapter);

        ThemeHelper.setImageButtonTheme(powerButton);
        ThemeHelper.setImageButtonTheme(plusButton);
        ThemeHelper.setImageButtonTheme(upButton);
        ThemeHelper.setImageButtonTheme(minusButton);
        ThemeHelper.setImageButtonTheme(downButton);
        ThemeHelper.setImageButtonTheme(muteButton);
        ThemeHelper.setButtonTheme(infoButton);
        ThemeHelper.setColorStateListTheme(numberGridLayout);
    }
}
