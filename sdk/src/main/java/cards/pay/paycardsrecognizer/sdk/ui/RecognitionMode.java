package cards.pay.paycardsrecognizer.sdk.ui;

import static cards.pay.paycardsrecognizer.sdk.ndk.RecognitionConstants.RECOGNIZER_MODE_DATE;
import static cards.pay.paycardsrecognizer.sdk.ndk.RecognitionConstants.RECOGNIZER_MODE_GRAB_CARD_IMAGE;
import static cards.pay.paycardsrecognizer.sdk.ndk.RecognitionConstants.RECOGNIZER_MODE_NAME;
import static cards.pay.paycardsrecognizer.sdk.ndk.RecognitionConstants.RECOGNIZER_MODE_NUMBER;

public class RecognitionMode {

    public static final boolean isScanCardHolderEnabled = true;
    public static final boolean isScanExpirationDateEnabled = true;
    public static final boolean isGrabCardImageEnabled = false;

    private int recognitionMode;

    public RecognitionMode() {
        this(isScanCardHolderEnabled, isScanExpirationDateEnabled, isGrabCardImageEnabled);
    }

    public RecognitionMode(
            boolean isScanCardHolderEnabled,
            boolean isScanExpirationDateEnabled,
            boolean isGrabCardImageEnabled
    ) {
        recognitionMode = RECOGNIZER_MODE_NUMBER;
        if (isScanCardHolderEnabled) recognitionMode |= RECOGNIZER_MODE_NAME;
        if (isScanExpirationDateEnabled) recognitionMode |= RECOGNIZER_MODE_DATE;
        if (isGrabCardImageEnabled) recognitionMode |= RECOGNIZER_MODE_GRAB_CARD_IMAGE;
    }

    public int getRecognitionMode() {
        return recognitionMode;
    }
}
