// Generated by view binder compiler. Do not edit!
package com.m_learning.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.m_learning.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityEditProfileBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button btnSave;

  @NonNull
  public final EditText edAddress;

  @NonNull
  public final EditText edBirthDate;

  @NonNull
  public final EditText edFirstName;

  @NonNull
  public final EditText edLastName;

  @NonNull
  public final EditText edMiddleName;

  @NonNull
  public final EditText edMobileNo;

  @NonNull
  public final ImageButton ibtnBack;

  @NonNull
  public final ImageView ivAvatar;

  @NonNull
  public final RelativeLayout rlImage;

  @NonNull
  public final LinearLayout root;

  @NonNull
  public final Toolbar toolbar;

  private ActivityEditProfileBinding(@NonNull LinearLayout rootView, @NonNull Button btnSave,
      @NonNull EditText edAddress, @NonNull EditText edBirthDate, @NonNull EditText edFirstName,
      @NonNull EditText edLastName, @NonNull EditText edMiddleName, @NonNull EditText edMobileNo,
      @NonNull ImageButton ibtnBack, @NonNull ImageView ivAvatar, @NonNull RelativeLayout rlImage,
      @NonNull LinearLayout root, @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.btnSave = btnSave;
    this.edAddress = edAddress;
    this.edBirthDate = edBirthDate;
    this.edFirstName = edFirstName;
    this.edLastName = edLastName;
    this.edMiddleName = edMiddleName;
    this.edMobileNo = edMobileNo;
    this.ibtnBack = ibtnBack;
    this.ivAvatar = ivAvatar;
    this.rlImage = rlImage;
    this.root = root;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityEditProfileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityEditProfileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_edit_profile, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityEditProfileBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_save;
      Button btnSave = ViewBindings.findChildViewById(rootView, id);
      if (btnSave == null) {
        break missingId;
      }

      id = R.id.ed_address;
      EditText edAddress = ViewBindings.findChildViewById(rootView, id);
      if (edAddress == null) {
        break missingId;
      }

      id = R.id.ed_birth_date;
      EditText edBirthDate = ViewBindings.findChildViewById(rootView, id);
      if (edBirthDate == null) {
        break missingId;
      }

      id = R.id.ed_first_name;
      EditText edFirstName = ViewBindings.findChildViewById(rootView, id);
      if (edFirstName == null) {
        break missingId;
      }

      id = R.id.ed_last_name;
      EditText edLastName = ViewBindings.findChildViewById(rootView, id);
      if (edLastName == null) {
        break missingId;
      }

      id = R.id.ed_middle_name;
      EditText edMiddleName = ViewBindings.findChildViewById(rootView, id);
      if (edMiddleName == null) {
        break missingId;
      }

      id = R.id.ed_mobileNo;
      EditText edMobileNo = ViewBindings.findChildViewById(rootView, id);
      if (edMobileNo == null) {
        break missingId;
      }

      id = R.id.ibtn_back;
      ImageButton ibtnBack = ViewBindings.findChildViewById(rootView, id);
      if (ibtnBack == null) {
        break missingId;
      }

      id = R.id.ivAvatar;
      ImageView ivAvatar = ViewBindings.findChildViewById(rootView, id);
      if (ivAvatar == null) {
        break missingId;
      }

      id = R.id.rlImage;
      RelativeLayout rlImage = ViewBindings.findChildViewById(rootView, id);
      if (rlImage == null) {
        break missingId;
      }

      LinearLayout root = (LinearLayout) rootView;

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityEditProfileBinding((LinearLayout) rootView, btnSave, edAddress,
          edBirthDate, edFirstName, edLastName, edMiddleName, edMobileNo, ibtnBack, ivAvatar,
          rlImage, root, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
