package com.example.detectingfaceapp;

import io.realm.RealmObject;

public class Pointt extends RealmObject {
    private Float x;
    private Float y;

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }
}
