package cards.pay.paycardsrecognizer.sdk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import cards.pay.paycardsrecognizer.sdk.camera.RecognitionAvailabilityChecker;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionCoreUtils;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionUnavailableException;
import cards.pay.paycardsrecognizer.sdk.ndk.RecognitionCore;

public class DeployCoreTask extends AsyncTask<Void, Void, Throwable> {

    InitListener mListener;

    @SuppressLint("StaticFieldLeak")
    private final Context appContext;

    DeployCoreTask(Context context, InitListener mListener) {
        this.appContext = context.getApplicationContext();
        this.mListener = mListener;
    }

    @Override
    protected Throwable doInBackground(Void... voids) {
        try {
            RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(appContext);
            if (checkResult.isFailed()) {
                throw new RecognitionUnavailableException();
            }
            RecognitionCoreUtils.deployRecognitionCoreSync(appContext);
            if (!RecognitionCore.getInstance(appContext).isDeviceSupported()) {
                throw new RecognitionUnavailableException();
            }
            return null;
        } catch (RecognitionUnavailableException e) {
            return e;
        }
    }

    @Override
    protected void onPostExecute(@Nullable Throwable lastError) {
        super.onPostExecute(lastError);
        if (mListener == null) return;

        if (lastError == null) {
            mListener.onInitLibraryComplete();
        } else {
            mListener.onInitLibraryFailed(lastError);
        }
    }
}