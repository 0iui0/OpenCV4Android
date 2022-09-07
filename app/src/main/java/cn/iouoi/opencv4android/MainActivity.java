package cn.iouoi.opencv4android;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity {
    private static String LOGTAG = "OpenCV_Log";
    private CameraBridgeViewBase mOpenCVCameraView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.v(LOGTAG, "OpenCV Loaded");
                    mOpenCVCameraView.enableView();
                }
                break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (OpenCVLoader.initDebug()) {
            Log.d(LOGTAG, "OpenCV initialized");
        }
        mOpenCVCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_surface_view);
        mOpenCVCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCVCameraView.setCvCameraViewListener(cvCameraViewListener);
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCVCameraView);
    }

    private CameraBridgeViewBase.CvCameraViewListener2 cvCameraViewListener = new CameraBridgeViewBase.CvCameraViewListener2() {
        @Override
        public void onCameraViewStarted(int width, int height) {

        }

        @Override
        public void onCameraViewStopped() {

        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            Mat input_rgba = inputFrame.rgba();
            Mat input_gray = inputFrame.gray();
            MatOfPoint corners = new MatOfPoint();
            Imgproc.goodFeaturesToTrack(input_gray, corners, 20, 0.01, 10, new Mat(), 3, false);
            Point[] cornersArr = corners.toArray();
            for (int i = 0; i < corners.rows(); i++) {
                Imgproc.circle(input_rgba, cornersArr[i], 10, new Scalar(0, 255, 0), 2);
            }
            return input_rgba;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCVCameraView != null) {
            mOpenCVCameraView.disableView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(LOGTAG, "OpenCV not found, Initializing");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCVCameraView != null) {
            mOpenCVCameraView.disableView();
        }
    }

}