package com.m_learning.feature.messages.adapter;

import static com.m_learning.utils.ConstantApp.TB_CHAT_ROOMS;
import static com.m_learning.utils.ConstantApp.TB_RECENT;
import static com.m_learning.utils.ConstantApp.TB_SEEN;
import static com.m_learning.utils.ConstantApp.TYPE_IMAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.m_learning.R;
import com.m_learning.feature.Chat.model.Chat;
import com.m_learning.feature.Chat.model.UserData;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.ToolUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;



public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.viewHolder> {
    private Activity activity;
    ArrayList<Chat> chatList;
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private String personImage;
    private String ReceiverName, ReceiverId;
    private String ReceiverImage;
    private long timeDiff;
    private String hours;
    private String minutes;
    private ValueEventListener valueListener;
    private View itemView;
    private static final int PENDING_REMOVAL_TIMEOUT = 10000; // 10sec

    ArrayList<Chat> itemsPendingRemoval;
    int lastInsertedIndex; // so we can add some more items for testing purposes
    boolean undoOn; // is undo on, you can turn it on from the toolbar menu
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Chat, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    private boolean swiped = false;
    ArrayList<UserData> userList;
    OnItemClickListener listener;
    private String myId;

    public MessagesAdapter() {
    }

    public MessagesAdapter(Activity activity, ArrayList<UserData> userList, ArrayList<Chat> chatList, OnItemClickListener listener) {
        this.activity = activity;
        this.userList = userList;
        this.chatList = chatList;
        this.listener = listener;
        itemsPendingRemoval = new ArrayList<>();

    }

    public void add(Chat chat) {
        chatList.add(chat);
        Collections.sort(chatList, new Comparator<Chat>() {
            @Override
            public int compare(Chat o1, Chat o2) {
                return (String.valueOf(o2.timestamp)).compareTo((String.valueOf(o1.timestamp)));
            }
        });
        notifyItemInserted(chatList.size() - 1);
    }

    public void remove(Chat chat) {
        int pos;
        int size = chatList.size();
        for (int i = 0; i < size; i++) {
            if (chatList.get(i).groupId.equals(chat.groupId)) {
                pos = i;
                chatList.remove(pos);
                System.out.println(i);
                chatList.add(chat);
                Collections.sort(chatList, new Comparator<Chat>() {
                    @Override
                    public int compare(Chat o1, Chat o2) {
                        return (String.valueOf(o2.timestamp)).compareTo((String.valueOf(o1.timestamp)));
                    }
                });
                notifyItemInserted(chatList.size() - 1);
            }
        }


    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_row, parent, false);
        return new viewHolder(itemView, TYPE_HEADER);

    }

    private static boolean containsId(ArrayList<Chat> list, long id) {
        for (Chat object : list) {
            if (object.timestamp == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.setIsRecyclable(false);
        Chat item = chatList.get(position);
        UserData userItem = null;
        if (AppSharedData.isUserLogin()) {
            myId = AppSharedData.getUserData().getUserId() + "";
        }
        if (item != null) {
            if (item.senderUid.equals(myId))
                userItem = new UserData(item.receiverUid, item.receiverName, item.receiverImage);//userList.get(position);
            else
                userItem = new UserData(item.senderUid, item.senderName, item.senderImage);//userList.get(position);
        }
        if (swiped) {
            holder.frameLayout.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.undoButton.setVisibility(View.VISIBLE);
            holder.parentPanel.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
            if (undoOn) {
                holder.undoButton.setVisibility(View.VISIBLE);
            } else {
                holder.undoButton.setVisibility(View.GONE);
            }
            holder.deleteButton.setOnClickListener(v -> remove(position));
            holder.undoButton.setOnClickListener(v -> cancel(holder, position));
        } else {
            holder.parentPanel.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
            holder.frameLayout.setVisibility(View.GONE);
            holder.undoButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);

        }

        assert userItem != null;

        assert userItem != null;
        ToolUtils.setRoundedImgWithProgress(activity, userItem.getUserImage(), holder.ivUserAvatar, holder.imageProgress);


        assert userItem != null;
        holder.tvName.setText(userItem.getUserFullName());
        if (item.type.equals(TYPE_IMAGE)) {
            holder.tvNotifDesc.setText(activity.getResources().getString(R.string.image_msg));
        } else {
            holder.tvNotifDesc.setText(item.message);
        }
        if (item.timestamp == -1) {
            holder.tvNotifTime.setVisibility(View.GONE);
        } else {
            holder.tvNotifTime.setVisibility(View.VISIBLE);
            holder.tvNotifTime.setText(ToolUtils.convertLongTime(item.timestamp));
        }

        getSeenStatus(holder, position);
        holder.itemView.setOnClickListener(view -> listener.onItemClick(view, position));

        holder.parentPanel.setBackgroundColor(activity.getResources().getColor(R.color.white));

    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }


    public void pendingRemoval(final int position) {
        final Chat item = chatList.get(position);
        if (!containsId(itemsPendingRemoval, item.timestamp)) {

            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        remove(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        swiped = false;
        FirebaseDatabase.getInstance().getReference()
                .child(TB_CHAT_ROOMS)
                .child(chatList.get(position).groupId).removeValue();
        FirebaseDatabase.getInstance().getReference()
                .child(TB_RECENT)
                .child(chatList.get(position).groupId).removeValue();
        Chat item = chatList.get(position);
        if (containsId(chatList, item.timestamp)) {
            chatList.remove(position);
            notifyItemRemoved(position);
        }
        notifyDataSetChanged();
    }

    public void cancel(viewHolder holder, int position) {
        swiped = false;

        Chat item = chatList.get(position);

        if (containsId(chatList, item.timestamp)) {
            holder.frameLayout.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.undoButton.setVisibility(View.GONE);
            holder.parentPanel.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
            notifyItemChanged(position);
        }

        notifyDataSetChanged();
    }

    public boolean isPendingRemoval(int position) {
        Chat item = chatList.get(position);
        return (containsId(itemsPendingRemoval, item.timestamp));
    }

    private void getSeenStatus(final viewHolder holder, int position) {
        FirebaseDatabase.getInstance().getReference().child(TB_SEEN)
                .child(chatList.get(position).groupId).child(AppSharedData.getUserData().getUserId() + "")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("///////////*", dataSnapshot.child("counter").getValue() + "");
                        if (dataSnapshot.child("counter").getValue() != null) {
                            int counter = Integer.parseInt(dataSnapshot.child("counter").getValue() + "");
                            if (counter != 0) {
                                holder.count.setText(counter + "");
                                holder.count.setVisibility(View.VISIBLE);
                            } else {
                                holder.count.setVisibility(View.GONE);
                            }
                        } else holder.count.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getReceiverToken(final Chat chat, final viewHolder holder) {
        if (TextUtils.equals(chat.senderUid,
                AppSharedData.getUserData().getUserId() + "")) {
            personImage = chat.receiverImage;
            ReceiverImage = chat.receiverImage;
            ReceiverName = chat.receiverName;
            ReceiverId = chat.receiverUid;
        } else {
            personImage = chat.senderImage;
            ReceiverImage = chat.senderImage;
            ReceiverName = chat.senderName;
            ReceiverId = chat.senderUid;
        }
        holder.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void showDeleteView(final viewHolder holder, final int swipedPosition) {
        swiped = true;
        notifyItemChanged(swipedPosition);
    }

    public void drawItem(viewHolder holder) {
        holder.frameLayout.setVisibility(View.VISIBLE);
        holder.deleteButton.setVisibility(View.VISIBLE);
        holder.undoButton.setVisibility(View.VISIBLE);
        holder.parentPanel.setVisibility(View.GONE);
        holder.line.setVisibility(View.GONE);
        notifyDataSetChanged();

    }


    public class viewHolder extends RecyclerView.ViewHolder {


        LinearLayout llChatParent;
        LinearLayout parentPanel;
        ImageView ivUserAvatar;
        CircularProgressIndicator imageProgress;
        TextView count;
        TextView tvName;
        TextView tvNotifTime;
        CircularProgressIndicator progress;
        TextView tvNotifDesc;
        RelativeLayout frameLayout;
        Button deleteButton;
        Button undoButton;
        View line;
        int viewType;

        public viewHolder(final View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            llChatParent = itemView.findViewById(R.id.llChatParent);
            parentPanel = itemView.findViewById(R.id.parentPanel);
            ivUserAvatar = itemView.findViewById(R.id.iv_userAvatar);
            imageProgress = itemView.findViewById(R.id.imageProgress);
            count = itemView.findViewById(R.id.count);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNotifTime = itemView.findViewById(R.id.tv_notifTime);
            progress = itemView.findViewById(R.id.progress);
            tvNotifDesc = itemView.findViewById(R.id.tv_notifDesc);
            frameLayout = itemView.findViewById(R.id.frameLayout);
            deleteButton = itemView.findViewById(R.id.delete_button);
            undoButton = itemView.findViewById(R.id.undo_button);
            line = itemView.findViewById(R.id.line);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return TYPE_HEADER;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
