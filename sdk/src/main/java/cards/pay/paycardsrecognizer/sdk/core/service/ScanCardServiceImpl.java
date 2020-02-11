package cards.pay.paycardsrecognizer.sdk.core.service;

import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import cards.pay.paycardsrecognizer.sdk.camera.RecognitionAvailabilityChecker;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionCoreUtils;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionUnavailableException;
import cards.pay.paycardsrecognizer.sdk.ui.InitLibraryFragment;
import cards.pay.paycardsrecognizer.sdk.ui.ScanCardFragment;

public class ScanCardServiceImpl implements ScanCardService {
    private final AppCompatActivity activity;
    private final ScanCardRequest scanCardRequest;
    private final InteractionListener interactionListener;
    private final int containerResId;

    public ScanCardServiceImpl(
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

    @Override
    public void initScanCardLib() {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(activity);
        if (checkResult.isFailed()
                && !checkResult.isFailedOnCameraPermission()) {
            interactionListener.onScanCardFailed(new RecognitionUnavailableException(checkResult.getMessage()));
        } else {
            if (RecognitionCoreUtils.isRecognitionCoreDeployRequired(activity)
                    || checkResult.isFailedOnCameraPermission()) {
                InitLibraryFragment.start(activity, scanCardRequest, interactionListener, containerResId);
            } else {
                ScanCardFragment.start(activity, scanCardRequest, interactionListener, containerResId);
            }
        }
    }

}
