package com.m_learning.feature.lecture;


import static com.m_learning.utils.ConstantApp.FROM_STUDENTS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.m_learning.R;
import com.m_learning.network.model.Lecture;
import com.m_learning.network.utils.FirebaseReferance;
import com.m_learning.utils.ToolUtils;

import java.util.ArrayList;


public class LecturesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int TYPE_ITEM = 0;
    public static final int TYPE_LAST_ITEM = 1;
    private final int fromWhere;
    public ArrayList<Lecture> list = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public LecturesAdapter(Context context, OnItemClickListener listener, int fromWhere) {
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
                view = LayoutInflater.from(context).inflate(R.layout.item_lecture, parent, false);
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

    private void setObj(MyViewHolder holder, Lecture item, int position) {
        holder.tvTitle.setText(item.getTitle());
        holder.tvDate.setText(ToolUtils.convertMillisecondToDate(item.getCreatedAt()));
        getViews(holder, item);
        ToolUtils.setImg(context, item.getImage(), holder.ivImage);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(item, position);
        });
        holder.root.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(item, position);
        });
        holder.ivDelete.setOnClickListener(view -> {
            if (listener != null) listener.onDelete(item, position);
        });
        holder.ivEdit.setOnClickListener(view -> {
            if (listener != null) listener.onEdit(item, position);
        });
        if (fromWhere == FROM_STUDENTS) {
            holder.llAction.setVisibility(View.GONE);
        } else holder.llAction.setVisibility(View.VISIBLE);


    }

    private void getViews(MyViewHolder holder, Lecture item) {
        FirebaseReferance.getLecturesViewsReference(item.getId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long viewsNo = 0;
                        if (dataSnapshot.exists()) {
                            viewsNo = dataSnapshot.getChildrenCount();
                            holder.tvViewNo.setText(String.format(context.getString(R.string.views_formatted), String.valueOf(viewsNo)));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public Lecture getItem(int pos) {
        return list.get(pos);
    }

    public void addLoadingFooter() {
        add(null);
    }

    public void add(int location, Lecture item) {
        list.add(location, item);
        notifyItemInserted(location);
    }

    public void add() {
        list.add(null);
        notifyDataSetChanged();
    }

    public void add(Lecture mc) {
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

    public void addItem(ArrayList<Lecture> userList) {
        for (Lecture mc : userList) {
            if (!ToolUtils.containLectureId(list, mc.getId()))
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

    public ArrayList<Lecture> getList() {
        return list;
    }

    public interface OnItemClickListener {
        void onItemClick(Lecture bean, int positions);

        void onDelete(Lecture bean, int positions);

        void onEdit(Lecture bean, int positions);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDelete;
        TextView tvTitle;
        TextView tvViewNo;
        TextView tvDate;
        ImageView ivImage;
        ImageView ivEdit;
        LinearLayout root;
        LinearLayout llAction;


        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvViewNo = itemView.findViewById(R.id.tv_view_no);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivImage = itemView.findViewById(R.id.iv_image);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            root = itemView.findViewById(R.id.root);
            llAction = itemView.findViewById(R.id.ll_actions);
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
