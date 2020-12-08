package ca.uwaterloo.cs349.pdfreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.util.Observable;
import java.util.Observer;

import ca.uwaterloo.cs349.pdfreader.DrawableObjects.DrawableObject;
import ca.uwaterloo.cs349.pdfreader.DrawableObjects.HighlightLine;
import ca.uwaterloo.cs349.pdfreader.DrawableObjects.PathBased;
import ca.uwaterloo.cs349.pdfreader.DrawableObjects.SimpleLine;

@SuppressLint("AppCompatCustomView")
public class PDFimage extends ImageView implements Observer {

    final String LOGNAME = "pdf_image";

    private Model model;

    // image to display
    private Bitmap bitmap;

    // constructor
    public PDFimage(Context context, Model model) {
        super(context);
        this.model = model;
        model.addObserver(this);
    }


    @Override
    public void update(Observable o, Object arg) {
    }

    // capture touch events (down/move/up) to create a path
    // and use that to create a stroke that we can draw

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (model.getMode() == Model.Mode.READ)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(LOGNAME, "Action down");
                model.initializeObject(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(LOGNAME, "Action move");
                model.changeObject(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                Log.d(LOGNAME, "Action up");
                model.finishObject();
                break;
        }
        return true;
    }

    // set image as background
    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
        if (bitmap != null) {
            this.setImageBitmap(bitmap);
        }
        // draw lines over it
        for (DrawableObject drawableObject : model.getDrawableObjects().get(model.getPageCounter())){
            if (drawableObject instanceof HighlightLine)
                canvas.drawPath(((HighlightLine) drawableObject).getPath(), drawableObject.getPaint());
        }

        DrawableObject dO = model.getNewDrawableObject();
        if (dO != null && dO instanceof HighlightLine && model.getMode() == Model.Mode.HIGHLIGHT){
            canvas.drawPath(((HighlightLine)dO).getPath(), ((HighlightLine)dO).getPaint());
        }

        for (DrawableObject drawableObject : model.getDrawableObjects().get(model.getPageCounter())){
            if (drawableObject instanceof SimpleLine)
                canvas.drawPath(((SimpleLine) drawableObject).getPath(), drawableObject.getPaint());
        }
        if (dO != null && dO instanceof PathBased && model.getMode() != Model.Mode.HIGHLIGHT){
            canvas.drawPath(((PathBased)dO).getPath(), ((PathBased)dO).getPaint());
        }

        super.onDraw(canvas);
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
    }
}
