package com.dengb;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dengb.facepp.DetectCallback;
import com.dengb.facepp.FaceppDetect;
import com.dengb.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * ScrollingActivity
 *
 * @author Dengb
 * @date 2016-6-14 09:51
 */
public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private Dialog mBottomDialog;
    private String compressUri;
    private Bitmap img;
    private ImageView mIv;
    private static ViewGroup.LayoutParams params;
    //识别失败
    private final int RECOGNIZE_OK = 0;
    //识别成功
    private final int RECOGNIZE_FAIL = 1;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mContext = this;
        mIv = (ImageView) findViewById(R.id.iv1);
    }

    public void showDialog(View v) {
        initBottomDialog();
        mBottomDialog.show();
    }

    /**
     * 初始化底部Dialog
     */
    private void initBottomDialog() {
        if (mBottomDialog != null) {
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.view_image_choose_dialog, null);
        Button btnOpenPhotos = (Button) view.findViewById(R.id.btn_album);
        Button btnOpenCamera = (Button) view.findViewById(R.id.btn_camera);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOpenPhotos.setOnClickListener(this);
        btnOpenCamera.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        mBottomDialog = new Dialog(mContext, R.style.DialogBottomTransparent);
        mBottomDialog.setContentView(view);
        Window window = mBottomDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        mBottomDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //打开系统相册获取图片
            case R.id.btn_album:
                ImageUtils.getImageFromAlbum(this);
                mBottomDialog.dismiss();
                break;
            //打开系统相机拍照
            case R.id.btn_camera:
                ImageUtils.getImageFromCamera(this, true);
                mBottomDialog.dismiss();
                break;
            //取消选择
            case R.id.btn_cancel:
                mBottomDialog.dismiss();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        String uri = null;
        //相册返回的图片
        if (requestCode == ImageUtils.REQUEST_CODE_ALBUM) {
            uri = ImageUtils.getImageAbsolutePath(this, data.getData());
        }
        //拍照缩略图
        if (requestCode == ImageUtils.REQUEST_CODE_CAMERA_THUMBNAIL) {
            uri = ImageUtils.getThumbnailImageAbsolutePath(ImageUtils
                    .getThumbnailBitmap(data));
        }
        //拍照返回的原图
        if (requestCode == ImageUtils.REQUEST_CODE_CAMERA_ORIGINAL) {
            uri = ImageUtils.getOriginalImageAbsolutePath();
        }
        compressUri = ImageUtils.getCompressPicturePath(uri);//获取压缩后的照片
        if (android.text.TextUtils.isEmpty(compressUri)) {
            Toast.makeText(getApplicationContext(), "选择图片有误，请重新选择", Toast.LENGTH_SHORT);
            return;
        }
        img = BitmapFactory.decodeFile(compressUri);
        initUI(img);
        faceRecognition();
    }

    /**
     * 根据返回图片的确定显示iv的宽高
     *
     * @param bitmap
     */
    private void initUI(Bitmap bitmap) {
        params = mIv.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        //获取屏幕的宽度
        int w_screen = dm.widthPixels;
        int h = bitmap.getHeight();
        int w = bitmap.getWidth();
        //得到图片的高宽比
        float r = (float) h / (float) w;
        //设置mIv的宽度为屏幕宽度的一半
        params.width = w_screen / 2;
        //高度值 根据图片高宽比确定
        params.height = (int) (params.width * r);
    }

    /**
     * 使用facepp进行人脸识别
     */
    private void faceRecognition() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("正在进行人脸识别。。。");
        progressDialog.show();
        FaceppDetect faceppDetect = new FaceppDetect();
        faceppDetect.setDetectCallback(new DetectCallback() {

            public void detectResult(JSONObject rst) {
                if (rst == null) {
                    handlerMsg(RECOGNIZE_FAIL, "识别失败！");
                }
                //use the red paint
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(5);
                paint.setStyle(Paint.Style.STROKE);
                //create a new canvas
                Bitmap bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(img, new Matrix(), null);
                try {
                    //find out all faces
                    final int count = rst.getJSONArray("face").length();
                    if (count == 0) {
                        handlerMsg(RECOGNIZE_FAIL, "未识别到人脸！");
                        return;
                    }
                    for (int i = 0; i < count; ++i) {
                        float x, y, w, h;
                        //检出的人脸框的中心点坐标, x & y 坐标分别表示在图片中的宽度和高度的百分比 (0~100之间的实数)
                        x = (float) rst.getJSONArray("face").getJSONObject(i)
                                .getJSONObject("position").getJSONObject("center").getDouble("x");
                        y = (float) rst.getJSONArray("face").getJSONObject(i)
                                .getJSONObject("position").getJSONObject("center").getDouble("y");
                        //0~100之间的实数，表示检出的脸的宽度在图片中百分比
                        w = (float) rst.getJSONArray("face").getJSONObject(i)
                                .getJSONObject("position").getDouble("width");
                        //0~100之间的实数，表示检出的脸的高度在图片中百分比
                        h = (float) rst.getJSONArray("face").getJSONObject(i)
                                .getJSONObject("position").getDouble("height");
                        //change percent value to the real size
                        x = x / 100 * img.getWidth();
                        w = w / 100 * img.getWidth() * 0.7f;
                        y = y / 100 * img.getHeight();
                        h = h / 100 * img.getHeight() * 0.7f;

                        //draw the box to mark it out
                        canvas.drawRect(x - w, y + h, x + w, y - h, paint);
                    }
                    //save new image
                    handlerMsg(RECOGNIZE_OK, bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handlerMsg(RECOGNIZE_FAIL, "识别失败！");
                }

            }
        });
        faceppDetect.detect(img);
    }

    private MyHandler mainHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            ScrollingActivity activity = (ScrollingActivity) reference.get();
            activity.progressDialog.dismiss();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        Bitmap b = (Bitmap) msg.obj;
                        activity.mIv.setLayoutParams(params);
                        activity.mIv.setImageBitmap(b);
                        Toast.makeText(activity, "识别成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        String handlermsg = (String) msg.obj;
                        Toast.makeText(activity, handlermsg, Toast.LENGTH_LONG).show();
                    default:
                        break;
                }
            }
        }
    }

    public void handlerMsg(int what, Object object) {
        Message m = mainHandler.obtainMessage(what);
        m.obj = object;
        mainHandler.sendMessage(m);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeCallbacksAndMessages(null);
    }
}
