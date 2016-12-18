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
import bei.m3c.helpers.FormatHelper;
import bei.m3c.models.BarArticle;

public class BarArticleAdapter extends BaseListAdapter<BarArticle> {

    private final LayoutInflater layoutInflater;
    private ImageView imageView;
    private TextView titleTextView;
    private TextView priceTextView;

    public BarArticleAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.gridview_item, parent, false);
        }

        BarArticle barArticle = getItem(position);

        if (barArticle != null) {
            imageView = (ImageView) convertView.findViewById(R.id.gridview_row_imageview);
            titleTextView = (TextView) convertView.findViewById(R.id.gridview_item_title_text);
            priceTextView = (TextView) convertView.findViewById(R.id.gridview_item_price_text);

            if (imageView != null) {
                Glide.with(layoutInflater.getContext()).load(barArticle.pictureUrl).centerCrop()
                        .placeholder(R.drawable.bar_article_placeholder).dontAnimate().into(new ImageViewTarget<GlideDrawable>(imageView) {
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
                titleTextView.setText(barArticle.name);
            }
            if (priceTextView != null) {
                priceTextView.setText(FormatHelper.asCurrency(barArticle.price));
            }
        }

        return convertView;
    }

}
