package com.m_learning.feature.assignment;


import static com.m_learning.utils.ConstantApp.FROM_LECTURES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.m_learning.R;
import com.m_learning.network.model.Assignments;
import com.m_learning.utils.ToolUtils;

import java.util.ArrayList;


public class AssignmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int TYPE_ITEM = 0;
    public static final int TYPE_LAST_ITEM = 1;
    public ArrayList<Assignments> list = new ArrayList<>();
    private Context context;
    private int fromWhere;
    private OnItemClickListener listener;

    public AssignmentsAdapter(Context context, int fromWhere, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.fromWhere = fromWhere;
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
                view = LayoutInflater.from(context).inflate(R.layout.item_assignments, parent, false);
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
                setObj(myViewHolder, getItem(position), position);
                break;
        }
    }

    private void setObj(MyViewHolder holder, Assignments item, int position) {
        holder.tvTitle.setText(item.getTitle());
        holder.tvNote.setText(item.getNotes());
        if (fromWhere == (FROM_LECTURES)) {
            holder.tvStudentName.setText(item.getStudentName());
        } else holder.tvStudentName.setText(item.getCourseName());
        holder.tvDate.setText(ToolUtils.convertMillisecondToDate(item.getCreatedAt()));

        ToolUtils.setImg(context, item.getFileImage(), holder.ivImage);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(item, position);
        });
        holder.root.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(item, position);
        });
        String file = item.getFile();
        if (file.contains(".doc") || file.contains(".docx")) {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.word)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(holder.img);
        } else if (file.contains(".pdf")) {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.pdf)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(holder.img);
        } else if (file.contains(".xls") || file.toString().contains(".xlsx")) {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.excel)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(holder.img);
        } else if (file.contains(".ppt") || file.toString().contains(".pptx")) {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.powerpoint)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(holder.img);
        } else if (file.contains(".txt")) {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(holder.img);
        } else if (file.contains(".zip") || file.toString().contains(".rar")) {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.rar)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(holder.img);
        } else
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(holder.img);
        holder.img.setOnClickListener(v -> {
            ToolUtils.openFileUrl(context, file);
        });

    }

    public Assignments getItem(int pos) {
        return list.get(pos);
    }

    public void addLoadingFooter() {
        add(null);
    }

    public void add(int location, Assignments item) {
        list.add(location, item);
        notifyItemInserted(location);
    }

    public void add() {
        list.add(null);
        notifyDataSetChanged();
    }

    public void add(Assignments mc) {
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

    public void addItem(ArrayList<Assignments> userList) {
        for (Assignments mc : userList) {
            if (!ToolUtils.containAssignmentsId(list, mc.getId()))
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
        void onItemClick(Assignments bean, int positions);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvNote;
        TextView tvStudentName;
        TextView tvTitle;
        TextView tvDate;
        ImageView ivImage;
        ImageButton ibtnView;
        ImageView img;
        LinearLayout root;


        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvNote = itemView.findViewById(R.id.tv_note);
            tvDate = itemView.findViewById(R.id.tv_date);
            ibtnView = itemView.findViewById(R.id.ibtn_view);
            img = itemView.findViewById(R.id.ibtn_download);
            ivImage = itemView.findViewById(R.id.iv_image);
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
