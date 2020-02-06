package cards.pay.paycardsrecognizer.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.Log;

import cards.pay.paycardsrecognizer.sdk.Card;

public class InteractionListenerActivityImpl implements InteractionListener {
    private static final String TAG = "ListenerActivityImpl";
    private Activity activity;

    public InteractionListenerActivityImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void onScanCardFailed(Exception e) {
        Log.e(TAG, "Scan card failed", new RuntimeException("onScanCardFinishedWithError()", e));
        activity.setResult(ScanCardActivity.RESULT_CODE_ERROR);
        activity.finish();
    }

    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void onScanCardFinished(Card card, @Nullable byte cardImage[]) {
        Intent intent = new Intent();
        intent.putExtra(ScanCardActivity.RESULT_PAYCARDS_CARD, (Parcelable) card);
        if (cardImage != null) intent.putExtra(ScanCardActivity.RESULT_CARD_IMAGE, cardImage);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }

    @Override
    public void onScanCardCanceled(@ScanCardActivity.CancelReason int actionId) {
        Intent intent = new Intent();
        intent.putExtra(ScanCardActivity.RESULT_CANCEL_REASON, actionId);
        activity.setResult(Activity.RESULT_CANCELED, intent);
        activity.finish();
    }

}
