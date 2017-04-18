package com.jerey.animationlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by Xiamin on 2017/4/18.
 */

public class AnimationHelper {

    public static final int MINI_RADIUS = 0;
    public static final int DEFAULT_DURIATION = 500;

    /**
     * 屏蔽Android提示错误, 5.0以下不做动画处理
     *
     * @param view
     * @param startRadius
     * @param durationMills
     */
    @SuppressLint("NewApi")
    public static void show(View view, float startRadius, long durationMills) {
        // Android L 以下不做处理,直接显示
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            view.setVisibility(View.VISIBLE);
            return;
        }

        int xCenter = (view.getLeft() + view.getRight()) / 2;
        int yCenter = (view.getTop() + view.getBottom()) / 2;
        int w = view.getWidth();
        int h = view.getHeight();
        //计算最大半径, 边界效应+1
        int endRadius = (int) (Math.sqrt(w * w + h * h) + 1);
        Animator animation = ViewAnimationUtils.createCircularReveal(view,
                xCenter, yCenter, startRadius, endRadius);
        view.setVisibility(View.VISIBLE);
        animation.setDuration(durationMills);
        animation.start();
    }

    @SuppressLint("NewApi")
    public static void hide(final View view, float endRadius, long durationMills, final int visible) {
        // Android L 以下不做处理,直接显示
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            view.setVisibility(View.INVISIBLE);
            return;
        }

        int xCenter = (view.getLeft() + view.getRight()) / 2;
        int yCenter = (view.getTop() + view.getBottom()) / 2;
        int w = view.getWidth();
        int h = view.getHeight();
        //计算最大半径, 边界效应+1
        int startRadius = (int) (Math.sqrt(w * w + h * h) + 1);
        Animator animation = ViewAnimationUtils.createCircularReveal(view,
                xCenter, yCenter, startRadius, endRadius);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(visible);
            }
        });
        animation.setDuration(durationMills);
        animation.start();
    }

    public static void show(View myView) {
        show(myView, MINI_RADIUS, DEFAULT_DURIATION);
    }

    /**
     * 默认View隐藏状态为 INVISIBLE
     * @param myView
     */
    public static void hide(View myView) {
        hide(myView, MINI_RADIUS, DEFAULT_DURIATION, View.INVISIBLE);
    }

    /*
     * @param myView 要隐藏的view
     * @param endVisible  动画执行结束是view的状态, 是View.INVISIBLE 还是GONE
     */
    public static void hide(View myView, int endVisible) {
        hide(myView, MINI_RADIUS, DEFAULT_DURIATION, endVisible);
    }
}
