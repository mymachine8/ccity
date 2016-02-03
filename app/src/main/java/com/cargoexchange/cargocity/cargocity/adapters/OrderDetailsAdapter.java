package com.cargoexchange.cargocity.cargocity.adapters;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.OrderStatus;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.fragments.OrdersListFragment;
import com.cargoexchange.cargocity.cargocity.models.Order;

import java.util.List;

/**
 * Created by root on 19/1/16.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>
{
    private List<Order> orderDetails;
    private RouteSession mRouteSession;
    private static OrderItemClickListener mItemClickListener;
    private Fragment mFragmentInstance;

    public OrderDetailsAdapter(List<Order> orderDetails, Fragment fragment) {
        mFragmentInstance = fragment;
        this.orderDetails = orderDetails;
    }

    public void setOnItemClickListener(OrderItemClickListener careClickListener) {
        this.mItemClickListener = careClickListener;
    }

    public OrderDetailsAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final LayoutInflater layoutInflator=LayoutInflater.from(parent.getContext());
        final View v=layoutInflator.inflate(R.layout.row_orderdetails, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {Log.d("HelloholderClick", "hi");

            }
        });
        final ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        mRouteSession=RouteSession.getInstance();
        //holder.mOrderno.setText(orderDetails.get(position).getOrderId());
        holder.mName.setText(orderDetails.get(position).getName());
        holder.mItems1.setText(orderDetails.get(position).getItems().get(0));
        holder.mItems2.setText(orderDetails.get(position).getItems().get(1));
        holder.mAddressLine1.setText(orderDetails.get(position).getAddress().getLine1());
        holder.mAddressLine2.setText(orderDetails.get(position).getAddress().getLine2());
        holder.mExtraName.setText(orderDetails.get(position).getName());
        holder.mExtraOrderno.setText(orderDetails.get(position).getOrderId());
        holder.mExtraPhone.setText(orderDetails.get(position).getPhones().get(0).getNumber());
        holder.mExtraEmail.setText(orderDetails.get(position).getMailId());

        //holder.mAddressLine1.setText(orderDetails.get(position).getAddress().getHouseNumber());
        //holder.mAddressLocality.setText(orderDetails.get(position).getAddress().getAddressLine1());
        //holder.mAddressLandmark.setText(orderDetails.get(position).getAddress().getAddressLine2());
        //holder.mCity.setText(orderDetails.get(position).getAddress().getCity());
        if( mRouteSession.getmMatrixDownloadStatus()==1)
        {
            if (mRouteSession.getmOrderList().get(position).getDeliveryStatus().equalsIgnoreCase(OrderStatus.IN_TRANSIT))
            {
                holder.mDistance.setText(mRouteSession.getmDistanceList().get(position));
                holder.mTime.setText(mRouteSession.getmDurationList().get(position));
                holder.mExtraDistance.setText(mRouteSession.getmDistanceList().get(position));
                holder.mExtraTime.setText(mRouteSession.getmDurationList().get(position));

            }
            else
            {
                holder.mStatusImage.setImageResource(R.drawable.ic_tick);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return orderDetails.size();
    }

    public Order getItem(int position) {
        return orderDetails.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView mOrderno;
        TextView mName;
        TextView mAddressLine1,mAddressLine2;
        TextView mAddressLocality;
        TextView mAddressLandmark;
        TextView mCity;
        TextView mItems1,mItems2;
        ImageView mStatusImage,mCallImage;
        TextView mDistance;
        TextView mTime;
        TextView mExtraName;
        TextView mExtraOrderno;
        TextView mExtraExpectedTime;
        TextView mExtraProducts;
        TextView mExtraPhone;
        TextView mExtraEmail;
        TextView mExtraAddress;
        TextView mExtraDistance;
        TextView mExtraTime;
        FloatingActionButton mCallCustomer;
        FloatingActionButton mFullScreenMapFAB;
        public ViewHolder(View itemView)
        {
            super(itemView);
            mName=(TextView)itemView.findViewById(R.id.nameedittext);
            mAddressLine1=(TextView)itemView.findViewById(R.id.Line1addressedittext);
            mAddressLine2=(TextView)itemView.findViewById(R.id.Line2addressedittext);
            mItems1=(TextView)itemView.findViewById(R.id.itemedittext1);
            mItems2=(TextView)itemView.findViewById(R.id.itemedittext2);
            mDistance=(TextView)itemView.findViewById(R.id.distancetextview);
            mTime=(TextView)itemView.findViewById(R.id.timetextview);
            mStatusImage=(ImageView)itemView.findViewById(R.id.orderstatusimage);
            mExtraName=(TextView)itemView.findViewById(R.id.nametextview);
            mExtraOrderno=(TextView)itemView.findViewById(R.id.ordernotextview);
            mExtraExpectedTime=(TextView)itemView.findViewById(R.id.expectedtimetextview);
            mExtraPhone=(TextView)itemView.findViewById(R.id.phonetextview);
            mExtraEmail=(TextView)itemView.findViewById(R.id.emailtextview);
            mExtraDistance=(TextView)itemView.findViewById(R.id.Extradistancetextview);
            mExtraTime=(TextView)itemView.findViewById(R.id.Extratimetextview);
            mFullScreenMapFAB=(FloatingActionButton)itemView.findViewById(R.id.FullScreenMapFloatingActionButton);
            mCallCustomer=(FloatingActionButton)itemView.findViewById(R.id.CallActionFloatingActionButton);
            itemView.setOnClickListener(this);
            mStatusImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OrdersListFragment) mFragmentInstance).onClickOrderStatus(v);
                }
            });
            mCallCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OrdersListFragment) mFragmentInstance).onClickCallExecutive(v);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public interface OrderItemClickListener {
        public void onItemClick(int position, View v);
    }
}


