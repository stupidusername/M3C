package bei.m3c.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import bei.m3c.R;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.BarArticle;

public class BarArticleAdapter extends BaseListAdapter<BarArticle> {

    private final LayoutInflater layoutInflater;
    private TextView titleTextView;
    private TextView priceTextView;
    private ImageButton infoImageButton;
    private BarArticleAdapter.OnClickListener clickListener;

    public BarArticleAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    /**
     * Set click listener for info button.
     * This method must be called before the adapter gets populated.
     */
    public void setOnClickListener(BarArticleAdapter.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        BarArticle barArticle = getItem(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.bar_article_listview_row, parent, false);
            // This goes here to avoid changing the view
            infoImageButton = (ImageButton) convertView.findViewById(R.id.bar_article_listview_row_info_button);
            if (infoImageButton != null) {
                ThemeHelper.setImageButtonTheme(infoImageButton);
            }

            if (barArticle != null) {
                if (barArticle.pictureUrl == null && barArticle.description == null) {
                    infoImageButton.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (barArticle != null) {
            titleTextView = (TextView) convertView.findViewById(R.id.bar_article_listview_row_title_textview);
            priceTextView = (TextView) convertView.findViewById(R.id.bar_article_listview_row_price_textview);
            infoImageButton = (ImageButton) convertView.findViewById(R.id.bar_article_listview_row_info_button);

            if (titleTextView != null) {
                titleTextView.setText(barArticle.name);
            }
            if (priceTextView != null) {
                priceTextView.setText(FormatHelper.asCurrency(barArticle.price));
            }
            if (infoImageButton != null) {
                if (clickListener != null) {
                    infoImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.onClick(position);
                        }
                    });
                }
            }
        }


        if (position % 2 == 0) {
            convertView.setBackgroundColor(ThemeHelper.getDarkAccentColor());
        } else {
            convertView.setBackgroundColor(ThemeHelper.getDarkerAccentColor());
        }

        return convertView;
    }

    public interface OnClickListener {
        void onClick(int position);
    }
}
