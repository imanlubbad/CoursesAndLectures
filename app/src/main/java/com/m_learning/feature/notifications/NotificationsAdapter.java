package com.m_learning.feature.notifications;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.m_learning.R;
import com.m_learning.feature.Chat.adapter.ChatRecyclerAdapter;
import com.m_learning.network.model.Notifications;
import com.m_learning.utils.ToolUtils;

import java.util.ArrayList;



public class NotificationsAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int TYPE_ITEM = 0;
    public static final int TYPE_LAST_ITEM = 1;
    public ArrayList<Notifications> list = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public NotificationsAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        switch (viewType) {
            case TYPE_LAST_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);
                viewHolder = new LoadMoreViewHolder(view);
                break;
            default:
                view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
                viewHolder = new MyViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_ITEM:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                setNotifications(myViewHolder, getItem(position), position);
                break;
        }
    }

    private void setNotifications(MyViewHolder holder, Notifications bean, int position) {
        holder.tvMsg.setText(bean.getMessage());
        holder.tvDate.setText(ToolUtils.convertTimeStamp(bean.getCreatedAt()));
        if ((position % 2) == 0) {
            holder.ivColor.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.icon_active);
            holder.ivColor.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.icon_active);
            holder.ivColor.setImageResource(R.drawable.red_circle);
        } else {
            holder.ivColor.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.marginBtnView);
            holder.ivColor.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.marginBtnView);
            holder.ivColor.setImageResource(R.drawable.circle_green);

        }
        if (!TextUtils.isEmpty(bean.getType())) {

        }

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(bean, position);
        });
        holder.llParent.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(bean, position);
        });

    }

    public Notifications getItem(int pos) {
        return list.get(pos);
    }

    public void addLoadingFooter() {
        add(null);
    }

    public void add(int location, Notifications item) {
        list.add(location, item);
        notifyItemInserted(location);
    }

    public void add() {
        list.add(null);
        notifyDataSetChanged();
    }

    public void add(Notifications mc) {
        list.add(mc);
        notifyItemInserted(list.size() - 1);
    }

    public void remove(int location) {
        if (location >= list.size())
            return;

        list.remove(location);
        notifyItemRemoved(location);
    }

    public void removeAll() {
        this.list.clear();
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<Notifications> userList) {
        for (Notifications mc : userList) {
            if (!ToolUtils.containNotificationsId(list, mc.getKey()))
                add(mc);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (list.get(position) == null) ? TYPE_LAST_ITEM : TYPE_ITEM;
    }

    public interface OnItemClickListener {
        void onItemClick(Notifications bean, int positions);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivColor;
        TextView tvMsg;
        TextView tvDate;
        LinearLayout llParent;


        public MyViewHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tv_msg);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivColor = itemView.findViewById(R.id.iv_color);
            llParent = itemView.findViewById(R.id.ll_parent);
        }

    }

    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        CircularProgressIndicator loadMore;

        LoadMoreViewHolder(final View itemView) {
            super(itemView);
            loadMore = itemView.findViewById(R.id.loadMore); }
    }


}
