package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Paint;
import android.graphics.Path;

public class DrawableObject implements Intersection{
    private Paint paint;

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public boolean intersects(Path path) {
        return false;
    }
}
