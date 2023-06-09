// Generated by view binder compiler. Do not edit!
package com.m_learning.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.m_learning.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentCourseOverviewBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final HorizontalScrollView hScrollView;

  @NonNull
  public final ImageView imgProduct;

  @NonNull
  public final CircularProgressIndicator progressCircular;

  @NonNull
  public final LinearLayout root;

  @NonNull
  public final TextView tvCourseCode;

  @NonNull
  public final TextView tvCourseDoc;

  @NonNull
  public final TextView tvDeadline;

  @NonNull
  public final TextView tvDescription;

  @NonNull
  public final TextView tvName;

  @NonNull
  public final TextView tvStudentsCount;

  @NonNull
  public final TextView tvViews;

  private FragmentCourseOverviewBinding(@NonNull LinearLayout rootView,
      @NonNull HorizontalScrollView hScrollView, @NonNull ImageView imgProduct,
      @NonNull CircularProgressIndicator progressCircular, @NonNull LinearLayout root,
      @NonNull TextView tvCourseCode, @NonNull TextView tvCourseDoc, @NonNull TextView tvDeadline,
      @NonNull TextView tvDescription, @NonNull TextView tvName, @NonNull TextView tvStudentsCount,
      @NonNull TextView tvViews) {
    this.rootView = rootView;
    this.hScrollView = hScrollView;
    this.imgProduct = imgProduct;
    this.progressCircular = progressCircular;
    this.root = root;
    this.tvCourseCode = tvCourseCode;
    this.tvCourseDoc = tvCourseDoc;
    this.tvDeadline = tvDeadline;
    this.tvDescription = tvDescription;
    this.tvName = tvName;
    this.tvStudentsCount = tvStudentsCount;
    this.tvViews = tvViews;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentCourseOverviewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentCourseOverviewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_course_overview, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentCourseOverviewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.hScrollView;
      HorizontalScrollView hScrollView = ViewBindings.findChildViewById(rootView, id);
      if (hScrollView == null) {
        break missingId;
      }

      id = R.id.img_product;
      ImageView imgProduct = ViewBindings.findChildViewById(rootView, id);
      if (imgProduct == null) {
        break missingId;
      }

      id = R.id.progress_circular;
      CircularProgressIndicator progressCircular = ViewBindings.findChildViewById(rootView, id);
      if (progressCircular == null) {
        break missingId;
      }

      LinearLayout root = (LinearLayout) rootView;

      id = R.id.tv_courseCode;
      TextView tvCourseCode = ViewBindings.findChildViewById(rootView, id);
      if (tvCourseCode == null) {
        break missingId;
      }

      id = R.id.tvCourse_doc;
      TextView tvCourseDoc = ViewBindings.findChildViewById(rootView, id);
      if (tvCourseDoc == null) {
        break missingId;
      }

      id = R.id.tvDeadline;
      TextView tvDeadline = ViewBindings.findChildViewById(rootView, id);
      if (tvDeadline == null) {
        break missingId;
      }

      id = R.id.tv_description;
      TextView tvDescription = ViewBindings.findChildViewById(rootView, id);
      if (tvDescription == null) {
        break missingId;
      }

      id = R.id.tv_name;
      TextView tvName = ViewBindings.findChildViewById(rootView, id);
      if (tvName == null) {
        break missingId;
      }

      id = R.id.tvStudentsCount;
      TextView tvStudentsCount = ViewBindings.findChildViewById(rootView, id);
      if (tvStudentsCount == null) {
        break missingId;
      }

      id = R.id.tvViews;
      TextView tvViews = ViewBindings.findChildViewById(rootView, id);
      if (tvViews == null) {
        break missingId;
      }

      return new FragmentCourseOverviewBinding((LinearLayout) rootView, hScrollView, imgProduct,
          progressCircular, root, tvCourseCode, tvCourseDoc, tvDeadline, tvDescription, tvName,
          tvStudentsCount, tvViews);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
