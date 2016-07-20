package bei.m3c.adapters;

import android.content.Context;
import android.content.res.Resources;
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
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.Radio;

public class RadioAdapter extends ArrayAdapter<Radio> {

    public static final int ROW_CORNER_RADIUS_DIP = 5;
    public static final int ROW_STROKE_WIDTH_DIP = 2;

    private TextView textView;

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

            // Set adapter list background according to theme color
            Resources resources = getContext().getResources();
            float cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_CORNER_RADIUS_DIP, resources.getDisplayMetrics());
            int strokeWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_STROKE_WIDTH_DIP, resources.getDisplayMetrics()));
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(cornerRadius);
            shape.setStroke(strokeWidth, ThemeHelper.getDarkAccentColor());
            GradientDrawable activatedShape = new GradientDrawable();
            activatedShape.setCornerRadius(cornerRadius);
            activatedShape.setColor(ThemeHelper.getDarkAccentColor());
            activatedShape.setStroke(strokeWidth, ThemeHelper.getAccentColor());
            StateListDrawable stateList = new StateListDrawable();
            stateList.addState(new int[]{android.R.attr.state_pressed}, activatedShape);
            stateList.addState(new int[]{android.R.attr.state_activated}, activatedShape);
            stateList.addState(new int[]{}, shape);
            v.setBackground(stateList);
        }

        Radio p = getItem(position);

        if (p != null) {
            textView = (TextView) v.findViewById(R.id.listview_row_text);

            if (textView != null) {
                textView.setText(p.title);
            }
        }

        return v;
    }

}
