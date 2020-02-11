package cards.pay.paycardsrecognizer.sdk.ui;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DeployCoreTaskResult {

    public static final int SUCCESS = 1;
    public static final int FAILED = 2;

    @Retention(SOURCE)
    @IntDef({SUCCESS, FAILED})
    public @interface Result {

    }

    private final @Result
    int result;
    @Nullable
    private final Throwable throwable;

    public DeployCoreTaskResult() {
        this.result = SUCCESS;
        throwable = null;
    }

    public DeployCoreTaskResult(@Result int result) {
        this.result = result;
        throwable = null;
    }

    public DeployCoreTaskResult(final Throwable throwable) {
        this.result = FAILED;
        this.throwable = throwable;
    }

    public DeployCoreTaskResult(@Result int result, final Throwable throwable) {
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
