package cards.pay.paycardsrecognizer.sdk.core.service;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import cards.pay.paycardsrecognizer.sdk.camera.RecognitionAvailabilityChecker;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionCoreUtils;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionUnavailableException;
import cards.pay.paycardsrecognizer.sdk.ui.InitLibraryFragment;
import cards.pay.paycardsrecognizer.sdk.ui.ScanCardFragment;

public class ScanCardServiceImpl implements ScanCardService {

    @Override
    public void initScanCardLib(
            Fragment fragment,
            ScanCardRequest scanCardRequest,
            InteractionListener interactionListener,
            int containerResId
    ) {
        final AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        if (activity == null) {
            return;
        }
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

    @Override
    public void initScanCardLib(
            AppCompatActivity activity,
            ScanCardRequest scanCardRequest,
            InteractionListener interactionListener,
            int containerResId
    ) {
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
