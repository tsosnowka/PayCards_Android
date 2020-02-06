package cards.pay.paycardsrecognizer.sdk.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cards.pay.paycardsrecognizer.sdk.Card;
import cards.pay.paycardsrecognizer.sdk.R;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionAvailabilityChecker;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionCoreUtils;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionUnavailableException;
import cards.pay.paycardsrecognizer.sdk.camera.ScanManager;
import cards.pay.paycardsrecognizer.sdk.camera.widget.CameraPreviewLayout;
import cards.pay.paycardsrecognizer.sdk.ndk.RecognitionResult;
import cards.pay.paycardsrecognizer.sdk.ui.views.ProgressBarIndeterminate;

public class ScanCardActivity extends AppCompatActivity {

    private static final String TAG = "ScanCardActivity";

    private static final int REQUEST_CAMERA_PERMISSION_CODE = 1;
    public static final int RESULT_CODE_ERROR = Activity.RESULT_FIRST_USER;
    public static final String RESULT_PAYCARDS_CARD = "RESULT_PAYCARDS_CARD";
    public static final String RESULT_CARD_IMAGE = "RESULT_CARD_IMAGE";
    public static final String RESULT_CANCEL_REASON = "RESULT_CANCEL_REASON";

    public static final int BACK_PRESSED = 1;
    public static final int ADD_MANUALLY_PRESSED = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {BACK_PRESSED, ADD_MANUALLY_PRESSED})
    public @interface CancelReason {
    }

    private CameraPreviewLayout mCameraPreviewLayout;

    private ProgressBarIndeterminate mProgressBar;

    private ViewGroup mMainContent;

    @Nullable
    private View mFlashButton;

    public InteractionListener mListener;

    ScanManager mScanManager;

    private DeployCoreTask mDeployCoreTask;

    private RecognitionMode recognitionMode = new RecognitionMode();

    private InteractionListener liste = new InteractionListenerActivityImpl(this);

    private InitListener initListe = new InitListener() {
        @Override
        public void onInitLibraryFailed(Throwable e) {
            Log.e(TAG, "Init library failed", new RuntimeException("onInitLibraryFailed()", e));
            setResult(RESULT_CODE_ERROR);
            finish();
        }

        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        public void onInitLibraryComplete() {
            if (isFinishing()) {
                return;
            }
            initScanManager();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.wocr_fragment_scan_card);
        getDelegate().onPostCreate(null);
        initView();

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
                    initScanManager();
                }
            }
        }
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

    @Override
    public void onResume() {
        super.onResume();
        if (mScanManager != null) {
            mScanManager.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScanManager != null) {
            mScanManager.onPause();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mDeployCoreTask != null) {
            mDeployCoreTask.cancel(false);
            mDeployCoreTask = null;
        }
    }

    private void subscribeToInitCore() {
        if (mDeployCoreTask != null) mDeployCoreTask.cancel(false);
        mDeployCoreTask = new DeployCoreTask(this, initListe);
        mDeployCoreTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initView() {
        mProgressBar = findViewById(R.id.wocr_progress_bar);

        mCameraPreviewLayout = findViewById(R.id.wocr_card_recognition_view);
        mMainContent = findViewById(R.id.wocr_main_content);
        mFlashButton = findViewById(R.id.wocr_iv_flash_id);

        showMainContent();
        mProgressBar.setVisibility(View.VISIBLE);

        findViewById(R.id.wocr_tv_enter_card_number_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (v.isEnabled()) {
                    v.setEnabled(false);
                    if (mListener != null)
                        mListener.onScanCardCanceled(ADD_MANUALLY_PRESSED);
                }
            }
        });
        if (mFlashButton != null) {
            mFlashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onToggleFlash();
                }
            });
        }
    }

    private void showMainContent() {
        mMainContent.setVisibility(View.VISIBLE);
        mCameraPreviewLayout.setVisibility(View.VISIBLE);
    }

    private void hideMainContent() {
        mMainContent.setVisibility(View.INVISIBLE);
        mCameraPreviewLayout.setVisibility(View.INVISIBLE);
    }


    private void initScanManager() {
        mScanManager = new ScanManager(recognitionMode.getRecognitionMode(), this, mCameraPreviewLayout, new ScanManager.Callbacks() {

            private byte mLastCardImage[] = null;

            @Override
            public void onCameraOpened(Camera.Parameters cameraParameters) {
                boolean isFlashSupported = (cameraParameters.getSupportedFlashModes() != null
                        && !cameraParameters.getSupportedFlashModes().isEmpty());
                if (mMainContent == null) return;
                mProgressBar.hideSlow();
//                mCameraPreviewLayout.setBackgroundDrawable(null);
                if (mFlashButton != null)
                    mFlashButton.setVisibility(isFlashSupported ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onOpenCameraError(Exception exception) {
                mProgressBar.hideSlow();
                hideMainContent();
                finishWithError(exception);
            }

            @Override
            public void onRecognitionComplete(RecognitionResult result) {
                if (result.isFirst()) {
                    if (mScanManager != null) mScanManager.freezeCameraPreview();
                }
                if (result.isFinal()) {
                    String date;
                    if (TextUtils.isEmpty(result.getDate())) {
                        date = null;
                    } else {
                        date = result.getDate().substring(0, 2) + '/' + result.getDate().substring(2);
                    }

                    Card card = new Card(result.getNumber(), result.getName(), date);
                    byte cardImage[] = mLastCardImage;
                    mLastCardImage = null;
                    finishWithResult(card, cardImage);
                }
            }

            @Override
            public void onCardImageReceived(Bitmap cardImage) {
                mLastCardImage = compressCardImage(cardImage);
            }

            @Override
            public void onFpsReport(String report) {
            }

            @Override
            public void onAutoFocusMoving(boolean start, String cameraFocusMode) {
            }

            @Override
            public void onAutoFocusComplete(boolean success, String cameraFocusMode) {
            }

            @Nullable
            private byte[] compressCardImage(Bitmap img) {
                byte result[];
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (img.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                    result = stream.toByteArray();
                } else {
                    result = null;
                }
                return result;
            }
        });
    }


    public void onToggleFlash() {
        if (mScanManager != null) mScanManager.toggleFlash();
    }


    private void finishWithError(Exception exception) {
        if (mListener != null) mListener.onScanCardFailed(exception);
    }

    private void finishWithResult(Card card, @Nullable byte cardImage[]) {
        if (mListener != null) mListener.onScanCardFinished(card, cardImage);
    }

}
