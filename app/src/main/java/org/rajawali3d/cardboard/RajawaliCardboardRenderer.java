package org.rajawali3d.cardboard;

import android.content.Context;
import android.view.MotionEvent;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.renderer.RajawaliRenderer;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class RajawaliCardboardRenderer extends RajawaliRenderer implements CardboardView.StereoRenderer {

    private static GL10 gl10;
    private long ellapsedRealtime;
    private double deltaTime;
    private Matrix4 eyeMatrix = new Matrix4();
    private Quaternion eyeQuaternion = new Quaternion();

    public RajawaliCardboardRenderer(Context context) {
        super(context);
    }

    public RajawaliCardboardRenderer(Context context, boolean registerForResources) {
        super(context, registerForResources);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        super.onRenderFrame(null);
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        this.ellapsedRealtime = ellapsedRealtime;
        this.deltaTime = deltaTime;
    }

    @Override
    public void onDrawEye(Eye eye) {

        // Apply the eye transformation to the camera
        eyeMatrix.setAll(eye.getEyeView());
        eyeQuaternion.fromMatrix(eyeMatrix);
        getCurrentCamera().setOrientation(eyeQuaternion);

        render(ellapsedRealtime, deltaTime);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onRenderSurfaceSizeChanged(null, width, height);
    }

    @Override
    public void onSurfaceCreated(EGLConfig config) {
        super.onRenderSurfaceCreated(config, gl10, -1, -1);
    }

    @Override
    public void onRendererShutdown() {
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
    }

}
