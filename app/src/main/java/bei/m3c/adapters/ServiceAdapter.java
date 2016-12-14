package bei.m3c.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bei.m3c.R;
import bei.m3c.models.Service;

public class ServiceAdapter extends BaseListAdapter<Service> {

    private final LayoutInflater layoutInflater;
    private TextView textView;

    public ServiceAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.service_listview_row, null);
        }

        Service service = getItem(position);

        if (service != null) {
            textView = (TextView) convertView.findViewById(R.id.service_listview_row_textview);

            if (textView != null) {
                textView.setText(service.title);
                textView.setSelected(true);
            }
        }

        return convertView;
    }

}
