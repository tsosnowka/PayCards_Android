package cards.pay.paycardsrecognizer.sdk.core.service;

import android.support.v7.app.AppCompatActivity;

public interface ScanCardService {

    void initScanCardLib(
            AppCompatActivity activity,
            ScanCardRequest scanCardRequest,
            InteractionListener interactionListener,
            int containerResId
    );

}
