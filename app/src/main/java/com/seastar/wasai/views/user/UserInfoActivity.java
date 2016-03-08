package com.seastar.wasai.views.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.User;
import com.seastar.wasai.R;
import com.seastar.wasai.service.LoginService;
import com.seastar.wasai.service.UserService;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import java.io.File;

public class UserInfoActivity extends BaseActivity {
    private EditText editTextView;
    private RoundedImageView avatarView;
    private View avatarLayout;
    private TextView logoutView;
    private TextView saveBtnView;
    private View actionBack;

    /**
     * 请求码
     */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    /**
     * 头像名称
     */
    private static final String IMAGE_FILE_NAME = "image.jpg";

    private String[] iamgeItems = new String[]{"选择本地图片", "拍照"};

    private DisplayImageOptions imageDisplayOptions;
    private boolean avatarIsUpdated = false;
    private Bitmap newAvatar;
    private String newNickname;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        imageDisplayOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        saveBtnView = (TextView) findViewById(R.id.btn_save);
        saveBtnView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(UserInfoActivity.this, null, "读取中...", true, false);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
                new SaveUserInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        editTextView = (EditText) findViewById(R.id.nickname);
        editTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                newNickname = editTextView.getText().toString().trim();
                if (!newNickname.equals(MyApplication.getCurrentUser().getNickname())
                        && StringUtil.isNotEmpty(newNickname)) {
                    saveBtnView.setVisibility(View.VISIBLE);
                } else if ((newNickname.equals(MyApplication.getCurrentUser().getNickname()) || !StringUtil
                        .isNotEmpty(newNickname)) && !avatarIsUpdated) {
                    saveBtnView.setVisibility(View.INVISIBLE);
                }
            }
        });
        avatarView = (RoundedImageView) findViewById(R.id.avatar);
        avatarLayout = findViewById(R.id.avatarLayout);
        avatarLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        actionBack = findViewById(R.id.action_back);
        actionBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setUserInfo();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: setUserInfo
     * @Description: 设置用户信息
     */
    private void setUserInfo() {
        if (MyApplication.isLogin()) {
            User user = MyApplication.getCurrentUser();
            editTextView.setText(user.getNickname());
            ImageLoader.getInstance().displayImage(user.getPictureUrl(), avatarView, imageDisplayOptions);
        }
    }

    @Override
    public void finishActivity() {

    }

    private class SaveUserInfo extends AsyncTask<Object, Object, Boolean> {
        private UserService userService = new UserService();

        @Override
        protected Boolean doInBackground(Object... params) {
            if (!newNickname.equals(MyApplication.getCurrentUser().getNickname())) {
                if (userService.updateNickName(newNickname)) {
                    User user = MyApplication.getCurrentUser();
                    user.setNickname(newNickname);
                    MyApplication.setCurrentUser(user);
                } else {
                    return false;
                }
            }
            if (avatarIsUpdated) {
                String picUrl = userService.uploadAvatar(newAvatar);
                if (StringUtil.isNotEmpty(picUrl)) {
                    User user = MyApplication.getCurrentUser();
                    user.setPictureUrl(picUrl);
                    MemoryCacheUtils.removeFromCache(picUrl, ImageLoader.getInstance().getMemoryCache());
                    DiskCacheUtils.removeFromCache(picUrl, ImageLoader.getInstance().getDiskCache());
                    MyApplication.setCurrentUser(user);
                    newAvatar.recycle();
                } else {
                    return false;
                }
            }
            return true;
        }

        protected void onPostExecute(Boolean flag) {
            progressDialog.dismiss();
            if (flag) {
                Toast.makeText(UserInfoActivity.this, ToastMessage.USER_INFO_UPDATED, Toast.LENGTH_SHORT).show();
                saveBtnView.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(UserInfoActivity.this, ToastMessage.OPERATION_FAILED, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 显示选择对话框
     */
    private void showDialog() {
        new AlertDialog.Builder(this).setTitle("设置头像").setItems(iamgeItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
//					Intent intentFromGallery = new Intent();
//					intentFromGallery.setType("image/*");
//					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);


                        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
                        intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);

                        break;
                    case 1:
                        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                            File file = new File(path, IMAGE_FILE_NAME);
                            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        }
                        startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                        break;
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        File tempFile = new File(path, IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getApplicationContext(), ToastMessage.SD_NOT_FOUND, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String url = getPath(this, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            newAvatar = photo;
            Drawable drawable = new BitmapDrawable(this.getResources(), photo);
            avatarView.setImageDrawable(drawable);
            avatarIsUpdated = true;
            saveBtnView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
