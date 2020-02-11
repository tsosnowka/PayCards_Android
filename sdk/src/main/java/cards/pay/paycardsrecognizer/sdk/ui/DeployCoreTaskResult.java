package cards.pay.paycardsrecognizer.sdk.ui;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class DeployCoreTaskResult {

    public static final int SUCCESS = 1;
    public static final int FAILED = 2;

    @Retention(SOURCE)
    @IntDef({SUCCESS, FAILED})
    public @interface Result {

    }

    private @Result
    int result;
    private Throwable throwable;

    public DeployCoreTaskResult() {
        this.result = SUCCESS;
    }

    public DeployCoreTaskResult(@Result int result) {
        this.result = result;
    }

    public DeployCoreTaskResult(Throwable throwable) {
        this.result = FAILED;
        this.throwable = throwable;
    }

    public DeployCoreTaskResult(@Result int result, Throwable throwable) {
        this.result = result;
        this.throwable = throwable;
    }

    public int getResult() {
        return result;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
