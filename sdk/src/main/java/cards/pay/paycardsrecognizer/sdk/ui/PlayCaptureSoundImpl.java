package cards.pay.paycardsrecognizer.sdk.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.RestrictTo;

import cards.pay.paycardsrecognizer.sdk.R;
import cards.pay.paycardsrecognizer.sdk.ndk.RecognitionResult;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class PlayCaptureSoundImpl implements PlayCaptureSound {
    public static final boolean isSoundEnabled = false;
    private int mCapturedSoundId = -1;
    private SoundPool mSoundPool;
    private Context context;

    public PlayCaptureSoundImpl(Context context) {
        this.context = context;
    }

    private void playCaptureSound() {
        if (mCapturedSoundId >= 0) mSoundPool.play(mCapturedSoundId, 1, 1, 0, 0, 1);
    }

    void onActivityCreated() {
        onCameraOpened();
    }

    void onCameraOpened() {
        innitSoundPool();
    }

    void onRecognitionComplete(RecognitionResult result) {
        if (result.isFirst()) {
            playCaptureSound();
        }
    }

    private void innitSoundPool() {
        if (isSoundEnabled) {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
            mCapturedSoundId = mSoundPool.load(context, R.raw.wocr_capture_card, 0);
        }
    }

    public void onDestroy() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
        mCapturedSoundId = -1;
    }
}
