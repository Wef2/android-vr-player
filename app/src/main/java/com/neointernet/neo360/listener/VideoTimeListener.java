package com.neointernet.neo360.listener;

/**
 * Created by neo-202 on 2016-03-23.
 */
public interface VideoTimeListener {
    void onVideoInit(int length);
    void listenTime(int time);
}
