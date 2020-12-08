package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Path;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class PathBased extends DrawableObject{

    private Path path;

    public PathBased() {
    }

    public PathBased(Path path) {
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
    public boolean intersects(DrawableObject drawableObject) {
        if (drawableObject instanceof PathBased) {
            Path intersection = new Path();
            intersection.op(this.path, ((PathBased) drawableObject).getPath(), Path.Op.INTERSECT);
            return !intersection.isEmpty();
        }
        return false;
    }

    @Override
    public void initialize(float x, float y) {
        path = new Path();
        path.moveTo(x, y);
    }

    @Override
    public void change(float x, float y) {
        path.lineTo(x, y);
    }
}
