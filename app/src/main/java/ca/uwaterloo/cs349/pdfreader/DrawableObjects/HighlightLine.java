package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class HighlightLine extends Line{
    public HighlightLine(Path path) {
        super(path);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(40);
        setPaint(paint);
    }
}
