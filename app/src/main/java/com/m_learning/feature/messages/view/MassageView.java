package com.m_learning.feature.messages.view;


import com.m_learning.feature.Chat.model.Chat;
import com.m_learning.feature.Chat.model.UserData;
import com.m_learning.feature.baseView.BaseView;

import java.util.ArrayList;

public interface MassageView extends BaseView {
    void showErrorMsg(String error);

    void showEmptyResult();

    void setMessagesList(ArrayList<Chat> list);

    void setUserData(UserData user, Chat chat);

    void showNotLoginView();

}
