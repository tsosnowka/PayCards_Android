package cards.pay.paycardsrecognizer.sdk.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import cards.pay.paycardsrecognizer.sdk.camera.RecognitionAvailabilityChecker;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionCoreUtils;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionUnavailableException;

public class ScanCardActivity extends AppCompatActivity {

    private static final String TAG = "ScanCardActivity";

    private static final int REQUEST_CAMERA_PERMISSION_CODE = 1;

    private DeployCoreTask mDeployCoreTask;

    private InteractionListener liste = new InteractionListenerActivityImpl(this);

    private InitListener initListe = new InitListener() {
        @Override
        public void onInitLibraryFailed(Throwable e) {
            Log.e(TAG, "Init library failed", new RuntimeException("onInitLibraryFailed()", e));
            setResult(ScanCardFragment.RESULT_CODE_ERROR);
            finish();
        }

        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        public void onInitLibraryComplete() {
            if (isFinishing()) return;
            showScanCard();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        getDelegate().onPostCreate(null);

        if (savedInstanceState == null) {
            RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(this);
            if (checkResult.isFailed() && !checkResult.isFailedOnCameraPermission()) {
                liste.onScanCardFailed(new RecognitionUnavailableException(checkResult.getMessage()));
            } else {
                if (RecognitionCoreUtils.isRecognitionCoreDeployRequired(this) || checkResult.isFailedOnCameraPermission()) {
                    if (checkResult.isFailedOnCameraPermission()) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
                    } else {
                        subscribeToInitCore();
                    }
                } else {
                    showScanCard();
                }
            }
        }
    }

    private void showScanCard() {
        ScanCardFragment fragment = new ScanCardFragment();
        fragment.mListener = liste;
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment, ScanCardFragment.TAG)
                .setCustomAnimations(0, 0)
                .commitNow();

        ViewCompat.requestApplyInsets(findViewById(android.R.id.content));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    subscribeToInitCore();
                } else {
                    initListe.onInitLibraryFailed(
                            new RecognitionUnavailableException(RecognitionUnavailableException.ERROR_NO_CAMERA_PERMISSION));
                }
                return;
            default:
                break;
        }
    }

    private void subscribeToInitCore() {
        if (mDeployCoreTask != null) mDeployCoreTask.cancel(false);
        mDeployCoreTask = new DeployCoreTask(this, initListe);
        mDeployCoreTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDeployCoreTask != null) {
            mDeployCoreTask.cancel(false);
            mDeployCoreTask = null;
        }
    }

}
