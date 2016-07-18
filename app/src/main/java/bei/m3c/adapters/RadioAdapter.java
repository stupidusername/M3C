package bei.m3c.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bei.m3c.R;
import bei.m3c.activities.MainActivity;
import bei.m3c.models.Radio;

public class RadioAdapter extends ArrayAdapter<Radio> {

    public RadioAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public RadioAdapter(Context context, int resource, List<Radio> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listview_row, null);
            GradientDrawable shape = new GradientDrawable();
            Resources r = getContext().getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
            shape.setCornerRadius(px);
            px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
            shape.setStroke(Math.round(px), MainActivity.COLOR_ACCENT);
            StateListDrawable stateList = new StateListDrawable();
            stateList.addState(new int[]{android.R.attr.state_pressed}, shape);
            stateList.addState(new int[]{}, shape);
            v.setBackground(stateList);
        }

        Radio p = getItem(position);

        if (p != null) {
            TextView textView = (TextView) v.findViewById(R.id.listview_row_text);

            if (textView != null) {
                textView.setText(p.title);
            }
        }

        return v;
    }

}
