package com.m_learning.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.iid.FirebaseInstanceId;
import com.m_learning.R;
import com.m_learning.feature.studentMain.MainActivity;
import com.m_learning.feature.lecturerHome.LecturerMainActivity;
import com.m_learning.feature.general.LoginActivity;
import com.m_learning.network.model.Assignments;
import com.m_learning.network.model.BaseItem;
import com.m_learning.network.model.Course;
import com.m_learning.network.model.Lecture;
import com.m_learning.network.model.Notifications;
import com.m_learning.network.model.UserInfo;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;


public class ToolUtils {

    public static boolean isNetworkConnected() {
        boolean HaveConnectedWifi = false;
        boolean HaveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) MLearningApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equals("WIFI")) ;
            if (ni.isConnected())
                HaveConnectedWifi = true;
            if (ni.getTypeName().equals("MOBILE"))
                if (ni.isConnected())
                    HaveConnectedMobile = true;
        }
        return HaveConnectedWifi || HaveConnectedMobile;
    }


    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (imm != null) {
                if (view == null) view = new View(activity);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }


    static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        float totalPixels = width * height;
        float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) inSampleSize++;

        return inSampleSize;
    }

    public static int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    public static void setRoundedImgWithProgress(Context context, String url, ImageView imageView,
                                                 CircularProgressIndicator prog) {
        prog.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(url)) {
            Glide.with(context).load(R.drawable.img_user).into(imageView);
            prog.setVisibility(View.GONE);
        } else
            Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()//.override(50,50)
                    .placeholder(R.drawable.img_user))
                    .listener(new RequestListener<Drawable>() {
                        @SuppressLint("CheckResult")
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Glide.with(context).load(R.drawable.img_user);
                            prog.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            prog.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(imageView);

    }


    public static void setImgWithProgress(Context context, String url, ImageView imageView, CircularProgressIndicator prog) {
        prog.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(url)) {
            Glide.with(context).load(R.drawable.no_img_placeholder).centerCrop().into(imageView);
            prog.setVisibility(View.GONE);
        } else Glide.with(context).load(url).placeholder(R.drawable.no_img_placeholder).centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Glide.with(context).load(R.drawable.no_img_placeholder).centerCrop();
                        prog.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        prog.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);

    }

    public static void setImgProgress(Context context, String url, ImageView imageView, CircularProgressIndicator prog) {
        prog.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(url)) {
            Glide.with(context).load(R.drawable.no_img_placeholder).into(imageView);
            prog.setVisibility(View.GONE);
        } else Glide.with(context).load(url).placeholder(R.drawable.no_img_placeholder)
                .listener(new RequestListener<Drawable>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Glide.with(context).load(R.drawable.no_img_placeholder).centerCrop();
                        prog.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        prog.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);

    }


    public static long getCurrentDateTimeLong() {
        return System.currentTimeMillis();
    }


    public static void navigateToLogin(Activity mActivity) {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
        mActivity.finish();
    }


    public static String getRandomKey() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static void setImg(Context context, String url, ImageView imageView) {
        if (!TextUtils.isEmpty(url))
            Glide.with(context).load(url).centerCrop()
                .into(imageView);
    }

    public static void setRoundImg(Context context, String url, ImageView imageView) {
        if (!TextUtils.isEmpty(url))
            Glide.with(context).load(url).apply(RequestOptions.circleCropTransform())
                    .into(imageView);
    }


    public static long getGMTTimeInMillisecond() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        return cal.getTimeInMillis() / 1000;
    }


    public static CharSequence convertToEngNo(String format) {
        String result = format;
        result = format.replace("١", "1").replace("٢", "2").replace("٣", "3")
                .replace("٤", "4").replace("٥", "5").replace("٦", "6")
                .replace("٧", "7").replace("٨", "8").replace("٩", "9")
                .replace("٠", "0")
                .replace("٫", ".");
        return result;
    }


    public static void goToMainUser(Activity mActivity, int fromWhere) {
        Intent intent = MainActivity.newInstance(mActivity, fromWhere);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    public static void goToTeacherMainActivity(Activity mActivity, int fromWhere) {
        Intent intent = LecturerMainActivity.newInstance(mActivity, fromWhere);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    public static String convertLongTime(long mileSegundos) {

        String strLocalDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat LocalDayAndMonthFormatter = new SimpleDateFormat("hh:mm a", Locale.US);

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(mileSegundos * 1000);
        String dateStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();

        try {
            Date date = formatter.parse(dateStr);
            LocalDayAndMonthFormatter.setTimeZone(TimeZone.getDefault());
            strLocalDate = LocalDayAndMonthFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return strLocalDate;

    }

    public static void setNewFcm() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) return;
                    if (task.getResult() != null) {
                        String token = task.getResult().getToken();
                        AppSharedData.setFcmToken(token);
                    }

                });
    }

    public static boolean checkADayAfterEnd(String endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDateByString(endDate, "dd/MM/yyyy"));

        Calendar currentTime = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String current = simpleDateFormat.format(currentTime.getTime());
        Date currentDate = getDateByString(current, "dd/MM/yyyy");
        Log.e("**Test currentDate: ", currentDate.toString());
        Log.e("**Test endDate : ", calendar.getTime().toString());
        return compareDateAfter(currentDate, calendar.getTime());
    }


    public static Date getDateByString(String dateStr, String format) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
//            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            date = simpleDateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String convert24ToAm(int hourOfDay, int minute) {
        String time = ((hourOfDay > 12) ? hourOfDay % 12 : hourOfDay) + ":" + (minute < 10 ? ("0" + minute) : minute) + " " + ((hourOfDay >= 12) ? "PM" : "AM");
        return time;
    }

    public static boolean compareDateAfter(Date startDate, Date endDate) {
        Log.e("date", "compareDateAfter: startDate" + startDate);
        Log.e("date", "compareDateAfter: endDate" + endDate);

        boolean isAfter = startDate != null && (endDate.after(startDate) || endDate.equals(startDate));
        Log.e("date", isAfter + " ");

        return isAfter;

    }


    public static void setupUI(Activity activity, View view) {
        if (!(view instanceof EditText)) view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    hideKeyboard(activity);
                } catch (Exception e) {
                }
                return false;
            }
        });
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(activity, innerView);
            }
    }

    public static String getGenderTxt(Activity activity, String gender) {
        String genderTxt = gender;
        switch (gender) {
            case "female":
                genderTxt = activity.getString(R.string.female);
                break;
            case "male":
                genderTxt = activity.getString(R.string.male);
                break;
        }
        return genderTxt;
    }

    public static String getGender(Activity activity, String gender) {
        if (gender.equals(activity.getString(R.string.female))) return "female";
        else if (gender.equals(activity.getString(R.string.male))) return "male";
        else return "";
    }

    public static void selectGender(Activity mActivity, String gender, onGenderSelected listener) {
        ArrayList<BaseItem> list = new ArrayList<>();
        boolean isFemale = false;
        boolean isMale = false;
        boolean isUndefined = false;
        if (gender.equalsIgnoreCase(mActivity.getString(R.string.female)))
            isFemale = true;
        else if (gender.equalsIgnoreCase(mActivity.getString(R.string.male)))
            isMale = true;
        list.add(new BaseItem(0, mActivity.getString(R.string.female), isFemale));
        list.add(new BaseItem(1, mActivity.getString(R.string.male), isMale));

//        GeneralBottomDialogFragment dialogFragment = GeneralBottomDialogFragment.newInstance(list);
//        dialogFragment.setListener(baseBean -> {
//            listener.onSelected(baseBean.getName().toLowerCase());
//        });
//        dialogFragment.show((((AppCompatActivity) mActivity).getSupportFragmentManager()), SelectJobTypeDialogFragment.class.getSimpleName());
    }


    public static String getPostalCodeByCoordinates(Context context, double lat, double lon) {

        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
        String zipcode = "";
        Address address = null;

        if (mGeocoder != null) {

            List<Address> addresses = null;
            try {
                addresses = mGeocoder.getFromLocation(lat, lon, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {

                for (int i = 0; i < addresses.size(); i++) {
                    address = addresses.get(i);
                    if (address.getPostalCode() != null) {
                        zipcode = address.getPostalCode();
                        break;
                    }
                }
            }
        }

        return zipcode;
    }

    public static CharSequence convertTimeStamp(long mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(mileSegundos * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    public static boolean containNotificationsId(ArrayList<Notifications> list, String key) {
        for (Notifications object : list) {
            if (object.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containUserInfo(ArrayList<UserInfo> list, String key) {
        for (UserInfo object : list) {
            if (object.getUserId().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containCourseId(ArrayList<Course> list, String key) {
        for (Course object : list) {
            if (object.getCourseId().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containLectureId(ArrayList<Lecture> list, String key) {
        for (Lecture object : list) {
            if (object.getId().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containAssignmentsId(ArrayList<Assignments> list, String key) {
        for (Assignments object : list) {
            if (object.getId().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static void openFileUrl(Context mActivity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mActivity.startActivity(browserIntent);
    }

    public static String getPath(Uri uri, Activity mActivity) {
        if (uri == null)
            return null;
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = mActivity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    public interface onRefreshListener {
        void onSuccess();
    }

    public interface onGenderSelected {
        void onSelected(String gender);
    }

    public static long convertDateToMillisecond(String dateS) {
        long timeInMilliseconds = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        try {
            Date date = formatter.parse(dateS);
            timeInMilliseconds = date.getTime();
//           Calendar calendar=Calendar.getInstance();
//           calendar.setTime(date);
//           mill=calendar.getTimeInMillis();
            System.out.println("Date in milli :: " + timeInMilliseconds);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static String convertMillisecondToDate(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return formatter.format(new Date(milliseconds));
    }

    public static String ShowTime(long diff) {
        String time = "";
        long diffDays = diff / (24 * 60 * 60 * 1000);
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        String fd = "" + diffDays;
        String fh = "" + diffHours;
        String fm = "" + diffMinutes;
        String fs = "" + diffSeconds;
        if (diffDays < 10)
            fd = "0" + diffDays;
        if (diffHours < 10)
            fh = "0" + diffHours;
        if (diffMinutes < 10)
            fm = "0" + diffMinutes;
        time = fd + " days, " + fh + "h, " + fm + "m" + fs + "s";
        Log.e("Time:", time);
        return time;
    }

    public void openWord(Activity mActivity, File file) {
        //Uri uri = Uri.parse("file://"+file.getAbsolutePath());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/msword";
        intent.setDataAndType(Uri.fromFile(file), type);
        mActivity.startActivity(intent);
    }

    public void openDocument(Activity mActivity, String name) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        File file = new File(name);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }
        // custom message for the intent
        mActivity.startActivity(Intent.createChooser(intent, "Choose an Application:"));
    }

}
