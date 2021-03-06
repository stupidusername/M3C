package bei.m3c.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bei.m3c.R;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.Radio;

public class RadioAdapter extends BaseListAdapter<Radio> {

    private final LayoutInflater layoutInflater;
    private TextView textView;

    public RadioAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_row, null);
            ThemeHelper.setColorStateListTheme(convertView);
        }

        Radio radio = getItem(position);

        if (radio != null) {
            textView = (TextView) convertView.findViewById(R.id.listview_row_text);

            if (textView != null) {
                textView.setText(radio.title);
            }
        }

        return convertView;
    }

}
