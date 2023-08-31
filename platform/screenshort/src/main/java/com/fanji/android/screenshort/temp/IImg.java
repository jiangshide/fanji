package com.fanji.android.screenshort.temp;

public interface IImg extends ImgPortrait {

    int getWidth();

    int getHeight();

    float getRotation();

    float getPivotX();

    float getPivotY();

    float getX();

    float getY();

    float getScale();

    void setScale(float scale);

    void addScale(float scale);
}
