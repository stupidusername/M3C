package bei.m3c.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bei.m3c.R;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.models.ServiceTariff;

public class ServiceTariffAdapter extends BaseListAdapter<ServiceTariff> {

    private final LayoutInflater layoutInflater;
    private TextView categoryTextView;
    private TextView priceShiftTextView;
    private TextView turnDurationTextView;
    private TextView priceOvernightTextView;

    public ServiceTariffAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.tariff_listview_row, null);
        }

        ServiceTariff serviceTariff = getItem(position);

        if (serviceTariff != null) {
            categoryTextView = (TextView) convertView.findViewById(R.id.tariff_listview_row_category_textview);
            priceShiftTextView = (TextView) convertView.findViewById(R.id.tariff_listview_row_price_shift_textview);
            turnDurationTextView = (TextView) convertView.findViewById(R.id.tariff_listview_row_turn_duration_textview);
            priceOvernightTextView = (TextView) convertView.findViewById(R.id.tariff_listview_row_price_overnight_textview);

            if (categoryTextView != null) {
                categoryTextView.setText(serviceTariff.roomCategoryName);
            }
            if (priceShiftTextView != null) {
                priceShiftTextView.setText(FormatHelper.asCurrency(serviceTariff.priceShift));
            }
            if (turnDurationTextView != null) {
                turnDurationTextView.setText(serviceTariff.turnDuration);
            }
            if (priceOvernightTextView != null) {
                String text = parent.getContext().getString(R.string.no_value);
                if(serviceTariff.showPriceOvernight) {
                    text = FormatHelper.asCurrency(serviceTariff.priceOvernight);
                }
                priceOvernightTextView.setText(text);
            }
        }

        return convertView;
    }

}
