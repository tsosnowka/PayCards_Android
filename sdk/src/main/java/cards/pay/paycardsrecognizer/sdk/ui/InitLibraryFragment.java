package cards.pay.paycardsrecognizer.sdk.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import cards.pay.paycardsrecognizer.sdk.ScanCardIntent;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionAvailabilityChecker;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionUnavailableException;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class InitLibraryFragment extends BaseScanCardFragment {

    public static final String TAG = "InitLibraryFragment";

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 1;

    private DeployCoreTaskCallback deployCoreTaskCallback = new DeployCoreTaskCallback() {
        @Override
        public void onResult(DeployCoreTaskResult result) {
            if (result.getResult() == DeployCoreTaskResult.SUCCESS) {
                final FragmentActivity activity = getActivity();
                if (activity == null || activity.isFinishing()) {
                    mListener.onInitLibraryFailed(new Throwable());
                    return;
                }
                ScanCardFragment.start(activity, scanCardRequest, mListener);
            } else {
                mListener.onInitLibraryFailed(result.getThrowable());
            }
        }
    };

    public static InitLibraryFragment getInstance(ScanCardRequest scanCardRequest) {
        InitLibraryFragment fragment = new InitLibraryFragment();
        Bundle args = new Bundle(1);
        args.putParcelable(ScanCardIntent.KEY_SCAN_CARD_REQUEST, scanCardRequest);
        fragment.setArguments(args);
        return fragment;
    }

    public DeployCoreTask mDeployCoreTask;

    public static void start(FragmentActivity activity, ScanCardRequest scanCardRequest) {
        Fragment fragment = InitLibraryFragment.getInstance(scanCardRequest);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment, InitLibraryFragment.TAG)
                .setCustomAnimations(0, 0)
                .commitNow();
    }

    @Override
    void onToggleFlashButtonClick() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(getContext());
        if (checkResult.isFailedOnCameraPermission()) {
            if (savedInstanceState == null) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            }
        } else {
            subscribeToInitCore();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                subscribeToInitCore();
            } else {
                if (mListener != null) {
                    mListener.onInitLibraryFailed(new RecognitionUnavailableException(
                            RecognitionUnavailableException.ERROR_NO_CAMERA_PERMISSION));
                }
            }
        }
    }

    private void subscribeToInitCore() {
        onShowProgress();
        if (mDeployCoreTask != null) {
            mDeployCoreTask.cancel(false);
        }
        mDeployCoreTask = new DeployCoreTask(getContext(), deployCoreTaskCallback);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
