package com.m_learning.network.feature;

import static com.m_learning.network.utils.FirebaseReferance.getAuthReference;
import static com.m_learning.network.utils.FirebaseReferance.getCourseStudentsReference;
import static com.m_learning.network.utils.FirebaseReferance.getUserReference;
import static com.m_learning.utils.ConstantApp.ACTION_DELETE;
import static com.m_learning.utils.ConstantApp.ACTION_EDIT;
import static com.m_learning.utils.ConstantApp.ANDROID;
import static com.m_learning.utils.ConstantApp.TYPE_ADD_ASSIGNMENT;
import static com.m_learning.utils.ConstantApp.TYPE_ADD_LECTURE;
import static com.m_learning.utils.ConstantApp.TYPE_COURSE_REGISTER;
import static com.m_learning.utils.ConstantApp.TYPE_COURSE_VIEW;
import static com.m_learning.utils.ConstantApp.TYPE_DELETE_LECTURE;
import static com.m_learning.utils.ConstantApp.TYPE_EDIT_LECTURE;
import static com.m_learning.utils.ConstantApp.TYPE_USER;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.m_learning.R;
import com.m_learning.fcm.FcmNotificationBuilder;
import com.m_learning.network.model.Assignments;
import com.m_learning.network.model.Course;
import com.m_learning.network.model.Lecture;
import com.m_learning.network.model.Notifications;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.CustomRequestListener;
import com.m_learning.network.utils.FirebaseReferance;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.network.utils.StringRequestListener;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.MLearningApp;
import com.m_learning.utils.ToolUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class CoursesAndLectures {
    private static String TAG = "CoursesAndLectures";
    private RequestListener<ArrayList<Course>> getCourseRequest;
    private RequestListener<ArrayList<Course>> getAllCourseRequest;
    private RequestListener<ArrayList<UserInfo>> courseStudentsListResponse;
    private RequestListener<ArrayList<Lecture>> courseLecturesListResponse;
    private RequestListener<ArrayList<Assignments>> courseAssignmentListResponse;
    private RequestListener<Boolean> checkIfIRegisterRequest;
    private RequestListener<Assignments> checkIfSubmitAssignmentRequest;
    private RequestListener<Course> courseResponse;
    private RequestListener<Lecture> lectureResponse;
    private CustomRequestListener<Course> LecturerCourseRequest;
    private static CoursesAndLectures instance;

    public static CoursesAndLectures getInstance() {
        if (instance == null)
            instance = new CoursesAndLectures();
        return instance;
    }

    private ValueEventListener getCoursesListListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                Log.e(TAG, "dataSnapshot{" + dataSnapshot.getValue().toString() + "}");
                ArrayList<Course> list = new ArrayList<>();
                for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {
                    Log.e(TAG, "snapshotChild{" + snapshotChild.getValue() + "}");
                    Course item = snapshotChild.getValue(Course.class);
                    if (item != null && item.getIsActive() == 1) {
                        list.add(item);
                    }
                }
//                Collections.reverse(groupBeans);
                getCourseRequest.onSuccess(list);
            } else {
                getCourseRequest.onFail(MLearningApp.getInstance().getString(R.string.no_data_found));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            getCourseRequest.onFail(databaseError.getMessage());
        }
    };

    public void startCoursesListListener(RequestListener<ArrayList<Course>> requestListener) {
        this.getCourseRequest = requestListener;
        FirebaseReferance.getUserCoursesReference(FirebaseReferance.getUserId())
                .orderByChild("createdAt")
                .addValueEventListener(getCoursesListListener);
    }

    public void stopCoursesListListener() {
        if (getCoursesListListener != null)
            FirebaseReferance
                    .getUserCoursesReference(FirebaseReferance.getUserId())
                    .removeEventListener(getCoursesListListener);
    }


    private ValueEventListener getAllCoursesListListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                Log.e(TAG, "dataSnapshot{" + dataSnapshot.getValue().toString() + "}");
                ArrayList<Course> list = new ArrayList<>();
                for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {
                    Log.e(TAG, "snapshotChild{" + snapshotChild.getValue() + "}");
                    Course item = snapshotChild.getValue(Course.class);
                    if (item != null && item.getIsActive() == 1 && !item.getIsDeleted()) {
                        list.add(item);
                    }
                }
                getAllCourseRequest.onSuccess(list);
            } else {
                getAllCourseRequest.onFail(MLearningApp.getInstance().getString(R.string.no_data_found));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            getAllCourseRequest.onFail(databaseError.getMessage());
        }
    };

    public void startAllCoursesListListener(RequestListener<ArrayList<Course>> requestListener) {
        this.getAllCourseRequest = requestListener;
        FirebaseReferance.getCoursesReference()
                .orderByChild("createdAt")
                .addValueEventListener(getAllCoursesListListener);
    }

    public void stopAllCoursesListListener() {
        if (getAllCoursesListListener != null)
            FirebaseReferance
                    .getCoursesReference()
                    .removeEventListener(getAllCoursesListListener);
    }


    private final ValueEventListener courseDetailsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Log.i(TAG, Objects.requireNonNull(dataSnapshot.getValue()).toString());
                Course obj = dataSnapshot.getValue(Course.class);
                if (obj != null && !obj.getIsDeleted())
                    if (!AppSharedData.getUserData().getUserType().equals(TYPE_USER)) {
                        courseResponse.onSuccess(obj);
                    } else {
                        if (obj.getIsActive() == 1) courseResponse.onSuccess(obj);
                    }
                else
                    courseResponse.onFail(MLearningApp.getInstance().getResources().getString(R.string.course_not_available));
            } else {
                courseResponse.onFail(MLearningApp.getInstance().getResources().getString(R.string.no_data_found));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            courseResponse.onFail(databaseError.getMessage());
        }
    };

    public void startCourseDetailsListener(String courseId, RequestListener<Course> requestListener) {
        this.courseResponse = requestListener;
        FirebaseReferance.getCourseDetailsReference(courseId).addValueEventListener(courseDetailsListener);
    }

    public void stopCourseDetailsListener(String courseId) {
        if (courseDetailsListener != null)
            FirebaseReferance
                    .getCourseDetailsReference(courseId)
                    .removeEventListener(courseDetailsListener);
    }


    private ValueEventListener courseStudentsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ArrayList<UserInfo> list = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot spotDataSnapshot : dataSnapshot.getChildren()) {
                    Log.e("courseStudents: ", spotDataSnapshot.getValue().toString());
                    UserInfo user = spotDataSnapshot.getValue(UserInfo.class);
                    list.add(user);
                }
                Log.e("cStudentsListSize: ", "" + list.size());
                courseStudentsListResponse.onSuccess(list);

            } else {
                courseStudentsListResponse.onFail(MLearningApp.getInstance().getResources().getString(R.string.no_data_found));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            courseStudentsListResponse.onFail(databaseError.getMessage());
        }
    };

    public void startCourseStudentsListListener(String courseId, RequestListener<ArrayList<UserInfo>> requestListener) {
        this.courseStudentsListResponse = requestListener;
        FirebaseReferance.getCourseStudentsReference(courseId).addValueEventListener(courseStudentsListener);
    }


    public void stopCourseStudentsListListener(String courseId) {
        if (courseStudentsListener != null)
            FirebaseReferance.getCourseStudentsReference(courseId)
                    .removeEventListener(courseStudentsListener);
    }


    private ValueEventListener LecturerCourseListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Log.i(TAG, Objects.requireNonNull(dataSnapshot.getValue()).toString());
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.i("LecturerCourse: ", data.getValue().toString());
                    Course obj = data.getValue(Course.class);
                    if (obj != null && !obj.getIsDeleted())
                        LecturerCourseRequest.onSuccess(obj);
                    else
                        LecturerCourseRequest.onFail(MLearningApp.getInstance().getResources().getString(R.string.course_not_available), 0);
                }
            } else {
                LecturerCourseRequest.onFail(MLearningApp.getInstance().getResources().getString(R.string.no_course), 1);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            LecturerCourseRequest.onFail(error.getMessage(), 0);

        }
    };

    public void getLecturerCourse(CustomRequestListener<Course> requestListener) {
        this.LecturerCourseRequest = requestListener;
        FirebaseReferance.getLecturerCourseReference(FirebaseReferance.getUserId()).addValueEventListener(LecturerCourseListener);

    }

    private ValueEventListener courseLecturesListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ArrayList<Lecture> list = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot spotDataSnapshot : dataSnapshot.getChildren()) {
                    Lecture lecture = spotDataSnapshot.getValue(Lecture.class);
                    list.add(lecture);
                }
                Collections.reverse(list);
                courseLecturesListResponse.onSuccess(list);

            } else {
                courseLecturesListResponse.onFail(MLearningApp.getInstance().getResources().getString(R.string.no_data_found));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            courseLecturesListResponse.onFail(databaseError.getMessage());
        }
    };

    public void startCourseLecturesListListener(String courseId, RequestListener<ArrayList<Lecture>> requestListener) {
        this.courseLecturesListResponse = requestListener;
        FirebaseReferance.getCourseLecturesReference(courseId).orderByChild("createdAt").addValueEventListener(courseLecturesListener);
    }

    public void stopCourseLecturesListListener(String courseId) {
        if (courseLecturesListener != null)
            FirebaseReferance.getCourseLecturesReference(courseId)
                    .removeEventListener(courseLecturesListener);
    }

    private ValueEventListener courseAssignmentsListListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ArrayList<Assignments> list = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot spotDataSnapshot : dataSnapshot.getChildren()) {
                    Assignments user = spotDataSnapshot.getValue(Assignments.class);
                    list.add(user);

                    courseAssignmentListResponse.onSuccess(list);

                }
            } else {
                courseAssignmentListResponse.onFail(MLearningApp.getInstance().getResources().getString(R.string.no_data_found));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            courseAssignmentListResponse.onFail(databaseError.getMessage());
        }
    };

    public void startCourseAssignmentListListener(String courseId, RequestListener<ArrayList<Assignments>> requestListener) {
        this.courseAssignmentListResponse = requestListener;
        FirebaseReferance.getCourseAssignmentReference(courseId).addValueEventListener(courseAssignmentsListListener);
    }

    public void stopCourseAssignmentListListener(String courseId) {
        if (courseAssignmentsListListener != null)
            FirebaseReferance.getCourseAssignmentReference(courseId)
                    .removeEventListener(courseAssignmentsListListener);
    }


    private final ValueEventListener lectureDetailsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Log.i(TAG, Objects.requireNonNull(dataSnapshot.getValue()).toString());
                Lecture obj = dataSnapshot.getValue(Lecture.class);
                if (obj != null && !obj.getIsDeleted() && obj.getIsActive() == 1)
                    lectureResponse.onSuccess(obj);
                else
                    lectureResponse.onFail(MLearningApp.getInstance().getResources().getString(R.string.course_not_available));
            } else {
                lectureResponse.onFail(MLearningApp.getInstance().getResources().getString(R.string.no_data_found));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            lectureResponse.onFail(databaseError.getMessage());
        }
    };

    public void startLectureDetailsListener(String courseId, String lectureId, RequestListener<Lecture> requestListener) {
        this.lectureResponse = requestListener;
        FirebaseReferance.getCourseLecturesReference(courseId, lectureId).addValueEventListener(lectureDetailsListener);
    }

    public void stopLectureDetailsListener(String courseId, String lectureId) {
        if (lectureDetailsListener != null)
            FirebaseReferance
                    .getCourseLecturesReference(courseId, lectureId)
                    .removeEventListener(lectureDetailsListener);
    }


    public void addEditCourseInDataBase(int from, Course course, StringRequestListener<Course> requestListener) {
        FirebaseReferance.getCoursesReference().child(course.getCourseId()).setValue(course).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveToLecturerCourse(course);
                if (from == ACTION_EDIT)
                    editCourseForAll(course);
                if (course.getIsActive() == 0) {
                    removeCourseFromStudent(course);
                }

                Log.e("add course: ", task.getResult() + "");
                requestListener.onSuccess(MLearningApp.getInstance().getString(R.string.success_add), course);

            } else {
                getAuthReference().signOut();
                Log.e("SignUp error: ", task.getException() + "");
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    private void removeCourseFromStudent(Course course) {
        FirebaseReferance.getCourseStudentsReference(course.getCourseId()).removeValue();
        FirebaseReferance.getUserCoursesReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.e("dataSnapshot: ", dataSnapshot.getKey());
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            String key = dataSnap.getKey();
                            Log.e("dataSnap: ", key);
                            if (key != null && key.equals(course.getCourseId())) {
                                FirebaseReferance.getUserCoursesReference(key).child(course.getCourseId()).removeValue();
                                getUserReference(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Log.e("datasnapshot: ", dataSnapshot.getValue().toString());
                                            UserInfo user = dataSnapshot.getValue(UserInfo.class);
                                            if (user != null && user.getCourseNo() > 0)
                                                getUserReference(key).child("courseNo").setValue(user.getCourseNo() - 1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void saveToLecturerCourse(Course course) {
        FirebaseReferance.getLecturerCourseReference(course.getLecturerId()).child(course.getCourseId()).setValue(course);
    }

    public void editCourseForAll(Course course) {
        FirebaseReferance.getUserCoursesReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userId = dataSnapshot.getKey();
                        Log.e("dataSnapshot: ", dataSnapshot.getKey());
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            String key = dataSnap.getKey();
                            Log.e("dataSnap: ", key);
                            if (key != null && key.equals(course.getCourseId())) {
                                FirebaseReferance.getUserCoursesReference(userId).child(course.getCourseId()).setValue(course);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteCourseForAll(Course course) {
        FirebaseReferance.getCoursesReference().child(course.getCourseId()).removeValue();
        FirebaseReferance.getLecturerCourseReference(course.getLecturerId()).child(course.getCourseId()).removeValue();
        FirebaseReferance.getCourseAssignmentReference(course.getCourseId()).removeValue();
        FirebaseReferance.getCourseStudentsReference(course.getCourseId()).removeValue();
        FirebaseReferance.getCourseViewerReference(course.getCourseId()).removeValue();
        FirebaseReferance.getCourseLecturesReference(course.getCourseId()).removeValue();
        FirebaseReferance.getUserCoursesReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.e("dataSnapshot: ", dataSnapshot.getKey());
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            String key = dataSnap.getKey();
                            Log.e("dataSnap: ", key);
                            if (key != null && key.equals(course.getCourseId())) {
                                FirebaseReferance.getUserCoursesReference(key).child(course.getCourseId()).removeValue();
                                getUserReference(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Log.e("datasnapshot: ", dataSnapshot.getValue().toString());
                                            UserInfo user = dataSnapshot.getValue(UserInfo.class);
                                            if (user != null && user.getCourseNo() > 0)
                                                getUserReference(key).child("courseNo").setValue(user.getCourseNo() - 1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveThumbFileToFirestore(String imageName, byte[] photo, RequestListener<Uri> requestListener) {
        StorageReference ref = FirebaseReferance.getInstance().getLectureStorageImageReference(imageName);
        UploadTask uploadTask = ref.putBytes(photo);
        uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
            if (!task.isSuccessful()) {
                requestListener.onFail(task.getException().getMessage());
            }
            Log.e("test: ", ref.getMetadata().toString());
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

    public void saveFileToFirestore(String fileName, File file, StringRequestListener<Uri> requestListener) {
        StorageReference ref = FirebaseReferance.getInstance().getCourseStorageImageReference(fileName);

        UploadTask uploadTask = ref.putFile(Uri.fromFile(file));
        uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
            if (!task.isSuccessful()) {
                requestListener.onFail(task.getException().getMessage());
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                ref.getMetadata().addOnSuccessListener(storageMetadata -> {
                    Log.e("test 1 : ", storageMetadata.getContentType());
                    requestListener.onSuccess(storageMetadata.getContentType(), Uri.parse(downloadUri.toString()));
                    // Metadata now contains the metadata for 'images/forest.jpg'
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        requestListener.onSuccess("", Uri.parse(downloadUri.toString()));
                        // Uh-oh, an error occurred!
                    }
                });

            } else {
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    public void saveAssignmentFileToFirestore(String fileName, File file, StringRequestListener<Uri> requestListener) {
        StorageReference ref = FirebaseReferance.getInstance().getAssignmentStorageImageReference(fileName);

        UploadTask uploadTask = ref.putFile(Uri.fromFile(file));
        uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
            if (!task.isSuccessful()) {
                requestListener.onFail(task.getException().getMessage());
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                ref.getMetadata().addOnSuccessListener(storageMetadata -> {
                    Log.e("test 1 : ", storageMetadata.getContentType());
                    requestListener.onSuccess(storageMetadata.getContentType(), Uri.parse(downloadUri.toString()));
                    // Metadata now contains the metadata for 'images/forest.jpg'
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        requestListener.onSuccess("", Uri.parse(downloadUri.toString()));
                        // Uh-oh, an error occurred!
                    }
                });

            } else {
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    public void saveLectureFileToFirestore(String fileName, File file, StringRequestListener<Uri> requestListener) {
        StorageReference ref = FirebaseReferance.getInstance().getLectureStorageImageReference(fileName);

        UploadTask uploadTask = ref.putFile(Uri.fromFile(file));
        uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
            if (!task.isSuccessful()) {
                requestListener.onFail(task.getException().getMessage());
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                ref.getMetadata().addOnSuccessListener(storageMetadata -> {
                    Log.e("test 1 : ", storageMetadata.getContentType());
                    requestListener.onSuccess(storageMetadata.getContentType(), Uri.parse(downloadUri.toString()));
                    // Metadata now contains the metadata for 'images/forest.jpg'
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        requestListener.onSuccess("", Uri.parse(downloadUri.toString()));
                        // Uh-oh, an error occurred!
                    }
                });

            } else {
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    public void addStudentCourse(Course course, RequestListener<Course> requestListener) {
        UserInfo userInfo = AppSharedData.getUserData();
        if (userInfo != null) {
            FirebaseReferance.getUserCoursesReference(userInfo.getUserId()).child(course.getCourseId()).setValue(course).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    saveToCourseStudents(course.getCourseId(), userInfo);
                    Log.e("add course: ", task.getResult() + "");
                    sendNotifications(TYPE_COURSE_REGISTER, course.getCourseId(), userInfo.getUserId(), userInfo.getShortName(),
                            String.format("Register in %s course", course.getCourseName()), course.getLecturerId());
                    requestListener.onSuccess(course);

                } else {
                    getAuthReference().signOut();
                    Log.e("SignUp error: ", task.getException() + "");
                    requestListener.onFail(task.getException().getMessage());
                }
            });
        }

    }

    public void sendNotifications(String type, String eventId, String senderId, String senderName, String message, String receiverId) {
        Notifications notifications = new Notifications();
        notifications.setCreatedAt(ToolUtils.getCurrentDateTimeLong());
        notifications.setDeleted(false);
        notifications.setType(type);
        notifications.setEventId(eventId);
        notifications.setUserId(senderId);
        notifications.setMessage(message);
        notifications.setStatus(0);
        NotificationsRequests.addNotificationItem(receiverId, notifications, null);
        User.getUserData(receiverId, new StringRequestListener<UserInfo>() {
            @Override
            public void onSuccess(String msg, UserInfo data) {
                FcmNotificationBuilder.initialize()
                        .type(type)
                        .title(senderName)
                        .message(message)
                        .os(ANDROID)
                        .uid(senderId)
                        .eventId(eventId)
                        .receiverFirebaseToken(data.getDeviceToken())
                        .send();
            }

            @Override
            public void onFail(String message) {

            }
        });

    }

    private void saveToCourseStudents(String courseId, UserInfo userInfo) {
        userInfo.setCourseNo(userInfo.getCourseNo() + 1);
        FirebaseReferance.getCourseStudentsReference(courseId).child(userInfo.getUserId()).setValue(userInfo);
        FirebaseReferance.getUserReference(userInfo.getUserId()).child("courseNo").setValue(userInfo.getCourseNo());
        updateCourseStudentNo(courseId);
    }

    private void updateCourseStudentNo(String courseId) {
        FirebaseReferance.getCourseDetailsReference(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, Objects.requireNonNull(dataSnapshot.getValue()).toString());
                    Course obj = dataSnapshot.getValue(Course.class);
                    if (obj != null) {
                        int studentsNo = obj.getStudentsNo();
                        studentsNo = studentsNo + 1;
                        FirebaseReferance.getCourseDetailsReference(courseId).child("studentsNo").setValue(studentsNo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void updateCourseViewsNo(Course course, UserInfo user) {
        String courseId = course.getCourseId();
        String userId = user.getUserId();
        FirebaseReferance.getCourseDetailsReference(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, Objects.requireNonNull(dataSnapshot.getValue()).toString());
                    Course obj = dataSnapshot.getValue(Course.class);
                    if (obj != null) {
                        int viewsNo = obj.getViewsNo();
                        viewsNo = viewsNo + 1;
                        FirebaseReferance.getCourseDetailsReference(courseId).child("viewsNo").setValue(viewsNo);
                        FirebaseReferance.getCourseViewerReference(courseId).child(userId).push().setValue(userId);
                        sendNotifications(TYPE_COURSE_VIEW, courseId, userId, user.getShortName(),
                                String.format("%s View %s course", user.getShortName(), course.getCourseName()), course.getLecturerId());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void updateCourseViewsNo(Course course) {
        FirebaseReferance.getLecturerCourseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                        String lecturerId = dataSnap.getKey();
                        for (DataSnapshot snapshot : dataSnap.getChildren()) {
                            String key = snapshot.getKey();
                            if (key != null && snapshot.getKey().equals(course.getCourseId())) {
                                Log.i(TAG, Objects.requireNonNull(dataSnapshot.getValue()).toString());
                                FirebaseReferance.getLecturerCourseReference(lecturerId).child(key).setValue(course);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    ValueEventListener checkIfIRegisterListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists())
                checkIfIRegisterRequest.onSuccess(true);
            else checkIfIRegisterRequest.onSuccess(false);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            checkIfIRegisterRequest.onFail(error.getMessage());
        }
    };

    public void checkIfIRegisterInCourse(String courseId, String userId, RequestListener<Boolean> requestListener) {
        this.checkIfIRegisterRequest = requestListener;
        FirebaseReferance.getUserCoursesReference(userId).child(courseId).addValueEventListener(checkIfIRegisterListener);
    }

    public void stopIfIRegisterInCourse(String courseId, String userId) {
        if (checkIfIRegisterListener != null)
            FirebaseReferance.getUserCoursesReference(userId).child(courseId).removeEventListener(checkIfIRegisterListener);
    }

    ValueEventListener checkIfSubmitAssignmentListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                Assignments assignments = snapshot.getValue(Assignments.class);
                checkIfSubmitAssignmentRequest.onSuccess(assignments);
            } else checkIfSubmitAssignmentRequest.onSuccess(null);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            checkIfSubmitAssignmentRequest.onFail(error.getMessage());
        }
    };

    public void checkIfSubmitAssignment(String courseId, String userId, RequestListener<Assignments> requestListener) {
        this.checkIfSubmitAssignmentRequest = requestListener;
        FirebaseReferance.getUserAssignmentReference(userId).child(courseId).addValueEventListener(checkIfSubmitAssignmentListener);
    }

    public void stopCheckIfSubmitAssignment(String courseId, String userId) {
        if (checkIfSubmitAssignmentListener != null)
            FirebaseReferance.getUserAssignmentReference(userId).child(courseId).removeEventListener(checkIfSubmitAssignmentListener);
    }

    public void checkIfViewCourse(Course course, UserInfo user) {
        FirebaseReferance.getCourseViewerReference(course.getCourseId()).child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    CoursesAndLectures.getInstance().updateCourseViewsNo(course, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addEditCourseLectureInDataBase(int from, Lecture lecture, StringRequestListener<Lecture> requestListener) {
        FirebaseReferance.getCourseLecturesReference(lecture.getCourseId(), lecture.getId()).setValue(lecture).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                    editCourseForAll(lecture);
                sendNotificationToAllStudents(from, lecture);
                Log.e("add course: ", task.getResult() + "");
                requestListener.onSuccess(MLearningApp.getInstance().getString(R.string.success_add), lecture);

            } else {
                getAuthReference().signOut();
                Log.e("SignUp error: ", task.getException() + "");
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    public void sendNotificationToAllStudents(int action, Lecture lecture) {
        getCourseStudentsReference(lecture.getCourseId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String receiverId = dataSnapshot.getKey();
                        String message = "";
                        String type = "";
                        if (action == ACTION_EDIT) {
                            message = String.format(MLearningApp.getInstance().getString(R.string.edit_lecture), lecture.getLecturerName(), lecture.getTitle());
                            type = TYPE_EDIT_LECTURE;
                        } else if (action == ACTION_DELETE) {
                            message = String.format(MLearningApp.getInstance().getString(R.string.delete_lecture), lecture.getLecturerName(), lecture.getTitle());
                            type = TYPE_DELETE_LECTURE;
                        } else {
                            message = String.format(MLearningApp.getInstance().getString(R.string.add_new_lecture), lecture.getLecturerName(), lecture.getTitle());
                            type = TYPE_ADD_LECTURE;
                        }
                        sendNotifications(type, lecture.getCourseId(), lecture.getLecturerId(), lecture.getLecturerName(), message, receiverId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void addCourseAssignmentInDataBase(Assignments assignments, String lecturerId, StringRequestListener<Assignments> requestListener) {

        FirebaseReferance.getCourseAssignmentReference(assignments.getCourseId()).child(assignments.getId()).setValue(assignments).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseReferance.getUserAssignmentReference(assignments.getStudentId()).child(assignments.getCourseId()).setValue(assignments);
                Log.e("add course: ", task.getResult() + "");
                String message = String.format(MLearningApp.getInstance().getString(R.string.add_new_assignment), assignments.getStudentName());
                sendNotifications(TYPE_ADD_ASSIGNMENT, assignments.getCourseId(), assignments.getStudentId(), assignments.getStudentName(), message, lecturerId);
                requestListener.onSuccess(MLearningApp.getInstance().getString(R.string.success_add), assignments);

            } else {
                getAuthReference().signOut();
                Log.e("SignUp error: ", task.getException() + "");
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    public void editCourseLectureForAll(Lecture lecture) {
        FirebaseReferance.getUserCoursesReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userId = dataSnapshot.getKey();
                        Log.e("dataSnapshot: ", dataSnapshot.getKey());
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            String key = dataSnap.getKey();
                            Log.e("dataSnap: ", key);
                            if (key != null && key.equals(lecture.getCourseId())) {
                                FirebaseReferance.getUserCoursesReference(userId).child(lecture.getCourseId()).setValue(lecture);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
