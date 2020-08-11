package com.example.detectingfaceapp;

import android.graphics.PointF;

import com.google.mlkit.vision.face.FaceContour;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FaceDetectionObject extends RealmObject {

    @PrimaryKey
    private String name;
    private RealmList<Pointt> leftEyeContour;
    private RealmList<Pointt> rightEyeContour;
    private RealmList<Pointt> noseBottomContour;
    private RealmList<Pointt> upperLipTopContour;
    private RealmList<Pointt> lowerLipBottomContour;
    private RealmList<Pointt> faceContour;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Pointt> getLeftEyeContour() {
        return leftEyeContour;
    }

    public void setLeftEyeContour(RealmList<Pointt> leftEyeContour) {
        this.leftEyeContour = leftEyeContour;
    }

    public RealmList<Pointt> getRightEyeContour() {
        return rightEyeContour;
    }

    public void setRightEyeContour(RealmList<Pointt> rightEyeContour) {
        this.rightEyeContour = rightEyeContour;
    }

    public RealmList<Pointt> getNoseBottomContour() {
        return noseBottomContour;
    }

    public void setNoseBottomContour(RealmList<Pointt> noseBottomContour) {
        this.noseBottomContour = noseBottomContour;
    }

    public RealmList<Pointt> getUpperLipTopContour() {
        return upperLipTopContour;
    }

    public void setUpperLipTopContour(RealmList<Pointt> upperLipTopContour) {
        this.upperLipTopContour = upperLipTopContour;
    }

    public RealmList<Pointt> getLowerLipBottomContour() {
        return lowerLipBottomContour;
    }

    public void setLowerLipBottomContour(RealmList<Pointt> lowerLipBottomContour) {
        this.lowerLipBottomContour = lowerLipBottomContour;
    }

    public RealmList<Pointt> getFaceContour() {
        return faceContour;
    }

    public void setFaceContour(RealmList<Pointt> faceContour) {
        this.faceContour = faceContour;
    }
}
