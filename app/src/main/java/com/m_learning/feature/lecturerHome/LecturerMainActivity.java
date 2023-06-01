package com.m_learning.feature.lecturerHome;

import static com.m_learning.network.feature.User.logoutUser;
import static com.m_learning.utils.ConstantApp.ACTION_ADD;
import static com.m_learning.utils.ConstantApp.ACTION_CLOSE;
import static com.m_learning.utils.ConstantApp.ACTION_EDIT;
import static com.m_learning.utils.ConstantApp.FROM_HOME;
import static com.m_learning.utils.ConstantApp.FROM_LECTURES;
import static com.m_learning.utils.ConstantApp.FROM_NOTIFICATION;
import static com.m_learning.utils.ConstantApp.FROM_NOTIFICATION_LIST;
import static com.m_learning.utils.ConstantApp.FROM_SPLASH;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;
import static com.m_learning.utils.ConstantApp.TYPE_ADD_ASSIGNMENT;
import static com.m_learning.utils.ConstantApp.TYPE_COURSE_REGISTER;
import static com.m_learning.utils.ConstantApp.TYPE_VIEW_LECTURE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
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

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.m_learning.R;
import com.m_learning.databinding.ActivityLecturerMainBinding;
import com.m_learning.feature.assignment.AssignmentsFragment;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.feature.courseStudents.StudentsFragment;
import com.m_learning.feature.courses.AddCourseActivity;
import com.m_learning.feature.courses.CourseOverviewFragment;
import com.m_learning.feature.lecture.LectureFragment;
import com.m_learning.feature.messages.view.MessagesActivity;
import com.m_learning.feature.notifications.NotificationsActivity;
import com.m_learning.feature.profile.ProfileActivity;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Course;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.CustomRequestListener;
import com.m_learning.network.utils.StringRequestListener;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.DialogUtils;
import com.m_learning.utils.OnSearchListener;
import com.m_learning.utils.ToolUtils;

public class LecturerMainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener,
        NavigationView.OnNavigationItemSelectedListener, TextView.OnEditorActionListener {

    private ActivityLecturerMainBinding binding;
    private ActionBarDrawerToggle toggle;
    private boolean adLoaded = false;
    Fragment fragment = null;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String textForSearch = "";
    private String courseId;
    Course course = null;
    private Menu menu;
    private int fromWhere;

    public static Intent newInstance(Activity mActivity, int fromWhere) {
        Intent intent = new Intent(mActivity, LecturerMainActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLecturerMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
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
        fromWhere = getIntent().getIntExtra(FROM_WHERE, FROM_SPLASH);
        getLecturerCourse();

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
        binding.btnCreateCourse.setOnClickListener(view1 -> startActivity(AddCourseActivity.newInstance(this, ACTION_ADD, null)));


    }

    private void getLecturerCourse() {
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            CoursesAndLectures.getInstance().getLecturerCourse(new CustomRequestListener<Course>() {
                @Override
                public void onSuccess(Course data) {
                    createTabView();
                    hideProgress();
                    course = data;
                    courseId = course.getCourseId();
                    binding.llSearch.setVisibility(View.GONE);
                    if (fromWhere == FROM_NOTIFICATION || fromWhere == FROM_NOTIFICATION_LIST) {
                        String action = getIntent().getAction();
                        if (action.equals(TYPE_COURSE_REGISTER)) {
                            binding.tabLayout.getTabAt(2).select();
                            binding.llSearch.setVisibility(View.VISIBLE);
                            fragment = new StudentsFragment().newInstance(courseId);
                        } else if (action.equals(TYPE_ADD_ASSIGNMENT)) {
                            binding.tabLayout.getTabAt(3).select();
                            binding.llSearch.setVisibility(View.VISIBLE);
                            fragment = new AssignmentsFragment().newInstance(courseId, FROM_LECTURES);
                        } else if (action.equals(TYPE_VIEW_LECTURE)) {
                            binding.tabLayout.getTabAt(1).select();
                            binding.llSearch.setVisibility(View.VISIBLE);
                            fragment = new LectureFragment().newInstance(courseId, FROM_LECTURES);
                        }
                    } else {
                        binding.tabLayout.getTabAt(0).select();
                        binding.llSearch.setVisibility(View.GONE);
                        fragment = new CourseOverviewFragment().newInstance(course, courseId);

                    }
                    replaceFragment(fragment);
                    updateMenuTitles();
                }

                @Override
                public void onFail(String message, int code) {
                    hideProgress();
                    createCourseView();
                }
            });
        } else {
            showErrorDialog(getString(R.string.noInternetConnection), getString(R.string.ok), "", ACTION_CLOSE);
        }
    }

    private void createCourseView() {
        binding.llCreateCourseView.setVisibility(View.VISIBLE);
        binding.llTabview.setVisibility(View.GONE);
    }

    private void createTabView() {
        binding.llCreateCourseView.setVisibility(View.GONE);
        binding.llTabview.setVisibility(View.VISIBLE);
    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commitAllowingStateLoss();
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
            tvName.setText(userInfo.getFullName());
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
                fragment = new CourseOverviewFragment().newInstance(course, courseId);
                binding.llSearch.setVisibility(View.GONE);
                break;
            case 1:
                binding.llSearch.setVisibility(View.VISIBLE);
                fragment = new LectureFragment().newInstance(courseId, FROM_LECTURES);
                break;
            case 2:
                binding.llSearch.setVisibility(View.VISIBLE);
                fragment = new StudentsFragment().newInstance(courseId);
                break;
            case 3:
                binding.llSearch.setVisibility(View.VISIBLE);
                fragment = new AssignmentsFragment().newInstance(courseId, FROM_LECTURES);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;

        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateMenuTitles() {
        String message = (course.getIsActive() == 1) ? getString(R.string.hide) : getString(R.string.show);
        if (menu != null) {
            MenuItem item = menu.findItem(R.id.hide);
            item.setTitle(message);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.edit:
                startActivity(AddCourseActivity.newInstance(this, ACTION_EDIT, course));
                return true;
            case R.id.delete:
                DialogUtils.showAlertDialogWithListener(this, "", getString(R.string.delete_msg), getString(R.string.ok),
                        getString(R.string.Cancel), new DialogUtils.onClickListener() {
                            @Override
                            public void onOkClick() {
                                deleteCourse();
                            }

                            @Override
                            public void onCancelClick() {

                            }
                        });
                return true;
            case R.id.hide:
                String message = (course.getIsActive() == 1) ? getString(R.string.hide_msg) : getString(R.string.show_msg);
                DialogUtils.showAlertDialogWithListener(this, "", message, getString(R.string.ok),
                        getString(R.string.Cancel), new DialogUtils.onClickListener() {
                            @Override
                            public void onOkClick() {
                                if (course.getIsActive() == 1) hideCourse();
                                else showCourse();
                            }

                            @Override
                            public void onCancelClick() {

                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteCourse() {
        CoursesAndLectures.getInstance().deleteCourseForAll(course);
    }

    private void hideCourse() {
        course.setIsActive(0);
        course.setStudentsNo(0);
        CoursesAndLectures.getInstance().addEditCourseInDataBase(ACTION_EDIT, course, new StringRequestListener<Course>() {
            @Override
            public void onSuccess(String message, Course data) {
                hideProgress();
                showErrorMessage(message);

            }

            @Override
            public void onFail(String message) {
                hideProgress();
                showErrorMessage(message);
            }
        });


    }

    private void showCourse() {
        course.setIsActive(1);
        CoursesAndLectures.getInstance().addEditCourseInDataBase(ACTION_EDIT, course, new StringRequestListener<Course>() {
            @Override
            public void onSuccess(String message, Course data) {
                hideProgress();
                showErrorMessage(message);

            }

            @Override
            public void onFail(String message) {
                hideProgress();
                showErrorMessage(message);
            }
        });


    }
}