package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import bei.m3c.R;
import bei.m3c.adapters.ChannelAdapter;
import bei.m3c.adapters.ChannelCategoryAdapter;
import bei.m3c.commands.TRCChannelDownCommand;
import bei.m3c.commands.TRCChannelUpCommand;
import bei.m3c.commands.TRCDigitCommand;
import bei.m3c.commands.TRCInfoCommand;
import bei.m3c.commands.TRCMacroCommand;
import bei.m3c.commands.TRCSetVideoTypeCommand;
import bei.m3c.commands.TRCVideoOnOffCommand;
import bei.m3c.commands.TRCVideoSourceCommand;
import bei.m3c.commands.TRCVolumeDownCommand;
import bei.m3c.commands.TRCVolumeUpCommand;
import bei.m3c.events.GetChannelCategoriesEvent;
import bei.m3c.events.GetChannelsEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.PICConnectionHelper;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.interfaces.FragmentInterface;
import bei.m3c.jobs.GetChannelCategoriesJob;
import bei.m3c.jobs.GetChannelsJob;
import bei.m3c.models.Channel;
import bei.m3c.models.ChannelCategory;
import bei.m3c.widgets.ToastWidget;

/**
 * TV fragment
 */
public class TVFragment extends Fragment implements FragmentInterface {

    // Max number in number grid layout
    public static final int NUMBER_MAX = 9;

    // Views
    private LinearLayout listViewHeaderLayout;
    private ListView listView;
    private ProgressBar listViewLoadingProgressBar;
    private ImageButton powerButton;
    private ImageButton srcButton;
    private ImageButton plusButton;
    private ImageButton upButton;
    private ImageButton minusButton;
    private ImageButton downButton;
    private ImageButton muteButton;
    private Button infoButton;
    private GridLayout numberGridLayout;
    private Button[] numberButtons;
    // Adapters
    private ChannelCategoryAdapter channelCategoryAdapter;
    private ChannelAdapter channelAdapter;
    // Variables
    private ChannelCategory selectedChannelCategory;
    // Toast widget
    private ToastWidget toastWidget;

    @Override
    public void onDestroyView() {
        JobManagerHelper.cancelJobsInBackground(GetChannelCategoriesJob.TAG);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tv, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Send TV code
        int tvRemoteCode = PreferencesHelper.getTVRemoteCode();
        if (tvRemoteCode != PreferencesHelper.DEFAULT_TV_CODE) {
            PICConnectionHelper.sendCommand(new TRCSetVideoTypeCommand(tvRemoteCode));
        }

        listViewHeaderLayout = (LinearLayout) view.findViewById(R.id.tv_listview_header_layout);
        listView = (ListView) view.findViewById(R.id.tv_listview);
        listViewLoadingProgressBar = (ProgressBar) view.findViewById(R.id.tv_listview_loading_progress_bar);
        powerButton = (ImageButton) view.findViewById(R.id.tv_power_button);
        srcButton = (ImageButton) view.findViewById(R.id.tv_src_button);
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
            final int finalNumber = number;
            numberButtons[number].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PICConnectionHelper.sendCommand(new TRCDigitCommand(finalNumber));
                }
            });
        }

        channelCategoryAdapter = new ChannelCategoryAdapter(getLayoutInflater(savedInstanceState));
        channelAdapter = new ChannelAdapter(getLayoutInflater(savedInstanceState));

        ThemeHelper.setColorStateListTheme(listViewHeaderLayout);
        ThemeHelper.setProgressBarTheme(listViewLoadingProgressBar);
        ThemeHelper.setImageButtonTheme(powerButton);
        ThemeHelper.setImageButtonTheme(srcButton);
        ThemeHelper.setImageButtonTheme(plusButton);
        ThemeHelper.setImageButtonTheme(upButton);
        ThemeHelper.setImageButtonTheme(minusButton);
        ThemeHelper.setImageButtonTheme(downButton);
        ThemeHelper.setImageButtonTheme(muteButton);
        ThemeHelper.setButtonTheme(infoButton);
        ThemeHelper.setColorStateListTheme(numberGridLayout);

        listView.setEmptyView(listViewLoadingProgressBar);

        // Register events and jobs
        EventBus.getDefault().register(this);
        JobManagerHelper.getJobManager().addJobInBackground(new GetChannelCategoriesJob());

        // Register UI listeners
        listViewHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChannelCategories();
            }
        });
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCVideoOnOffCommand());
            }
        });
        srcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCVideoSourceCommand());
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCVolumeUpCommand());
            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCVolumeDownCommand());
            }
        });
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Command not implemented
            }
        });
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCChannelUpCommand());
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCChannelDownCommand());
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCInfoCommand());
            }
        });

        // Show channels or categories depending on saved channel category
        if (selectedChannelCategory == null) {
            showChannelCategories();
        } else {
            showChannels();
        }
    }

    public void showChannelCategories() {
        // Clear selected channel
        selectedChannelCategory = null;
        // Hide category header
        listViewHeaderLayout.setVisibility(View.GONE);
        // Populate listview with channel categories
        listView.setAdapter(channelCategoryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Clear old listview and save selected category
                channelAdapter.replaceList(new ArrayList<Channel>());
                selectedChannelCategory = channelCategoryAdapter.getItem(position);
                showChannels();
            }
        });
    }

    public void showChannels() {
        // Show category header
        listViewHeaderLayout.setVisibility(View.VISIBLE);
        // Populate listview with channels
        JobManagerHelper.getJobManager().addJobInBackground(new GetChannelsJob(selectedChannelCategory));
        listView.setAdapter(channelAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Channel channel = channelAdapter.getItem(position);
                PICConnectionHelper.sendCommand(new TRCMacroCommand(channel.number));
            }
        });
    }

    private void showWarning() {
        if (toastWidget == null && getContext() != null) {
            toastWidget = new ToastWidget(getContext(), getContext().getString(R.string.tv_warning), getActivity().findViewById(android.R.id.content));
        }
        if (toastWidget != null) {
            toastWidget.flash();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetChannelCategoriesEvent event) {
        channelCategoryAdapter.replaceList(event.channelCategories);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetChannelsEvent event) {
        channelAdapter.replaceList(event.channels);
    }

    @Override
    public void fragmentBecameVisible() {
        if (PreferencesHelper.showVideoControls()) {
            showWarning();
        }
    }

    @Override
    public void fragmentBecameInvisible() {
        if (toastWidget != null) {
            toastWidget.dismiss();
        }
    }
}
