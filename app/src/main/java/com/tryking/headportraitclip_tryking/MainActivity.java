package com.tryking.headportraitclip_tryking;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.head_portrait)
    SimpleDraweeView headPortrait;
    @Bind(R.id.head_portrait_layout)
    LinearLayout headPortraitLayout;

    private static final int REQUEST_PHOTO_ZOOM = 0;//图库
    private static final int REQUEST_PHOTO_TAKE = 1;//拍照
    private static final int IMAGE_COMPLETE = 2;//结果
    private static final int RESULT_RESTART = 3;//裁剪图片时重新返回相册结果码

    private LayoutInflater mLayoutInflater;
    private String PortraitSaveName;//存储的名字
    private String PortraitSavePath;//存储总路径
    private String path;    //单个确定图片存储的路径
    private PopupWindow mPopupWindow;

    TextView photograph;
    TextView albums;
    LinearLayout cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SimpleDraweeView使用前必须在setContentView前初始化
        Fresco.initialize(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        //刚开始就找头像路径进行设置
        setPortrait(getSharedPreferences("HeadPortraitClip", MODE_PRIVATE).getString("ic_portrait", ""));
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        File file = new File(Environment.getExternalStorageDirectory(), "/Tryking_clip_portrait");
        if (!file.exists())
            file.mkdirs();
        PortraitSavePath = Environment.getExternalStorageDirectory() + "/Tryking_clip_portrait/";
        PortraitSaveName = System.currentTimeMillis() + ".png";
    }

    @OnClick({R.id.head_portrait_layout})
    void click(View v) {
        switch (v.getId()) {
            case R.id.head_portrait_layout:
                showPopupWindow(headPortraitLayout);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_RESTART) {
            Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
            openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(openAlbumIntent, REQUEST_PHOTO_ZOOM);
        } else {
            if (resultCode != RESULT_OK) {
                return;
            }
            Uri uri = null;
            switch (requestCode) {
                case REQUEST_PHOTO_ZOOM://相册
                    if (data == null)
                        return;
                    uri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor;
                    if (Build.VERSION.SDK_INT < 11) {
                        cursor = managedQuery(uri, proj, null, null, null);
                    } else {
                        CursorLoader cursorLoader = new CursorLoader(this, uri, null, null, null, null);
                        cursor = cursorLoader.loadInBackground();
                    }
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    path = cursor.getString(column_index);//图片存储路径
                    Intent intent1 = new Intent(MainActivity.this, ClipPortraitActivity.class);
                    intent1.putExtra("path", path);
                    startActivityForResult(intent1, IMAGE_COMPLETE);
                    break;
                case REQUEST_PHOTO_TAKE://拍照
                    path = PortraitSavePath + PortraitSaveName;
                    uri = Uri.fromFile(new File(path));
                    Intent intent2 = new Intent(MainActivity.this, ClipPortraitActivity.class);
                    intent2.putExtra("path", path);
                    startActivityForResult(intent2, IMAGE_COMPLETE);
                    break;
                case IMAGE_COMPLETE:
                    String finalPath = data.getStringExtra("path");
                    setPortrait(finalPath);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //设置头像
    private void setPortrait(String finalPath) {
        if (finalPath == "")
            return;
        getSharedPreferences("HeadPortraitClip", MODE_PRIVATE).edit().putString("ic_portrait", finalPath);
        Uri portraitUri = Uri.fromFile(new File(finalPath));
        headPortrait.setImageURI(portraitUri);
        //可以设置一个控制器
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                //加载的图片URI地址
//                .setUri(portraitUri)
//                //设置点击重试是否开启
//                .setTapToRetryEnabled(true)
//                //设置旧的Controller
//                .setOldController(pcenterHeadPortrait.getController())
//                //构建
//                .build();
//        //设置DraweeController
//        pcenterHeadPortrait.setController(controller);
    }

    /*
    点击头像弹出PopupWindow
     */
    private void showPopupWindow(View v) {
        if (mPopupWindow == null) {
            View view = mLayoutInflater.inflate(R.layout.pop_select_portrait, null);
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            initPopup(view);
        }
        mPopupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00FFFFFF));
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    /*
    初始化Popup
     */
    private void initPopup(View view) {
        photograph = (TextView) view.findViewById(R.id.photograph);
        albums = (TextView) view.findViewById(R.id.albums);
        cancel = (LinearLayout) view.findViewById(R.id.cancel);

        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                PortraitSaveName = String.valueOf(System.currentTimeMillis()) + ".png";
                Uri imageUri = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(PortraitSavePath, PortraitSaveName));
                openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

//                openCameraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(openCameraIntent, REQUEST_PHOTO_TAKE);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
                //使用下面这个有的手机收不到路径
//                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(openAlbumIntent, REQUEST_PHOTO_ZOOM);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }
}
