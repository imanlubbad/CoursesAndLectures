package com.m_learning.feature.courseStudents;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.m_learning.R;
import com.m_learning.network.model.UserInfo;
import com.m_learning.utils.ToolUtils;

import java.util.ArrayList;


public class StudentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int TYPE_ITEM = 0;
    public static final int TYPE_LAST_ITEM = 1;
    public ArrayList<UserInfo> list = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public StudentsAdapter(Context context, OnItemClickListener listener) {
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
                view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
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
                setObject(myViewHolder, getItem(position), position);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setObject(MyViewHolder holder, UserInfo item, int position) {
        holder.tvName.setText(item.getFullName());

        ToolUtils.setImgWithProgress(context, item.getUserImage(), holder.ivImage, holder.progress);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(item, position);
        });
        holder.root.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(item, position);
        });

    }

    public UserInfo getItem(int pos) {
        return list.get(pos);
    }

    public void addLoadingFooter() {
        add(null);
    }

    public void add(int location, UserInfo item) {
        list.add(location, item);
        notifyItemInserted(location);
    }

    public void add() {
        list.add(null);
        notifyDataSetChanged();
    }

    public void add(UserInfo mc) {
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

    public void addItem(ArrayList<UserInfo> userList) {
        for (UserInfo mc : userList) {
            if (!ToolUtils.containUserInfo(list, mc.getUserId()))
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
        void onItemClick(UserInfo item, int positions);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircularProgressIndicator progress;
        TextView tvName;
        ImageView ivImage;
        LinearLayout root;


        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            ivImage = itemView.findViewById(R.id.iv_image);
            progress = itemView.findViewById(R.id.progress);
            root = itemView.findViewById(R.id.root);
        }

    }

    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        CircularProgressIndicator loadMore;

        LoadMoreViewHolder(final View itemView) {
            super(itemView);
            loadMore = itemView.findViewById(R.id.loadMore);
        }
    }


}
