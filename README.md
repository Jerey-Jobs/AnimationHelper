
## AnimationHelper

这是一个动画帮助类, 提供简单的帮助实现以下效果的工具类.我将其上传了Jcenter, 大家可以引用<br>
工程见: [https://github.com/Jerey-Jobs/AnimationHelper](https://github.com/Jerey-Jobs/AnimationHelper)

先看一张效果图:

![](/img/post1/animationhelper2.gif)

## import/引入

project's build.gradle (工程下的 build.gradle)

``` gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
}
```

module's build.gradle (模块的build.gradle)

``` gradle
	dependencies {
	         compile 'com.github.Jerey-Jobs:AnimationHelper:1.0'
	}
```


### 简介

Android5.0系统源码中增加了新的API - `ViewAnimationUtils` 是其作用就是可以使控件能够呈现水波一样展开。

源码介绍也很简单, 我们ctrl+左击该类可看到

``` java

public final class ViewAnimationUtils {
    private ViewAnimationUtils() {}
    /**
     * </code></pre>
     *
     * @param view The View will be clipped to the animating circle.
     * @param centerX The x coordinate of the center of the animating circle, relative to
     *                <code>view</code>.
     * @param centerY The y coordinate of the center of the animating circle, relative to
     *                <code>view</code>.
     * @param startRadius The starting radius of the animating circle.
     * @param endRadius The ending radius of the animating circle.
     */
    public static Animator createCircularReveal(View view,
            int centerX,  int centerY, float startRadius, float endRadius) {
        return new RevealAnimator(view, centerX, centerY, startRadius, endRadius);
    }
}
```

这个类就只有一个方法, `createCircularReveal`  

参数1 view： 要实现波纹效果的view; <br>
参数2 centerX： 动画的中心点的x坐标; <br>
参数3 centerY：动画的中心点的y坐标; <br>
参数4 startRadius: 动画开始的波纹半径; <br>
参数5 endRadius：动画结束时的波纹半径;

### 封装

我们开始封装一个Helper类, 先实现View的隐藏和显示动画

``` java
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

```

之后我们使用就很简单了, 想要显示一个View的展示动画, 则
``` java
  AnimationHelper.show(mImageView);
```

隐藏,则

``` java
  AnimationHelper.hide(mImageView);
```

我们看一下,目前的使用的效果:

### 效果

![](/img/post1/animationhelper1.gif)

### 拓展 - 过渡动画

我们可以使用该动画,创造一个页面过渡动画, 如何弄呢?

可以通过给我们的`DecordView`添加一个View, 然后`overridePendingTransition` 即可.

以下是代码:

``` java
@SuppressLint("NewApi")
public static void startActivityForResult(
        final Activity thisActivity, final Intent intent, final Integer requestCode,
        final Bundle bundle, final View view,
        int colorOrImageRes, final long durationMills) {
    // SDK 低于LOLLIPOP不做处理,直接跳转
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        if (requestCode == null) {
            thisActivity.startActivity(intent);
        } else if (bundle == null) {
            thisActivity.startActivityForResult(intent, requestCode);
        } else {
            thisActivity.startActivityForResult(intent, requestCode, bundle);
        }
        return;
    }
    int[] location = new int[2];
    view.getLocationInWindow(location);
    final int xCenter = location[0] + view.getWidth() / 2;
    final int yCenter = location[1] + view.getHeight() / 2;
    final ImageView imageView = new ImageView(thisActivity);
    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    imageView.setImageResource(colorOrImageRes);

    final ViewGroup decorView = (ViewGroup) thisActivity.getWindow().getDecorView();
    int w = decorView.getWidth();
    int h = decorView.getHeight();
    decorView.addView(imageView, w, h);

    // 计算中心点至view边界的最大距离
    int maxW = Math.max(xCenter, w - xCenter);
    int maxH = Math.max(yCenter, h - yCenter);
    final int finalRadius = (int) Math.sqrt(maxW * maxW + maxH * maxH) + 1;

    Animator anim = ViewAnimationUtils.createCircularReveal(imageView, xCenter, yCenter, 0, finalRadius);
    int maxRadius = (int) Math.sqrt(w * w + h * h) + 1;
    long finalDuration = durationMills;
    /**
     * 计算时间
     */
    if (finalDuration == DEFAULT_DURIATION) {
        // 算出实际边距与最大边距的比率
        double rate = 1d * finalRadius / maxRadius;
        // 水波扩散的距离与扩散时间成正比
        finalDuration = (long) (DEFAULT_DURIATION * rate);
    }
    anim.setDuration(finalDuration);
    anim.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if (requestCode == null) {
                thisActivity.startActivity(intent);
            } else if (bundle == null) {
                thisActivity.startActivityForResult(intent, requestCode);
            } else {
                thisActivity.startActivityForResult(intent, requestCode, bundle);
            }

            // 默认渐隐过渡动画.
            thisActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            // 默认显示返回至当前Activity的动画.
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animator anim =
                            ViewAnimationUtils.createCircularReveal(imageView, xCenter, yCenter, finalRadius, 0);
                    anim.setDuration(durationMills);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            try {
                                decorView.removeView(imageView);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    anim.start();
                }
            }, 1000);
        }
    });
    anim.start();
}

```

### 使用

``` java
AnimationHelper.startActivity(MainActivity.this,
        new Intent(MainActivity.this, LoginActivity.class),
        mStartAvtivityBtn,
        R.color.colorPrimary
);
```

### 效果

![](/img/post1/animationhelper2.gif)




----------
本文作者：Anderson/Jerey_Jobs

博客地址   ： [http://jerey.cn/](http://jerey.cn/)<br>
简书地址   :  [Anderson大码渣](http://www.jianshu.com/users/016a5ba708a0/latest_articles)<br>
github地址 :  [https://github.com/Jerey-Jobs](https://github.com/Jerey-Jobs)
