// Generated by view binder compiler. Do not edit!
package com.m_learning.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.m_learning.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemLectureBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageView ivDelete;

  @NonNull
  public final ImageView ivEdit;

  @NonNull
  public final ImageView ivImage;

  @NonNull
  public final LinearLayout llActions;

  @NonNull
  public final LinearLayout llViews;

  @NonNull
  public final RelativeLayout rlImage;

  @NonNull
  public final LinearLayout root;

  @NonNull
  public final TextView tvDate;

  @NonNull
  public final TextView tvTitle;

  @NonNull
  public final TextView tvViewNo;

  private ItemLectureBinding(@NonNull LinearLayout rootView, @NonNull ImageView ivDelete,
      @NonNull ImageView ivEdit, @NonNull ImageView ivImage, @NonNull LinearLayout llActions,
      @NonNull LinearLayout llViews, @NonNull RelativeLayout rlImage, @NonNull LinearLayout root,
      @NonNull TextView tvDate, @NonNull TextView tvTitle, @NonNull TextView tvViewNo) {
    this.rootView = rootView;
    this.ivDelete = ivDelete;
    this.ivEdit = ivEdit;
    this.ivImage = ivImage;
    this.llActions = llActions;
    this.llViews = llViews;
    this.rlImage = rlImage;
    this.root = root;
    this.tvDate = tvDate;
    this.tvTitle = tvTitle;
    this.tvViewNo = tvViewNo;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemLectureBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemLectureBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_lecture, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemLectureBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv_delete;
      ImageView ivDelete = ViewBindings.findChildViewById(rootView, id);
      if (ivDelete == null) {
        break missingId;
      }

      id = R.id.iv_edit;
      ImageView ivEdit = ViewBindings.findChildViewById(rootView, id);
      if (ivEdit == null) {
        break missingId;
      }

      id = R.id.iv_image;
      ImageView ivImage = ViewBindings.findChildViewById(rootView, id);
      if (ivImage == null) {
        break missingId;
      }

      id = R.id.ll_actions;
      LinearLayout llActions = ViewBindings.findChildViewById(rootView, id);
      if (llActions == null) {
        break missingId;
      }

      id = R.id.ll_views;
      LinearLayout llViews = ViewBindings.findChildViewById(rootView, id);
      if (llViews == null) {
        break missingId;
      }

      id = R.id.rlImage;
      RelativeLayout rlImage = ViewBindings.findChildViewById(rootView, id);
      if (rlImage == null) {
        break missingId;
      }

      LinearLayout root = (LinearLayout) rootView;

      id = R.id.tv_date;
      TextView tvDate = ViewBindings.findChildViewById(rootView, id);
      if (tvDate == null) {
        break missingId;
      }

      id = R.id.tv_title;
      TextView tvTitle = ViewBindings.findChildViewById(rootView, id);
      if (tvTitle == null) {
        break missingId;
      }

      id = R.id.tv_view_no;
      TextView tvViewNo = ViewBindings.findChildViewById(rootView, id);
      if (tvViewNo == null) {
        break missingId;
      }

      return new ItemLectureBinding((LinearLayout) rootView, ivDelete, ivEdit, ivImage, llActions,
          llViews, rlImage, root, tvDate, tvTitle, tvViewNo);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
