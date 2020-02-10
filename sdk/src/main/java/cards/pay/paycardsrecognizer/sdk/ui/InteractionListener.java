package cards.pay.paycardsrecognizer.sdk.ui;

import cards.pay.paycardsrecognizer.sdk.Card;
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent;

public interface InteractionListener {
    void onScanCardCanceled(@ScanCardIntent.CancelReason int actionId);

    void onInitLibraryFailed(Throwable e);

    void onInitLibraryComplete();

    void onScanCardFailed(Exception e);

    void onScanCardFinished(Card card, byte[] cardImage);
}
