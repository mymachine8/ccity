package com.cargoexchange.cargocity.cargocity.adapters;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
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
import com.cargoexchange.cargocity.cargocity.models.Order;

import java.util.List;

/**
 * Created by root on 19/1/16.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>
{
    private List<Order> orderDetails;
    private RouteSession mRouteSession;
    public OrderDetailsAdapter(List<Order> orderDetails)
    {
        this.orderDetails=orderDetails;
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
        holder.mName.setText(orderDetails.get(position).getCustomer().getFirstName()+" "+orderDetails.get(position).getCustomer().getLastName());
        holder.mItems1.setText(orderDetails.get(position).getOrderItemsList().get(0).getItemName());
        holder.mItems2.setText(orderDetails.get(position).getOrderItemsList().get(1).getItemName());
        holder.mAddressLine1.setText(orderDetails.get(position).getAddress().getAddressLine1());
        holder.mAddressLine2.setText(orderDetails.get(position).getAddress().getAddressLine2());

        //holder.mAddressLine1.setText(orderDetails.get(position).getAddress().getHouseNumber());
        //holder.mAddressLocality.setText(orderDetails.get(position).getAddress().getAddressLine1());
        //holder.mAddressLandmark.setText(orderDetails.get(position).getAddress().getAddressLine2());
        //holder.mCity.setText(orderDetails.get(position).getAddress().getCity());
        if(mRouteSession.getmOrderList().get(position).getMstatus()== OrderStatus.PENDING_DELIVERY && mRouteSession.getmMatrixDownloadStatus()==1)
        {
            holder.mDistance.setText(mRouteSession.getmDistanceList().get(position));
            holder.mTime.setText(mRouteSession.getmDurationList().get(position));
        }
        else
        {
            holder.mStatusImage.setImageResource(R.drawable.ic_tick);
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

    public class ViewHolder extends RecyclerView.ViewHolder
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
        FloatingActionButton mCallCustomer;
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
            mCallCustomer=(FloatingActionButton)itemView.findViewById(R.id.CallActionFloatingActionButton);
            //mCallImage=(ImageView)itemView.findViewById(R.id.callimage);
            //mOrderno=(TextView)itemView.findViewById(R.id.ordernoedittext);
            //mAddressLocality=(TextView)itemView.findViewById(R.id.Localityaddressedittext);
            //mAddressLandmark=(TextView)itemView.findViewById(R.id.Landmarkaddressedittext);
            //mCity=(TextView)itemView.findViewById(R.id.cityedittext);
        }
    }
}

