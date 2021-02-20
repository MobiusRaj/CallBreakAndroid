package com.mobius.callbreakandroid.utility_base;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobius.callbreakandroid.R;

import java.util.Timer;

public class GlobalLoader_new {
    private static final String TAG = "GlobalLoader_new";
    public Activity activity;
    Dialog dialog;
    TextView tv;
    Timer t;
    AnimationDrawable frameAnimation;
    C c = C.getInstance();
    String PreloaderText;
    ImageView image;
    ImageView rLayout;
    //    private GifImageView rLayout;
    int w, h;

    public GlobalLoader_new(Activity activity1) {

        try {
            Log.e("STEP_9"," setUp loader ");
            activity = activity1;
            System.gc();
            dialog = new Dialog(activity1, R.style.Theme_Transparent);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progress_layoutnew);

            tv = dialog.findViewById(R.id.progress_text);
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(c.typeface);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, c.width * 40 / 1280);

            LinearLayout.LayoutParams frm;
            frm = (LinearLayout.LayoutParams) dialog.findViewById(R.id.progress_text).getLayoutParams();
            frm.topMargin = c.height * 10 / 720;

            rLayout = dialog.findViewById(R.id.progressContainer);
            rLayout.setBackgroundResource(R.drawable.preloader_animation);

            w = c.width * 276 / 1280;
            frm = (LinearLayout.LayoutParams) dialog.findViewById(R.id.progressContainer).getLayoutParams();
            frm.width = w;
            frm.height = w * 69 / 276;

            frameAnimation = (AnimationDrawable) rLayout.getBackground();

            dialog.setCancelable(false);
            Log.e("STEP_10"," setUp loader ");
        } catch (OutOfMemoryError e) {
            Log.e("STEP_11"," OutOfMemoryError e -> "+e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("STEP_12"," Exception e -> "+e);
            e.printStackTrace();
        }

    }

    public void ShowMe(String PreloaderText1) {
        /*StackTraceElement e[] = Thread.currentThread().getStackTrace();
        for (int i = 0; i < e.length; i++) {
            Logger.print("wwwwww Disconnect:::::::::::::::::::::::::::: " + i + " : " + e[i].toString());
        }*/
        Log.e("STEP_1"," Show me ");
        try {
            PreloaderText = PreloaderText1;
            //Logger.print("GLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO 1 => " + PreloaderText1 + " " + c.height + " " + c.width);
            Log.e("STEP_2"," is dialog != null -> "+(dialog != null));
            if (dialog != null) {
                Log.e("STEP_3"," is dialog showing -> "+(!dialog.isShowing()));
                if (!dialog.isShowing()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("STEP_3"," in runOnUiThread ");
                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            }
                            dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
                            dialog.show();
                            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        }
                    });
                }
                Log.e("STEP_4"," framAnim is null -> "+(frameAnimation == null));
                if (frameAnimation == null) {
                    frameAnimation = (AnimationDrawable) rLayout.getBackground();
                } else {
                    Log.e("STEP_5"," else is running -> "+(frameAnimation.isRunning()));
                    if (frameAnimation.isRunning()) {
                        frameAnimation.stop();
                    }
                    Log.e("STEP_6"," ani start ");
                    frameAnimation.start();
                }

                Log.e("STEP_7"," loader msg -> "+PreloaderText);
                tv.setText(PreloaderText);
                tv.invalidate();
            }
        } catch (Exception e1) {
            Log.e("STEP_8"," in Exception msg -> "+e1);
            e1.printStackTrace();
        }
    }


    public void FinishMe(int no) {

        Logger.print(TAG, "Finish Me :: " + no);
        try {
            if (t != null) {
                t.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    try {
                        if (frameAnimation != null && frameAnimation.isRunning()) {
                            frameAnimation.stop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        System.gc();
    }

    public void Destroy() {

        try {
            try {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            } catch (Exception e) {

            }

            if (t != null) {
                t.cancel();
            }
            t = null;
            tv = null;
            try {
                if (image.getAnimation() != null) {
                    image.clearAnimation();
                }
                image.setBackgroundResource(0);
            } catch (Exception e) {

            }
            image = null;
            frameAnimation = null;

            dialog = null;
            PreloaderText = null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.gc();

    }
}
