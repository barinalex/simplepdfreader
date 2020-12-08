package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class EraseShape extends PathBased{
    public EraseShape() {
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        setPaint(paint);
    }

    public EraseShape(Path path) {
        this();
        setPath(path);
    }
}
