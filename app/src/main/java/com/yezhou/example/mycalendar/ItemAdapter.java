package com.yezhou.example.mycalendar;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/1.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    ArrayList<String> titles;
    ArrayList<String> dates;
    ArrayList<String> times;


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTitle;
        TextView itemDate;
        View titleView;

        public ViewHolder(View view) {
            super(view);
            titleView = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemDate = (TextView) view.findViewById(R.id.item_date);
        }
    }

    public ItemAdapter(ArrayList<String> titlesList, ArrayList<String> datesList, ArrayList<String> timesList) {
        titles = titlesList;
        dates= datesList;
        times = timesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                intent.putExtra("date", dates.get(holder.getPosition()));
                intent.putExtra("time", times.get(holder.getPosition()));
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = titles.get(position);
        String date = dates.get(position);

        holder.itemTitle.setText(title);
        holder.itemDate.setText("设置日期 :" + date);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
