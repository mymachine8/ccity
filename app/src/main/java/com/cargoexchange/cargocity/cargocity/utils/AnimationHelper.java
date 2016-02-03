package com.cargoexchange.cargocity.cargocity.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.fragments.OrdersListFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private OrdersListFragment context;
    private Context Activitycontext;
    private SupportMapFragment mSmallMapFragment;
    private List<List<HashMap<String, String>>> routes;
    final MarkerOptions markerA = new MarkerOptions();
    final MarkerOptions markerB = new MarkerOptions();
    ArrayList<LatLng> points;
    LatLng start;
    PolylineOptions lineOptions;
    public AnimationHelper(View view,int cardStatus,OrdersListFragment context,Context Activitycontext)
    {
        this.view=view;
        this.cardStatus=cardStatus;
        this.context=context;
        this.Activitycontext=Activitycontext;
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
                    return;

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
