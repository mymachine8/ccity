package com.cargoexchange.cargocity.cargocity.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.MapActivity;
import com.cargoexchange.cargocity.cargocity.OrdersActivity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.CargoSharedPreferences;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.constants.OrderStatus;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.models.Feedback;
import com.cargoexchange.cargocity.cargocity.utils.GenerateRequest;
import com.cargoexchange.cargocity.cargocity.utils.ImageHelper;
import com.cargoexchange.cargocity.cargocity.utils.ParseJSON;
import com.google.gson.Gson;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedbackFragment extends Fragment
{
    //TODO:Implement the image window logic here
    private static final int SCAN_REQUEST_CODE = 99;
    private ImageView mCameraImage;
    private Activity mActivityContext;

    private RatingBar mRatingBar;
    private TextView mTextView;
    private TextView mItemRecievedLabel,mCommentLabel,mDeliveryProofLabel,mRatingLabel,mRatingValueLabel,mFeedBackLabel;
    private EditText mAdditionalCommentsEditText,mCommentEditText;
    private Button mSubmitButton;
    private ImageView mProofUploadImageView1,mProofUploadImageView2,mProofUploadImageView3,mCaptureImageImageView,mPopUpImageView;
    private Switch isItemGoodConditionSwitch;
    private Dialog mImagePopUpDialog;
    private Bitmap UploadImage1,UploadImage2,UploadImage3;
    private Resources mResource;
    private ProgressDialog mProgressDialog;
    private String base64Images[] = new String[3];

    private String mOrderNo=new String();
    private int count=0;

    public FeedbackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivityContext = getActivity();
        mActivityContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        bindViewVariables(view);
        mResource=this.getResources();

        InitializeDialog();

        mTextView.setText("Excellent");
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v < 1)
                    mRatingBar.setRating(1);
                if (v <= 1)
                    mTextView.setText("Poor");
                else if (v > 1 && v <= 3)
                    mTextView.setText("Good");
                else if (v > 3 && v <= 4)
                    mTextView.setText("Very Good");
                else
                    mTextView.setText("Excellent");
            }
        });

        try {
            mCameraImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int preference = ScanConstants.OPEN_CAMERA;
                    Intent intent = new Intent(getActivity(), ScanActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                    startActivityForResult(intent, SCAN_REQUEST_CODE);
                }
            });
        } catch(Exception ex) {
            Log.e("DELIVERY_FEEDBACK", ex.getMessage());
        }


        mProofUploadImageView1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mImagePopUpDialog=new Dialog(mActivityContext);
                mImagePopUpDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                LayoutInflater mInflater=mActivityContext.getLayoutInflater();
                View layout=mInflater.inflate(R.layout.activity_show_image, (ViewGroup) v.findViewById(R.id.ShowImageRoot));
                mImagePopUpDialog.setContentView(layout);
                mPopUpImageView=(ImageView)layout.findViewById(R.id.ProofImageView);
                Button mDiscardImageButton=(Button)layout.findViewById(R.id.CancelImagePopUpButton);
                mPopUpImageView.setImageBitmap(UploadImage1);
                mDiscardImageButton.setClickable(true);
                mImagePopUpDialog.show();
                mDiscardImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProofUploadImageView1.setImageBitmap(UploadImage2);
                        mProofUploadImageView2.setImageBitmap(UploadImage3);
                        UploadImage1 = UploadImage2;
                        UploadImage2 = UploadImage3;
                        UploadImage3 = BitmapFactory.decodeResource(mResource, R.drawable.ic_imagedefault2);
                        mProofUploadImageView3.setImageResource(R.drawable.ic_imagedefault2);
                        count = (count - 1) % 3;
                        mImagePopUpDialog.cancel();
                    }
                });

            }
        });
        mProofUploadImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImagePopUpDialog = new Dialog(mActivityContext);
                mImagePopUpDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                LayoutInflater mInflater = mActivityContext.getLayoutInflater();
                View layout = mInflater.inflate(R.layout.activity_show_image, (ViewGroup) v.findViewById(R.id.ShowImageRoot));
                mImagePopUpDialog.setContentView(layout);
                mPopUpImageView = (ImageView) layout.findViewById(R.id.ProofImageView);
                Button mDiscardImageButton = (Button) layout.findViewById(R.id.CancelImagePopUpButton);
                mPopUpImageView.setImageBitmap(UploadImage2);
                mDiscardImageButton.setClickable(true);
                mImagePopUpDialog.show();
                mDiscardImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProofUploadImageView2.setImageBitmap(UploadImage3);
                        UploadImage2 = UploadImage3;
                        UploadImage3 = BitmapFactory.decodeResource(mResource, R.drawable.ic_imagedefault2);
                        ;
                        mProofUploadImageView3.setImageResource(R.drawable.ic_imagedefault2);
                        count = (count - 1) % 3;
                        mImagePopUpDialog.cancel();
                    }
                });
            }
        });
        mProofUploadImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImagePopUpDialog = new Dialog(mActivityContext);
                mImagePopUpDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                LayoutInflater mInflater = mActivityContext.getLayoutInflater();
                View layout = mInflater.inflate(R.layout.activity_show_image, (ViewGroup) v.findViewById(R.id.ShowImageRoot));
                mImagePopUpDialog.setContentView(layout);
                mPopUpImageView = (ImageView) layout.findViewById(R.id.ProofImageView);
                Button mDiscardImageButton = (Button) layout.findViewById(R.id.CancelImagePopUpButton);
                mPopUpImageView.setImageBitmap(UploadImage3);
                mDiscardImageButton.setClickable(true);
                mImagePopUpDialog.show();
                mDiscardImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UploadImage3 = BitmapFactory.decodeResource(mResource, R.drawable.ic_imagedefault2);
                        mProofUploadImageView3.setImageResource(R.drawable.ic_imagedefault2);
                        count = (count - 1) % 3;
                        mImagePopUpDialog.cancel();
                    }
                });
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                submitFeedback();

            }
        });
    }

    public void bindViewVariables(View view){
        mRatingBar=(RatingBar)view.findViewById(R.id.FeedbackRatingBar);
        mTextView=(TextView)view.findViewById(R.id.RatingValueLabel);
        mItemRecievedLabel=(TextView)view.findViewById(R.id.ItemRecievedLabel);
        mCommentLabel=(TextView)view.findViewById(R.id.CommentsLabel);
        mDeliveryProofLabel=(TextView)view.findViewById(R.id.DeliveryProofLabel);
        mFeedBackLabel=(TextView)view.findViewById(R.id.FeedBackLabel);
        mSubmitButton=(Button)view.findViewById(R.id.SubmitButton);
        mProofUploadImageView1=(ImageView)view.findViewById(R.id.ProofUploadImageView1);
        mProofUploadImageView2=(ImageView)view.findViewById(R.id.ProofUploadImageView2);
        mProofUploadImageView3=(ImageView)view.findViewById(R.id.ProofUploadImageView3);
        mCameraImage = (ImageView) view.findViewById(R.id.camera_image);
        isItemGoodConditionSwitch=(Switch)view.findViewById(R.id.ItemReceivedSwitch);
        mAdditionalCommentsEditText=(EditText)view.findViewById(R.id.FeedBackEditText);
        mCommentEditText=(EditText)view.findViewById(R.id.CommentsEditText);
        mRatingLabel=(TextView)view.findViewById(R.id.RatingLabel);

        UploadImage1= BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_imagedefault2);
        UploadImage2= BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_imagedefault2);
        UploadImage3= BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_imagedefault2);
    }

    private Feedback createFeedbackObject(){
        Feedback feedback = new Feedback();
        feedback.setInGoodCondition(isItemGoodConditionSwitch.isChecked());
        feedback.setRating(mRatingBar.getNumStars());
        feedback.setAdditionalComments(mAdditionalCommentsEditText.getText().toString());
        feedback.setFeedback(mCommentEditText.getText().toString());
        List<String> images = new ArrayList<String>();
        for(String image : base64Images) {
            if(!image.isEmpty()){
                images.add(image);
            }
        }
        feedback.setDocumentImageList(images);
        return feedback;
    }

    public void afterSubmitCallback(){
        RouteSession mRouteSession=RouteSession.getInstance();
        int pos=mRouteSession.getPosition();
        mOrderNo=mRouteSession.getmOrderList().get(pos).getOrderId();
        mRouteSession.getmOrderList().get(pos).setDeliveryStatus(OrderStatus.DELIVERED);

        if(pos<mRouteSession.getmOrderList().size()-1)
        {
            Intent MapInent=new Intent(mActivityContext,OrdersActivity.class);
            MapInent.putExtra("source","FeedbackFragment");
            MapInent.putExtra("fragment","OrdersListFragment");
            startActivity(MapInent);
        }
        else //For last Order go to Maps
        {
            Intent OrdersIntent=new Intent(mActivityContext, MapActivity.class);
            startActivity(OrdersIntent);
        }
    }

    private void InitializeDialog(){
        mProgressDialog = new ProgressDialog(mActivityContext,
                R.style.AppTheme);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Submitting Feedback...");
    }

    private void submitFeedback(){
        Feedback feedback = createFeedbackObject();
        mProgressDialog.show();
        String uri = Constants.CARGO_API_BASE_URL + "orderdetail/feedback";
        Gson gson = new Gson();
        try {
            JSONObject feedbackJson = new JSONObject(gson.toJson(feedback));
                JsonRequest request = CargoCity.getmInstance().postLoginRequest(
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String status;
                                mProgressDialog.hide();
                                try {
                                    status = response.getString("status");
                                    if (status.equalsIgnoreCase("success")) {
                                        afterSubmitCallback();

                                    } else {
                                        String error_message = response.getString("message");
                                        Toast toast = Toast.makeText(mActivityContext,
                                                error_message, Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mProgressDialog.hide();
                                String json = null;

                                NetworkResponse response = error.networkResponse;
                                if (response != null && response.data != null) {
                                    switch (response.statusCode) {
                                        case 401:
                                        case 400:
                                            json = new String(response.data);
                                            json = ParseJSON.trimMessage(json, "message");
                                            if (json != null) displayToastMessage(json);
                                            break;
                                    }
                                    //Additional cases
                                }
                            }
                        }, uri, feedbackJson);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        30000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                CargoCity.getmInstance().getRequestQueue().add(request);
        }catch (JSONException ex){
            Log.e("PARSE_ERROR",ex.getMessage().toString());
        }
    }

    private void displayToastMessage(String message) {
        Toast.makeText(mActivityContext, message, Toast.LENGTH_LONG).show();
        Log.e("LOGIN", message);
    }

    public void DismissPopUp(View v)
    {
        mImagePopUpDialog.cancel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(mActivityContext.getContentResolver(), uri);
                String base64Image = ImageHelper.convertBitmapToBase64(bitmap);
                mActivityContext.getContentResolver().delete(uri, null, null);
                base64Images[count] = base64Image;
                if (count == 0)
                {
                    UploadImage1=Bitmap.createScaledBitmap(bitmap,300,300,true);
                    mProofUploadImageView1.setImageBitmap(UploadImage1);
                    count = (count + 1) % 3;

                } else if (count == 1) {
                    UploadImage2 = Bitmap.createScaledBitmap(bitmap,300,300,true);
                    mProofUploadImageView2.setImageBitmap(UploadImage2);
                    count = (count + 1) % 3;
                } else {
                    UploadImage3 = Bitmap.createScaledBitmap(bitmap,300,300,true);
                    mProofUploadImageView3.setImageBitmap(UploadImage3);
                    count = (count + 1) % 3;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
