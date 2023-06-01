package com.m_learning.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.m_learning.databinding.FragmentCustomFloatDialogBinding;


public class CustomFloatDialog extends DialogFragment {


    private FragmentCustomFloatDialogBinding binding;

    private String title;
    private String msg;
    private String okMsg;
    private String cancelMsg;
    private Boolean isCancelable = true;
    private onClickListener listener;

    public CustomFloatDialog() {
        // Required empty public constructor
    }

    public static CustomFloatDialog newInstance(String title, String msg, String okMsg, String cancelMsg) {
        CustomFloatDialog fragment = new CustomFloatDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("msg", msg);
        args.putString("okMsg", okMsg);
        args.putString("cancelMsg", cancelMsg);
        fragment.setArguments(args);
        return fragment;
    }


    private void getArgumentsData() {
        if (getArguments() != null) {
            this.title = getArguments().getString("title");
            this.msg = getArguments().getString("msg");
            this.okMsg = getArguments().getString("okMsg");
            this.cancelMsg = getArguments().getString("cancelMsg");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomFloatDialogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        getArgumentsData();

        setCancelable(isCancelable);
        if (TextUtils.isEmpty(title)) {
            binding.tvTitle.setVisibility(View.INVISIBLE);
        } else binding.tvTitle.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(okMsg)) {
            binding.btnOk.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(cancelMsg)) {
            binding.btnCancel.setVisibility(View.GONE);
        }


        binding.btnOk.setText(okMsg);
        binding.btnCancel.setText(cancelMsg);
        binding.tvTitle.setText(title);
        binding.tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
        if (!TextUtils.isEmpty(msg))
            binding.tvMsg.setText(Html.fromHtml(msg));

        binding.tvMsg.setMovementMethod(LinkMovementMethod.getInstance());

        binding.btnOk.setOnClickListener(view1 -> {
            getDialog().dismiss();
            if (listener != null)
                listener.onOkClick();
        });

        binding.btnCancel.setOnClickListener(view1 -> {
            getDialog().dismiss();
            if (listener != null)
                listener.onCancelClick();
        });


        return view;

    }

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

//        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        super.onResume();
//        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
//            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
//                dismiss();
////                getActivity().finish();
//                return true;
//            } else return false;
//        });
    }

    public interface onClickListener {
        void onOkClick();

        void onCancelClick();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
