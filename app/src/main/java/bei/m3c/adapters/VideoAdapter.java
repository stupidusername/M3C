package bei.m3c.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import bei.m3c.R;
import bei.m3c.models.Video;

public class VideoAdapter extends BaseListAdapter<Video> {

    private final LayoutInflater layoutInflater;
    private ImageView imageView;
    private TextView titleTextView;

    public VideoAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.video_gridview_item, parent, false);
        }

        Video video = getItem(position);

        if (video != null) {
            imageView = (ImageView) convertView.findViewById(R.id.video_gridview_row_imageview);
            titleTextView = (TextView) convertView.findViewById(R.id.video_gridview_item_title_text);

            if (imageView != null) {
                Glide.with(layoutInflater.getContext()).load(video.coverUrl).centerCrop()
                        .placeholder(R.drawable.bar_article_placeholder).crossFade().into(imageView);
            }
            if (titleTextView != null) {
                titleTextView.setText(video.title);
            }
        }

        return convertView;
    }

}
