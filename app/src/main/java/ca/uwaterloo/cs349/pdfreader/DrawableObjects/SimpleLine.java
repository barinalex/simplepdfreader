package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class SimpleLine extends Line{
    public SimpleLine() {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        setPaint(paint);
    }

    public SimpleLine(Path path) {
        this();
        setPath(path);
    }

}
