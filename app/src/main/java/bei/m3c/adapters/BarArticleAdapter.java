package bei.m3c.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bei.m3c.R;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.models.BarArticle;

public class BarArticleAdapter extends ArrayAdapter<BarArticle> {

    private ImageView imageView;
    private TextView titleTextView;
    private TextView priceTextView;

    public BarArticleAdapter(Context context, int resource, List<BarArticle> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.gridview_item, parent, false);
        }

        BarArticle p = getItem(position);

        if (p != null) {
            imageView = (ImageView) convertView.findViewById(R.id.gridview_row_imageview);
            titleTextView = (TextView) convertView.findViewById(R.id.gridview_item_title_text);
            priceTextView = (TextView) convertView.findViewById(R.id.gridview_item_price_text);

            if (imageView != null) {
                imageView.setImageResource(R.drawable.bar_demo_coke);
            }
            if (titleTextView != null) {
                titleTextView.setText(p.name);
            }
            if (priceTextView != null) {
                priceTextView.setText(FormatHelper.asCurrency(p.price));
            }
        }

        return convertView;
    }

}
