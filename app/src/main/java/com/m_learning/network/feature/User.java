package com.m_learning.network.feature;

import static com.m_learning.network.utils.FirebaseReferance.getAuthCurrentUserReference;
import static com.m_learning.network.utils.FirebaseReferance.getAuthReference;
import static com.m_learning.network.utils.FirebaseReferance.getUserId;
import static com.m_learning.network.utils.FirebaseReferance.getUserReference;
import static com.m_learning.network.utils.FirebaseReferance.saveUserDeviceOs;
import static com.m_learning.network.utils.FirebaseReferance.saveUserFcmTokenReference;
import static com.m_learning.utils.ConstantApp.ANDROID;
import static com.m_learning.utils.ConstantApp.FROM_HOME;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.m_learning.R;
import com.m_learning.feature.general.LoginActivity;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.CustomRequestListener;
import com.m_learning.network.utils.FirebaseReferance;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.network.utils.StringRequestListener;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.DialogUtils;
import com.m_learning.utils.MLearningApp;
import com.m_learning.utils.ToolUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class User {
    private static String TAG = User.class.getSimpleName();
    private RequestListener<Integer> getCountOfFollowersListener;
    private RequestListener<UserInfo> getUserDataListener;
    private static User instance;
    private ValueEventListener friendsValueEventListener;


    public static User getInstance() {
        if (instance == null)
            instance = new User();
        return instance;
    }


    public static void userSignUp(UserInfo UserInfo, String password, RequestListener<UserInfo> requestListener) {
        getAuthReference().createUserWithEmailAndPassword(UserInfo.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = getAuthCurrentUserReference();
                        Log.e("SignUp: ", firebaseUser.getUid() + "");
                        UserInfo.setUserId(firebaseUser.getUid());
                        requestListener.onSuccess(UserInfo);
                    } else {
                        requestListener.onFail(taskVoidException2(task));
                    }
                });
    }

    public static void saveUserInfoToDatabase(UserInfo UserInfo, StringRequestListener<UserInfo> requestListener) {
        getUserReference(UserInfo.getUserId()).setValue(UserInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                requestListener.onSuccess(MLearningApp.getInstance().getString(R.string.success_signup), UserInfo);

            } else {
                getAuthReference().signOut();
                Log.e("SignUp error: ", task.getException().toString() + "");
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }


    public static void sendVerificationEmail() {
        FirebaseUser user = getAuthReference().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    FirebaseAuth.getInstance().signOut();
                });
    }

    public void saveImageToFirestore(String fileName, File file, RequestListener<Uri> requestListener) {
        StorageReference ref = FirebaseReferance.getInstance().getStorageUserImageReference(fileName);

        UploadTask uploadTask = ref.putFile(Uri.fromFile(file));
        uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
            if (!task.isSuccessful()) {
                requestListener.onFail(task.getException().getMessage());
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                requestListener.onSuccess(Uri.parse(downloadUri.toString()));


            } else {
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }


    public static void saveImageToFirestore(String imageName, byte[] photo, RequestListener<Uri> requestListener) {
        StorageReference ref = FirebaseReferance.getInstance().getStorageUserImageReference(imageName);
        UploadTask uploadTask = ref.putBytes(photo);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                requestListener.onFail(task.getException().getMessage());
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                requestListener.onSuccess(Uri.parse(downloadUri.toString()));
            } else {
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    public static boolean checkIfEmailVerified() {
        boolean result = false;
        FirebaseUser user = getAuthReference().getCurrentUser();
        if (user.isEmailVerified()) {
            result = true;
        } else {
            sendVerificationEmail();
            result = false;
        }
        return result;
    }

    public static void loginWithEmailAndPassword(String email, String password, StringRequestListener<UserInfo> requestListener) {
        getAuthReference().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(getAuthReference().getCurrentUser()).getUid();
                        Log.e("Login userId: ", userId);
                        getUserData(userId, new StringRequestListener<UserInfo>() {
                            @Override
                            public void onSuccess(String msg, UserInfo data) {
                                getUserReference(data.getUserId()).child("deviceToken").setValue(FirebaseInstanceId.getInstance().getToken())
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                requestListener.onSuccess(MLearningApp.getInstance().getString(R.string.update_success), data);
                                            } else {
                                                requestListener.onFail(task.getException().getMessage());
                                            }
                                        });
                            }

                            @Override
                            public void onFail(String message) {
                                requestListener.onFail(message);
                            }
                        });

                    } else {
                        Log.e("Loginee: ", task.getException().toString());
                        requestListener.onFail(taskVoidException2(task));
                    }
                });
    }

    public static void getUserData(String userId, final StringRequestListener<UserInfo> requestListener) {
        getUserReference(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.e("datasnapshot: ", dataSnapshot.getValue().toString());
                    UserInfo user = dataSnapshot.getValue(UserInfo.class);
                    requestListener.onSuccess("", user);
                } else {
                    requestListener.onFail(MLearningApp.getInstance().getString(R.string.error));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                requestListener.onFail(databaseError.getMessage());
            }
        });
    }


    private ValueEventListener UserInfo = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                UserInfo user = dataSnapshot.getValue(UserInfo.class);
                getUserDataListener.onSuccess(user);
            } else {
                getUserDataListener.onFail(MLearningApp.getInstance().getString(R.string.error));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            getUserDataListener.onFail(databaseError.getMessage());
        }
    };

    public void startGetUserData(String userId, RequestListener<UserInfo> requestListener) {
        this.getUserDataListener = requestListener;
        getUserReference(userId).addValueEventListener(UserInfo);
    }

    public void stopGetUserData(String userId) {
        if (getUserDataListener != null)
            getUserReference(userId).removeEventListener(UserInfo);
    }

    public static void resetPassword(String email, RequestListener<String> requestListener) {
        getAuthReference().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requestListener.onSuccess("");
                    } else {
                        requestListener.onFail(taskVoidException(task));
                    }
                });
    }

    private static String taskVoidException(Task<Void> task) {
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseAuthWeakPasswordException e) {
            return MLearningApp.getInstance().getString(R.string.error_week_password);
        } catch (FirebaseAuthInvalidCredentialsException e) {
            return MLearningApp.getInstance().getString(R.string.pass_email_invalid);
        } catch (FirebaseAuthInvalidUserException e) {
            return MLearningApp.getInstance().getString(R.string.pass_email_invalid);
        } catch (FirebaseNoSignedInUserException e) {
            return MLearningApp.getInstance().getString(R.string.emailInvalid);
        } catch (FirebaseAuthEmailException e) {
            return MLearningApp.getInstance().getString(R.string.emailInvalid);
        } catch (FirebaseAuthUserCollisionException e) {
            return MLearningApp.getInstance().getString(R.string.ERR_emailExist);
        } catch (Exception e) {
            return MLearningApp.getInstance().getString(R.string.error);
        }
    }

    private static String taskVoidException2(Task<AuthResult> task) {
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseAuthWeakPasswordException e) {
            return MLearningApp.getInstance().getString(R.string.error_week_password);
        } catch (FirebaseAuthInvalidCredentialsException e) {
            return MLearningApp.getInstance().getString(R.string.pass_email_invalid);
        } catch (FirebaseAuthInvalidUserException e) {
            return MLearningApp.getInstance().getString(R.string.pass_email_invalid);
        } catch (FirebaseNoSignedInUserException e) {
            return MLearningApp.getInstance().getString(R.string.emailInvalid);
        } catch (FirebaseAuthEmailException e) {
            return MLearningApp.getInstance().getString(R.string.emailInvalid);
        } catch (FirebaseAuthUserCollisionException e) {
            return MLearningApp.getInstance().getString(R.string.ERR_emailExist);
        } catch (Exception e) {
            return MLearningApp.getInstance().getString(R.string.error);
        }
    }

    public static void changePassword(String oldPassword, String newPassword,
                                      RequestListener<String> requestListener) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(AppSharedData.getUserData().getEmail(), oldPassword);
        // Prompt the user to re-provide their sign-in credentials
        getAuthCurrentUserReference().reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getAuthCurrentUserReference().updatePassword(newPassword).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                UserInfo UserInfo = AppSharedData.getUserData();
                                requestListener.onSuccess("done");
                                //  changeUserInDatabase(UserInfo, requestListener);
                            } else {
                                requestListener.onFail(task2.getException().getMessage());
                            }
                        });
                    } else {
                        requestListener.onFail(task.getException().getMessage());
                    }
                });

    }


    public static void logoutUser(Activity mActivity) {
        DialogUtils.showAlertDialogWithListener(mActivity, mActivity.getString(R.string.logout), mActivity.getString(R.string.logout_msg), mActivity.getString(R.string.ok),
                mActivity.getString(R.string.Cancel), new DialogUtils.onClickListener() {
                    @Override
                    public void onOkClick() {
                        try {
                            FirebaseReferance.saveUserFcmTokenReference(getUserId()).setValue("");
                        } catch (Exception ignore) {
                        }
                        getAuthReference().signOut();
                        AppSharedData.setUserLogin(false);
                        AppSharedData.setUserData(null);

                        Intent intent = new Intent(LoginActivity.newInstance(MLearningApp.getInstance().getApplicationContext(), FROM_HOME));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MLearningApp.getInstance().startActivity(intent);

                    }

                    @Override
                    public void onCancelClick() {

                    }
                });


    }

    public static void getAllUsers(String name, CustomRequestListener<ArrayList<UserInfo>> requestListener) {
//        getUsersListReference().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<UserInfo> UserInfoArrayList = new ArrayList<>();
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot spotDataSnapshot : dataSnapshot.getChildren()) {
//                        UserInfo user = spotDataSnapshot.getValue(UserInfo.class);
//                        if (!TextUtils.isEmpty(user.getAllName())) {
//                            Log.e("test", user.getAllName());
//                        }
//                        if (!TextUtils.isEmpty(user.getUserId())
//                                && ((!TextUtils.isEmpty(user.getAllName()) && user.getAllName().toLowerCase().contains(name.toLowerCase()))
//                                ||
//                                (!TextUtils.isEmpty(user.getFirstName()) && user.getFirstName().toLowerCase().contains(name.toLowerCase()))
//                                ||
//                                (!TextUtils.isEmpty(user.getLastName()) && user.getLastName().toLowerCase().contains(name.toLowerCase())))) {
//                            isInMyFriends(user.getUserId(), new RequestListener<Friend>() {
//                                @Override
//                                public void onSuccess(Friend data) {
//                                    if (data != null) {
//                                        user.setFriend(data.getStatus());
//                                        user.setDateFriend(data.getDate());
//                                        if (!data.getStatus()) {
//                                            if (!TextUtils.isEmpty(data.getSenderId())) {
//                                                if (data.getSenderId().equals(AppSharedData.getUserData().getUserId())) {
//                                                    user.setFriendStatus(FRIEND_PENDING);
//                                                } else {
//                                                    user.setFriendStatus(FRIEND_REQUEST);
//                                                }
//                                            }
//                                        }
//
//                                    } else {
//                                        user.setFriend(false);
//                                        user.setFriendStatus("");
//                                    }
//
//                                    UserInfoArrayList.add(user);
//                                    requestListener.onSuccess(1, UserInfoArrayList);
//                                }
//
//                                @Override
//                                public void onFail(String message) {
//
//                                }
//                            });
//                        } else {
//                            requestListener.onSuccess(0, UserInfoArrayList);
//                        }
//                    }
//                } else {
//                    requestListener.onSuccess(0, UserInfoArrayList);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                requestListener.onFail(databaseError.getMessage());
//            }
//        });
    }


    public static void updateUserInfo(Activity context, UserInfo
            UserInfo, RequestListener<UserInfo> requestListener) {
        if (!TextUtils.isEmpty(getUserId())) {
            getUserReference(getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                        UserInfo UserInfo = dataSnapshot.getValue(UserInfo.class);
                        AppSharedData.setUserData(UserInfo);
                        AppSharedData.setUserLogin(true);
                        requestListener.onSuccess(UserInfo);

                    } else {
                        saveUserInfoToDatabase(UserInfo, new StringRequestListener<UserInfo>() {
                                    @Override
                                    public void onSuccess(String msg, UserInfo data) {
                                        requestListener.onSuccess(data);
                                    }

                                    @Override
                                    public void onFail(String message) {
                                        getAuthReference().signOut();
                                        requestListener.onFail(message);
                                    }
                                }
                        );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    getAuthReference().signOut();
                    requestListener.onFail(databaseError.getMessage());
                }
            });
        } else {
            getAuthReference().signOut();
            ToolUtils.navigateToLogin(context);
        }
    }

    public static void saveNewFcmToken(String userId, String
            token, RequestListener<Boolean> requestListener) {
        if (TextUtils.isEmpty(userId)) return;
        saveUserDeviceOs(userId).setValue(ANDROID);
        saveUserFcmTokenReference(userId).setValue(token);
    }

    public static void forgetPassword(String email, RequestListener<String> requestListener) {
        getAuthReference().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requestListener.onSuccess(MLearningApp.getInstance().getString(R.string.email_sent));
                    } else {
                        requestListener.onFail(MLearningApp.getInstance().getString(R.string.account_not_found));
                    }
                });
    }


}
