package com.m_learning.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.m_learning.R;
import com.m_learning.databinding.FragmentItemListDialogBinding;

public class ItemSelectImageDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String ARG_DIALOG_TITLE = "title";
    private Listener mListener;

    private FragmentItemListDialogBinding binding;

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public static ItemSelectImageDialogFragment newInstance(String title, int type) {
        final ItemSelectImageDialogFragment fragment = new ItemSelectImageDialogFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_DIALOG_TITLE, title);
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentItemListDialogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.selectImageCamera.setOnClickListener(this);
        binding.selectImageGallery.setOnClickListener(this);
        binding.selectVideoGallery.setOnClickListener(this);
        int type = getArguments().getInt("type");
        switch (type) {
            case 1:
                binding.selectImageGallery.setVisibility(View.VISIBLE);
                binding.selectImageCamera.setVisibility(View.GONE);
                binding.selectVideoGallery.setVisibility(View.GONE);
                break;
            case 2:
                binding.selectImageCamera.setVisibility(View.VISIBLE);
                binding.selectImageGallery.setVisibility(View.VISIBLE);
                binding.selectVideoGallery.setVisibility(View.GONE);
                break;
            case 3:
                binding.selectImageGallery.setVisibility(View.VISIBLE);
                binding.selectImageCamera.setVisibility(View.GONE);
                binding.selectVideoGallery.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_image_camera:
                if (mListener != null) {
                    dismiss();
                    mListener.onCameraClicked();
                }
                break;
            case R.id.select_image_gallery:
                if (mListener != null) {
                    dismiss();
                    mListener.onGalleryClicked();
                }
                break;
            case R.id.select_video_gallery:
                if (mListener != null) {
                    dismiss();
                    mListener.onVideoClicked();
                }
                break;
        }
    }

    public interface Listener {
        void onGalleryClicked();

        void onCameraClicked();

        void onVideoClicked();
    }
}