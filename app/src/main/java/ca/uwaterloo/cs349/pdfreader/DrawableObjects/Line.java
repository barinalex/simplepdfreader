package ca.uwaterloo.cs349.pdfreader.DrawableObjects;

import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Line extends PathBased{
    public Line() {
    }

    public Line(Path path) {
        super(path);
    }
}
