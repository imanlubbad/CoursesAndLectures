// Generated by view binder compiler. Do not edit!
package com.m_learning.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.m_learning.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LoadMoreBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final CircularProgressIndicator loadMore;

  private LoadMoreBinding(@NonNull LinearLayout rootView,
      @NonNull CircularProgressIndicator loadMore) {
    this.rootView = rootView;
    this.loadMore = loadMore;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LoadMoreBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LoadMoreBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.load_more, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LoadMoreBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.loadMore;
      CircularProgressIndicator loadMore = ViewBindings.findChildViewById(rootView, id);
      if (loadMore == null) {
        break missingId;
      }

      return new LoadMoreBinding((LinearLayout) rootView, loadMore);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
