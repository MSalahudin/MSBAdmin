package solutions.theta.msbadmin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {


    private static final int INPUT_FILE_REQUEST_CODE = 101;
    public Uri imageUri;

    private static final int FILECHOOSER_RESULTCODE   = 2888;
    private Uri mCapturedImageURI = null;



    private static final List<String> permissions = Arrays.asList(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    );

    public static final int MULTIPLE_PERMISSION_REQUEST = 100;

    private static final Object TAG = "MainActivity";

    private WebView webView;
    private ProgressDialog mProgressDialog;
    private String mCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadWebView();

/*
        if (PermissionsHelper.isExplicitPermissionsRequired()) {


            if (PermissionsHelper.arePermissionGranted(this,permissions)){


                loadWebView();

            }else {

                PermissionsHelper.checkForMultiplePermission(this,MULTIPLE_PERMISSION_REQUEST,permissions);

            }

        }else {


            loadWebView();



        }
*/


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 100){

            Map<String, Integer> perms = new HashMap<String, Integer>();

            for (int i = 0; i < permissions.length; i++){
                perms.put(permissions[i], grantResults[i]);
            }

            // Check for Camera Permission
            if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // All Permissions Granted


                loadWebView();


            } else {
                // Permission Denied
                Toast.makeText(this, "Some Permission are Denied", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }



    private void loadWebView(){

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();

        webView = (WebView) findViewById(R.id.web_view);

        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        //Updated
        mWebSettings.setSupportZoom(false);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);



        webView.loadUrl(getString(R.string.url));
        webView.setWebViewClient(new MyWebClient());
        webView.setWebChromeClient(new MyWebChromeClient());

    }


    ValueCallback<Uri[]> mFilePathCallback = null;

    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;



    private class MyWebChromeClient extends WebChromeClient {


        /**
         * This is the method used by Android 5.0+ to upload files towards a mWebView form in a Webview
         *
         * @param webView
         * @param filePathCallback
         * @param fileChooserParams
         * @return
         */
        @Override
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {


            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            Intent contentSelectionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");

            Intent[] intentArray = getCameraIntent();

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose file");
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(contentSelectionIntent, INPUT_FILE_REQUEST_CODE);

            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            Log.d("LogTag", message);
            result.confirm();
            return true;
        }

        private Intent[] getCameraIntent() {

            // Determine Uri of camera image to save.
            Intent takePictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                // Create the File where the photo should go
//                File photoFile = null;
//                try {
//                    photoFile = createImageFile();
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                            Uri.fromFile(photoFile));
//                } catch (IOException ex) {
//                    // Error occurred while creating the File
//                    Log.e("TAG", "Unable to create Image File", ex);
//                }
//
//                // Continue only if the File was successfully created
//                if (photoFile != null) {
//                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                            Uri.fromFile(photoFile));
//                } else {
//                    takePictureIntent = null;
//                }
//            }

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            return intentArray;

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCameraPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    public static Uri savePicture(Context context, Bitmap bitmap, int maxSize) {

        int cropWidth = bitmap.getWidth();
        int cropHeight = bitmap.getHeight();

        if (cropWidth > maxSize) {
            cropHeight = cropHeight * maxSize / cropWidth;
            cropWidth = maxSize;

        }

        if (cropHeight > maxSize) {
            cropWidth = cropWidth * maxSize / cropHeight;
            cropHeight = maxSize;

        }

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, cropWidth, cropHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.getString(R.string.app_name)
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(
                mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg"
        );

        // Saving the bitmap
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            FileOutputStream stream = new FileOutputStream(mediaFile);
            stream.write(out.toByteArray());
            stream.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Mediascanner need to scan for the image saved
        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri fileContentUri = Uri.fromFile(mediaFile);
        mediaScannerIntent.setData(fileContentUri);
        context.sendBroadcast(mediaScannerIntent);

        return fileContentUri;
    }




    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (resultCode == RESULT_OK) {

            // This is for Android 4.4.4- (JellyBean & KitKat)
            if (requestCode == FILECHOOSER_RESULTCODE) {

                if (null == mFilePathCallback) {
                    super.onActivityResult(requestCode, resultCode, intent);
                    return;
                }

                final boolean isCamera;

                if (intent == null) {
                    isCamera = true;
                } else {
                    final String action = intent.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri[] selectedImageUri;

                if (isCamera) {

                    selectedImageUri = new Uri[] {intent.getData()};
                    mFilePathCallback.onReceiveValue(selectedImageUri);
                    mFilePathCallback = null;

                    return;

                } /*else {

                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), intent.getData());
                        selectedImageUri = intent == null ? null : savePicture(this, bitmap, 1400);

                        mFilePathCallback.onReceiveValue(selectedImageUri);
                        mFilePathCallback = null;

                        return;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/

                // And this is for Android 5.0+ (Lollipop)
            } else if (requestCode == INPUT_FILE_REQUEST_CODE) {

                Uri[] results = null;

                // Check that the response is a good one
                if (resultCode == Activity.RESULT_OK) {

                    /*//
                    final boolean isCamera;

                    if (intent == null) {
                        isCamera = true;
                    } else {
                        final String action = intent.getAction();
                        if (action == null) {
                            isCamera = false;
                        } else {
                            isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    Uri[] selectedImageUri;

                    if (isCamera) {

                        selectedImageUri = new Uri[] {intent.getData()};
                        mFilePathCallback.onReceiveValue(selectedImageUri);
                        mFilePathCallback = null;

                        return;

                    }
                    // ends camera code here*/
                    if (intent == null) {
                        // If there is not data, then we may have taken a photo
                        if (mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
/*
                        Bitmap bitmap = null;

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), intent.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Uri dataUri = savePicture(this, bitmap, 1400);*/

                        if (intent.getData() != null) {
                            results = new Uri[]{intent.getData()};
                        }
                    }
                }

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;

                return;
            }
        } else {

            super.onActivityResult(requestCode, resultCode, intent);
            return;
        }

    }







    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {

        if(webView.canGoBack()) {

            webView.goBack();

        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }


    public class MyWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            mProgressDialog.dismiss();


        }
    }




}
