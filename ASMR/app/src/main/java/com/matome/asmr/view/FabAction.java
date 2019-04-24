package com.matome.asmr.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

public class FabAction extends FloatingActionButton.Behavior {
    boolean mIsAnimating = false;
    boolean isVisible = false;


    public FabAction(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if(!mIsAnimating) {
            if (dyConsumed > 0) {
                isVisible = false;
                animateHide(child);
            } else {
                isVisible = true;
                child.setVisibility(View.VISIBLE);
                animateShow(child);
            }
        }
    }

    private void animateShow(FloatingActionButton child) {
        ViewCompat.animate(child).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                .setInterpolator(new FastOutSlowInInterpolator());
    }

    private void animateHide(FloatingActionButton child) {
        ViewCompat.animate(child).scaleX(0.0F).scaleY(0.0F).alpha(0.0F)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        mIsAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        mIsAnimating = false;
                        if(!isVisible){
                            view.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        mIsAnimating = false;
                    }
                });
    }
}
