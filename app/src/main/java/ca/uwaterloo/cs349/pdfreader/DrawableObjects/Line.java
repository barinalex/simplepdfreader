package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Line extends DrawableObject{
    private Path path;

    public Line(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean intersects(Path path) {
        Path intersection = new Path();
        intersection.op(this.path, path, Path.Op.INTERSECT);
        return !intersection.isEmpty();
    }
}
