package com.laundry.mpick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.laundry.model.Orders;

import java.util.ArrayList;

/**
 * Created by rekha_p on 19-01-2018.
 */
public class CustomOrderAdapter extends ArrayAdapter<Orders> {
    private ArrayList<Orders> orders;
    private Context mContext;

    public CustomOrderAdapter(ArrayList<Orders> data, Context context) {
        super(context, R.layout.orders_list_layout, data);
        this.orders = data;
        this.mContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Orders orders = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.orders_list_layout, parent, false);
            viewHolder.txtOrderId = (TextView) convertView.findViewById(R.id.order_id);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.status);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtOrderId.setText(orders.getOrderId());
        viewHolder.txtDate.setText(orders.getDate());
        viewHolder.txtStatus.setText(orders.getStatus());

        return convertView;
    }

    private static class ViewHolder {
        TextView txtOrderId;
        TextView txtDate;
        TextView txtStatus;
    }
}
