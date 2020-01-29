package com.laundry.mpick;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by rekha_p on 18-01-2018.
 */
public class DrawerAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private String [] namesList;
    private int[] imagesList;
    private AppCompatActivity context;

    public DrawerAdapter ( AppCompatActivity context, String[] namesList, int[] imagesList) {
        this.namesList = namesList;
        this.imagesList = imagesList;
        this.context = context;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return namesList.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.drawer_list_layout, null);

        holder.header = (LinearLayout) rowView.findViewById(R.id.header);
        holder.header.setVisibility(View.GONE);

        holder.tv = (TextView) rowView.findViewById(R.id.text);
        holder.tv.setText(namesList[position]);

        holder.img = (ImageView) rowView.findViewById(R.id.image);
        holder.img.setImageResource(imagesList[position]);

        if(position == 0) {
            holder.tv.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
            holder.header.setVisibility(View.VISIBLE);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 1:
                        Intent userInfoIntent = new Intent(context, UserInfoActivity.class);
                        context.startActivity(userInfoIntent);
                        break;

                    case 2:
                        Intent ordersIntent = new Intent(context, OrderListActivity.class);
                        context.startActivity(ordersIntent);
                        break;

                    case 3:
                        Intent changePasswordIntent = new Intent(context, ChangePasswordActivity.class);
                        context.startActivity(changePasswordIntent);
                        break;

                    case 4:
                        Intent loginIntent = new Intent(context, LoginActivity.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(loginIntent);
                        break;
                }
            }
        });
        return rowView;
    }

    public class Holder {
        LinearLayout header;
        TextView tv;
        ImageView img;
    }

}
