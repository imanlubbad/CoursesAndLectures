package com.m_learning.feature.messages.view;

import static com.m_learning.utils.ConstantApp.ADMIN_ID;
import static com.m_learning.utils.ConstantApp.FROM_OTHERS;
import static com.m_learning.utils.ConstantApp.TB_RECENT;
import static com.m_learning.utils.ConstantApp.TB_Users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.m_learning.R;
import com.m_learning.databinding.ActivityMessagesBinding;
import com.m_learning.feature.Chat.model.Chat;
import com.m_learning.feature.Chat.model.UserData;
import com.m_learning.feature.Chat.view.ChatActivity;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.feature.messages.adapter.MessagesAdapter;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.ToolUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class MessagesActivity extends BaseActivity implements MassageView {

    private String myId;
    private UserData myData;
    private DatabaseReference userRef;
    MassageView mView;
    private ValueEventListener valueListener;
    private ValueEventListener userValueListener;
    ArrayList<Chat> chatList = new ArrayList<>();
    ArrayList<String> userList = new ArrayList<>();
    ArrayList<UserData> usersList = new ArrayList<>();
    private DatabaseReference ref;
    private Query query;
    private ActivityMessagesBinding binding;
    private LinearLayoutManager mLayoutManager;
    ArrayList<Chat> chatsList = new ArrayList<>();
    private Activity activity;


    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, MessagesActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ref = FirebaseDatabase.getInstance().getReference().child(TB_RECENT);
        userRef = FirebaseDatabase.getInstance().getReference().child(TB_Users);

        initView();
        checkIfUserLoginOrNot();
    }


    private void initView() {
        binding.ibBack.setOnClickListener(v -> onBackPressed());
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvList.setLayoutManager(mLayoutManager);
        binding.rvList.setHasFixedSize(true);
    }


    public void showNotLoginView() {
//        binding.layout_not_login.setVisibility(View.VISIBLE);
        binding.rlIsLogin.setVisibility(View.GONE);
    }

    @Override
    public void showErrorMsg(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showEmptyResult() {
        binding.rvList.setVisibility(View.GONE);
        binding.llNoResult.setVisibility(View.VISIBLE);
        binding.tvNoResult.setText(R.string.no_messages);
    }

    @Override
    public void setMessagesList(ArrayList<Chat> list) {
        binding.llNoResult.setVisibility(View.GONE);
        binding.rvList.setVisibility(View.VISIBLE);
        userList.clear();
        chatsList.clear();
        chatsList.addAll(list);
        setAdapter(usersList, chatsList);
        Log.i("///: ", list.size() + " //");
    }

    @Override
    public void setUserData(UserData user, Chat chat) {
        binding.llNoResult.setVisibility(View.GONE);
        binding.rvList.setVisibility(View.VISIBLE);
        usersList.add(user);
        chatsList.add(chat);
        setAdapter(new ArrayList<>(), chatsList);
    }

    private void setAdapter(final ArrayList<UserData> userList, final ArrayList<Chat> list) {
        MessagesAdapter adapter = new MessagesAdapter(this, userList, list, (view, position) -> {
            String userId = "";
            Chat messages = list.get(position);
            if (AppSharedData.isUserLogin()) {
                myId = AppSharedData.getUserData().getUserId() + "";
            }
            if (messages.senderUid.equals(myId))
                userId = messages.receiverUid;
            else userId = messages.senderUid;
            openChat(userId);
        });
        binding.rvList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userValueListener != null)
            userRef.removeEventListener(userValueListener);
        if (valueListener != null)
            query.removeEventListener(valueListener);
    }


    public void checkIfUserLoginOrNot() {
        if (AppSharedData.isUserLogin()) {
            myData = new UserData(AppSharedData.getUserData());
            myId = myData.getMyIdMember();

            getMyMessages();
        } else {
            mView.showNotLoginView();
        }
    }


    public void getMyMessages() {
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            query = ref.orderByChild("timestamp");
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatList.clear();

                    for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
//                        Log.i("---- dataSnap key//", dataSnap.getKey());
                        String key = dataSnap.getKey();
                        String[] keyS = key.split("_");
                        String id1 = keyS[0];
                        String id2 = keyS[1];
                        if (id1.equals(myId) || id2.equals(myId)) {
                            Log.i("---- data //", dataSnap.getValue() + "");
                            Chat messages = dataSnap.getValue(Chat.class);
                            if (id1.equals(ADMIN_ID) || id2.equals(ADMIN_ID)) {
                                Log.i("---- data //", " chat with admin");
                                Log.i("---- ADMIN_ID //", ADMIN_ID);
                            }
                            chatList.add(messages);
                        }
                    }

                    Collections.sort(chatList, (o1, o2) -> (String.valueOf(o2.timestamp)).compareTo((String.valueOf(o1.timestamp))));

                    hideProgress();
                    if (!chatList.isEmpty()) {
                        setMessagesList(chatList);
                    } else {
                        showEmptyResult();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgress();
                }
            };
            query.addValueEventListener(valueListener);
        } else mView.showErrorMsg(getString(R.string.noInternetConnection));
    }


    public void getUserData(final Chat messages) {

        String userId = "";
        if (messages.senderUid.equals(myId))
            userId = messages.receiverUid;
        else userId = messages.senderUid;
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            userValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        JSONObject jsonObject = new JSONObject((Map) dataSnapshot.getValue());
                        UserData user = new Gson().fromJson(String.valueOf(jsonObject), UserData.class);
                        if (mView != null) {
                            mView.setUserData(user, messages);
                        }
                    }
                    hideProgress();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgress();
                }
            };
            userRef.child(userId).addListenerForSingleValueEvent(userValueListener);
        } else mView.showErrorMsg(getString(R.string.noInternetConnection));

    }

    public void openChat(String userId) {
        startActivity(ChatActivity.getIntent(this, "", userId, FROM_OTHERS));
    }


}
