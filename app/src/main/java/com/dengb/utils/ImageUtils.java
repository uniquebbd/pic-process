package com.dengb.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 图片相关工具类
 *
 * @author Dengb
 * @date 2016-6-14 09:51
 */
public class ImageUtils {
    /**
     * 拍照图片保存的文件夹
     */
    public final static String DIR_ROOT = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .getAbsolutePath()
            + File.separator + "PicProcess";
    /**
     * 从系统相册获取图片的请求码
     */
    public final static int REQUEST_CODE_ALBUM = 31221;
    /**
     * 从系统照相机获取图片的请求码(原图)
     */
    public final static int REQUEST_CODE_CAMERA_ORIGINAL = 31222;
    /**
     * 从系统照相机获取图片的请求码(缩略图)
     */
    public final static int REQUEST_CODE_CAMERA_THUMBNAIL = 31223;

    /**
     * 获取图片成功的返回码
     */
    public final static int RESULT_OK = Activity.RESULT_OK;

    private static Uri mUri;

    /**
     * 打开系统相册获取图片
     *
     * @param fragment
     */
    public static void getImageFromAlbum(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    /**
     * 打开系统相册获取图片
     *
     * @param activity
     */
    public static void getImageFromAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    /**
     * 打开系统相机获取图片
     *
     * @param fragment
     * @param origImage
     */
    public static void getImageFromCamera(Fragment fragment, boolean origImage) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            mUri = null;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //获取原图
            if (origImage) {
                File imageDir = new File(DIR_ROOT);
                if (!imageDir.exists()) {
                    imageDir.mkdir();
                }
                File imageFile = new File(DIR_ROOT, "image" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                mUri = Uri.fromFile(imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                intent.putExtra("return-data", true);
                fragment.startActivityForResult(intent, REQUEST_CODE_CAMERA_ORIGINAL);
            }
            //获取缩略图
            else {
                fragment.startActivityForResult(intent, REQUEST_CODE_CAMERA_THUMBNAIL);
            }
        } else {
            Toast.makeText(fragment.getActivity(), "请确认已经插入SD卡",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 打开系统照相机获取图片
     *
     * @param activity
     * @param origImage 是否获取的为缩略图
     */
    public static void getImageFromCamera(Activity activity, boolean origImage) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            mUri = null;
            Intent getImageByCamera = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            // 获取原图
            if (origImage) {
                File imageDir = new File(DIR_ROOT);
                if (!imageDir.exists()) {
                    imageDir.mkdirs();
                }
                File imageFile = new File(DIR_ROOT, "image"
                        + String.valueOf(System.currentTimeMillis()) + ".jpg");
                mUri = Uri.fromFile(imageFile);
                getImageByCamera.putExtra(
                        MediaStore.EXTRA_OUTPUT, mUri);
                getImageByCamera.putExtra("return-data", true);
                activity.startActivityForResult(getImageByCamera,
                        REQUEST_CODE_CAMERA_ORIGINAL);
            }
            // 获取缩略图
            else {
                activity.startActivityForResult(getImageByCamera,
                        REQUEST_CODE_CAMERA_THUMBNAIL);
            }
        } else {
            Toast.makeText(activity, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 获取bitmap
     *
     * @param imageUri
     * @return bitmap
     */
    public static Bitmap getImageBitmap(Context context, Uri imageUri) {
        Bitmap bm = null;
        if (imageUri == null) {
            return bm;
        }
        try {
            bm = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 获取缩略图bitmap
     *
     * @param data
     * @return bitmap
     */
    public static Bitmap getThumbnailBitmap(Intent data) {
        Bitmap bm = null;
        if (data == null) {
            return bm;
        }
        bm = (Bitmap) data.getExtras().get("data");
        return bm;
    }
    /**
     * 获取缩略图的绝对路径
     *
     * @param bitmap
     * @return
     */
    public static String getThumbnailImageAbsolutePath(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        File imageDir = new File(DIR_ROOT);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        File imageFile = null;
        try {
            imageFile = new File(DIR_ROOT, "thumb"
                    + String.valueOf(System.currentTimeMillis()) + ".jpg");
            if (!imageFile.exists()) {
                imageFile.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return imageFile.getAbsolutePath();
    }

    /**
     * 获取原图bitmap
     *
     * @param context
     * @return
     */
    public static Bitmap getOriginalBitmap(Context context) {
        Bitmap bm = null;
        ContentResolver cr = context.getContentResolver();
        try {
            bm = BitmapFactory.decodeStream(cr.openInputStream(mUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 获取原图的绝对路径
     *
     * @return
     */
    public static String getOriginalImageAbsolutePath() {
        if (mUri == null) {
            return null;
        }
        return mUri.getPath();
    }

    /**
     * 获取图片的绝对路径
     *
     * @param context  Activity实例
     * @param imageUri 图片的uri
     * @return 图片的绝对路径
     */
    @SuppressLint("NewApi")
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 图片压缩 参考自volley ImageRequest
     */
    private static Bitmap doParse(String path, int width, int height) {
        // 最大宽度
        int mMaxWidth = width;
        // 最大长度
        int mMaxHeight = height;

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        //  如果设置为true，并不会把图像的数据完全解码，亦即decodeXyz()返回值为null，但是Options的outAbc中解出了图像的基本信息。
        decodeOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, decodeOptions);

        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        // Then compute the dimensions we would ideally like to decode to.
        int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                actualHeight, actualWidth);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        // TODO(ficus): Do we need this or is it okay since API 8 doesn't
        // support it?
        // decodeOptions.inPreferQualityOverSpeed =
        // PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
                actualHeight, desiredWidth, desiredHeight);
        Log.e("Tag", "actualHeight=" + actualHeight + "\nactualWidth=" + actualWidth + "\ndesiredHeight=" + desiredHeight + "\ndesiredWidth=" + desiredWidth);
        Bitmap tempBitmap = BitmapFactory.decodeFile(path, decodeOptions);
        Bitmap bitmap = null;
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null
                && (tempBitmap.getWidth() > desiredWidth || tempBitmap
                .getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
                    desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }
        return bitmap;
    }

    /**
     * 將原图壓縮后，压缩的新圖片保存到新文件 传入原图URI 返回新文件URI
     */
    public static String getCompressPicturePath(String originalUri) {
        int quality = 90;
        String newPath;
        newPath = originalUri;
        FileOutputStream fos = null;
        try {
            Bitmap bitmap = doParse(originalUri, 768, 1024);
            while (FileSizeUtil.getFileOrFilesSize(newPath, FileSizeUtil.SIZETYPE_KB) > 100) {
                newPath = createNewFlilePath();
                if (fos != null) fos.close();
                fos = new FileOutputStream(new File(newPath));
                quality -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
                Log.e("tag", "quality=" + quality + "\nFile size=" + FileSizeUtil.getFileOrFilesSize(newPath, FileSizeUtil.SIZETYPE_KB));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return newPath;
    }

    /**
     * 创建JPG新文件，以毫秒值命名 返回新文件URI
     */
    private static String createNewFlilePath() {
        String path = "";
        try {
            File dirPath = new File(DIR_ROOT);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
            path = DIR_ROOT + "/" + "thumb"
                    + String.valueOf(System.currentTimeMillis()) + ".jpg";
            File tempFlie = new File(path);
            if (!tempFlie.exists()) {
                tempFlie.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            path = "";
        }
        return path;
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     * <p>
     * *******copy form volley ImageRequest*******
     *
     * @param maxPrimary      Maximum size of the primary dimension (i.e. width for max
     *                        width), or zero to maintain aspect ratio with secondary
     *                        dimension
     * @param maxSecondary    Maximum size of the secondary dimension, or zero to maintain
     *                        aspect ratio with primary dimension
     * @param actualPrimary   Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     *                        //     * @param scaleType       The ScaleType used to calculate the needed image size.
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary,
                                           int actualPrimary, int actualSecondary) {

        // If no dominant value at all, just return the actual.
        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling
        // ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        if ((resized * ratio) > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     * <p>
     * *******copy form volley ImageRequest*******
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    static int findBestSampleSize(int actualWidth, int actualHeight,
                                  int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    /**
     * 获取认证图片文件
     *
     * @param filePath 原图文件路径
     * @return
     */
    public static File getRecognizeImageFileFromCamera(String filePath) {
        Bitmap bmp = doParse(filePath, 1024, 768);
        File imageFile = new File(DIR_ROOT, "image"
                + String.valueOf(System.currentTimeMillis()) + "_1024x768.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imageFile;
    }

    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }
    /**
     * byte(字节)根据长度转成kb(千字节)
     *
     * @param bytes
     * @return
     */
    public static int bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal kilobyte = new BigDecimal(1024);
        float returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (int)returnValue;
    }
}
