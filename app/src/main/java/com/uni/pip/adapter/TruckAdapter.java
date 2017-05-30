package com.uni.pip.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uni.pip.R;
import com.uni.pip.datalayer.models.Destination;
import com.uni.pip.datalayer.models.Event;
import com.uni.pip.datalayer.models.Truck;
import com.uni.pip.datalayer.models.User;
import com.uni.pip.utilities.Methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by sammy on 4/6/2017.
 */

public class TruckAdapter extends RecyclerView.Adapter<TruckAdapter.ViewHolder> {
    final  Drawable arrowDownGrey;
    final  Drawable arrowUpGrey;
    final  Drawable arrowDownBlack;
    final  Drawable arrowUpBlack;
    private ArrayList<Event> myChildren;
    private Context context;
    ArrayList<User> myUsers;
    ArrayList<Truck> myTrucks;
    private onClick onClick;
    private boolean sorted = false;
    private int sorting = -1;
    ArrayList<Destination> destinations;
    public void setOnClick(TruckAdapter.onClick onClick) {
        this.onClick = onClick;
    }
    private onLongClick onLongClick;

    public void setOnLongClick(TruckAdapter.onLongClick onLongClick) {
        this.onLongClick = onLongClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView truck;
        public TextView driver;
        public TextView enter;
        public TextView exit;
        public TextView date;

        private ImageView upEnter, upExit, downEnter, downExit;

        public ViewHolder(View v) {
            super(v);

            truck = (TextView) v.findViewById(R.id.truck);
            driver = (TextView) v.findViewById(R.id.driver);
            enter = (TextView) v.findViewById(R.id.enter);
            exit = (TextView) v.findViewById(R.id.exit);
            upEnter = (ImageView) v.findViewById(R.id.up_enter);
            upExit = (ImageView) v.findViewById(R.id.up_exit);
            downEnter = (ImageView) v.findViewById(R.id.down_enter);
            downExit = (ImageView) v.findViewById(R.id.down_exit);


        }

    }

    public TruckAdapter(Context context, ArrayList<Event> myDataset, ArrayList<User> myUsers, ArrayList<Truck> myTrucks, ArrayList<Destination> destinations) {
        this.myChildren = myDataset;
        this.context = context;
        this.myUsers = myUsers;
        this.myTrucks = myTrucks;
        this.destinations = destinations;


          arrowDownGrey = ContextCompat.getDrawable(context, R.drawable.arrow_down_grey).mutate();
          arrowUpGrey = ContextCompat.getDrawable(context, R.drawable.arrow_up_grey).mutate();
          arrowDownBlack = ContextCompat.getDrawable(context, R.drawable.arrow_down_black).mutate();
          arrowUpBlack = ContextCompat.getDrawable(context, R.drawable.arrow_up_black).mutate();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.card_view_expenses, parent, false);
                .inflate(R.layout.row_truck, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(TextUtils.isEmpty(holder.exit.getText().toString()))
                    if(onLongClick!=null)
                        onLongClick.onLongClick(myChildren.get(position-1));
                return true;
            }
        });
        if (position == 0) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            holder.truck.setText(context.getString(R.string.truck));
            holder.driver.setText(context.getString(R.string.driver));
            holder.enter.setText(context.getString(R.string.enter));
            holder.exit.setText(context.getString(R.string.destination));


            holder.truck.setTypeface(holder.truck.getTypeface(), Typeface.BOLD);
            holder.driver.setTypeface(holder.driver.getTypeface(), Typeface.BOLD);
            holder.enter.setTypeface(holder.enter.getTypeface(), Typeface.BOLD);
            holder.exit.setTypeface(holder.exit.getTypeface(), Typeface.BOLD);


            holder.downEnter.setVisibility(View.VISIBLE);
            holder.downExit.setVisibility(View.VISIBLE);
            holder.upEnter.setVisibility(View.VISIBLE);
            holder.upExit.setVisibility(View.VISIBLE);

            if(sorting==0) {
                holder.downEnter.setImageDrawable(arrowDownBlack);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }else if(sorting==1){
                holder.downEnter.setImageDrawable(arrowDownBlack);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }else if (sorting ==2){
                holder.downEnter.setImageDrawable(arrowDownGrey);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpBlack);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }else if (sorting ==3){
                holder.downEnter.setImageDrawable(arrowDownGrey);
                holder.downExit.setImageDrawable(arrowDownBlack);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }else if(sorting ==4){
                holder.downEnter.setImageDrawable(arrowDownGrey);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpBlack);
            }else {
                holder.downEnter.setImageDrawable(arrowDownGrey);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }



            holder.downEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sorted=true;
                    sorting=1;
//                    Collections.sort(myChildren., new ChairHeightComparator());


                    for (int m = 0; m < myChildren.size(); m++) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        holder.exit.setText("");
                        holder.driver.setText("");
                        holder.truck.setText("");
                    }
//                    sorted = true;

                    TableComparator tableComparator = new TableComparator();
                    Collections.sort(myChildren,tableComparator);
                    notifyDataSetChanged();


                }
            });
            holder.upEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sorted=true;
                    sorting=2;
                    for (int m = 0; m < myChildren.size(); m++) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        holder.exit.setText("");

                    }
                    TableComparator tableComparator = new TableComparator();
                    Collections.sort(myChildren,tableComparator);
                    Collections.reverse(myChildren);



                    notifyDataSetChanged();
                }
            });

            holder.downExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sorted=true;
                    sorting=3;
                    for (int m = 0; m < myChildren.size(); m++) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        holder.exit.setText("");

                    }
                    ChairHeightComparator tableComparator = new ChairHeightComparator();
                    Collections.sort(myChildren,tableComparator);

                    notifyDataSetChanged();
                }
            });
            holder.upExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sorted=true;
                    sorting=4;
                    for (int m = 0; m < myChildren.size(); m++) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        holder.exit.setText("");

                    }
                    ChairHeightComparator tableComparator = new ChairHeightComparator();
                    Collections.sort(myChildren,tableComparator);
                    Collections.reverse(myChildren);
                    notifyDataSetChanged();
                }
            });
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        } else {

            holder.truck.setTypeface(holder.truck.getTypeface(), Typeface.NORMAL);
            holder.driver.setTypeface(holder.driver.getTypeface(), Typeface.NORMAL);
            holder.enter.setTypeface(holder.enter.getTypeface(), Typeface.NORMAL);
            holder.exit.setTypeface(holder.exit.getTypeface(), Typeface.NORMAL);


            holder.downEnter.setVisibility(View.INVISIBLE);
            holder.downExit.setVisibility(View.INVISIBLE);
            holder.upEnter.setVisibility(View.INVISIBLE);
            holder.upExit.setVisibility(View.INVISIBLE);

            holder.exit.setText(myChildren.get(position-1).getDestination_Name());

                holder.truck.setText(myChildren.get(position-1).getTruckNumber());

//            for(int i =0; i<myTrucks.size();i++) {
//                if(myTrucks.get(i).getTruck_Id().equals(myChildren.get(position-1).getTruck_Id())) {
//                    holder.truck.setText(myTrucks.get(position - 1).getTruck_number() + "");
//                    break;
//                }
//                else holder.truck.setText("");
//            }


                holder.driver.setText(myChildren.get(position-1).getDriverName());
            if (myChildren.get(position - 1) != null) {
                holder.enter.setText(Methods.splitAfter(myChildren.get(position - 1).getTimestamp())
                        .substring(0, Methods.splitAfter(myChildren.get(position - 1).getTimestamp()).length() - 3));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position!=0) {


                        if (onClick != null)
                            onClick.OnClick(myChildren.get(position - 1),
                                     myTrucks.get(position - 1), myUsers.get(position - 1));
                    }
                }
            });

        }
    }
    class ChairHeightComparator implements Comparator<Event> {
        public int compare(Event s1, Event s2) {
            return s1.getDestination_Id().compareToIgnoreCase(s2.getDestination_Id());
        }
    }
    class TableComparator implements Comparator<Event> {
        public int compare(Event s1, Event s2) {
            if(s1.getTimestamp()==null ||s2.getTimestamp()==null)
                return -1;
            else
            if (Methods.convertToMilli(s1.getTimestamp()) > Methods.convertToMilli(s2.getTimestamp()))
                return -1;
            else if (Methods.convertToMilli(s1.getTimestamp()) < Methods.convertToMilli(s2.getTimestamp()))
                return +1;
            return 0;
        }
    }

    public void sort(){
        sorted=true;
        sorting=3;

        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return myChildren.size() + 1;
    }

    public interface onClick {
        void OnClick(Event event, Truck truck, User user);
    }
    public interface onLongClick {
        void onLongClick(Event event);
    }
    private void sortEnterUp() {

    }
}

