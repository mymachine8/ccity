package com.cargoexchange.cargocity.cargocity.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.models.Order;

import java.util.List;

/**
 * Created by root on 19/1/16.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>
{
    private List<Order> orderDetails;
    public OrderDetailsAdapter(List<Order> orderDetails)
    {
        this.orderDetails=orderDetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final LayoutInflater layoutInflator=LayoutInflater.from(parent.getContext());
        final View v=layoutInflator.inflate(R.layout.row_orderdetails, parent, false);
        final ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        holder.mOrderno.setText(orderDetails.get(position).getOrderId());
        holder.mName.setText(orderDetails.get(position).getCustomer().getFirstName());
        holder.mAddressLine1.setText(orderDetails.get(position).getAddress().getHouseNumber());
        holder.mAddressLocality.setText(orderDetails.get(position).getAddress().getAddressLine1());
        holder.mAddressLandmark.setText(orderDetails.get(position).getAddress().getAddressLine2());
        holder.mCity.setText(orderDetails.get(position).getAddress().getCity());
    }

    @Override
    public int getItemCount()
    {
        return orderDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView mOrderno;
        TextView mName;
        TextView mAddressLine1;
        TextView mAddressLocality;
        TextView mAddressLandmark;
        TextView mCity;
        TextView mItems;
        ImageView mStatusImage;
        TextView mDistance;
        TextView mTime;
        public ViewHolder(View itemView)
        {
            super(itemView);
            mOrderno=(TextView)itemView.findViewById(R.id.ordernoedittext);
            mName=(TextView)itemView.findViewById(R.id.nameedittext);
            mAddressLine1=(TextView)itemView.findViewById(R.id.Line1addressedittext);
            mAddressLocality=(TextView)itemView.findViewById(R.id.Localityaddressedittext);
            mAddressLandmark=(TextView)itemView.findViewById(R.id.Landmarkaddressedittext);
            mCity=(TextView)itemView.findViewById(R.id.cityedittext);
            mItems=(TextView)itemView.findViewById(R.id.itemedittext);
            mStatusImage=(ImageView)itemView.findViewById(R.id.orderstatusimage);
            mDistance=(TextView)itemView.findViewById(R.id.distancetextview);
            mTime=(TextView)itemView.findViewById(R.id.timetextview);
        }
    }
}

