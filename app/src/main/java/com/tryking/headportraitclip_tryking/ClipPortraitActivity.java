package com.tryking.headportraitclip_tryking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tryking.headportraitclip_tryking.clip_widget.ClipImageLayout;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClipPortraitActivity extends AppCompatActivity {
    private static final int RESULT_RESTART = 3;//返回了，重新进入相册选取照片

    @Bind(R.id.clip_ImageLayout)
    ClipImageLayout clipImageLayout;
    @Bind(R.id.bt_clip)
    Button btClip;
    private Intent intent;
    private String path;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_portrait);
        ButterKnife.bind(this);

//        //这步必须要加
//        //防止不同手机的通知栏高度会影响判断裁剪区域是否超出图片范围的准确性
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("请稍后...");
        path = getIntent().getStringExtra("path");
        if (TextUtils.isEmpty(path) || !(new File(path).exists())) {
            Toast.makeText(this, "图片加载失败...", Toast.LENGTH_SHORT).show();
            return;
        }
        //裁剪界面是将选择的图片转换为bitmap进行显示的，如果资源过大，会导致内存溢出，所以还需要进行一步压缩
        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtil.getBitmap(path, ScreenUtil.getScreenWidth(ClipPortraitActivity.this) / 2, ScreenUtil.getScreenHeight(ClipPortraitActivity.this) / 2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            Toast.makeText(this, "图片加载失败...", Toast.LENGTH_SHORT).show();
            return;
        }
        clipImageLayout.setBitmap(bitmap);
    }

    @OnClick(R.id.bt_clip)
    void click(View v) {
        switch (v.getId()) {
            case R.id.bt_clip:
                loadingDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = clipImageLayout.clip();
                        String path = Environment.getExternalStorageDirectory() + "/Tryking_clip_portrait/" + System.currentTimeMillis() + ".png";
                        File file = new File(path);
                        BitmapUtil.storeImage(bitmap, file);
                        //取消dialog
                        loadingDialog.dismiss();
                        intent = new Intent();
                        intent.putExtra("path", path);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).start();
                break;
        }
    }

    //监听返回键，让其返回相册
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //getRepeatCount：防止点的过快，触发两次后退事件
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(RESULT_RESTART, intent);
            finish();
            return true;
        }
        return false;
    }
}
