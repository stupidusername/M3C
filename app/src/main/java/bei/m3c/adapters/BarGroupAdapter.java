package bei.m3c.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bei.m3c.R;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.BarGroup;

public class BarGroupAdapter extends ArrayAdapter<BarGroup> {

    private TextView textView;

    public BarGroupAdapter(Context context, int resource, List<BarGroup> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.listview_row, null);
            ThemeHelper.setColorStateListTheme(convertView);
        }

        BarGroup p = getItem(position);

        if (p != null) {
            textView = (TextView) convertView.findViewById(R.id.listview_row_text);

            if (textView != null) {
                textView.setText(p.name);
            }
        }

        return convertView;
    }

}
