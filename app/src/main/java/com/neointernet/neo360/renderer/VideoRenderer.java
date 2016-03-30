package com.neointernet.neo360.renderer;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;

import com.google.vrtoolkit.cardboard.Eye;
import com.neointernet.neo360.activity.VideoActivity;
import com.neointernet.neo360.listener.CardboardEventListener;
import com.neointernet.neo360.listener.VideoTimeListener;

import org.rajawali3d.cardboard.RajawaliCardboardRenderer;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;

import java.io.File;

public class VideoRenderer extends RajawaliCardboardRenderer implements CardboardEventListener {


    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private VideoActivity videoActivity;
    private String videoPath;
    private int mode = NONE;
    private float posX1, posX2, posY1, posY2;
    private float distance1, distance2;
    private VideoTimeListener listener;
    private MediaPlayer mediaPlayer;
    private StreamingTexture streamingTexture;
    private boolean vrMode;

    private long elapsedRealtime;
    private double deltaTime;
    private Matrix4 eyeMatrix = new Matrix4();
    private Quaternion eyeQuaternion = new Quaternion();
    private Quaternion rotateQuaternion = new Quaternion();

    private float angleX = 0;
    private float angleY = 0;
    private float angleZ = 0;

    public VideoRenderer(Activity activity) {
        super(activity.getApplicationContext());
        videoActivity = (VideoActivity) activity;
    }

    public VideoRenderer(Activity activity, String videoPath) {
        super(activity.getApplicationContext());
        this.videoPath = videoPath;
        videoActivity = (VideoActivity) activity;
    }

    @Override
    protected void initScene() {
        File file = new File(videoPath);
        Uri uri = Uri.fromFile(file);
        Log.i("URI", uri.toString());
        mediaPlayer = MediaPlayer.create(getContext(), uri);
        mediaPlayer.setLooping(true);

        streamingTexture = new StreamingTexture("texture", mediaPlayer);
        Material material = new Material();
        material.setColorInfluence(0);
        try {
            material.addTexture(streamingTexture);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }

        Sphere sphere = new Sphere(100, 128, 64);
        sphere.setScaleX(-1);
        sphere.setMaterial(material);

        getCurrentScene().addChild(sphere);
        getCurrentCamera().setPosition(Vector3.ZERO);
        getCurrentCamera().setFieldOfView(75);

        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("Media Player Status", "Completed");
                mp.stop();
                mp.release();
                videoActivity.finish();
            }
        });

        notifyVideoInit(mediaPlayer.getDuration());
    }

    public void setVideoTimeListener(VideoTimeListener listener) {
        this.listener = listener;
    }

    public void notifyVideoInit(int length) {
        listener.onVideoInit(length);
    }

    public void notifyTime(int time) {
        listener.listenTime(time);
    }

    public void setMediaPlayerSeekTo(int progress) {
        mediaPlayer.seekTo(progress);
    }

    @Override
    protected void onRender(long elapsedRealTime, double deltaTime) {
        this.elapsedRealtime = elapsedRealTime;
        this.deltaTime = deltaTime;
        streamingTexture.update();
        notifyTime(mediaPlayer.getCurrentPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onRenderSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        super.onRenderSurfaceDestroyed(surfaceTexture);
        mediaPlayer.release();
    }

    @Override
    public void onCardboardTouch(MotionEvent e) {
        int action = e.getAction();
        double fieldOfView = getCurrentCamera().getFieldOfView();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                posX1 = e.getX();
                posY1 = e.getY();
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    posX2 = e.getX();
                    posY2 = e.getY();

                    if (posX2 - posX1 > 0) {
                        angleX = angleX + (posX2 - posX1) / 10;
                    } else if (posX1 - posX2 > 0) {
                        angleX = angleX - (posX1 - posX2) / 10;
                    }
                    if (posY2 - posY1 > 0) {
                        angleY = angleY + (posY2 - posY1) / 10;
                    } else if (posY1 - posY2 > 0) {
                        angleY = angleY - (posY1 - posY2) / 10;
                    }

                    if (Math.abs(posX2 - posX1) > 15 || Math.abs(posY2 - posY1) > 15) {
                        posX1 = posX2;
                        posY1 = posY2;
                    }
                } else if (mode == ZOOM) {
                    distance1 = calculateDistance(e);
                    if (distance1 - distance2 > 0) {
                        if (fieldOfView < 130) {
                            fieldOfView = fieldOfView + (distance1 - distance2) / 10;
                            getCurrentCamera().setFieldOfView(fieldOfView);
                        }
                        distance2 = distance1;
                    } else if (distance2 - distance1 > 0) {
                        if (fieldOfView > 20) {
                            fieldOfView = fieldOfView - (distance2 - distance1) / 10;
                            getCurrentCamera().setFieldOfView(fieldOfView);
                        }
                        distance2 = distance1;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                distance1 = calculateDistance(e);
                distance2 = calculateDistance(e);
                break;
            case MotionEvent.ACTION_CANCEL:
            default:
                break;
        }
    }

    private float calculateDistance(MotionEvent e) {
        float x = e.getX(0) - e.getX(1);
        float y = e.getY(0) - e.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    @Override
    public void onDrawEye(Eye eye) {
        eyeMatrix.setAll(eye.getEyeView());
        eyeQuaternion.fromMatrix(eyeMatrix);
        rotateQuaternion.fromEuler(angleX, angleY, angleZ);
        getCurrentCamera().setOrientation(eyeQuaternion);
        getCurrentCamera().rotate(rotateQuaternion);
        render(elapsedRealtime, deltaTime);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
}