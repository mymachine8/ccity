package com.cargoexchange.cargocity.cargocity.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.cargoexchange.cargocity.cargocity.R;

/**
 * Created by root on 30/1/16.
 */
public class AnimationHelper
{
    private final int CARD_EXPANDED=1;
    private final int CARD_COMPACT=0;
    private int cardStatus=0;
    private CardView parentCard;
    private CardView childCard;
    private int parentInitialHeight;
    private int childInitialHeight;
    private int EXECUTION_COUNT=0;
    private View view;
    public AnimationHelper(View view,int cardStatus)
    {
        this.view=view;
        this.cardStatus=cardStatus;
        parentCard = (CardView)view.findViewById(R.id.orderdetailscardview);
        childCard = (CardView)view.findViewById(R.id.extraorderdetailscardview);
        if(EXECUTION_COUNT==0) {
            parentInitialHeight = parentCard.getHeight();
            childInitialHeight = (parentInitialHeight*2)+20;
            EXECUTION_COUNT++;
        }
        childCard.setVisibility(View.GONE);
        if(cardStatus==CARD_COMPACT)
            unFoldWithAnimation(view);
        else if(cardStatus==CARD_EXPANDED)
            foldWithAnimation(view);
    }
    public void foldWithAnimation(final View view)
    {
        if(cardStatus!=CARD_COMPACT)
        {
            childCard.setVisibility(View.VISIBLE);
            parentCard.setVisibility(View.VISIBLE);
            parentCard.getLayoutParams().height=childInitialHeight;
            parentCard.requestLayout();
            parentCard.setAlpha(0);
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
            {
                childCard.setElevation(0);
                childCard.setCardElevation(0);
                parentCard.setElevation(0);
                parentCard.setCardElevation(0);
            }

            final ValueAnimator animator = ValueAnimator.ofInt(180000, 0);
            animator.setDuration(200);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    final float animatedFraction=animation.getAnimatedFraction();
                    view.setRotationY(animatedFraction * 180);
                    childCard.setAlpha(1 - animation.getAnimatedFraction());
                    if (animation.getAnimatedFraction() > 0.6)
                    {
                        childCard.setAlpha(0);
                    }
                }
            });
            animator.start();
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation)
                {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    cardStatus = CARD_COMPACT;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        childCard.setElevation(8);
                        childCard.setCardElevation(8);
                        parentCard.setElevation(8);
                        parentCard.setCardElevation(8);
                    }
                    view.setRotationY(0);
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(parentCard, "alpha", 0, 1);
                    alphaAnimator.setDuration(1200);
                    alphaAnimator.setInterpolator(new LinearInterpolator());
                    alphaAnimator.start();
                    parentCard.requestLayout();
                    parentCard.setVisibility(View.VISIBLE);
                    childCard.setVisibility(View.GONE);
                    animator.removeAllUpdateListeners();
                    parentCard.requestLayout();
                    childCard.requestLayout();
                    reduceHeight();

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }
    public void reduceHeight()
    {
        ValueAnimator animator = ValueAnimator.ofInt(180000, 0);
        animator.setDuration(500);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                parentCard.getLayoutParams().height = (int) (childInitialHeight - (childInitialHeight - parentInitialHeight) * animation.getAnimatedFraction());
                parentCard.requestLayout();
            }
        });
        animator.start();

    }
    public void unFoldWithAnimation(final View view)
    {
        if(cardStatus!=CARD_EXPANDED)
        {
            //parentInitialHeight = parentCard.getHeight();
            //childInitialHeight = 2 * parentInitialHeight;

            childCard.setVisibility(View.VISIBLE);
            childCard.setAlpha(0);
            childCard.requestLayout();
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            {
                parentCard.setElevation(0);
                parentCard.setCardElevation(0);
                childCard.setElevation(0);
                childCard.setCardElevation(0);
            }

            final ValueAnimator animator = ValueAnimator.ofInt(0,-180);
            animator.setDuration(200);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float animationFraction = animation.getAnimatedFraction();
                    view.setRotationY(animationFraction * -180);
                    parentCard.setAlpha(1 - animationFraction);
                    view.invalidate();

                }
            });
            animator.start();
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    cardStatus = CARD_EXPANDED;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        parentCard.setElevation(8);
                        childCard.setElevation(8);
                        parentCard.setCardElevation(8);
                        childCard.setCardElevation(8);
                    }
                    view.setRotationY(0);
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(childCard, "alpha", 0, 1);
                    alphaAnimator.setDuration(200);
                    alphaAnimator.setInterpolator(new LinearInterpolator());
                    alphaAnimator.start();
                    parentCard.setVisibility(View.GONE);
                    animator.removeAllUpdateListeners();
                    parentCard.requestLayout();
                    childCard.requestLayout();

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

}
