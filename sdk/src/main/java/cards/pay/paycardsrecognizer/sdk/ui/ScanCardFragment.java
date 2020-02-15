package cards.pay.paycardsrecognizer.sdk.ui;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import java.io.ByteArrayOutputStream;

import cards.pay.paycardsrecognizer.sdk.Card;
import cards.pay.paycardsrecognizer.sdk.R;
import cards.pay.paycardsrecognizer.sdk.camera.ScanManager;
import cards.pay.paycardsrecognizer.sdk.core.base.BaseScanCardFragment;
import cards.pay.paycardsrecognizer.sdk.core.service.InteractionListener;
import cards.pay.paycardsrecognizer.sdk.core.service.ScanCardRequest;
import cards.pay.paycardsrecognizer.sdk.ndk.RecognitionResult;

import static cards.pay.paycardsrecognizer.sdk.ndk.RecognitionConstants.RECOGNIZER_MODE_DATE;
import static cards.pay.paycardsrecognizer.sdk.ndk.RecognitionConstants.RECOGNIZER_MODE_GRAB_CARD_IMAGE;
import static cards.pay.paycardsrecognizer.sdk.ndk.RecognitionConstants.RECOGNIZER_MODE_NAME;
import static cards.pay.paycardsrecognizer.sdk.ndk.RecognitionConstants.RECOGNIZER_MODE_NUMBER;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ScanCardFragment extends BaseScanCardFragment {
    @SuppressWarnings("unused")
    public static final String TAG = "ScanCardFragment";

    public static void start(
            final FragmentActivity activity,
            final ScanCardRequest scanCardRequest,
            final InteractionListener listener,
            int containerResId
    ) {
        final ScanCardFragment fragment = new ScanCardFragment();
        fragment.interactionListener = listener;
        fragment.containerResId = containerResId;
        fragment.scanCardRequest = scanCardRequest;

        activity.getSupportFragmentManager().beginTransaction()
                .replace(containerResId, fragment, ScanCardFragment.TAG)
                .setCustomAnimations(0, 0)
                .commit();
    }

    public static void start(
            final Fragment fragment,
            final ScanCardRequest scanCardRequest,
            final InteractionListener listener,
            int containerResId
    ) {
        final ScanCardFragment scanCardFragment = new ScanCardFragment();
        scanCardFragment.interactionListener = listener;
        scanCardFragment.containerResId = containerResId;
        scanCardFragment.scanCardRequest = scanCardRequest;

        fragment.getChildFragmentManager().beginTransaction()
                .replace(containerResId, scanCardFragment, ScanCardFragment.TAG)
                .setCustomAnimations(0, 0)
                .commit();
    }

    @Nullable
    private ScanManager mScanManager;

    private SoundPool mSoundPool;

    private int mCapturedSoundId = -1;

    @Override
    protected void onToggleFlashButtonClick() {
        if (mScanManager != null) {
            mScanManager.toggleFlash();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isTablet()) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mCameraPreviewLayout.setBackgroundColor(Color.BLACK);
        }

        int recognitionMode = RECOGNIZER_MODE_NUMBER;
        if (scanCardRequest.isScanCardHolderEnabled()) recognitionMode |= RECOGNIZER_MODE_NAME;
        if (scanCardRequest.isScanExpirationDateEnabled()) recognitionMode |= RECOGNIZER_MODE_DATE;
        if (scanCardRequest.isGrabCardImageEnabled())
            recognitionMode |= RECOGNIZER_MODE_GRAB_CARD_IMAGE;

        mScanManager = new ScanManager(recognitionMode, getActivity(), mCameraPreviewLayout, new ScanManager.Callbacks() {

            private byte[] mLastCardImage = null;

            @Override
            public void onCameraOpened(Camera.Parameters cameraParameters) {
                final boolean isFlashSupported = (cameraParameters.getSupportedFlashModes() != null
                        && !cameraParameters.getSupportedFlashModes().isEmpty());
                if (getView() == null) {
                    return;
                }
                mProgressBar.hideSlow();
                mCameraPreviewLayout.setBackgroundDrawable(null);
                if (mFlashButton != null) {
                    mFlashButton.setVisibility(isFlashSupported ? View.VISIBLE : View.GONE);
                }

                innitSoundPool();
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
                    if (mScanManager != null) {
                        mScanManager.freezeCameraPreview();
                    }
                    playCaptureSound();
                }
                if (result.isFinal()) {
                    final String date;
                    if (TextUtils.isEmpty(result.getDate())) {
                        date = null;
                    } else {
                        date = result.getDate().substring(0, 2) + '/' + result.getDate().substring(2);
                    }

                    final Card card = new Card(result.getNumber(), result.getName(), date);
                    byte[] cardImage = mLastCardImage;
                    mLastCardImage = null;
                    if (cardImage == null) {
                        cardImage = new byte[0];
                    }
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
                byte[] result;
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (img.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                    result = stream.toByteArray();
                } else {
                    result = null;
                }
                return result;
            }
        });
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
    public void onDestroy() {
        super.onDestroy();
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
        mCapturedSoundId = -1;
    }

    private void innitSoundPool() {
        if (scanCardRequest.isSoundEnabled()) {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
            mCapturedSoundId = mSoundPool.load(getActivity(), R.raw.wocr_capture_card, 0);
        }
    }

    private void finishWithError(Exception exception) {
        if (interactionListener != null) {
            interactionListener.onScanCardFailed(exception);
        }
    }

    private void finishWithResult(@NonNull Card card, @NonNull byte[] cardImage) {
        if (interactionListener != null) {
            interactionListener.onScanCardFinished(
                    card.getCardNumberChars(),
                    card.getExpirationDate(),
                    card.getCardHolderName(),
                    cardImage
            );
        }
    }

    private boolean isTablet() {
        return getResources().getBoolean(R.bool.wocr_is_tablet);
    }

    private void playCaptureSound() {
        if (mCapturedSoundId >= 0) {
            mSoundPool.play(mCapturedSoundId, 1, 1, 0, 0, 1);
        }
    }

}
