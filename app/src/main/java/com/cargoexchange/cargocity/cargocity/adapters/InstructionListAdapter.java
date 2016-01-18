package com.cargoexchange.cargocity.cargocity.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.models.NavigationInstructionModel;

import java.util.ArrayList;

/**
 * Created by root on 18/1/16.
 */
public class InstructionListAdapter extends RecyclerView.Adapter<InstructionListAdapter.InstructionViewHolder>
{
    private final ArrayList<NavigationInstructionModel> navigationList;
    public InstructionListAdapter(ArrayList<NavigationInstructionModel> navigationList)
    {
        this.navigationList=navigationList;

    }
    @Override
    public InstructionViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final LayoutInflater mLayoutInflator=LayoutInflater.from(parent.getContext());
        View v=mLayoutInflator.inflate(R.layout.row_instructions, parent, false);
        final InstructionViewHolder vh= new InstructionViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(InstructionViewHolder holder, int position)
    {
        holder.instruction.setText(navigationList.get(position).getInstruction());
        holder.maneuver.setImageDrawable(null);
        switch(navigationList.get(position).getManeuver().toString())
        {
            case "turn-left":
                holder.maneuver.setImageResource(R.drawable.ic_turnleft);
                break;
            case "turn-right":
                holder.maneuver.setImageResource(R.drawable.ic_turnright);
                break;
            case "uturn-right":
                break;
            case "uturn-left":
                break;
            case "straight":
                holder.maneuver.setImageResource(R.drawable.ic_straight);
                break;
            case "keep-right":
                holder.maneuver.setImageResource(R.drawable.ic_keepright);
                break;
            case "keep-left":
                holder.maneuver.setImageResource(R.drawable.ic_keepleft);
                break;
            case "turn-sharp-right":
                break;
            case "turn-sharp-left":
                break;
            case "turn-slight-left":
                break;
            case "turn-slight-right":
                break;
        }
    }

    @Override
    public int getItemCount() {
        return navigationList.size();
    }


    class InstructionViewHolder extends RecyclerView.ViewHolder
    {
        TextView instruction;
        ImageView maneuver;
        public InstructionViewHolder(View itemView)
        {
            super(itemView);
            instruction=(TextView)itemView.findViewById(R.id.html_instructions);
            maneuver=(ImageView)itemView.findViewById(R.id.maneuver_image);
        }
    }
}
