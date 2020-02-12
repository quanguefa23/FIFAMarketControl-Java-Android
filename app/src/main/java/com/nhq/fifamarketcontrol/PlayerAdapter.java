package com.nhq.fifamarketcontrol;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PlayerAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<FootballPlayer> listPlayer;

    public PlayerAdapter(Context context, int layout, List<FootballPlayer> listPlayer) {
        this.context = context;
        this.layout = layout;
        this.listPlayer = listPlayer;
    }

    @Override
    public int getCount() {
        return listPlayer.size();
    }

    @Override
    public Object getItem(int i) {
        return listPlayer.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void moveUp(int i) {
        if (i != 0)
            swapListPlayer(i, i - 1);
    }

    public void moveDown(int i) {
        if (i != getCount() - 1)
            swapListPlayer(i, i + 1);
    }

    public void moveTop(int i) {
        if (i != 0)
            swapListPlayer(i, 0);
    }

    public void moveBottom(int i) {
        if (i != getCount() - 1)
            swapListPlayer(i, getCount() - 1);
    }

    private void swapListPlayer(int n1, int n2) {
        FootballPlayer t = listPlayer.get(n1);
        listPlayer.set(n1, listPlayer.get(n2));
        listPlayer.set(n2, t);
    }

    private class ViewHolder {
        TextView nameTV;
        TextView positionTV;
        TextView ovrTV;
        TextView priceTV;
        TextView isBuyTV;
        TextView dateTV;
        ImageView upIV;
        ImageView downIV;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder = new ViewHolder();

            //map
            holder.nameTV = view.findViewById(R.id.name);
            holder.positionTV = view.findViewById(R.id.position);
            holder.ovrTV = view.findViewById(R.id.ovr);
            holder.priceTV = view.findViewById(R.id.price);
            holder.isBuyTV = view.findViewById(R.id.isBuy);
            holder.dateTV = view.findViewById(R.id.date);
            holder.upIV = view.findViewById(R.id.up);
            holder.downIV = view.findViewById(R.id.down);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        //get value
        FootballPlayer temp = listPlayer.get(i);
        holder.nameTV.setText(temp.getName());
        holder.positionTV.setText(temp.getPostion());
        holder.ovrTV.setText("" + temp.getOvr());
        //holder.ovrTV.setText("" + temp.getId());

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(temp.getDate().getTime());
        holder.dateTV.setText(strDate);

        if (temp.isBuy()) {
            holder.isBuyTV.setTextColor(Color.parseColor("#F44336"));
            holder.isBuyTV.setText("BUY");
            holder.priceTV.setText("" + temp.getPrice());
        }
        else {
            holder.isBuyTV.setTextColor(Color.parseColor("#4CAF50"));
            holder.isBuyTV.setText("SELL");
            holder.priceTV.setText("" + temp.getPrice());
        }

        final PlayerAdapter adap = this;
        //set on click
        holder.upIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adap.moveUp(i);
                adap.notifyDataSetChanged();
            }
        });

        holder.downIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adap.moveDown(i);
                adap.notifyDataSetChanged();
            }
        });
        return view;
    }

    public void setShowed(Boolean showed) {
    }

    public void remove(int i) {
        listPlayer.remove(i);
    }
}
