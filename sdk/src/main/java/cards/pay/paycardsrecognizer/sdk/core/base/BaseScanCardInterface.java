package cards.pay.paycardsrecognizer.sdk.core.base;

import android.support.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface BaseScanCardInterface {
    void showProgress();

    void hideProgress();

    void showMainContent();

    void hideMainContent();
}
