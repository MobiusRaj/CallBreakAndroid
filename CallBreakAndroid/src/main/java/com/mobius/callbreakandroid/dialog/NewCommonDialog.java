package com.mobius.callbreakandroid.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobius.callbreakandroid.R;
import com.mobius.callbreakandroid.interfaces.OnDialogClick;
import com.mobius.callbreakandroid.utility_base.C;

public class NewCommonDialog extends Dialog {

    private Activity activity;
//    private Music_Manager musicManager;

    private NewCommonDialog(Builder builder) {
        super(builder.activity, R.style.Theme_Transparent);
        
        initDialog(builder);
    }
    
    private void initDialog(final Builder builder) {
        
        this.activity = builder.activity;
        
        C c = C.getInstance();
        
//        musicManager = Music_Manager.getInstance(this.activity);
        
        setContentView(R.layout.dialog_newcommon);
        setCancelable(builder.cancelable);
        
        if (this.getWindow() != null) {
            this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        
        TextView titleText = findViewById(R.id.alertTitle);
        titleText.setText(builder.title);

        ImageView PopupBg = findViewById(R.id.exit_popup_bg);
        PopupBg.setBackgroundResource(builder.MainPopupBg);

        TextView messageText = findViewById(R.id.tvMessage);
        messageText.setText(builder.message);
        
        Button positiveButton = findViewById(R.id.button1);
        Button negativeButton = findViewById(R.id.button2);
//        Button extraButton = findViewById(R.id.button3);
        
        
        if (builder.positiveButton != null) {
            positiveButton.setVisibility(View.VISIBLE);
            positiveButton.setText(builder.positiveButton);
            positiveButton.setOnClickListener(v -> {
//                musicManager.buttonClick();
                if (builder.onPositiveButtonClickListener != null) {
                    builder.onPositiveButtonClickListener.onClick();
                }
                dismiss();
            });
        } else {
            positiveButton.setVisibility(View.GONE);
        }
        
        if (builder.negativeButton != null) {
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setText(builder.negativeButton);
            negativeButton.setOnClickListener(v -> {
//                musicManager.buttonClick();
                if (builder.onNegativeButtonClickListener != null) {
                    builder.onNegativeButtonClickListener.onClick();
                }
                dismiss();
            });
        } else {
            negativeButton.setVisibility(View.GONE);
        }
        
//        if (builder.extraButton != null) {
//            extraButton.setVisibility(View.VISIBLE);
//            extraButton.setTypeface(c.typeface);
//            extraButton.setText(builder.extraButton);
//            extraButton.setOnClickListener(v -> {
//                musicManager.buttonClick();
//                if (builder.onExtraButtonClickListener != null) {
//                    builder.onExtraButtonClickListener.onClick();
//                }
//                dismiss();
//            });
//        } else {
//            extraButton.setVisibility(View.GONE);
//        }

    }
    
    @Override
    public void show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!activity.isFinishing() && !activity.isDestroyed() && !this.isShowing()) {
                try {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    super.show();
                    this.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
                    this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!activity.isFinishing() && !this.isShowing()) {
                try {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    super.show();
                    this.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
                    this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    @Override
    public void dismiss() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!activity.isFinishing() && !activity.isDestroyed() && this.isShowing()) {
                try {
                    super.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Api < 17. Unfortunately cannot check for isDestroyed()
            if (!activity.isFinishing() && this.isShowing()) {
                try {
                    super.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static class Builder {
        
        private int MainPopupBg = R.drawable.cb_popup_bg;
        private String title = "Title";
        private String message = "Message";
        private String positiveButton;
        private String negativeButton;
        private String extraButton;
        private boolean closeVisible = false;
        private boolean cancelable = false;
        private Activity activity;
        private OnDialogClick onPositiveButtonClickListener;
        private OnDialogClick onNegativeButtonClickListener;
//        private OnDialogClick onExtraButtonClickListener;
        
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setPopupBg(int PopupBg) {
            this.MainPopupBg = PopupBg;
            return this;
        }
        
        public Builder setPositiveButton(String text, OnDialogClick onClickListener) {
            this.positiveButton = text;
            this.onPositiveButtonClickListener = onClickListener;
            return this;
        }
        
        public Builder setNegativeButton(String text, OnDialogClick onClickListener) {
            this.negativeButton = text;
            this.onNegativeButtonClickListener = onClickListener;
            return this;
        }
        
//        public Builder setExtraButton(String text, OnDialogClick onClickListener) {
//            this.extraButton = text;
//            this.onExtraButtonClickListener = onClickListener;
//            return this;
//        }
        
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }
        
        public Builder setCloseVisible(boolean closeVisible) {
            this.closeVisible = closeVisible;
            return this;
        }
        
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }
        
        public NewCommonDialog create(Activity activity) {
            this.activity = activity;
            return new NewCommonDialog(this);
        }
    }
}

