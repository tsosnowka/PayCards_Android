package cards.pay.paycardsrecognizer.sdk.core.service;

import android.support.annotation.NonNull;

public interface InteractionListener {

    void onScanCardFailed(Exception e);

    void onScanCardFinished(
            @NonNull char[] cardNumber,
            @NonNull String expirationDate,
            @NonNull String cardHolder,
            @NonNull byte[] cardImage
    );
}
