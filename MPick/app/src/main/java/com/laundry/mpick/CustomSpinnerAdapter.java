package com.laundry.mpick;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rekha_p on 12-01-2018.
 */
public class CustomSpinnerAdapter  extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private ArrayList<String> list;

    public CustomSpinnerAdapter(Context context, ArrayList<String> list) {
        this.list = list;
        activity = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int i) {
        return list.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity);
        txt.setPadding(15, 15, 15, 15);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(list.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(activity);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(5, 5, 5, 5);
        txt.setTextSize(16);
        //txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down, 0);
        txt.setText(list.get(i));
        txt.setTextColor(Color.parseColor("#000000"));
        return txt;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return false;
    }
}
