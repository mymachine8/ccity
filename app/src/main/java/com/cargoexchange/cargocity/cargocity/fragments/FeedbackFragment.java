package com.cargoexchange.cargocity.cargocity.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.OrdersActivity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.SignatureActivity;

import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.constants.OrderStatus;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.models.Feedback;
import com.cargoexchange.cargocity.cargocity.utils.Deserializer;
import com.cargoexchange.cargocity.cargocity.utils.ImageHelper;
import com.cargoexchange.cargocity.cargocity.utils.NetworkAvailability;
import com.cargoexchange.cargocity.cargocity.utils.ParseJSON;
import com.cargoexchange.cargocity.cargocity.utils.SaveSignPad;
import com.google.gson.Gson;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener
{
    //TODO:Implement the image window logic here

    private static final int SCAN_REQUEST_CODE = 99;
    private static final int SCAN_MISSED_TAG = 200;
    private static final int SIGNATURE_REQUEST_CODE = 100;

    private int documentCount = 3;
    private ImageView mCameraImage;
    private Activity mActivityContext;
    private RatingBar mRatingBar;
    private TextView mTextView;
    private EditText mAdditionalCommentsEditText,mCommentEditText;
    private Button mSubmitButton;
    private ImageView mProofUploadImageViews[] = new ImageView[3];
    private ImageView mPopUpImageView;
    private Switch isItemGoodConditionSwitch;
    private Dialog mImagePopUpDialog;
    private Bitmap UploadImages[] = new Bitmap[documentCount];
    private Resources mResource;
    private String base64Images[] = new String[documentCount];
    private String mMissedtagBase64Image;
    private String mOrderNo=new String();
    private int count=0;
    private Switch isCustomerPresent;
    private ImageButton mUploadSignatureButton;
    private CheckBox mAcceptGoods;
    private TextView mTimeOfDelivery;
    private ImageView mSignatureImage;
    private ImageView mMissedTagImage;
    private RelativeLayout mMissedLayout;
    private LinearLayout mDeliveredLayout;
    private FloatingActionButton mUploadMissedTagFAB;
    private Button mSubmitMissedTagButton;
    public SaveSignPad signPadInstance = null;
    private boolean isOrderDelivered = false;
    private RouteSession mRouteSession;
    private int pos;
    private String ratingText[] = new String []{"Very Poor","Poor", "Good", "Very Good", "Excellent"};

    @Override
    public void onCreate(Bundle inBundle){
        super.onCreate(inBundle);
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
        mResource=this.getResources();
        mRouteSession=RouteSession.getInstance();
        pos=mRouteSession.getPosition();
        mOrderNo=mRouteSession.getmOrderList().get(pos).getOrderId();
        mActivityContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        bindViewVariables(view);
        bindListeners();
        mTextView.setText("Excellent");
    }

    public void bindViewVariables(View view) {
        mRatingBar=(RatingBar)view.findViewById(R.id.FeedbackRatingBar);
        mTextView=(TextView)view.findViewById(R.id.RatingValueLabel);
        mSubmitButton=(Button)view.findViewById(R.id.SubmitButton);
        for (int i=0;i<documentCount;i++){
            int resID = getResources().getIdentifier("ProofUploadImageView" + (i+1),"id",getActivity().getPackageName());
            mProofUploadImageViews[i]=(ImageView)view.findViewById(resID);
            UploadImages[i]= BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_imagedefault2);
            mProofUploadImageViews[i].setOnClickListener(new UploadImageViewListener(i));
        }

        mCameraImage = (ImageView) view.findViewById(R.id.camera_image);
        isItemGoodConditionSwitch=(Switch)view.findViewById(R.id.ItemReceivedSwitch);
        mAdditionalCommentsEditText=(EditText)view.findViewById(R.id.FeedBackEditText);
        mCommentEditText=(EditText)view.findViewById(R.id.CommentsEditText);
        isCustomerPresent=(Switch)view.findViewById(R.id.isCustomerPresentSwitch);
        mAcceptGoods=(CheckBox)view.findViewById(R.id.acceptGoodsCheckbox);
        mTimeOfDelivery=(TextView)view.findViewById(R.id.deliveryTimeTextView);
        mUploadSignatureButton =(ImageButton)view.findViewById(R.id.signRecieptButton); //Go to sign_pad button
        mMissedLayout=(RelativeLayout)view.findViewById(R.id.uploadtagLayout);
        mDeliveredLayout=(LinearLayout)view.findViewById(R.id.goodsDeliveredLayout);
        mSubmitMissedTagButton=(Button)view.findViewById(R.id.submitMissedTagButton);
        mUploadMissedTagFAB=(FloatingActionButton)view.findViewById(R.id.uploadMissedTagFAB);
        mSignatureImage =(ImageView)view.findViewById(R.id.signatureImageView);
        mMissedTagImage=(ImageView)view.findViewById(R.id.missedTagImageView);
    }
    private void bindListeners() {

        isCustomerPresent.setOnCheckedChangeListener(new isCustomerPresentListener());
        mSubmitMissedTagButton.setOnClickListener(this);
        mUploadMissedTagFAB.setOnClickListener(this);
        mUploadSignatureButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
        mRatingBar.setOnRatingBarChangeListener(new RatingChangedListener());
        mCameraImage.setOnClickListener(this);

    }

    public void setSignature(String fileName){
        if(fileName!=null) {
            File image = new File(fileName);
            if(image.exists())
            {
                Bitmap bitmap=BitmapFactory.decodeFile(fileName);
                mSignatureImage.setImageBitmap(bitmap);
            }
        }
    }

    private Feedback createFeedbackObject(){
        Feedback feedback = new Feedback();
        feedback.setOrderId(mOrderNo);
        feedback.setIsOrderDelivered(isCustomerPresent.isChecked());
        if(!feedback.isOrderDelivered()){
            feedback.setDeliveryFailedImage(mMissedtagBase64Image);
            isOrderDelivered = false;
        }else {
            isOrderDelivered = true;
            feedback.setInGoodCondition(isItemGoodConditionSwitch.isChecked());
            feedback.setDeliveryRating(mRatingBar.getNumStars());
            feedback.setAdditionalComments(mAdditionalCommentsEditText.getText().toString());
            feedback.setFeedback(mCommentEditText.getText().toString());
            List<String> images = new ArrayList<String>();
            for (String image : base64Images) {
                if (image != null && !image.isEmpty()) {
                    images.add(image);
                }
            }
            if (images.size() > 0) {
                feedback.setDocumentImageList(images);
            }
        }
        return feedback;
    }

    public void afterSubmitCallback(){
        //RouteSession mRouteSession=RouteSession.getInstance();
        //int pos=mRouteSession.getPosition();
        //mOrderNo=mRouteSession.getmOrderList().get(pos).getOrderId();
        if(isOrderDelivered) {
            mRouteSession.getmOrderList().get(pos).setDeliveryStatus(OrderStatus.DELIVERED);
        }else {
            mRouteSession.getmOrderList().get(pos).setDeliveryStatus(OrderStatus.DELIVERY_FAILED);
        }
            goToOrdersListFragment();
    }

    private void goToOrdersListFragment(){
        Intent MapInent=new Intent(mActivityContext,OrdersActivity.class);
        MapInent.putExtra("context_of_intent", "FeedbackFragment");
        MapInent.putExtra("fragment", "OrdersListFragment");
        startActivity(MapInent);
    }

    private void submitFeedback(){
        Feedback feedback = createFeedbackObject();
        String uri = Constants.CARGO_API_BASE_URL + "orderdetail/feedback";
        Gson gson = new Gson();
        try {
            JSONObject feedbackJson = new JSONObject(gson.toJson(feedback));
                JsonRequest request = CargoCity.getmInstance().postRequest(
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                onSuccessResponse(response);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String json;
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
                                }
                            }
                        }, uri, feedbackJson);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        Constants.SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                if(isNetworkAvailable())
                {
                    CargoCity.getmInstance().getRequestQueue().add(request);
                }
                else
                {
                    //TODO:Save the response in JSON file and send to orders page
                    saveTheResponseToFileForUploadingLater(feedbackJson);
                    afterSubmitCallback();

                }
        }catch (JSONException ex){
            Log.e("PARSE_ERROR",ex.getMessage().toString());
        }
    }

    private boolean saveTheResponseToFileForUploadingLater(JSONObject feedbackJSON)
    {
        File rootFile = new File(Environment.getExternalStorageDirectory(), "FeedBack");
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return false;
        }
        if (!rootFile.exists())
        {
            rootFile.mkdir();
        }
        File file = new File(rootFile,mOrderNo+ ".ser");
        String filename=file.getAbsolutePath();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(feedbackJSON.toString());
            outputStream.flush();
            outputStream.close();
            mRouteSession.getDelayedUploadOrderNoList().add(mOrderNo);
            mRouteSession.getDelayedUploadFileNameList().add(file.getAbsoluteFile());

        }
        catch (FileNotFoundException e){e.printStackTrace();}
        catch (IOException e){e.printStackTrace();}
        return true;
    }

    private boolean isNetworkAvailable()
    {
        NetworkAvailability mNetworkAvailability=new NetworkAvailability(mActivityContext);
        if(!mNetworkAvailability.isNetworkAvailable()) {
            return false;
        }
        return true;

    }

    private void onSuccessResponse(JSONObject response) {
        String status;
        try {
            status = response.getString("status");
            if (status.equalsIgnoreCase("success")) {
                afterSubmitCallback();
            } else {
                displayToastMessage(response.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void checkPermissionAndCallSignature(){
        int accessWritePermissionCheck = ContextCompat.checkSelfPermission(mActivityContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int accessReadPermissionCheck=ContextCompat.checkSelfPermission(mActivityContext,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(accessWritePermissionCheck!= PackageManager.PERMISSION_GRANTED &&
                accessReadPermissionCheck!=PackageManager.PERMISSION_GRANTED)
        {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.PERMISSION_EXTERNAL_STORAGE);
        }
        else {
            callSignatureActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == Constants.PERMISSION_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callSignatureActivity();
            }
        }
    }

    private void callSignatureActivity() {
        Intent intent = new Intent(getActivity(), SignatureActivity.class);
        startActivityForResult(intent, SIGNATURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            try
            {
               Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivityContext.getContentResolver(), uri);
                String base64Image = ImageHelper.convertBitmapToBase64(bitmap);
                mActivityContext.getContentResolver().delete(uri, null, null);
                base64Images[count] = base64Image;
                UploadImages[count]=Bitmap.createScaledBitmap(bitmap,300,300,true);
                mProofUploadImageViews[count].setImageBitmap(UploadImages[count]);
                count = (count+1) %3;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(requestCode==SCAN_MISSED_TAG && resultCode==Activity.RESULT_OK)
        {
            //TODO:Show the image in missed tag image viewer
            Uri uri =data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap=null;
            try
            {
                bitmap=MediaStore.Images.Media.getBitmap(mActivityContext.getContentResolver(),uri);
                mMissedTagImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,300,300,true));
                mMissedtagBase64Image = ImageHelper.convertBitmapToBase64(bitmap);
                mActivityContext.getContentResolver().delete(uri,null,null);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(requestCode == SIGNATURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setSignature(data.getExtras().getString("SIGN_FILE_NAME"));
        }
    }


    //LISTENER CLASSES

    private class isCustomerPresentListener implements CompoundButton.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if(!isChecked) {
                mDeliveredLayout.setVisibility(View.GONE);
                mMissedLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                mMissedLayout.setVisibility(View.GONE);
                mDeliveredLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class UploadImageViewListener implements View.OnClickListener{
        int index;
        UploadImageViewListener(int index) {
            this.index = index;
        }
        @Override
        public void onClick(View v)
        {
            uploadImageClick(index,v);
        }
    }


    private void uploadImageClick(final int index, View v) {
        mImagePopUpDialog=new Dialog(mActivityContext);
        mImagePopUpDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater mInflater=mActivityContext.getLayoutInflater();
        View layout=mInflater.inflate(R.layout.activity_show_image, (ViewGroup) v.findViewById(R.id.ShowImageRoot));
        mImagePopUpDialog.setContentView(layout);
        mPopUpImageView=(ImageView)layout.findViewById(R.id.ProofImageView);
        Button mDiscardImageButton=(Button)layout.findViewById(R.id.CancelImagePopUpButton);
        Button mOkImageButton=(Button)layout.findViewById(R.id.OkImageButton);
        mPopUpImageView.setImageBitmap(UploadImages[index]);
        mDiscardImageButton.setClickable(true);
        mImagePopUpDialog.show();
        mDiscardImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=0;
                for(i = index;i<documentCount-1;i++){
                    UploadImages[i] = UploadImages[i+1];
                    mProofUploadImageViews[i].setImageBitmap(UploadImages[i]);
                }
                UploadImages[i] = BitmapFactory.decodeResource(mResource, R.drawable.ic_imagedefault2);
                mProofUploadImageViews[2].setImageResource(R.drawable.ic_imagedefault2);
                count = (count - 1) % 3;
                mImagePopUpDialog.cancel();
            }
        });
        mOkImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DismissPopUp(v);
            }
        });
    }

    private class RatingChangedListener implements RatingBar.OnRatingBarChangeListener{
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            mTextView.setText(ratingText[(int)rating-1]);
        }
    }

    private void startCameraActivity(int requestCode){
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submitMissedTagButton:
                        submitFeedback();
                        break;
            case R.id.SubmitButton:
                        if(!mAcceptGoods.isChecked()) {
                            displayToastMessage("Check the Accept of Receipt");
                            return;
                        }
                        submitFeedback();
                        break;
            case R.id.signRecieptButton:
                        checkPermissionAndCallSignature();
                        break;
            case R.id.uploadMissedTagFAB:
                        startCameraActivity(SCAN_MISSED_TAG);
                        break;
            case R.id.camera_image:
                        startCameraActivity(SCAN_REQUEST_CODE);
                        break;
        }
    }
}
