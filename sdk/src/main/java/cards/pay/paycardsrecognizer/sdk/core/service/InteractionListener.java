package cards.pay.paycardsrecognizer.sdk.core.service;

import android.support.annotation.NonNull;

import cards.pay.paycardsrecognizer.sdk.PaymentCard;

public interface InteractionListener {

    void onScanCardFailed(@NonNull Exception e);

    void onScanCardFinished(@NonNull PaymentCard paymentCard);
}
