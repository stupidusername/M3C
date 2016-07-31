package bei.m3c.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import bei.m3c.R;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.ChannelCategory;

public class ChannelCategoryAdapter extends BaseListAdapter<ChannelCategory> {

    private final LayoutInflater layoutInflater;
    private TextView textView;

    public ChannelCategoryAdapter(LayoutInflater layoutInflater) {
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

        ChannelCategory channelCategory = getItem(position);

        if (channelCategory != null) {
            textView = (TextView) convertView.findViewById(R.id.listview_row_text);

            if (textView != null) {
                textView.setText(channelCategory.title);
            }
        }

        return convertView;
    }

}
