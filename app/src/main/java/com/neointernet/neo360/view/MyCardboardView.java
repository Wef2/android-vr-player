package com.neointernet.neo360.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.neointernet.neo360.listener.CardboardEventListener;

import org.rajawali3d.cardboard.RajawaliCardboardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neo-202 on 2016-03-22.
 */
public class MyCardboardView extends RajawaliCardboardView {

    private List<CardboardEventListener> listeners = new ArrayList<>();

    public MyCardboardView(Context context) {
        super(context);
    }

    public MyCardboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent e) {
        notifyTouchEvent(e);
        return super.onTouchEvent(e);
    }

    public void changeVRMode(){
        if(getVRMode()){
            setVRModeEnabled(false);
        }
        else{
            setVRModeEnabled(true);
        }
    }

    public void addCardboardEventListener(CardboardEventListener listener){
        listeners.add(listener);
    }

    public void notifyTouchEvent(MotionEvent e){
        for(CardboardEventListener listener : listeners){
            listener.onCardboardTouch(e);
        }
    }

}