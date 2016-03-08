package com.seastar.wasai.views.order;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.waitingdlg.WaitingDlg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 找回订单
 */
public class FindOrderActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    private WaitingDlg waitingDlg;
    private Button finishBtn;
    private EditText realNameView;
    private EditText mobileNoView;
    private EditText descView;
    private Spinner mSpinner;
    private ImageView mImageView01, mImageView02, mImageView03;
    private static final int PHOTO_REQUEST_GALLERY = 0;
    private static final int PHOTO_REQUEST_GALLERY1 = 1;
    private static final int PHOTO_REQUEST_GALLERY2 = 2;
    private PopupWindow mPopupWindow;
    private boolean is_show_img01 = false;
    private boolean is_show_img02 = false;
    private boolean is_show_img03 = false;
    private ArrayList<String> imgUriList = new ArrayList<>();
    private Bitmap bitmap01,bitmap02,bitmap03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_order);
        initView();
        initData();

    }

    private void initView() {
        TextView mTitleName = (TextView) findViewById(R.id.titleName);
        mTitleName.setText("申诉");

        waitingDlg = new WaitingDlg(this);

//        orderNoView = (EditText) findViewById(R.id.order_no);
        realNameView = (EditText) findViewById(R.id.real_name);
        mobileNoView = (EditText) findViewById(R.id.mobile_no);
        descView = (EditText) findViewById(R.id.desc);
//        dateView = (EditText) findViewById(R.id.date);
        finishBtn = (Button) findViewById(R.id.finishBtn);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mImageView01 = (ImageView) findViewById(R.id.find_order_img_01);
        mImageView02 = (ImageView) findViewById(R.id.find_order_img_02);
        mImageView03 = (ImageView) findViewById(R.id.find_order_img_03);

        findViewById(R.id.leftButton).setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        mImageView01.setOnClickListener(this);
        mImageView02.setOnClickListener(this);
        mImageView03.setOnClickListener(this);
    }


    private void initData() {
        String[] mItems = getResources().getStringArray(R.array.spinnername);
        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, mItems);
        mSpinner.setAdapter(Adapter);
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getFragmentManager(), "datePicker");
    }

    public void setDate(String date) {
//        dateView.setText(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton:
                finish();
                break;
            case R.id.finishBtn:

                if (!StringUtil.isNotEmpty(realNameView.getText().toString())) {
                    Toast.makeText(FindOrderActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!StringUtil.isNotEmpty(mobileNoView.getText().toString())) {
                    Toast.makeText(FindOrderActivity.this, "请输入电话", Toast.LENGTH_SHORT).show();
                    break;
                }
//                if (!StringUtil.isNotEmpty(dateView.getText().toString())) {
//                    Toast.makeText(FindOrderActivity.this, "请选择日期", Toast.LENGTH_SHORT).show();
//                    break;
//                }

                findOrder();
                break;
            case R.id.find_order_img_01:
                if(is_show_img01 == false){
                    showSuccessPopupwindow(1);
                }else {
                    ToPreviewActivity();
                }
                break;
            case R.id.find_order_img_02:
                if(is_show_img02 == false){
                    showSuccessPopupwindow(2);
                }else {
                ToPreviewActivity();
                }
                break;
            case R.id.find_order_img_03:
                if(is_show_img03 == false){
                    showSuccessPopupwindow(3);
                }else{
                    ToPreviewActivity();
                }
                break;
        }

    }

    private void ToPreviewActivity() {
        Intent intent = new Intent(this, PreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("preview_url", imgUriList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showSuccessPopupwindow(int position) {
        initSuccessPopuptWindow(position);
        ColorDrawable dw = new ColorDrawable(0x7F000000);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.showAtLocation(findViewById(R.id.header), Gravity.CENTER, 0, 0);
    }

    private void initSuccessPopuptWindow(final int positon) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupWindow = layoutInflater.inflate(R.layout.find_order_popupwindow, null);
        Button pButton = (Button) popupWindow.findViewById(R.id.photo_button);
        Button cancaelButton = (Button) popupWindow.findViewById(R.id.cancel_button);
        cancaelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                if (positon == 1) {
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                } else if (positon == 2) {
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY1);
                } else {
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY2);
                }
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow = new PopupWindow(popupWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
    }


    private void findOrder() {
        waitingDlg.showWaitingDlg(true);
        String url = InterfaceConstant.FIND_ORDER;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                boolean isSuccess = false;
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    isSuccess = gson.fromJson(dataJsonStr, Boolean.class);
                }
                if (isSuccess) {
                    Toast.makeText(FindOrderActivity.this, "申诉成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FindOrderActivity.this, "申诉失败", Toast.LENGTH_SHORT).show();
                }
                waitingDlg.showWaitingDlg(false);
            }

            @Override
            public void doErrorResponse(JSONObject response) {
                try {
                    if (response.getInt("code") == 10006) {
                        Toast.makeText(FindOrderActivity.this, "此订单已申诉过了", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                waitingDlg.showWaitingDlg(false);
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url);
        Map<String, String> requestBody = new HashMap<String, String>();
//        requestBody.put("order_id", orderNoView.getText().toString());
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    @Override
    public void finishActivity() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            showDatePickerDialog(v);
        }
        return false;
    }

    private String changeUriToPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualImageCursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualImageCursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualImageCursor.moveToFirst();
        return actualImageCursor
                .getString(actual_image_column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case PHOTO_REQUEST_GALLERY:
                    if (data != null) {
                        Uri imgUri = getUri(data.getData());
                        mImageView01.setImageURI(imgUri);
                        is_show_img01 = true;
                        imgUriList.add(String.valueOf(imgUri));
                    }
                    break;
                case PHOTO_REQUEST_GALLERY1:
                    if (data != null) {
                        Uri imgUri = getUri(data.getData());
                        mImageView02.setImageURI(imgUri);
                        is_show_img02 = true;
                        imgUriList.add(String.valueOf(imgUri));
                    }
                    break;
                case PHOTO_REQUEST_GALLERY2:
                    if (data != null) {
                        Uri imgUri = getUri(data.getData());
                        mImageView03.setImageURI(imgUri);
                        is_show_img03 = true;
                        imgUriList.add(String.valueOf(imgUri));
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Uri getUri(Uri fileUri) {
        String path = changeUriToPath(fileUri);
        return Uri.fromFile(new File(path));
    }
}
