package cards.pay.paycardsrecognizer.sdk.ui;

import cards.pay.paycardsrecognizer.sdk.Card;

public interface InteractionListener {
    void onScanCardCanceled(@ScanCardActivity.CancelReason int cancelReason);

    void onScanCardFailed(Exception e);

    void onScanCardFinished(Card card, byte cardImage[]);

}
