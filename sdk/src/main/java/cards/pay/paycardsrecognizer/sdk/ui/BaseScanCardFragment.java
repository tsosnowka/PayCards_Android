package cards.pay.paycardsrecognizer.sdk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import cards.pay.paycardsrecognizer.sdk.R;
import cards.pay.paycardsrecognizer.sdk.camera.widget.CameraPreviewLayout;
import cards.pay.paycardsrecognizer.sdk.ui.views.ProgressBarIndeterminate;
import cards.pay.paycardsrecognizer.sdk.utils.Constants;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class BaseScanCardFragment extends Fragment implements BaseScanCardInterface {

    public static final String TAG = "BaseScanCardFragment";

    public ProgressBarIndeterminate mProgressBar;
    public CameraPreviewLayout mCameraPreviewLayout;
    public ViewGroup mMainContent;
    public View mFlashButton;

    protected int containerResId;
    protected ScanCardRequest scanCardRequest;
    protected InteractionListener interactionListener;

    abstract void onToggleFlashButtonClick();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.wocr_fragment_scan_card, container, false);

        mMainContent = root.findViewById(R.id.wocr_main_content);
        mProgressBar = root.findViewById(R.id.wocr_progress_bar);
        mCameraPreviewLayout = root.findViewById(R.id.wocr_card_recognition_view);
        mFlashButton = root.findViewById(R.id.wocr_iv_flash_id);
        mFlashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onToggleFlashButtonClick();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mProgressBar = null;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (Constants.DEBUG) {
            Log.d(TAG, "onCreateAnimation() called with: " + "transit = [" + transit + "], enter = [" + enter + "], nextAnim = [" + nextAnim + "]");
        }
        // SurfaceView is hard to animate
        Animation a = new Animation() {
        };
        a.setDuration(0);
        return a;
    }

    @Override
    public void onShowProgress() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHideProgress() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMainContent() {
        mMainContent.setVisibility(View.VISIBLE);
        mCameraPreviewLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMainContent() {
        mMainContent.setVisibility(View.INVISIBLE);
        mCameraPreviewLayout.setVisibility(View.INVISIBLE);
    }

}
