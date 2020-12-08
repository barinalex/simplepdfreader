package ca.uwaterloo.cs349.pdfreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import ca.uwaterloo.cs349.pdfreader.DrawableObjects.DrawableObject;
import ca.uwaterloo.cs349.pdfreader.DrawableObjects.HighlightLine;
import ca.uwaterloo.cs349.pdfreader.DrawableObjects.Line;
import ca.uwaterloo.cs349.pdfreader.DrawableObjects.SimpleLine;

@SuppressLint("AppCompatCustomView")
public class PDFimage extends ImageView implements Observer {

    final String LOGNAME = "pdf_image";

    private Model model;

    // drawing path
    private Path path = null;

    public ArrayList<ArrayList<Path>> lines = new ArrayList<>();
    public ArrayList<ArrayList<Path>> highlightLines = new ArrayList<>();

    // image to display
    private Bitmap bitmap;
    private Paint drawPaint = new Paint();
    private Paint highlightPaint = new Paint();
    private Paint erasePaint = new Paint();

    // constructor
    public PDFimage(Context context, Model model) {
        super(context);
        this.model = model;
        model.addObserver(this);
        drawPaint.setColor(Color.BLUE);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeWidth(5);
        highlightPaint.setColor(Color.YELLOW);
        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setStrokeWidth(40);
        erasePaint.setColor(Color.LTGRAY);
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
                path = new Path();
                path.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(LOGNAME, "Action move");
                path.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                Log.d(LOGNAME, "Action up");
                model.addAction(path);
                path = null;
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

        if (path != null && model.getMode() == Model.Mode.HIGHLIGHT){
            canvas.drawPath(path, highlightPaint);
        }

        for (DrawableObject drawableObject : model.getDrawableObjects().get(model.getPageCounter())){
            if (drawableObject instanceof SimpleLine)
                canvas.drawPath(((SimpleLine) drawableObject).getPath(), drawableObject.getPaint());
        }

        if (path != null && model.getMode() != Model.Mode.HIGHLIGHT){
            Paint p = (model.getMode() == Model.Mode.DRAW) ? drawPaint : erasePaint;
            canvas.drawPath(path, p);
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
