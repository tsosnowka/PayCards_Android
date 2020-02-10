package cards.pay.paycardsrecognizer.sdk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.ref.WeakReference;

import cards.pay.paycardsrecognizer.sdk.camera.RecognitionAvailabilityChecker;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionCoreUtils;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionUnavailableException;
import cards.pay.paycardsrecognizer.sdk.ndk.RecognitionCore;

public class DeployCoreTask extends AsyncTask<Void, Void, Throwable> {

    private final WeakReference<InitLibraryFragment> fragmentRef;

    @SuppressLint("StaticFieldLeak")
    private final Context appContext;

    DeployCoreTask(InitLibraryFragment parent) {
        this.fragmentRef = new WeakReference<>(parent);
        this.appContext = parent.getContext().getApplicationContext();
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
        InitLibraryFragment fragment = fragmentRef.get();
        if (fragment == null
                || fragment.mProgressBar == null
                || fragment.mListener == null) return;

        fragment.mProgressBar.setVisibility(View.GONE);
        if (lastError == null) {
            fragment.mListener.onInitLibraryComplete();
        } else {
            fragment.mListener.onInitLibraryFailed(lastError);
        }
    }
}