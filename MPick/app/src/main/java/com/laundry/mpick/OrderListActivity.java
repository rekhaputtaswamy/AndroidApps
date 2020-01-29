package com.laundry.mpick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.laundry.model.Orders;

import java.util.ArrayList;

public class OrderListActivity extends AppCompatActivity {

    private ListView ordersList;
    private ArrayList<Orders> ordersArrayList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initalise();
    }

    private void initalise() {
        setOrdersValues();
        ordersList = (ListView) findViewById(R.id.orders_list);
        CustomOrderAdapter adapter = new CustomOrderAdapter(ordersArrayList, OrderListActivity.this);
        ordersList.setAdapter(adapter);
        ordersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Orders dataModel = ordersArrayList.get(position);
                Intent intent = new Intent(OrderListActivity.this, SummaryActivity.class);
                intent.putExtra("type", "ordersList");
                startActivity(intent);
            }
        });
    }

    private void setOrdersValues() {
        ordersArrayList = new ArrayList<>();
        ordersArrayList.add(new Orders("TC123", "15/01/2018", "Closed"));
        ordersArrayList.add(new Orders("TG265", "16/01/2018", "In Progress"));
        ordersArrayList.add(new Orders("TT388", "18/01/2018", "In Progress"));
    }
}
