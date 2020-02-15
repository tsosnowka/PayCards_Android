package cards.pay.paycardsrecognizer.sdk.core.service;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public interface ScanCardService {

    void startScanner(
            AppCompatActivity activity,
            ScanCardRequest scanCardRequest,
            InteractionListener interactionListener,
            int containerResId
    );

    void startScanner(
            Fragment fragment,
            ScanCardRequest scanCardRequest,
            InteractionListener interactionListener,
            int containerResId
    );

}
