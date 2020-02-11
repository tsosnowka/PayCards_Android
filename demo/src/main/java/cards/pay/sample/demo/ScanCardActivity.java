package cards.pay.sample.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cards.pay.paycardsrecognizer.sdk.Card;
import cards.pay.paycardsrecognizer.sdk.ui.InteractionListener;
import cards.pay.paycardsrecognizer.sdk.ui.ScanCardRequest;
import cards.pay.paycardsrecognizer.sdk.ui.ScanCardService;

public class ScanCardActivity extends AppCompatActivity implements InteractionListener {

    public static final int RESULT_CODE_ERROR = Activity.RESULT_FIRST_USER;
    public static final String RESULT_PAYCARDS_CARD = "RESULT_PAYCARDS_CARD";
    public static final String RESULT_CARD_IMAGE = "RESULT_CARD_IMAGE";
    public static final String RESULT_CANCEL_REASON = "RESULT_CANCEL_REASON";
    public static final int BACK_PRESSED = 1;
    public static final int ADD_MANUALLY_PRESSED = 2;
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static final String KEY_SCAN_CARD_REQUEST = "cards.pay.paycardsrecognizer.sdk.ui.ScanCardActivity.SCAN_CARD_REQUEST";
    private static final String TAG = "ScanCardActivity";
    private final ScanCardService scanCardService = new ScanCardService(this, ScanCardRequest.getDefault(), this, R.id.scan_card_service_frame_layout);
    private View enterManuallyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_card);
        enterManuallyButton = findViewById(R.id.wocr_tv_enter_card_number_id);
        enterManuallyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanCardCanceled(ADD_MANUALLY_PRESSED);
            }
        });

        scanCardService.initLib();
    }

    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void onScanCardFailed(Exception e) {
        Log.e(TAG, "Scan card failed", new RuntimeException("onScanCardFinishedWithError()", e));
        setResult(RESULT_CODE_ERROR);
        finish();
    }

    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void onScanCardFinished(Card card, @Nullable byte[] cardImage) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_PAYCARDS_CARD, (Parcelable) card);
        if (cardImage != null) intent.putExtra(RESULT_CARD_IMAGE, cardImage);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onInitLibraryFailed(Throwable e) {
        Log.e(TAG, "Init library failed", new RuntimeException("onInitLibraryFailed()", e));
        setResult(RESULT_CODE_ERROR);
        finish();
    }

    public void onScanCardCanceled(@CancelReason int actionId) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_CANCEL_REASON, actionId);
        setResult(RESULT_CANCELED, intent);
        finish();
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {BACK_PRESSED, ADD_MANUALLY_PRESSED})
    public @interface CancelReason {
    }

}
