package bei.m3c.fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import bei.m3c.R;
import bei.m3c.adapters.ServiceAdapter;
import bei.m3c.adapters.ServiceTariffAdapter;
import bei.m3c.commands.TPCAccountInfoCommand;
import bei.m3c.commands.TPCGetAccountInfoCommand;
import bei.m3c.events.GetServiceTariffsEvent;
import bei.m3c.events.GetServicesEvent;
import bei.m3c.events.TPCAccountInfoCommandEvent;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.helpers.SGHConnectionHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.interfaces.FragmentInterface;
import bei.m3c.jobs.GetServiceTariffsJob;
import bei.m3c.jobs.GetServicesJob;

/**
 * Info fragment
 */
public class InfoFragment extends Fragment implements FragmentInterface {

    public static final BigDecimal DEFAULT_MONEY = new BigDecimal(0);
    public static final int POPUP_MARGIN_DP = 50;
    public static final int GET_ACCOUNT_INFO_DELAY_MILLIS = 5000;
    public static final int UPDATE_DATE_TIME_INTERVAL_MILLIS = 60000;

    private TPCAccountInfoCommand accountInfo;
    private Date date;
    private Time time;
    private Timer dateTimeUpdateTimer;
    private long lastDateTimeUpdateTimeMillis;
    // views
    private TextView dateTextView;
    private TextView timeTextView;
    private RelativeLayout activityLayout;
    private GridLayout gridLayout;
    private TextView specialOfferTextView;
    private TextView shiftStartTextView;
    private TextView shiftEndTextView;
    private TextView alarmTextView;
    private GridLayout billGridLayout;
    private TextView billLodgingTextView;
    private TextView billSurchargeTextView;
    private TextView billBarTextView;
    private TextView billBonusTextView;
    private TextView billDiscountTextView;
    private TextView billPaidTextView;
    private TextView billTotalTextView;
    private Button servicesButton;
    private Button tariffsButton;
    private PopupWindow popupWindow;
    private ImageView popupImageView;
    private TextView popupTitleTextView;
    private LinearLayout popupTariffListViewHeaderLayout;
    private ListView popupListView;
    private Button popupCloseButton;
    // adapters
    private ServiceAdapter serviceAdapter;
    private ServiceTariffAdapter serviceTariffAdapter;

    @Override
    public void onDestroyView() {
        if (dateTimeUpdateTimer != null) {
            dateTimeUpdateTimer.cancel();
            dateTimeUpdateTimer = null;
        }
        JobManagerHelper.cancelJobsInBackground(GetServicesJob.TAG);
        JobManagerHelper.cancelJobsInBackground(GetServiceTariffsJob.TAG);
        JobManagerHelper.cancelJobsInBackground(TPCGetAccountInfoCommand.TAG);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateTextView = (TextView) view.findViewById(R.id.info_date_textview);
        timeTextView = (TextView) view.findViewById(R.id.info_time_textview);
        activityLayout = (RelativeLayout) getActivity().findViewById(R.id.activity_layout);
        gridLayout = (GridLayout) view.findViewById(R.id.info_gridlayout);
        specialOfferTextView = (TextView) view.findViewById(R.id.info_special_offer_textview);
        shiftStartTextView = (TextView) view.findViewById(R.id.info_shift_start_textview);
        shiftEndTextView = (TextView) view.findViewById(R.id.info_shift_end_textview);
        alarmTextView = (TextView) view.findViewById(R.id.info_alarm_textview);
        billGridLayout = (GridLayout) view.findViewById(R.id.info_bill_gridlayout);
        billLodgingTextView = (TextView) view.findViewById(R.id.info_bill_lodging_textview);
        billSurchargeTextView = (TextView) view.findViewById(R.id.info_bill_surcharge_textview);
        billBarTextView = (TextView) view.findViewById(R.id.info_bill_bar_textview);
        billBonusTextView = (TextView) view.findViewById(R.id.info_bill_bonus_textview);
        billDiscountTextView = (TextView) view.findViewById(R.id.info_bill_discount_textview);
        billPaidTextView = (TextView) view.findViewById(R.id.info_bill_paid_textview);
        billTotalTextView = (TextView) view.findViewById(R.id.info_bill_total_textview);
        servicesButton = (Button) view.findViewById(R.id.info_services_button);
        tariffsButton = (Button) view.findViewById(R.id.info_tariffs_button);

        updateInfo();

        // Set theme
        ThemeHelper.setColorStateListTheme(gridLayout);
        ThemeHelper.setColorStateListTheme(billGridLayout);
        ThemeHelper.setColorStateListTheme(servicesButton);
        ThemeHelper.setColorStateListTheme(tariffsButton);

        // Create popup
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int popupMargin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, POPUP_MARGIN_DP, getResources().getDisplayMetrics()));
        int popupWidth = displayMetrics.widthPixels - popupMargin * 2;
        int popupHeight = displayMetrics.heightPixels - popupMargin * 2;
        View popupView = getLayoutInflater(savedInstanceState).inflate(R.layout.popup_info, null);
        popupWindow = new PopupWindow(popupView, popupWidth, popupHeight);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupImageView = (ImageView) popupView.findViewById(R.id.popup_info_imageview);
        Glide.with(this).load(M3SHelper.getServicesImageUrl()).signature(new StringSignature((new Timestamp(System.currentTimeMillis()))
                .toString())).centerCrop().dontAnimate().into(popupImageView);
        popupTitleTextView = (TextView) popupView.findViewById(R.id.popup_info_title);
        popupTariffListViewHeaderLayout = (LinearLayout) popupView.findViewById(R.id.popup_info_tariff_listview_header_layout);
        popupListView = (ListView) popupView.findViewById(R.id.popup_info_listview);
        popupCloseButton = (Button) popupView.findViewById(R.id.popup_info_close_button);
        ThemeHelper.setButtonTheme(popupCloseButton);
        serviceAdapter = new ServiceAdapter(getLayoutInflater(savedInstanceState));
        serviceTariffAdapter = new ServiceTariffAdapter(getLayoutInflater(savedInstanceState));
        popupListView.setEmptyView(popupView.findViewById(R.id.popup_info_listview_loading_progress_bar));

        // Add UI listeners
        servicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showServicesPopup();
            }
        });
        tariffsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTariffsPopup();
            }
        });
        popupCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // Register events and jobs
        EventBus.getDefault().register(this);
        JobManagerHelper.getJobManager().addJobInBackground(new GetServicesJob());
        JobManagerHelper.getJobManager().addJobInBackground(new GetServiceTariffsJob());
        SGHConnectionHelper.sendCommand(new TPCGetAccountInfoCommand(), GET_ACCOUNT_INFO_DELAY_MILLIS);

        if (date == null) {
            date = Calendar.getInstance().getTime();
        }
        if (time == null) {
            time = new Time(date.getTime());
        }
        updateDateTime();

        // Update date and time periodically
        if (dateTimeUpdateTimer == null) {
            dateTimeUpdateTimer = new Timer();
            dateTimeUpdateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long timeDelta = System.currentTimeMillis() - lastDateTimeUpdateTimeMillis;
                            time = new Time(time.getTime() + timeDelta);
                            date = time;
                            updateDateTime();
                        }
                    });
                }
            }, UPDATE_DATE_TIME_INTERVAL_MILLIS, UPDATE_DATE_TIME_INTERVAL_MILLIS);
        }
    }

    private void showServicesPopup() {
        popupTitleTextView.setText(getString(R.string.info_services));
        popupTariffListViewHeaderLayout.setVisibility(View.GONE);
        popupListView.setAdapter(serviceAdapter);
        showPopup();
    }

    private void showTariffsPopup() {
        popupTitleTextView.setText(getString(R.string.info_tariffs));
        if (!serviceTariffAdapter.isEmpty()) {
            popupTariffListViewHeaderLayout.setVisibility(View.VISIBLE);
        }
        popupListView.setAdapter(serviceTariffAdapter);
        showPopup();
    }

    private void showPopup() {
        popupWindow.showAtLocation(activityLayout, Gravity.CENTER, 0, 0);
    }

    private void updateDateTime() {
        lastDateTimeUpdateTimeMillis = System.currentTimeMillis();
        dateTextView.setText(FormatHelper.asLongDate(date));
        timeTextView.setText(FormatHelper.asTime(time));
    }

    private void updateInfo() {
        if (accountInfo == null || !accountInfo.serviceOpen) {
            specialOfferTextView.setText(getString(R.string.no_value));
            shiftStartTextView.setText(getString(R.string.time_default));
            shiftEndTextView.setText(getString(R.string.time_default));
            alarmTextView.setText(getString(R.string.time_default));
            billLodgingTextView.setText(FormatHelper.asCurrency(DEFAULT_MONEY));
            billSurchargeTextView.setText(FormatHelper.asCurrency(DEFAULT_MONEY));
            billBarTextView.setText(FormatHelper.asCurrency(DEFAULT_MONEY));
            billBonusTextView.setText(FormatHelper.asCurrency(DEFAULT_MONEY));
            billDiscountTextView.setText(FormatHelper.asCurrency(DEFAULT_MONEY));
            billPaidTextView.setText(FormatHelper.asCurrency(DEFAULT_MONEY));
            billTotalTextView.setText(FormatHelper.asCurrency(DEFAULT_MONEY));
        } else {
            String specialOffer = getString(R.string.no_value);
            if (!accountInfo.specialOffer.equals("")) {
                specialOffer = accountInfo.specialOffer;
            }
            specialOfferTextView.setText(specialOffer);
            shiftStartTextView.setText(FormatHelper.asTime(accountInfo.shiftStartTime));
            shiftEndTextView.setText(FormatHelper.asTime(accountInfo.shiftEndTime));
            alarmTextView.setText(FormatHelper.asTime(accountInfo.alarmTime));
            billLodgingTextView.setText(FormatHelper.asCurrency(accountInfo.billLodging));
            billSurchargeTextView.setText(FormatHelper.asCurrency(accountInfo.billSurcharge));
            billBarTextView.setText(FormatHelper.asCurrency(accountInfo.billBar));
            billBonusTextView.setText(FormatHelper.asCurrency(accountInfo.billBonus));
            billDiscountTextView.setText(FormatHelper.asCurrency(accountInfo.billDiscount));
            billPaidTextView.setText(FormatHelper.asCurrency(accountInfo.billPaid));
            billTotalTextView.setText(FormatHelper.asCurrency(accountInfo.billTotal));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetServicesEvent event) {
        serviceAdapter.replaceList(event.services);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetServiceTariffsEvent event) {
        if (event.serviceTariffs.size() > 0 && popupListView.getAdapter() instanceof ServiceTariffAdapter) {
            popupTariffListViewHeaderLayout.setVisibility(View.VISIBLE);
        } else {
            popupTariffListViewHeaderLayout.setVisibility(View.GONE);
        }
        serviceTariffAdapter.replaceList(event.serviceTariffs);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TPCAccountInfoCommandEvent event) {
        accountInfo = event.command;
        date = accountInfo.date;
        time = accountInfo.time;
        updateInfo();
        updateDateTime();
    }

    @Override
    public void fragmentBecameVisible() {

    }

    @Override
    public void fragmentBecameInvisible() {

    }
}
