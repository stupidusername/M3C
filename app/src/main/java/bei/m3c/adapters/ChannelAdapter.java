package bei.m3c.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import bei.m3c.R;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.Channel;

public class ChannelAdapter extends BaseListAdapter<Channel> {

    private final LayoutInflater layoutInflater;
    private ImageView imageView;
    private TextView textView;

    public ChannelAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.channel_listview_row, null);
            ThemeHelper.setColorStateListTheme(convertView);
        }

        Channel channel = getItem(position);

        if (channel != null) {
            imageView = (ImageView) convertView.findViewById(R.id.channel_listview_row_imageview);
            textView = (TextView) convertView.findViewById(R.id.channel_listview_row_text);

            if (imageView != null) {
                Glide.with(layoutInflater.getContext()).load(channel.logoUrl).centerCrop()
                        .placeholder(R.drawable.channel_logo_placeholder).crossFade().into(imageView);
            }

            if (textView != null) {
                textView.setText(channel.title);
            }
        }

        return convertView;
    }

}
