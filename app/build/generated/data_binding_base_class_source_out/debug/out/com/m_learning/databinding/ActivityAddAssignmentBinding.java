// Generated by view binder compiler. Do not edit!
package com.m_learning.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.m_learning.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityAddAssignmentBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button btnSend;

  @NonNull
  public final EditText edName;

  @NonNull
  public final LinearLayout frame;

  @NonNull
  public final ImageButton ibtnBack;

  @NonNull
  public final ImageView img;

  @NonNull
  public final ImageView imgPhotoOrVideo;

  @NonNull
  public final LinearLayout llAddImage;

  @NonNull
  public final FrameLayout llChooseFile;

  @NonNull
  public final LinearLayout root;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final TextView tvFileName;

  @NonNull
  public final TextView tvTitle;

  @NonNull
  public final TextView tvTitle1;

  private ActivityAddAssignmentBinding(@NonNull LinearLayout rootView, @NonNull Button btnSend,
      @NonNull EditText edName, @NonNull LinearLayout frame, @NonNull ImageButton ibtnBack,
      @NonNull ImageView img, @NonNull ImageView imgPhotoOrVideo, @NonNull LinearLayout llAddImage,
      @NonNull FrameLayout llChooseFile, @NonNull LinearLayout root, @NonNull Toolbar toolbar,
      @NonNull TextView tvFileName, @NonNull TextView tvTitle, @NonNull TextView tvTitle1) {
    this.rootView = rootView;
    this.btnSend = btnSend;
    this.edName = edName;
    this.frame = frame;
    this.ibtnBack = ibtnBack;
    this.img = img;
    this.imgPhotoOrVideo = imgPhotoOrVideo;
    this.llAddImage = llAddImage;
    this.llChooseFile = llChooseFile;
    this.root = root;
    this.toolbar = toolbar;
    this.tvFileName = tvFileName;
    this.tvTitle = tvTitle;
    this.tvTitle1 = tvTitle1;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityAddAssignmentBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityAddAssignmentBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_add_assignment, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityAddAssignmentBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_send;
      Button btnSend = ViewBindings.findChildViewById(rootView, id);
      if (btnSend == null) {
        break missingId;
      }

      id = R.id.ed_name;
      EditText edName = ViewBindings.findChildViewById(rootView, id);
      if (edName == null) {
        break missingId;
      }

      id = R.id.frame;
      LinearLayout frame = ViewBindings.findChildViewById(rootView, id);
      if (frame == null) {
        break missingId;
      }

      id = R.id.ibtn_back;
      ImageButton ibtnBack = ViewBindings.findChildViewById(rootView, id);
      if (ibtnBack == null) {
        break missingId;
      }

      id = R.id.img;
      ImageView img = ViewBindings.findChildViewById(rootView, id);
      if (img == null) {
        break missingId;
      }

      id = R.id.imgPhotoOrVideo;
      ImageView imgPhotoOrVideo = ViewBindings.findChildViewById(rootView, id);
      if (imgPhotoOrVideo == null) {
        break missingId;
      }

      id = R.id.ll_add_image;
      LinearLayout llAddImage = ViewBindings.findChildViewById(rootView, id);
      if (llAddImage == null) {
        break missingId;
      }

      id = R.id.llChooseFile;
      FrameLayout llChooseFile = ViewBindings.findChildViewById(rootView, id);
      if (llChooseFile == null) {
        break missingId;
      }

      LinearLayout root = (LinearLayout) rootView;

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.tvFileName;
      TextView tvFileName = ViewBindings.findChildViewById(rootView, id);
      if (tvFileName == null) {
        break missingId;
      }

      id = R.id.tvTitle;
      TextView tvTitle = ViewBindings.findChildViewById(rootView, id);
      if (tvTitle == null) {
        break missingId;
      }

      id = R.id.tv_title;
      TextView tvTitle1 = ViewBindings.findChildViewById(rootView, id);
      if (tvTitle1 == null) {
        break missingId;
      }

      return new ActivityAddAssignmentBinding((LinearLayout) rootView, btnSend, edName, frame,
          ibtnBack, img, imgPhotoOrVideo, llAddImage, llChooseFile, root, toolbar, tvFileName,
          tvTitle, tvTitle1);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}