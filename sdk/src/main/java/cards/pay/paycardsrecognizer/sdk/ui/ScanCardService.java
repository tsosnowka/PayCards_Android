package cards.pay.paycardsrecognizer.sdk.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import cards.pay.paycardsrecognizer.sdk.Card;
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionAvailabilityChecker;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionCoreUtils;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionUnavailableException;

public class ScanCardService implements InteractionListener {
    private static final String TAG = "ScanCardService";
    private final AppCompatActivity activity;
    private final ScanCardRequest scanCardRequest;
    private final InteractionListener interactionListener;
    private final int containerResId;//android.R.id.content

    public ScanCardService(
            AppCompatActivity activity,
            ScanCardRequest scanCardRequest,
            InteractionListener interactionListener,
            int containerResId
    ) {
        this.activity = activity;
        this.scanCardRequest = scanCardRequest;
        this.interactionListener = interactionListener;
        this.containerResId = containerResId;
    }

    public void initLib() {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(activity);
        if (checkResult.isFailed()
                && !checkResult.isFailedOnCameraPermission()) {
            onScanCardFailed(new RecognitionUnavailableException(checkResult.getMessage()));
        } else {
            if (RecognitionCoreUtils.isRecognitionCoreDeployRequired(activity)
                    || checkResult.isFailedOnCameraPermission()) {
                InitLibraryFragment.start(activity, scanCardRequest, interactionListener, containerResId);
            } else {
                ScanCardFragment.start(activity, scanCardRequest, interactionListener, containerResId);
            }
        }
    }

    @Override
    public void onScanCardFailed(Exception e) {
        Log.e(TAG, "Scan card failed", new RuntimeException("onScanCardFinishedWithError()", e));
//        setResult(ScanCardIntent.RESULT_CODE_ERROR);
//        finish();
    }

    @Override
    public void onScanCardFinished(Card card, @Nullable byte[] cardImage) {
        Intent intent = new Intent();
        intent.putExtra(ScanCardIntent.RESULT_PAYCARDS_CARD, (Parcelable) card);
        if (cardImage != null) intent.putExtra(ScanCardIntent.RESULT_CARD_IMAGE, cardImage);
//        setResult(RESULT_OK, intent);
//        finish();
    }

    @Override
    public void onInitLibraryFailed(Throwable e) {
        Log.e(TAG, "Init library failed", new RuntimeException("onInitLibraryFailed()", e));
//        setResult(ScanCardIntent.RESULT_CODE_ERROR);
//        finish();
    }

    @Override
    public void onScanCardCanceled(@ScanCardIntent.CancelReason int actionId) {
        Intent intent = new Intent();
        intent.putExtra(ScanCardIntent.RESULT_CANCEL_REASON, actionId);
//        setResult(RESULT_CANCELED, intent);
//        finish();
    }

}
