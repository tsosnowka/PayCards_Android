package cards.pay.paycardsrecognizer.sdk.core.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import cards.pay.paycardsrecognizer.sdk.camera.RecognitionAvailabilityChecker;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionCoreUtils;
import cards.pay.paycardsrecognizer.sdk.camera.RecognitionUnavailableException;
import cards.pay.paycardsrecognizer.sdk.ndk.RecognitionCore;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DeployCoreTask extends AsyncTask<Void, Void, Throwable> {

    private final DeployCoreTaskCallback callback;
    @SuppressLint("StaticFieldLeak")
    private final Context appContext;

    public DeployCoreTask(final Context context, final DeployCoreTaskCallback callback) {
        this.callback = callback;

        appContext = context == null
                ? null
                : context.getApplicationContext();
    }

    @Override
    protected Throwable doInBackground(Void... voids) {
        try {
            if (callback == null) {
                throw new RecognitionUnavailableException();
            }
            final RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(appContext);
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
        final DeployCoreTaskResult deployCoreTaskResult = lastError == null
                ? new DeployCoreTaskResult()
                : new DeployCoreTaskResult(lastError);

        callback.onResult(deployCoreTaskResult);
    }
}