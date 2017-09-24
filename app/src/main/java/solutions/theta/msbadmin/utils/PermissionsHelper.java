package solutions.theta.msbadmin.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abubaker on 5/18/16.
 */
public class PermissionsHelper {



    public static boolean isPermissionGranted(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }


    public static boolean arePermissionGranted(Context context, List<String> permission) {
        int count = 0;
        for (int i = 0;i<permission.size();i++){
            if (PermissionChecker.checkSelfPermission(context,permission.get(i)) == PackageManager.PERMISSION_GRANTED){
                count++;
            }
        }

        return count == permission.size();
    }

    public static boolean isExplicitPermissionsRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }



    private static void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, Context context) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }




    @TargetApi(Build.VERSION_CODES.M)
    public static void checkForMultiplePermission(final Context context , final int requestCode ,final List<String>
            permissionList) {

        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> pList = new ArrayList<String>();

        for (int i=0; i<permissionList.size();i++){

            if (!addPermission(context , pList , permissionList.get(i))){
                permissionsNeeded.add(getPermissionString(permissionList.get(i)));
            }

        }

            if (pList.size() > 0) {

                if (permissionsNeeded.size() > 0){

                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanForActivity(context).requestPermissions(permissionList.toArray(new String[permissionList.size()]), requestCode);
                            }
                        },context);
                return;
                }
                scanForActivity(context).requestPermissions(permissionList.toArray(new String[permissionList
                        .size()]),
                        requestCode);

            }


    }




    @TargetApi(Build.VERSION_CODES.M)
    private static boolean addPermission(Context context , List<String> list, String permission) {
        if (scanForActivity(context).checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            list.add(permission);
            // Check for Rationale Option
            if (!scanForActivity(context).shouldShowRequestPermissionRationale(permission)){
                return false;
            }

        }
        return true;
    }


    private static String getPermissionString(String permission){

        if (Manifest.permission.CAMERA.equals(permission)){
            return "Camera";
        }else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)){
            return "Read";
        }else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)){
            return "Write External Storage";
        }

        return null;

    }



    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }


}
