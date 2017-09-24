package solutions.theta.msbadmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;

import solutions.theta.msbadmin.utils.Utils;


public class SplashScreen extends Activity {

    private int TIME_FOR_SPLASH_SCREEN = 3000;
    private AlertDialog dialog;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    Runnable mSplashRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mSplashRunnable);

            startActivity(new Intent(SplashScreen.this, MainActivity1.class));
            finish();
        }
    };

    private void startApp() {
        mHandler.postDelayed(mSplashRunnable, TIME_FOR_SPLASH_SCREEN);
    }

    private void showSettingsPrompt() {

        if (dialog == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
            builder.setTitle(getString(R.string.dialog_title));
            builder.setMessage(getString(R.string.internet_not_connected))
                    .setPositiveButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            dialog.dismiss();
                            openSettings();

                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                            finish();

                        }
                    });
            // Create the AlertDialog object and return it

            dialog = builder.create();
            dialog.setCancelable(false);

        }

        dialog.show();

    }

    private void openSettings() {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utils.isNetworkConnected(this)) {
            startApp();
        } else {
            showSettingsPrompt();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mSplashRunnable);
    }
}
