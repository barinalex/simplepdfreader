package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Path;

public interface drawableInterface {
    public boolean intersects(DrawableObject drawableObject);
    public void initialize(float x, float y);
    public void change(float x, float y);
}
