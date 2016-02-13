package com.example.root.foldablelayout;

/**
 * Created by root on 11/2/16.
 */

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DimenRes;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Layout which allow a foldable animation between two other layouts. The only limitation of this
 * layout is than the big view should be exactly twice bigger in height than the small view {@link #setupViews(int, int, int, Context)}.
 */
public class FoldableLayout extends RelativeLayout
{

    private static final int ANIMATION_DURATION = 500;

    protected RelativeLayout mContentLayout;
    protected ImageView mImageViewBelow;
    protected ImageView mImageViewAbove;
    protected ViewGroup mViewGroupCover;
    protected ViewGroup mViewGroupDetail;
    protected ViewGroup mRootView;
    protected Bitmap mDetailBitmap;
    protected Bitmap mDetailTopBitmap;
    protected Bitmap mDetailBottomBitmap;
    protected int mCoverHeight;
    protected int mDetailHeight;
    private boolean mIsFolded = true;
    private boolean mIsAnimating = false;
    private Matrix mMatrix;
    private FoldListener mFoldListener = new FoldListener() {
        @Override
        public void onUnFoldStart() {

        }

        @Override
        public void onUnFoldEnd() {

        }

        @Override
        public void onFoldStart() {

        }

        @Override
        public void onFoldEnd() {

        }
    };

    private final TimeInterpolator mTimeInterpolator = new AccelerateDecelerateInterpolator();

    /**
     * Basic constructor.
     *
     * @param context is a valid context.
     */
    public FoldableLayout(Context context) {
        super(context);
        setupView(context);
    }

    /**
     * Basic constructor.
     *
     * @param context is a valid context.
     * @param attrs   used to build this view.
     */
    public FoldableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(context);
    }

    /**
     * Basic constructor.
     *
     * @param context      is a valid context.
     * @param attrs        used to build this view.
     * @param defStyleAttr used to build this view.
     */
    public FoldableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(context);
    }

    /**
     * Basic constructor.
     *
     * @param context      is a valid context.
     * @param attrs        used to build this view.
     * @param defStyleAttr used to build this view.
     * @param defStyleRes  used to build this view.
     */
    public FoldableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupView(context);
    }

    /**
     * Add a listener to folding animations.
     *
     * @param foldListener is the listener to add.
     */
    public void setFoldListener(FoldListener foldListener) {
        mFoldListener = foldListener;
    }

    public View getCoverView() {
        return mViewGroupCover;
    }

    public View getDetailView() {
        return mViewGroupDetail;
    }

    private void setupView(Context context) {
        setClipChildren(false);
        mRootView = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.foldable_layout, this, true);  //The layout file containing main,lower and upper
        mContentLayout = (RelativeLayout) findViewById(R.id.foldable_content_view);  //the base view
        mImageViewBelow = (ImageView) findViewById(R.id.foldable_layout_below_bitmap);  //lower part view
        mImageViewAbove = (ImageView) findViewById(R.id.foldable_layout_above_bitmap);  //upper part view
        mMatrix = new Matrix();         //idealy used for applying transformations
        mMatrix.postScale(1, -1);         //Scale transformation
    }
    public boolean isFolded() {
        return mIsFolded;
    }     //checks if the cards are folded or not

    public boolean isAnimating(){return  mIsAnimating;}

    /**
     * Init the two views which will compose the foldable sides. To ensure nice animations,
     * the big view height should be exactly twice bigger than the small view height.
     *
     * @param coverLayoutId  is the "small" view which is used as the cover.
     * @param detailLayoutId is the "big" view which is used as the detail.
     * @param coverHeight is the height of the cover view.
     * @param context        is a valid context.
     */
    public void setupViews(@LayoutRes int coverLayoutId, @LayoutRes int detailLayoutId, int coverHeight, Context context) {
        mViewGroupCover = (ViewGroup) LayoutInflater.from(context).inflate(coverLayoutId, mContentLayout, false);  //Smaller layout or the parent layout which acts as cover
        mViewGroupDetail = (ViewGroup) LayoutInflater.from(context).inflate(detailLayoutId, mContentLayout, false); //Larger layout or the child layout which acts as the detaied
        ViewTreeObserver viewTreeObserver = mViewGroupCover.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mViewGroupCover.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mCoverHeight = (mViewGroupCover.getHeight());
                }
            });
        }
        viewTreeObserver = mViewGroupDetail.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mViewGroupDetail.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mDetailHeight = mViewGroupDetail.getHeight();
                }
            });
        }
        mContentLayout.addView(mViewGroupCover);   //adding the child to the base layout
        mContentLayout.addView(mViewGroupDetail);   //adding the parent to the base layout
        mViewGroupDetail.setVisibility(GONE);       //initially the parent view is set as gone
        //mCoverHeight = context.getResources().getDimensionPixelSize(R.dimen.card_cover_height);   //height of the cover or parent view

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {      //Handle the touch event..functionality yet unknown
        if (mIsAnimating) {
            return false;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void clearImageView(ImageView imageView) {    //clears the image view
        setImageBackground(imageView, null);
        imageView.setImageDrawable(null);
    }

    private void setImageBackground(ImageView imageView, Drawable drawable) {     //sets the drawable to the image view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(drawable);
        } else {
            imageView.setBackgroundDrawable(drawable);
        }
    }

    /**
     * Fold the layout without animations.
     */
    public void foldWithoutAnimation() {                           //Folding without animation
        if (!mIsFolded && !mIsAnimating) {                          //Checks if the view is already folded or is folding
            mIsFolded = true;                                       //sets the folded count as true
            this.clearImageView(mImageViewAbove);                        //Clears the image from the upper part view
            this.clearImageView(mImageViewBelow);                        //Clears the image from the lower part view
            this.mViewGroupCover.setVisibility(VISIBLE);                 //When folded cover should be visible hence setting its visibility to visible
            this.mViewGroupDetail.setVisibility(GONE);                   //When folded detail should not be visible hence setting its visibility to gone
            if (FoldableLayout.this.getLayoutParams() != null) {
                FoldableLayout.this.getLayoutParams().height = mCoverHeight+35;        //Setting the height of the cover view to default height
            }
            this.requestLayout();                                        //Rendering the layout
        }
    }

    /**
     * Unfold the layout without animations.
     */
    public void unfoldWithoutAnimation() {                  //Unfolding without animation
        if (mIsFolded && !mIsAnimating) {                   //Checks if the view is already unfolded or is unfolding
            mViewGroupCover.setVisibility(GONE);
            mViewGroupDetail.setVisibility(VISIBLE);
            clearImageView(mImageViewAbove);                //Clears the image from the upper part view
            clearImageView(mImageViewBelow);                //Clears the image from the lower part view
            if (FoldableLayout.this.getLayoutParams() != null) {
                FoldableLayout.this.getLayoutParams().height = (mCoverHeight*2);        //Sets the height of the unfolded layout as double the folded layout
            }
            requestLayout();                         //Rendering the layout
            mIsFolded = false;                      //Setting folded flag as false
        }
    }

    /**
     * Fold the layout with animations.
     */
    public void foldWithAnimation() {                   //Folding with animation
        if (!mIsAnimating) {
            computeBitmaps();                               //generates the part to show in top and in bottom
            final Bitmap rotatedAboveBitmap;
            rotatedAboveBitmap = Bitmap.createBitmap(mDetailBottomBitmap, 0, 0, mDetailBottomBitmap.getWidth(), mDetailBottomBitmap.getHeight(), mMatrix, true); //this is unclear
            final Drawable belowShadow;     //below shadow
            final Drawable aboveShadow;        //above shadow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                belowShadow = getResources().getDrawable(R.drawable.shadow, null);
                aboveShadow = getResources().getDrawable(R.drawable.shadow, null);
            } else {
                belowShadow = getResources().getDrawable(R.drawable.shadow);
                aboveShadow = getResources().getDrawable(R.drawable.shadow);
            }

            ValueAnimator animator;
            animator = ValueAnimator.ofFloat(-180, 0);

            final int initialHeight = this.getHeight();   //gets the height ..but still unclear as to whose height..reference of this is unclear

            animator.setInterpolator(mTimeInterpolator);
            animator.setDuration(ANIMATION_DURATION);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private boolean mReplaceDone = false;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getAnimatedFraction() >= 0.5 && !mReplaceDone) {    //replaces the image after the animation has completed 50%
                        mReplaceDone = true;
                        clearImageView(mImageViewAbove);
                        mViewGroupCover.setVisibility(VISIBLE);   //sets the visibility of parent to visible
                    }
                    mContentLayout.setRotationX((Float) animation.getAnimatedValue());       //Rotate the base layout
                    belowShadow.setAlpha((int) (255 * animation.getAnimatedFraction()));
                    aboveShadow.setAlpha((int) (255 * animation.getAnimatedFraction()));
                    FoldableLayout.this.getLayoutParams().height = (int) (initialHeight - (initialHeight+90) / 2 * animation.getAnimatedFraction());
                    FoldableLayout.this.requestLayout();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mFoldListener.onFoldStart();
                    mIsAnimating = true;
                    mContentLayout.setRotationX(180);    //rotate the view so that it does not shows inverted while rotating
                    mViewGroupDetail.setVisibility(GONE);   //Detail view should be set as gone
                    mImageViewAbove.setImageDrawable(belowShadow);
                    setImageBackground(mImageViewAbove, new BitmapDrawable(getResources(), rotatedAboveBitmap));  //
                    setImageBackground(mImageViewBelow, new BitmapDrawable(getResources(), mDetailTopBitmap));
                    mImageViewBelow.setImageDrawable(aboveShadow);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    clearImageView(mImageViewBelow);
                    mIsFolded = true;
                    mIsAnimating = false;
                    mFoldListener.onFoldEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsAnimating = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }
    }

    /**
     * Unfold the view with animations.
     */
    public void unfoldWithAnimation() {
        if (!mIsAnimating) {
            computeBitmaps();
            final Bitmap rotatedAboveBitmap;
            rotatedAboveBitmap = Bitmap.createBitmap(mDetailBottomBitmap, 0, 0, mDetailBottomBitmap.getWidth(), mDetailBottomBitmap.getHeight(), mMatrix, true);
            ValueAnimator animator;
            animator = ValueAnimator.ofFloat(0, -180);

            mContentLayout.setPivotY(mCoverHeight+35);
            mContentLayout.setPivotX(mViewGroupCover.getWidth() / 2);

            final int initialHeight = mCoverHeight+20;   //Adding margin compensation...10dp is margin used in between cards Hence 2*10=20dp

            final Drawable belowShadow;
            final Drawable aboveShadow;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                belowShadow = getResources().getDrawable(R.drawable.shadow, null);
                aboveShadow = getResources().getDrawable(R.drawable.shadow, null);
            } else {
                belowShadow = getResources().getDrawable(R.drawable.shadow);
                aboveShadow = getResources().getDrawable(R.drawable.shadow);
            }

            animator.setInterpolator(mTimeInterpolator);
            animator.setDuration(ANIMATION_DURATION);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private boolean mReplaceDone = false;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getAnimatedFraction() >= 0.5 && !mReplaceDone) {
                        mReplaceDone = true;
                        mViewGroupCover.setVisibility(GONE);
                        setImageBackground(mImageViewAbove, new BitmapDrawable(getResources(), rotatedAboveBitmap));
                        mImageViewAbove.setImageDrawable(aboveShadow);
                    }
                    mContentLayout.setRotationX((Float) animation.getAnimatedValue());

                    belowShadow.setAlpha((int) (255 * (1 - animation.getAnimatedFraction())));
                    aboveShadow.setAlpha((int) (255 * (1 - animation.getAnimatedFraction())));

                    FoldableLayout.this.getLayoutParams().height = (int) (initialHeight + (initialHeight+90) * animation.getAnimatedFraction());  //Add 90(35+35+20) because the detail layout is actually bigger
                    FoldableLayout.this.requestLayout();

                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mFoldListener.onUnFoldStart();
                    mContentLayout.setRotationX(180);
                    mIsAnimating = true;
                    setImageBackground(mImageViewBelow, new BitmapDrawable(getResources(), mDetailTopBitmap));
                    mImageViewBelow.setImageDrawable(belowShadow);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mViewGroupDetail.setVisibility(VISIBLE);
                    clearImageView(mImageViewBelow);
                    clearImageView(mImageViewAbove);
                    mContentLayout.setRotationX(0);
                    mIsFolded = false;
                    mIsAnimating = false;
                    mFoldListener.onUnFoldEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsAnimating = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }
    }

    private void computeBitmaps() {
        mDetailBitmap = computeBitmap(mViewGroupDetail);
        mDetailTopBitmap = Bitmap.createBitmap(mDetailBitmap, 0, 0, mDetailBitmap.getWidth(), (mDetailBitmap.getHeight() / 2)-35);     //sets the top bitmap as half of full bitmap
        mDetailBottomBitmap = Bitmap.createBitmap(mDetailBitmap, 0, (mDetailBitmap.getHeight() / 2)-35, mDetailBitmap.getWidth(), (mDetailBitmap.getHeight() / 2)+35);  //sets the bottom bitmap as lower half of the full bitmap
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private Bitmap computeBitmap(ViewGroup viewGroup) {
        Bitmap bitmap;             //Blank bitmap
        Rect rect = new Rect();     //This area under this rect will be visible while rotating
        viewGroup.getWindowVisibleDisplayFrame(rect);   //setting rects as displayable area
        viewGroup.destroyDrawingCache();                //not clear
        viewGroup.setDrawingCacheEnabled(true);             //not clear
        viewGroup.buildDrawingCache(true);              //not clear
        bitmap = viewGroup.getDrawingCache(true);           //not clear
        /**
         * After rotation, the DecorView has no height and no width. Therefore
         * .getDrawingCache() returns null. That's why we  have to force measure and layout.
         */
        int newheight;
        if(mCoverHeight!=0)
        {
            newheight=(mCoverHeight*2)+70;    //35+35...details layout is 70dp larger
        }
         else
        {
            mCoverHeight=mViewGroupCover.getHeight();
            newheight=(mCoverHeight*2)+35+35;  //15
        }
        if (bitmap == null)
        {
            viewGroup.measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(newheight, MeasureSpec.EXACTLY));
            viewGroup.layout(0, 0, viewGroup.getMeasuredWidth(),viewGroup.getMeasuredHeight());  //creating the layout according to the measured height and width
            viewGroup.destroyDrawingCache();
            viewGroup.setDrawingCacheEnabled(true);
            viewGroup.buildDrawingCache(true);
            bitmap = viewGroup.getDrawingCache(true);
        }
        if (bitmap == null) {
            return Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        } else {
            return bitmap.copy(Bitmap.Config.ARGB_8888, false);
            //return Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        }
    }

    /**
     * Interface to dispatch folding events.
     */
    public interface FoldListener {

        /**
         * Dispatch when un foldWithAnimation start.
         */
        void onUnFoldStart();

        /**
         * Dispatch when un foldWithAnimation end.
         */
        void onUnFoldEnd();

        /**
         * Dispatch when foldWithAnimation start.
         */
        void onFoldStart();

        /**
         * Dispatch when foldWithAnimation end.
         */
        void onFoldEnd();
    }
}
