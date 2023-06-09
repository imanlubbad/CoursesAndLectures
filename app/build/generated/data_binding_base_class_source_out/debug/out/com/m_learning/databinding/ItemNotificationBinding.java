// Generated by view binder compiler. Do not edit!
package com.m_learning.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.m_learning.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemNotificationBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageView ivColor;

  @NonNull
  public final LinearLayout llParent;

  @NonNull
  public final LinearLayout swipe;

  @NonNull
  public final TextView tvDate;

  @NonNull
  public final TextView tvMsg;

  private ItemNotificationBinding(@NonNull LinearLayout rootView, @NonNull ImageView ivColor,
      @NonNull LinearLayout llParent, @NonNull LinearLayout swipe, @NonNull TextView tvDate,
      @NonNull TextView tvMsg) {
    this.rootView = rootView;
    this.ivColor = ivColor;
    this.llParent = llParent;
    this.swipe = swipe;
    this.tvDate = tvDate;
    this.tvMsg = tvMsg;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemNotificationBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemNotificationBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_notification, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemNotificationBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv_color;
      ImageView ivColor = ViewBindings.findChildViewById(rootView, id);
      if (ivColor == null) {
        break missingId;
      }

      id = R.id.ll_parent;
      LinearLayout llParent = ViewBindings.findChildViewById(rootView, id);
      if (llParent == null) {
        break missingId;
      }

      LinearLayout swipe = (LinearLayout) rootView;

      id = R.id.tv_date;
      TextView tvDate = ViewBindings.findChildViewById(rootView, id);
      if (tvDate == null) {
        break missingId;
      }

      id = R.id.tv_msg;
      TextView tvMsg = ViewBindings.findChildViewById(rootView, id);
      if (tvMsg == null) {
        break missingId;
      }

      return new ItemNotificationBinding((LinearLayout) rootView, ivColor, llParent, swipe, tvDate,
          tvMsg);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
