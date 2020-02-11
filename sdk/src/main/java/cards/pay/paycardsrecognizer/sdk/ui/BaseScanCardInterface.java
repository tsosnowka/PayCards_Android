package cards.pay.paycardsrecognizer.sdk.ui;

import android.support.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface BaseScanCardInterface {
    void onShowProgress();

    void onHideProgress();

    void showMainContent();

    void hideMainContent();
}
