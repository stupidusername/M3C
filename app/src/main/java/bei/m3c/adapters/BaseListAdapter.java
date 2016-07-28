package bei.m3c.adapters;

import android.widget.BaseAdapter;

import java.util.List;

abstract public class BaseListAdapter<T> extends BaseAdapter {

    List<T> list = null;

    public BaseListAdapter() {

    }

    public void replaceList(List<T> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public T getItem(int i) {
        return list == null ? null : list.get(i);
    }
}