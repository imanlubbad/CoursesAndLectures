package com.m_learning.feature.studentMain;

import static com.m_learning.network.feature.User.logoutUser;
import static com.m_learning.utils.ConstantApp.FROM_HOME;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.m_learning.R;
import com.m_learning.databinding.ActivityMainBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.feature.courses.DiscoverCoursesFragment;
import com.m_learning.feature.courses.MyCoursesFragment;
import com.m_learning.utils.OnSearchListener;
import com.m_learning.feature.messages.view.MessagesActivity;
import com.m_learning.feature.notifications.NotificationsActivity;
import com.m_learning.feature.profile.ProfileActivity;
import com.m_learning.network.feature.User;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.MLearningApp;
import com.m_learning.utils.ToolUtils;

public class MainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener,
        NavigationView.OnNavigationItemSelectedListener, TextView.OnEditorActionListener {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;
    private boolean adLoaded = false;
    Fragment fragment = null;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String textForSearch = "";

    public static Intent newInstance(Activity mActivity, int fromWhere) {
        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.open, R.string.close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable =
                ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_baseline_menu_24,
                        getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(view1 -> binding.drawerLayout.openDrawer(GravityCompat.START));

        toggle.syncState();
        binding.navigationView.setNavigationItemSelectedListener(this);

        fragment = new MyCoursesFragment();
        replaceFragment(fragment);


        binding.tabLayout.addOnTabSelectedListener(this);
        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textForSearch = charSequence + "";
                search();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.edSearch.setOnEditorActionListener(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // setting adLoaded to true
                adLoaded = true;
                // Showing a simple Toast message to user when an ad is loaded
//                Toast.makeText(MainActivity.this, "Ad is Loaded", Toast.LENGTH_LONG).show();

            }


            @Override
            public void onAdOpened() {

                // Showing a simple Toast message to user when an ad opens and overlay and covers the device screen
//                Toast.makeText(MainActivity.this, "Ad Opened", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAdClicked() {

                // Showing a simple Toast message to user when a user clicked the ad
//                Toast.makeText(MainActivity.this, "Ad Clicked", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAdClosed() {
                //   Toast.makeText(MainActivity.this, "Ad is Closed", Toast.LENGTH_LONG).show();

            }
        });

        User.getInstance().startGetUserData(AppSharedData.getUserData().getUserId(), new RequestListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo data) {
                AppSharedData.setUserData(data);
                setInfoForUser();

            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    private void goToProfile() {
        startActivity(ProfileActivity.newInstance(this, AppSharedData.getUserData(), FROM_HOME));

    }

    private void goToChats() {
        startActivity(MessagesActivity.newInstance(this));


    }

    private void goToNotifications() {
        startActivity(NotificationsActivity.newInstance(this, FROM_HOME));

    }

    @SuppressLint("SetTextI18n")
    private void setInfoForUser() {
        UserInfo userInfo = AppSharedData.getUserData();
        if (userInfo != null) {
            View header = binding.navigationView.getHeaderView(0).getRootView();
            ImageView ivAvatar = header.findViewById(R.id.ivAvatar);
            CircularProgressIndicator progressAvatar = header.findViewById(R.id.progressAvatar);
            TextView tvName = header.findViewById(R.id.tv_name);
            TextView tvEmail = header.findViewById(R.id.tv_email);
            ToolUtils.setRoundedImgWithProgress(this, userInfo.getUserImage(), ivAvatar, progressAvatar);
            tvName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
            tvEmail.setText(userInfo.getEmail());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppSharedData.isUserLogin() && AppSharedData.getUserData() != null) {
            setInfoForUser();
            AppSharedData.setBadgeCount(AppSharedData.getUserData().getUserId() + "", 0);
            getUnReadNotifications();
        }
    }

    private void getUnReadNotifications() {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                fragment = new MyCoursesFragment();
                break;
            case 1:
                fragment = new DiscoverCoursesFragment();
                break;
        }

        replaceFragment(fragment);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        if (item.getItemId() == R.id.navLogout) {
            logoutUser(this);
        } else if (item.getItemId() == R.id.navProfile) {
            goToProfile();
        } else if (item.getItemId() == R.id.navChat) {
            goToChats();
        } else if (item.getItemId() == R.id.navNotification) {
            goToNotifications();
        }
        return true;
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            textForSearch = binding.edSearch.getText().toString().trim();
            search();
            return true;

        }
        return false;
    }

    private void search() {
        if (fragment instanceof OnSearchListener)
            ((OnSearchListener) fragment).onSearch(textForSearch);
        // }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AppSharedData.getUserData() != null)
            User.getInstance().stopGetUserData(AppSharedData.getUserData().getUserId());
    }
}