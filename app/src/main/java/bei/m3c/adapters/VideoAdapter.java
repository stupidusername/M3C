package bei.m3c.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;

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
                        .placeholder(R.drawable.video_cover_placeholder).dontAnimate().into(new ImageViewTarget<GlideDrawable>(imageView) {
                    @Override
                    protected void setResource(GlideDrawable resource) {

                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        view.setImageDrawable(resource);
                        super.onResourceReady(resource, glideAnimation);

                    }
                });
            }
            if (titleTextView != null) {
                titleTextView.setText(video.title);
                titleTextView.setSelected(true);
            }
        }

        return convertView;
    }

}
