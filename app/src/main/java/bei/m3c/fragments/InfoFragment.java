package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.text.BidiFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Date;

import bei.m3c.R;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.ThemeHelper;

/**
 * Info fragment
 */
public class InfoFragment extends Fragment {

    public static final BigDecimal DEFAULT_MONEY = new BigDecimal(0);

    private GridLayout gridLayout;
    private TextView dateTextView;
    private TextView timeTextView;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridLayout = (GridLayout) view.findViewById(R.id.info_gridlayout);
        dateTextView = (TextView) view.findViewById(R.id.info_date_textview);
        timeTextView = (TextView) view.findViewById(R.id.info_time_textview);
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

        // Set default texts
        dateTextView.setText(FormatHelper.asDateLong(new Date()));
        timeTextView.setText(FormatHelper.asTime(new Date()));
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

        // Set theme
        ThemeHelper.setColorStateListTheme(gridLayout);
        ThemeHelper.setColorStateListTheme(billGridLayout);
        ThemeHelper.setColorStateListTheme(servicesButton);
        ThemeHelper.setColorStateListTheme(tariffsButton);
    }
}
