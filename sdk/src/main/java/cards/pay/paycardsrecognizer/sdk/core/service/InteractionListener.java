package cards.pay.paycardsrecognizer.sdk.core.service;

import cards.pay.paycardsrecognizer.sdk.Card;

public interface InteractionListener {

    void onInitLibraryFailed(Throwable e);

    void onScanCardFailed(Exception e);

    void onScanCardFinished(Card card, byte[] cardImage);
}
