package com.m_learning.network.utils;

import static com.m_learning.utils.ConstantApp.ANDROID;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.m_learning.utils.AppSharedData;

public final class FirebaseReferance {

    private static FirebaseReferance instance;

    public static FirebaseReferance getInstance() {
        if (instance == null)
            instance = new FirebaseReferance();
        return instance;
    }

    private static final String USERS_REF = "Users";
    private static final String COURSES_REF = "Courses";
    private static final String COURSES_VIEWER_REF = "CoursesViewer";
    private static final String COURSES_STUDENTS_REF = "CourseStudents";
    private static final String LECTURER_COURSES_REF = "LecturerCourse";
    private static final String COURSE_LECTURES_REF = "Lectures";
    private static final String COURSE_LECTURES_VIEWS_REF = "LecturesViews";
    private static final String SEEN_LECTURES_REF = "SeenLectures";
    private static final String COURSE_ASSIGNMENT_REF = "CourseAssignment";
    private static final String USER_ASSIGNMENT_REF = "UserAssignment";
    private static final String USER_COURSES_REF = "UserCourses";
    private static final String TOKEN_REF = "deviceToken";
    private static final String OS = "userOs";
    private static final String CHAT_ROOMS = "ChatRooms";
    private static final String ACTIVE = "Active";
    public static final String COUNTER = "counter";
    private static final String SEEN = "Seen";
    private static final String TYPE_INDICATOR = "TypeIndicator";
    private static final String TYPE_STATUS = "TypeStatus";
    private static final String IMAGE_STORGE_FOLDER_REF = "Images";
    private static final String CHAT_IMAGE_STORAGE_FOLDER_REF = "ChatImages";
    private static final String USER_IMAGE_STORAGE_FOLDER_REF = "UserImages";
    private static final String VERSION_REF = "Version";
    public static final String NOTIFICATIONS_REF = "Notifications";


    private DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference();
    }


    public static DatabaseReference getUserReference(String userId) {
        return getUsersListReference()
                .child(userId);
    }

    public static DatabaseReference saveUserFcmTokenReference(String userId) {
        return getUserReference(userId)
                .child(TOKEN_REF);
    }

    public static DatabaseReference saveUserDeviceOs(String userId) {
        return getUserReference(userId)
                .child(OS);
    }

    public DatabaseReference getChatRoomListReference() {
        return getReference().child(CHAT_ROOMS);
    }

    public DatabaseReference getChatRoomReference(String roomId) {
        return getChatRoomListReference()
                .child(roomId);
    }

    public DatabaseReference statusUserInRoomReference(String roomId, String userId) {
        return getChatRoomReference(roomId)
                .child(ACTIVE)
                .child(userId);
    }

    public DatabaseReference changeStatusUserInRoom(String roomId, String userId) {
        return statusUserInRoomReference(roomId, userId)
                .child(ACTIVE);
    }

    public DatabaseReference countUnReadMessage(String roomId, String userId) {
        return getReference().child(SEEN)
                .child(roomId)
                .child(userId)
                .child(COUNTER);
    }

    public DatabaseReference getCountUnReadMessage(String roomId, String userId) {
        return getReference().child(SEEN)
                .child(roomId)
                .child(userId);
    }

    public DatabaseReference getSeen() {
        return getReference().child(SEEN);
    }

    public DatabaseReference typeIndicatorReference(String roomId, String userId) {
        return getChatRoomReference(roomId)
                .child(TYPE_INDICATOR)
                .child(userId);
    }

    public DatabaseReference setTypeStatus(String roomId, String userId) {
        return typeIndicatorReference(roomId, userId)
                .child(TYPE_STATUS);
    }

    ///////////// firestore
    private StorageReference getStorageReference() {
        return FirebaseStorage.getInstance().getReference();
    }

    public StorageReference getStorageChatImageReference(String imageName) {
        return getStorageReference().child(IMAGE_STORGE_FOLDER_REF).child(CHAT_IMAGE_STORAGE_FOLDER_REF).child(imageName);
    }

    public StorageReference getStorageUserImageReference(String imageName) {
        return getStorageReference().child(IMAGE_STORGE_FOLDER_REF).child(USER_IMAGE_STORAGE_FOLDER_REF).child(imageName);
    }

    public StorageReference getCourseStorageImageReference(String imageName) {
        return getStorageReference().child(IMAGE_STORGE_FOLDER_REF).child(COURSES_REF).child(imageName);
    }

    public StorageReference getAssignmentStorageImageReference(String imageName) {
        return getStorageReference().child(IMAGE_STORGE_FOLDER_REF).child(COURSE_ASSIGNMENT_REF).child(imageName);
    }

    public StorageReference getLectureStorageImageReference(String imageName) {
        return getStorageReference().child(IMAGE_STORGE_FOLDER_REF).child(COURSE_LECTURES_REF).child(imageName);
    }


    public DatabaseReference getVersionCode() {
        return getReference()
                .child(VERSION_REF).child(ANDROID);
    }


    public static FirebaseAuth getAuthReference() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getAuthCurrentUserReference() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DatabaseReference getDatabaseReference() {
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference();
        scoresRef.keepSynced(true);
        return scoresRef;
    }

    public static DatabaseReference getUsersListReference() {
        return getDatabaseReference()
                .child(USERS_REF);
    }


    public static String getUserId() {
        return (getAuthReference().getCurrentUser() != null) ? getAuthReference().getCurrentUser().getUid() :
                ((AppSharedData.getUserData() != null) ? AppSharedData.getUserData().getUserId() : "");
    }

    public static DatabaseReference getNotificationsReference() {
        return getDatabaseReference()
                .child(NOTIFICATIONS_REF);
    }

    public static DatabaseReference getUserNotificationsReference(String userId) {
        return getDatabaseReference()
                .child(NOTIFICATIONS_REF)
                .child(userId);
    }

    public static DatabaseReference getUserCoursesReference(String userId) {
        return getDatabaseReference()
                .child(USER_COURSES_REF)
                .child(userId);
    }

    public static DatabaseReference getUserCoursesReference() {
        return getDatabaseReference()
                .child(USER_COURSES_REF);
    }

    public static DatabaseReference getCoursesReference() {
        return getDatabaseReference()
                .child(COURSES_REF);
    }

    public static DatabaseReference getCourseDetailsReference(String courseId) {
        return getDatabaseReference()
                .child(COURSES_REF).child(courseId);
    }

    public static DatabaseReference getCourseViewerReference(String courseId) {
        return getDatabaseReference()
                .child(COURSES_VIEWER_REF).child(courseId);
    }


    public static DatabaseReference getCourseStudentsReference(String courseId) {
        return getDatabaseReference()
                .child(COURSES_STUDENTS_REF).child(courseId);
    }

    public static DatabaseReference getCourseLecturesReference(String courseId) {
        return getDatabaseReference()
                .child(COURSE_LECTURES_REF).child(courseId);
    }

    public static DatabaseReference getSeenLecturesReference(String userId) {
        return getDatabaseReference()
                .child(SEEN_LECTURES_REF).child(userId);
    }
   public static DatabaseReference getLecturesViewsReference(String lectureId) {
        return getDatabaseReference()
                .child(COURSE_LECTURES_VIEWS_REF).child(lectureId);
    }

    public static DatabaseReference getCourseAssignmentReference(String courseId) {
        return getDatabaseReference()
                .child(COURSE_ASSIGNMENT_REF).child(courseId);
    }

    public static DatabaseReference getUserAssignmentReference(String userId) {
        return getDatabaseReference()
                .child(USER_ASSIGNMENT_REF).child(userId);
    }

    public static DatabaseReference getCourseLecturesReference(String courseId, String lectureId) {
        return getDatabaseReference()
                .child(COURSE_LECTURES_REF).child(courseId).child(lectureId);
    }

    public static DatabaseReference getLecturerCourseReference(String userId) {
        return getDatabaseReference()
                .child(LECTURER_COURSES_REF).child(userId);
    }

    public static DatabaseReference getLecturerCourseReference() {
        return getDatabaseReference()
                .child(LECTURER_COURSES_REF);
    }

}
