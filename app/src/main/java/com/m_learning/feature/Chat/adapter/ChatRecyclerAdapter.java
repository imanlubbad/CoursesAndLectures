package com.m_learning.feature.Chat.adapter;


import static com.m_learning.utils.ConstantApp.TYPE_IMAGE;
import static com.m_learning.utils.ConstantApp.TYPE_TEXT;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.m_learning.R;
import com.m_learning.feature.Chat.model.Chat;
import com.m_learning.feature.Chat.model.UserData;
import com.m_learning.feature.Chat.view.ChatContract;
import com.m_learning.utils.AppSharedData;
import com.m_learning.feature.general.ImageFullScreenActivity;
import com.m_learning.utils.MediaData;
import com.m_learning.utils.ToolUtils;

import java.util.Collections;
import java.util.List;


public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_LOADMORE = 0;
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_ME_IMG = 2;
    private static final int VIEW_TYPE_OTHER = 3;
    private static final int VIEW_TYPE_OTHER_IMG = 4;
    private List<Chat> mChats;
    Activity mActivity;
    RecyclerView mRecycleView;
    private ChatContract.OnLoadMoreListener onLoadMoreListener;
    private boolean loading = false;
    private UserData otherPerson;
    private String otherId;
    String receiverImg = "";

    public ChatRecyclerAdapter(Activity activity, List<Chat> chats, String receiverImg/*, RecyclerView recycleView*/) {
        mChats = chats;
        mActivity = activity;
        this.receiverImg = receiverImg;

    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setOnLoadMoreListener(ChatContract.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void update(Chat chat) {
        try {
            String chatKey = chat.messageId;
            int eventIndex = indexOfMessage(mChats, chatKey);
            if (eventIndex > -1) {
                if (containsUnReadMsg(mChats, chat.receiverUid)) {
                    chat.status = 1;
                }
                mChats.set(eventIndex, chat);
                notifyItemChanged(eventIndex);
            } else {
                add(chat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(String messageId) {

        int eventIndex = indexOfMessage(mChats, messageId);
        if (eventIndex > -1) {
            // Remove data from the list
            mChats.remove(eventIndex);

            // Update the RecyclerView
            notifyItemRemoved(eventIndex);
        }
    }

    public void add(Chat chat) {
        if (!containsId(mChats, chat.timestamp)) {
            if (containsUnReadMsg(mChats, chat.receiverUid)) {
                chat.status = 1;
            }
            mChats.add(chat);

            Collections.sort(mChats, (o1, o2) -> (String.valueOf(o1.timestamp)).compareTo((String.valueOf(o2.timestamp))));
            notifyItemInserted(mChats.size() - 1);
        }
    }

    public static int indexOfMessage(List<Chat> list, String key) {
        for (Chat object : list) {
            if (object.messageId.equals(key)) {
                return list.indexOf(object);
            }
        }
        return -1;
    }

    public static boolean containsId(List<Chat> list, long id) {
        for (Chat object : list) {
            if (object.timestamp == id) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsUnReadMsg(List<Chat> list, String receiverId) {
        String myId = (AppSharedData.getUserData() != null) ? AppSharedData.getUserData().getUserId() + "" : "";
        for (Chat object : list) {
            if (object.status == 0 && object.receiverUid.equals(myId)) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_ME_IMG:
                View viewChatMineImg = layoutInflater.inflate(R.layout.item_chat_mine_img, parent, false);
                viewHolder = new MyChatImgViewHolder(viewChatMineImg);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
            case VIEW_TYPE_OTHER_IMG:
                View viewChatOtherImg = layoutInflater.inflate(R.layout.item_chat_other_img, parent, false);
                viewHolder = new OtherChatImgViewHolder(viewChatOtherImg);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (TextUtils.equals(mChats.get(position).senderUid, AppSharedData.getUserData().getUserId() + "")) {
            if (mChats.get(position).type.equals(TYPE_TEXT))
                configureMyChatViewHolder((MyChatViewHolder) holder, position);
            if (mChats.get(position).type.equals(TYPE_IMAGE))
                configureMyChatViewHolderImg((MyChatImgViewHolder) holder, position);
        } else {
            if (mChats.get(position).type.equals(TYPE_TEXT))
                configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
            if (mChats.get(position).type.equals(TYPE_IMAGE))
                configureOtherChatViewHolderImg((OtherChatImgViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);

//        String alphabet = chat.senderName.substring(0, 1);
        Log.i("////:chat ", chat.message + " ");
        myChatViewHolder.txtChatMessage.setText(chat.message);

        if (chat.timestamp + "" == null) return;
        else
            myChatViewHolder.tv_date.setText(ToolUtils.convertLongTime(chat.timestamp));
    }

    private void configureMyChatViewHolderImg(MyChatImgViewHolder myChatViewHolder, int position) {
        final Chat chat = mChats.get(position);
        Log.i("////:chat ", chat.mediaUrl + " ");
        try {
            ToolUtils.setImg(mActivity, chat.mediaUrl, myChatViewHolder.img_view_chat_message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        myChatViewHolder.img_view_chat_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MediaData mediaData=new MediaData(chat.mediaUrl, "image", "");
                mActivity.startActivity(ImageFullScreenActivity.newInstance(mActivity, mediaData,""));
            }
        });
        if (chat.timestamp + "" == null) return;
        else
            myChatViewHolder.tv_date.setText(ToolUtils.convertLongTime(chat.timestamp));
    }

    private void configurLoadMoreViewHolder(Loadmore loadMoreViewHolder, int position) {
        if (position == 0) {
            loadMoreViewHolder.loadMore.setVisibility(View.VISIBLE);
        } else {
            loadMoreViewHolder.loadMore.setVisibility(View.GONE);
        }
    }

    private void configureOtherChatViewHolder(final OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        if (chat.senderUid.equals(AppSharedData.getUserData().getUserId() + "")) {
            otherId = chat.receiverUid;
        } else {
            otherId = chat.senderUid;
        }
        try {
            Log.i("///receiverImg: ", receiverImg + " *");
//            Glide.with(mActivity)
//                    .load(receiverImg).into(otherChatViewHolder.ivUserChat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        otherChatViewHolder.txtChatMessage.setText(chat.message);

        if (chat.timestamp + "" == null) return;
        else
            otherChatViewHolder.tv_date.setText(ToolUtils.convertLongTime(chat.timestamp));
        if (chat.status == 0 && chat.receiverUid.equals(AppSharedData.getUserData().getUserId().toString())) {
            otherChatViewHolder.llUnreadMsg.setVisibility(View.VISIBLE);
        } else {
            otherChatViewHolder.llUnreadMsg.setVisibility(View.GONE);
        }
    }

    private void configureOtherChatViewHolderImg(final OtherChatImgViewHolder otherChatViewHolder,
                                                 int position) {
        final Chat chat = mChats.get(position);
        if (chat.senderUid.equals(AppSharedData.getUserData().getUserId().toString())) {
            otherId = chat.receiverUid;
        } else {
            otherId = chat.senderUid;
        }
        try {
            Glide.with(mActivity).load(chat.mediaUrl).into(otherChatViewHolder.img_view_chat_message);
        } catch (Exception e) {
        }
        otherChatViewHolder.img_view_chat_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaData mediaData = new MediaData(chat.mediaUrl, "image", "");
                mActivity.startActivity(ImageFullScreenActivity.newInstance(mActivity, mediaData,""));
            }
        });

        if (chat.timestamp + "" == null) return;
        else
            otherChatViewHolder.tv_date.setText(ToolUtils.convertLongTime(chat.timestamp));
        if (chat.status == 0 && chat.receiverUid.equals(AppSharedData.getUserData().getUserId().toString())) {
            otherChatViewHolder.llUnreadMsg.setVisibility(View.VISIBLE);
        } else {
            otherChatViewHolder.llUnreadMsg.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (AppSharedData.getUserData() != null && TextUtils.equals(mChats.get(position).senderUid,
                AppSharedData.getUserData().getUserId().toString())) {
            if (mChats.get(position).type.equals(TYPE_TEXT))
                return VIEW_TYPE_ME;
            if (mChats.get(position).type.equals(TYPE_IMAGE))
                return VIEW_TYPE_ME_IMG;
            else return VIEW_TYPE_ME;

        } else {
            if (mChats.get(position) != null && mChats.get(position).type.equals(TYPE_TEXT))
                return VIEW_TYPE_OTHER;
            if (mChats.get(position).type.equals(TYPE_IMAGE))
                return VIEW_TYPE_OTHER_IMG;
            else return VIEW_TYPE_OTHER;

        }
    }


    private class MyChatViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llUnreadMsg;
        private TextView txtChatMessage;
        private TextView tv_date, txtUserAlphabet;
        private ImageView ivUserChat;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            llUnreadMsg = itemView.findViewById(R.id.llUnreadMsg);
            txtChatMessage = itemView.findViewById(R.id.text_view_chat_message);
            tv_date = itemView.findViewById(R.id.tv_date);
            ivUserChat = itemView.findViewById(R.id.ivUserChat);
            ivUserChat.setVisibility(View.GONE);
        }
    }

    private class MyChatImgViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_view_chat_message;
        private TextView tv_date, txtUserAlphabet;
        private ImageView ivUserChat;
        private LinearLayout llUnreadMsg;

        public MyChatImgViewHolder(View itemView) {
            super(itemView);
            llUnreadMsg = itemView.findViewById(R.id.llUnreadMsg);
            img_view_chat_message = itemView.findViewById(R.id.img_view_chat_message);
            tv_date = itemView.findViewById(R.id.tv_date);
            ivUserChat = itemView.findViewById(R.id.ivUserChat);
            ivUserChat.setVisibility(View.GONE);
        }
    }

    private static class Loadmore extends RecyclerView.ViewHolder {
        private final ProgressBar loadMore;

        public Loadmore(View itemView) {
            super(itemView);
            loadMore = itemView.findViewById(R.id.progress);
            loadMore.setVisibility(View.GONE);
            loadMore.getIndeterminateDrawable().setColorFilter(Color.parseColor("#c0c0c0"), PorterDuff.Mode.MULTIPLY);

        }
    }

    private class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llUnreadMsg;
        private TextView txtChatMessage;
        private TextView tv_date, txtUserAlphabet;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            llUnreadMsg = itemView.findViewById(R.id.llUnreadMsg);
            txtChatMessage = itemView.findViewById(R.id.text_view_chat_message);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

    private class OtherChatImgViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_view_chat_message;
        private TextView tv_date, txtUserAlphabet;
        private LinearLayout llUnreadMsg;

        public OtherChatImgViewHolder(View itemView) {
            super(itemView);
            llUnreadMsg = itemView.findViewById(R.id.llUnreadMsg);
            img_view_chat_message = itemView.findViewById(R.id.img_view_chat_message);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }
}
