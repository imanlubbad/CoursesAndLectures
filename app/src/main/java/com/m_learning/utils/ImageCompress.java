package com.m_learning.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import com.m_learning.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageCompress {
    Context mContext;
    boolean isMainImage;
    onFinishedCompressListListener listListener;
    List<String> imageUriList = new ArrayList<>();

    public ImageCompress(Context context) {
        this.mContext = context;
    }

    private static boolean deleteNon_EmptyDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteNon_EmptyDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public void compressImages(boolean isMainImage, List<String> imageUriList, onFinishedCompressListListener listener) {
        this.imageUriList = imageUriList;
        this.isMainImage = isMainImage;
        this.listListener = listener;
        new ImageListCompressionAsyncTask().execute(imageUriList);
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentURI) {
        String result = null;

        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }

    @SuppressLint("Range")
    public String getRealPathFromURI1(Uri contentURI) {
        String result = null;

        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }


    private Bitmap getResizedBitmap(String path, Float widthRatio, Float heightRatio) {
        float scale = 1f;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Uri.parse(path).getPath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        if (imageHeight > imageWidth) {
            if (imageHeight > heightRatio) {
                scale = heightRatio / imageHeight;
            }

        } else {
            if (imageWidth > widthRatio) {
                scale = widthRatio / imageWidth;
            }
        }
        if (scale == 0f) return null;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap bm = BitmapFactory.decodeFile(Uri.parse(path).getPath());
        if (bm != null) {
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);
            bm.recycle();
            return resizedBitmap;
        }

        return null;
    }

    private String saveToFile(Bitmap bm) {
        File sd = getTempStoreDirectory(MLearningApp.getInstance());
        String path = null;
        FileOutputStream fOut = null;
        try {
            if (sd.canWrite()) {
                File temp = new File(sd, "temp" + System.currentTimeMillis() + ".png");
                fOut = new FileOutputStream(temp);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                path = temp.getPath();

                bm.recycle();
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.flush();
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return path;
    }

    private File getTempStoreDirectory(Context context) {
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        return context.getExternalFilesDir("temp").getAbsoluteFile();
    }

    private String compressImage(String imageUri) {
        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        try {

            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

        } catch (ArithmeticException ex) {
            listListener.onError(mContext.getString(R.string.failLoadImg));
            return null;
        }


        if (actualHeight != 0 && actualWidth != 0) {

            options.inSampleSize = ToolUtils.calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

        } else {
            listListener.onError(mContext.getString(R.string.failLoadImg));
            return null;

        }

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            return null;

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            return null;
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return filename;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private String getFilename() {
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MLearningApp/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    public boolean deleteFolder() {
        boolean isDeleted = false;
        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "MLearningApp");
        try {
            isDeleted = deleteNon_EmptyDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    public File createFileFromInputStream(InputStream inputStream, String fileName) {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        try {
            File f = new File(path, "/" + fileName);

            f.setWritable(true, false);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e) {
            System.out.println("error in creating a file");
            e.printStackTrace();
        }

        return null;
    }


    public interface onFinishedCompressListListener {
        void onError(String error);

        void onSuccessMainImageCompress(List<String> photos);

        void onSuccessImageCompress(List<String> photos);
    }

    @SuppressLint("StaticFieldLeak")
    class ImageListCompressionAsyncTask extends AsyncTask<List<String>, Void, List<String>> {

        @Override
        protected List<String> doInBackground(List<String>... params) {
            List<String> photos = new ArrayList<>();
            for (int i = 0; i < params[0].size(); i++) {
                String image = compressImage(params[0].get(i));
                if (image != null) photos.add(image);
            }

            return photos;
        }

        @Override
        protected void onPostExecute(List<String> photos) {
            super.onPostExecute(photos);
            if (listListener != null) {
                if (photos != null) {
                    try {
                        if (isMainImage) {
                            listListener.onSuccessMainImageCompress(photos);
                        } else {
                            listListener.onSuccessImageCompress(photos);
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        listListener.onError(mContext.getString(R.string.failLoadImg));
                    }

                } else listListener.onError(mContext.getString(R.string.failLoadImg));
            }

        }
    }


}
