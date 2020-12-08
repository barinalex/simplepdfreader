package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Paint;

public class DrawableObject implements drawableInterface {
    private Paint paint;

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public boolean intersects(DrawableObject drawableObject) {
        return false;
    }

    @Override
    public void initialize(float x, float y) {

    }

    @Override
    public void change(float x, float y) {

    }
}
