package com.guy.gonenapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class Adapter_Channel extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Channel> channels = new ArrayList<>();

    public Adapter_Channel(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_channel, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;

            Channel channel = getItem(position);

            holder.channel_LBL_title.setText(channel.getTitle());
            holder.channel_SPC_graph.refreshData(channel.getData());

        }
    }


    @Override
    public int getItemCount() {
        return channels.size();
    }

    private Channel getItem(int position) {
        if (position >= 0  &&  position < channels.size()) {
            return channels.get(position);
        } 
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView channel_LBL_title;
        private VM channel_SPC_graph;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.channel_LBL_title =  itemView.findViewById(R.id.channel_LBL_title);
            this.channel_SPC_graph =  itemView.findViewById(R.id.channel_SPC_graph);
        }

    }

}

