package cards.pay.paycardsrecognizer.sdk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import cards.pay.paycardsrecognizer.sdk.R;
import cards.pay.paycardsrecognizer.sdk.camera.widget.CameraPreviewLayout;
import cards.pay.paycardsrecognizer.sdk.utils.Constants;

public abstract class BaseScanCardFragment extends Fragment implements BaseScanCardInterface {

    public static final String TAG = "InitLibraryFragment";

    public View mProgressBar;
    public CameraPreviewLayout mCameraPreviewLayout;
    public ViewGroup mMainContent;
    public View mFlashButton;
    public View enterManuallyButton;

    abstract void onEnterManuallyButtonClick();

    abstract void onToggleFlashButtonClick();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.wocr_fragment_scan_card, container, false);

        mMainContent = root.findViewById(R.id.wocr_main_content);
        mProgressBar = root.findViewById(R.id.wocr_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mCameraPreviewLayout = root.findViewById(R.id.wocr_card_recognition_view);
        mFlashButton = root.findViewById(R.id.wocr_iv_flash_id);
        if (mFlashButton != null) {
            mFlashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onToggleFlashButtonClick();
                }
            });
        }

        enterManuallyButton = root.findViewById(R.id.wocr_tv_enter_card_number_id);
        enterManuallyButton.setVisibility(View.VISIBLE);
        enterManuallyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEnterManuallyButtonClick();
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar.setVisibility(View.GONE);
        mMainContent.setVisibility(View.VISIBLE);
        mCameraPreviewLayout.setVisibility(View.VISIBLE);
        mCameraPreviewLayout.getSurfaceView().setVisibility(View.GONE);
        mCameraPreviewLayout.setBackgroundColor(Color.BLACK);
        if (mFlashButton != null) {
            mFlashButton.setVisibility(View.GONE);
        }
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
