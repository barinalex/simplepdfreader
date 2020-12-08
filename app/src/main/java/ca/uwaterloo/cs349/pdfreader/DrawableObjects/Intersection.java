package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Path;

public interface Intersection {
    public boolean intersects(Path path);
}
