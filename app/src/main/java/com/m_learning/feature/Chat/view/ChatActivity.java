package com.m_learning.feature.Chat.view;
import static com.m_learning.utils.ConstantApp.TYPE_USER;

import static com.m_learning.utils.ConstantApp.ADMIN_ID;
import static com.m_learning.utils.ConstantApp.FROM_CHAT;
import static com.m_learning.utils.ConstantApp.FROM_NOTIFICATION;
import static com.m_learning.utils.ConstantApp.FROM_OTHERS;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;
import static com.m_learning.utils.ConstantApp.RECEIVER_ID;
import static com.m_learning.utils.ConstantApp.TB_CHAT_ROOMS;
import static com.m_learning.utils.ConstantApp.TYPE_IMAGE;
import static com.m_learning.utils.ConstantApp.TYPE_TEXT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.m_learning.R;
import com.m_learning.databinding.ActivityChatBinding;
import com.m_learning.feature.Chat.adapter.ChatRecyclerAdapter;
import com.m_learning.feature.Chat.model.Chat;
import com.m_learning.feature.Chat.model.UserData;
import com.m_learning.feature.Chat.presenter.ChatPresenter;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.network.feature.ChatFirebase;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.ItemSelectImageDialogFragment;
import com.m_learning.utils.LoadingDialog;
import com.m_learning.utils.MLearningApp;
import com.m_learning.utils.ToolUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;


public class ChatActivity extends BaseActivity implements TextWatcher, ChatContract.View,
        TextView.OnEditorActionListener, ItemSelectImageDialogFragment.Listener,
        View.OnClickListener, View.OnFocusChangeListener {


    private ActivityChatBinding binding;


    ImageButton btEmoji;
    int LIMIT = 30;
    int MORE_ITEM = 10;
    private ItemSelectImageDialogFragment mItemSelectImageDialogFragment;
    private LinearLayoutManager mLinearLayoutManager;
    private EmojIconActions emojIcon;
    //File
    private String receiverName = "";
    private String receiverEmail;
    private String receiverImage;
    private String receiverId;
    private String receiverFCMToken;
    private ChatPresenter mChatPresenter;
    private ChatRecyclerAdapter mChatRecyclerAdapter;
    private String roomId;
    private boolean statusActive;
    private boolean loading = false;
    private boolean userScrolled = false;
    private int comparison;
    private Activity activity;
    private String myId;
    private String receiverOS;
    private LoadingDialog mLoadingDialog;
    private ArrayList<String> messageIdsList = new ArrayList<>();

    public static Intent getIntent(Context activity, String roomId, String receiverId, int fromWhere) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        intent.putExtra(RECEIVER_ID, receiverId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        setContentView(R.layout.activity_chat);
        activity = this;
        mLoadingDialog = new LoadingDialog(activity);
        mLoadingDialog.setDialogCancelable();

        if (AppSharedData.getUserData() == null) {
            finish();
            return;
        }
        myId = AppSharedData.getUserData().getUserId() + "";
        receiverId = getIntent().getStringExtra(RECEIVER_ID);

        comparison = myId.compareTo(receiverId);

        if (comparison < 0) {
            roomId = myId + "_" + receiverId;
        } else {
            roomId = receiverId + "_" + myId;
        }
        Log.e("////myId: ", myId + " *");
        Log.e("////receiverId: ", receiverId + " *");
        Log.e("////roomId: ", roomId + " *");
        if (!ToolUtils.isNetworkConnected()) {
            Toast.makeText(this, getString(R.string.noInternetConnection), Toast.LENGTH_LONG).show();
            finish();
        } else {
            getOtherUserData();
        }
    }

    public String getRoomIdWithAdmin() {
        String chatId = "";
        int comparison = myId.compareTo(ADMIN_ID);

        if (comparison < 0) {
            chatId = myId + "_" + ADMIN_ID;
        } else {
            chatId = ADMIN_ID + "_" + myId;
        }
        Log.e("chat_d:", roomId + ", " + chatId);
        return chatId;
    }

    public void initDialog() {
        mItemSelectImageDialogFragment = ItemSelectImageDialogFragment.newInstance(getString(R.string.select_image_dialog_choose_image), 2);
        mItemSelectImageDialogFragment.setListener(this);
    }

    private void getOtherUserData() {
        try {
            showProgress();
            ChatFirebase.getInstance().startUserDataProfileListenerForSingleValueEvent(receiverId, new RequestListener<UserData>() {
                @Override
                public void onSuccess(UserData data) {

                    receiverName = data.getUserFullName();
                    receiverImage = data.getUserImage();
                    receiverFCMToken = data.getDeviceToken();
                    receiverOS = data.getUserOs();
                    try {
                        bindViews();
                        initDialog();
                        hideProgress();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }

                @Override
                public void onFail(String message) {
                    hideProgress();
                    Toast.makeText(activity, getString(R.string.notExist), Toast.LENGTH_LONG).show();
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindViews() {
        binding.tvTitle.setText(receiverName);
        binding.ivBack.setOnClickListener(this);

        binding.tvWarn.setVisibility(View.GONE);
        binding.editTextMessage.setOnEditorActionListener(this);
        binding.editTextMessage.setScroller(new Scroller(this));
        binding.editTextMessage.setMaxLines(4);
        binding.editTextMessage.setVerticalScrollBarEnabled(true);
        binding.editTextMessage.setMovementMethod(new ScrollingMovementMethod());

        binding.ibAddImg.setOnClickListener(this);
        binding.buttonMessage.setOnClickListener(this);
        binding.buttonMessage.setVisibility(View.VISIBLE);
        btEmoji = findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this, binding.contentRoot, binding.editTextMessage, btEmoji);
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.ic_sticker);

        emojIcon.ShowEmojIcon();

        binding.messageRecyclerView.setVisibility(View.VISIBLE);
        binding.messageRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.messageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mChatRecyclerAdapter = new ChatRecyclerAdapter(ChatActivity.this, new ArrayList<>(), receiverImage/*,rvListMessage*/);
        binding.messageRecyclerView.setAdapter(mChatRecyclerAdapter);
        binding.messageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                userScrolled = newState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ?
                        0 : recyclerView.getChildAt(0).getTop();
                LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItem = linearLayoutManager1.findFirstVisibleItemPosition();
                if (userScrolled && !loading && firstVisibleItem == 0 && topRowVerticalPosition >= 0) {
                    //Is this the place where top position of first item is reached ?
                    if (mChatRecyclerAdapter.getItemCount() > 10) {
                        binding.loadMore.setVisibility(View.VISIBLE);
                        loading = true;
                        mChatPresenter = new ChatPresenter(ChatActivity.this, ChatActivity.this);
                        mChatPresenter.getMessage(myId,
                                receiverId, MORE_ITEM + mChatRecyclerAdapter.getItemCount(), 1);

                    }
                } else {
                    binding.loadMore.setVisibility(View.GONE);
                }

            }
        });

        ChatFirebase.getInstance().startUserDataProfileListener(receiverId, new RequestListener<UserData>() {
            @Override
            public void onSuccess(UserData data) {
                receiverName = data.getUserFullName();
                receiverImage = data.getUserImage();
                receiverFCMToken = data.getDeviceToken();
                receiverOS = data.getUserOs();
            }

            @Override
            public void onFail(String message) {
                receiverFCMToken = "";
            }
        });

        mChatPresenter = new ChatPresenter(this, this);
        mChatPresenter.checkIfRoomExist(myId,
                receiverId);

        mChatPresenter.getMessage(myId, receiverId, LIMIT, 0);

        ChatFirebase.getInstance().changeStatusUserInRoom(roomId, myId, true);
        binding.editTextMessage.addTextChangedListener(this);
        binding.editTextMessage.setOnFocusChangeListener(this::onFocusChange);
        changeReceiverActivate(statusActive);
        checkReceiverUserActivate();
    }

    private void changeToReadStatus() {
        if (!messageIdsList.isEmpty()) {
            Log.i("messageIdsList size: ", messageIdsList.size() + "");
            for (int i = 0; i < messageIdsList.size(); i++) {
                String messageId = messageIdsList.get(i);
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(TB_CHAT_ROOMS)
                        .child(roomId).child("messages").child(messageId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child(TB_CHAT_ROOMS)
                                            .child(roomId).child("messages").child(messageId)
                                            .child("status").setValue(1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                if (i == messageIdsList.size() - 1)
                    messageIdsList.clear();
            }
        }
    }


    @Override
    public void onBackPressed() {
        try {
            ChatFirebase.getInstance().setTypeStatus(roomId, myId, false);
            ChatFirebase.getInstance().changeStatusUserInRoom(roomId, myId, false);
            ChatFirebase.getInstance().stopUserDataProfileListener(receiverId);

            ChatFirebase.getInstance().stopChildActiveListener(roomId, receiverId);
            ChatFirebase.getInstance().stopChildTypetListener(roomId, receiverId);

            if (mChatPresenter != null) mChatPresenter.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent().getIntExtra(FROM_WHERE, FROM_OTHERS) == FROM_NOTIFICATION && AppSharedData.getUserData() != null) {
            if (AppSharedData.getUserData().getUserType().equalsIgnoreCase(TYPE_USER)) {
                ToolUtils.goToMainUser(this, FROM_CHAT);
            } else {
                ToolUtils.goToTeacherMainActivity(this, FROM_CHAT);
            }
        } else {
            finish();
            super.onBackPressed();
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        MLearningApp.setChatActivityOpen(true);
    }

    private void checkReceiverUserActivate() {
        ChatFirebase.getInstance().startChildActiveListener(roomId, receiverId, new RequestListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                changeReceiverActivate(data);
            }

            @Override
            public void onFail(String message) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MLearningApp.setChatActivityOpen(true);
        ChatFirebase.getInstance().changeStatusUserInRoom(roomId, myId, true);

        ChatFirebase.getInstance().getRoomReference(new RequestListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChild(roomId)) {

                        ChatFirebase.getInstance().changeStatusUserInRoom(roomId, myId, true);
                        ChatFirebase.getInstance().countUnReadMessage(roomId, myId, 0);
                        ChatFirebase.getInstance().setTypeStatus(roomId, myId, false);

//                        ChatFirebase.getInstance().startChildActiveListener(roomId, receiverId, new RequestListener<Boolean>() {
//                            @Override
//                            public void onSuccess(Boolean data) {
//                                try {
//                                    statusActive = data;
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onFail(String message) {
//
//                            }
//                        });

                        ChatFirebase.getInstance().startChildTypetListener(roomId, receiverId, new RequestListener<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                if (data) {
                                    binding.viewTypingInd.setVisibility(View.VISIBLE);
                                    binding.messageRecyclerView.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
                                } else {
                                    binding.viewTypingInd.setVisibility(View.GONE);
                                    binding.llProgress.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFail(String message) {

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changeReceiverActivate(boolean statusActive) {
        this.statusActive = statusActive;
        if (statusActive || roomId.equals(getRoomIdWithAdmin())) {
            binding.ivConnect.setImageDrawable(getResources().getDrawable(R.drawable.green_circle_shape));
            binding.tvStatusTitle.setText(getResources().getString(R.string.online));
        } else {
            binding.ivConnect.setImageDrawable(getResources().getDrawable(R.drawable.gray_circle_shape));
            binding.tvStatusTitle.setText(getResources().getString(R.string.offline));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MLearningApp.setChatActivityOpen(false);

        try {
            ChatFirebase.getInstance().changeStatusUserInRoom(roomId, myId, false);
            ChatFirebase.getInstance().setTypeStatus(roomId, myId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            ChatFirebase.getInstance().changeStatusUserInRoom(roomId, myId, false);
            ChatFirebase.getInstance().setTypeStatus(roomId, myId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MLearningApp.setChatActivityOpen(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MLearningApp.setChatActivityOpen(false);
        try {
            ChatFirebase.getInstance().changeStatusUserInRoom(roomId, myId, false);
            ChatFirebase.getInstance().setTypeStatus(roomId, myId, false);

            ChatFirebase.getInstance().stopUserDataProfileListener(receiverId);
            ChatFirebase.getInstance().stopChildActiveListener(roomId, receiverId);
            ChatFirebase.getInstance().stopChildTypetListener(roomId, receiverId);

            if (mChatPresenter != null) mChatPresenter.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (!TextUtils.isEmpty(binding.editTextMessage.getText().toString().trim())) {
                binding.buttonMessage.setEnabled(false);
                sendTextMessage(binding.editTextMessage.getText().toString().trim());
            }
            return true;
        }
        return false;
    }

    private void sendTextMessage(String message) {
        String receiver = receiverName;
        String receiverUid = receiverId;
        String receiverIMAGE = receiverImage;
        UserData senderUser = new UserData(AppSharedData.getUserData());
        if (senderUser != null) {
            String sender = senderUser.getUserFullName();
            String senderImage = senderUser.getUserImage();
            String senderUid = myId;
            String receiverFirebaseToken = receiverFCMToken;
            int msgStatus = 1;
            if (!statusActive) {
                msgStatus = 0;
            }
            Chat chat = new Chat(TYPE_TEXT, sender,
                    receiver, senderImage, receiverIMAGE, senderUid, receiverUid, message, "",
                    ToolUtils.getGMTTimeInMillisecond(), msgStatus);
            mChatPresenter.sendMessage(getApplicationContext(),
                    chat,
                    receiverFirebaseToken, receiverOS);
        }
    }

    private void sendImgMessage(String imgUrl) {
        String receiver = receiverName;
        String receiverUid = receiverId;
        String receiverIMAGE = receiverImage;
        UserData senderUser = new UserData(AppSharedData.getUserData());
        if (senderUser != null) {
            String sender = senderUser.getUserFullName();
            String senderImage = senderUser.getUserImage();
            String senderUid = myId;
            String receiverFirebaseToken = receiverFCMToken;
            int msgStatus = 1;
            if (!statusActive) {
                msgStatus = 0;
            }
            Chat chat = new Chat(TYPE_IMAGE, sender,
                    receiver, senderImage, receiverIMAGE, senderUid, receiverUid, "", imgUrl,
                    ToolUtils.getGMTTimeInMillisecond(), msgStatus);
            mChatPresenter.sendMessage(getApplicationContext(),
                    chat,
                    receiverFirebaseToken, receiverOS);
            hideProgress();
        }
    }

    @Override
    public void onSendMessageSuccess(Chat chat) {
        binding.editTextMessage.setText("");
        binding.buttonMessage.setEnabled(true);
        ChatFirebase.getInstance().countUnReadMessage(roomId, myId, 0);
        if (!statusActive) {
            if (AppSharedData.getUserData() != null) {
                if (chat.type.equals(TYPE_IMAGE)) {
                    chat.message = getString(R.string.image_msg);
                }
                mChatPresenter.sendPushNotificationToReceiver(chat.senderName, chat.message, chat.senderUid,
                        "", receiverFCMToken, receiverOS, receiverId);
            }

            ChatFirebase.getInstance().getCountUnReadMessage(roomId, receiverId);
        }
        Log.e("active_onSendMessageSu", receiverFCMToken + "");

    }

    @Override
    public void onSendMessageFailure(String message) {
        binding.buttonMessage.setEnabled(true);
    }

    @Override
    public void onGetMessagesSuccess(Chat chat, int i) {
        binding.progress.setVisibility(View.GONE);
        binding.loadMore.setVisibility(View.GONE);
        loading = false;

        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(ChatActivity.this,
                    new ArrayList<Chat>(), receiverImage/*,rvListMessage*/);
            binding.messageRecyclerView.setAdapter(mChatRecyclerAdapter);
        }
        Log.e("size myId: ", myId + "");
        if (chat.status == 0 && chat.receiverUid.equals(myId)) {
            messageIdsList.add(chat.messageId);
            Log.e("size message add: ", messageIdsList.size() + "");
        } else Log.e("size myId: ", chat.status + " " + chat.receiverUid);
        mChatRecyclerAdapter.add(chat);


        if (i == 1) {
//            binding.messageRecyclerView.smoothScrollToPosition(0);
        } else {
            binding.messageRecyclerView.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
        }
        mChatRecyclerAdapter.notifyDataSetChanged();


    }

    @Override
    public void onUpdateMessagesStatus(Chat chat, int i) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(ChatActivity.this,
                    new ArrayList<Chat>(), receiverImage/*,rvListMessage*/);
            binding.messageRecyclerView.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.update(chat);
        if (chat.status == 0 && chat.receiverUid.equals(myId)) {
            messageIdsList.add(chat.messageId);
            Log.e("size message update: ", messageIdsList.size() + "");
        }
    }

    @Override
    public void onDeleteMessagesStatus(String messageId, int i) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(ChatActivity.this,
                    new ArrayList<Chat>(), receiverImage/*,rvListMessage*/);
            binding.messageRecyclerView.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.remove(messageId);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        binding.progress.setVisibility(View.GONE);
        binding.loadMore.setVisibility(View.GONE);
        loading = false;
    }

    @Override
    public void showProgress() {
        mLoadingDialog.showDialog();
    }

    @Override
    public void hideProgress() {
        mLoadingDialog.hideDialog();
    }

    @Override
    public void onSuccessResponse(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorMessage(String string) {
        AppShareMethods.showSnackBar(this, binding.contentRoot, string);
    }

    @Override
    public void showImageDialog() {
        mItemSelectImageDialogFragment.show(getSupportFragmentManager(), ItemSelectImageDialogFragment.class.getSimpleName());
    }

    @Override
    public void setLicenseImageName(File file) {
        try {
            showProgress();

            String imageName = UUID.randomUUID().toString();

            Bitmap bmp = MediaStore.Images.Media.getBitmap((ChatActivity.this).getContentResolver(), Uri.fromFile(file));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            ChatFirebase.getInstance().saveImageToFirestore(imageName, data, new RequestListener<Uri>() {
                @Override
                public void onSuccess(Uri data) {
                    sendImgMessage(data.toString());
                }

                @Override
                public void onFail(String message) {
                    AppShareMethods.showSnackBar(ChatActivity.this, binding.contentRoot, message);
                }
            });
        } catch (IOException e) {
            AppShareMethods.showSnackBar(this, binding.contentRoot, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_add_img:
                mChatPresenter.chooseProfileImage();
                break;
            case R.id.buttonMessage:
                if (!TextUtils.isEmpty(binding.editTextMessage.getText().toString().trim())) {
                    binding.buttonMessage.setEnabled(false);
                    sendTextMessage(binding.editTextMessage.getText().toString().trim());
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mChatPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        try {
            ChatFirebase.getInstance().countUnReadMessage(roomId, myId, 0);
            ChatFirebase.getInstance().changeStatusUserInRoom(roomId, myId, true);

            if (!charSequence.toString().equals("")) {
//                binding.buttonMessage.setVisibility(View.VISIBLE);
                ChatFirebase.getInstance().setTypeStatus(roomId, myId, true);
            } else {
//                binding.buttonMessage.setVisibility(View.GONE);
                ChatFirebase.getInstance().setTypeStatus(roomId, myId, false);
            }
            changeToReadStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        try {
            if (!hasFocus) {
                try {
                    ChatFirebase.getInstance().setTypeStatus(roomId, myId, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGalleryClicked() {
        mChatPresenter.onGalleryClicked();
    }

    @Override
    public void onCameraClicked() {
        mChatPresenter.onCameraClicked();
    }

    @Override
    public void onVideoClicked() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mChatPresenter.onRequestPermissionsResult(requestCode, grantResults);
    }
}
