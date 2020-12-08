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

@SuppressLint("AppCompatCustomView")
public class PDFimage extends ImageView {

    final String LOGNAME = "pdf_image";

    // drawing path
    private Path path = null;
    private ArrayList<ArrayList<Pair<Path, Paint>>> paths = new ArrayList<>();

    private ArrayList<Pair<Integer, Pair<Path, Paint>>> undopaths = new ArrayList<>();
    private ArrayList<ArrayList<Pair<Integer, Pair<Path, Paint>>>> undoerasestack = new ArrayList<>();

    private ArrayList<Pair<Integer, Pair<Path, Paint>>> redopaths = new ArrayList<>();
    private ArrayList<ArrayList<Pair<Integer, Pair<Path, Paint>>>> redoerasestack = new ArrayList<>();

    private int pageindex = 0;


    // image to display
    private Bitmap bitmap;
    private Paint drawpaint = new Paint();
    private Paint highlightpaint = new Paint();
    private Paint erasePaint = new Paint();

    //focused on/off
    private boolean focused = false;
    private boolean draw = true;
    private boolean highlight = false;
    private boolean erase = false;

    //logs
    private final int logSize = 10;
    private enum operation{
        DRAW,
        ERASE
    }
    private ArrayList<operation> log = new ArrayList<>();
    private ArrayList<operation> redolog = new ArrayList<>();



    // constructor
    public PDFimage(Context context) {
        super(context);
        paths.add(new ArrayList<Pair<Path, Paint>>());
        drawpaint.setColor(Color.BLUE);
        drawpaint.setStyle(Paint.Style.STROKE);
        drawpaint.setStrokeWidth(5);
        highlightpaint.setColor(Color.YELLOW);
        highlightpaint.setStyle(Paint.Style.STROKE);
        highlightpaint.setStrokeWidth(40);
        erasePaint.setColor(Color.LTGRAY);
    }

    // capture touch events (down/move/up) to create a path
    // and use that to create a stroke that we can draw

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!focused)
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
                if (draw){
                    Pair p = new Pair(path, drawpaint);
                    paths.get(pageindex).add(p);
                    log.add(operation.DRAW);
                    undopaths.add(new Pair(pageindex, p));
                }else if (highlight){
                    Pair p = new Pair(path, highlightpaint);
                    paths.get(pageindex).add(p);
                    log.add(operation.DRAW);
                    undopaths.add(new Pair(pageindex, p));
                }
                else {
                    erase(path);
                    log.add(operation.ERASE);
                }
                path = null;
                redolog.clear();
                redopaths.clear();
                redoerasestack.clear();
                break;
        }
        while (log.size() > logSize)
            log.remove(0);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void erase(Path path){
        Path intersection = new Path();
        undoerasestack.add(new ArrayList());
        for (Pair<Path, Paint> pair : paths.get(pageindex)){
            intersection.op(pair.first, path, Path.Op.INTERSECT);
            if (!intersection.isEmpty()) {
                undoerasestack.get(undoerasestack.size() - 1).add(new Pair(pageindex, pair));
            }
        }
        for (Pair<Integer, Pair<Path, Paint>> pair : undoerasestack.get(undoerasestack.size() - 1)){
            paths.get(pair.first).remove(pair.second);
        }
        if (undoerasestack.size() > logSize)
            undoerasestack.remove(0);
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
        for (Pair<Path, Paint> pair : paths.get(pageindex)){
            if (pair.second == highlightpaint){
                canvas.drawPath(pair.first, pair.second);
            }
        }
        if (path != null && highlight){
            canvas.drawPath(path, highlightpaint);
        }
        for (Pair<Path, Paint> pair : paths.get(pageindex)){
            if (pair.second == drawpaint){
                canvas.drawPath(pair.first, pair.second);
            }
        }
        if (path != null && !highlight){
            Paint p = (draw) ? drawpaint : erasePaint;
            canvas.drawPath(path, p);
        }
        super.onDraw(canvas);
    }

    public void nextPage(){
        pageindex++;
        if (pageindex >= paths.size()) {
            paths.add(new ArrayList<Pair<Path, Paint>>());
        }
    }

    public void prevPage(){
        if (pageindex > 0)
            pageindex--;
    }

    public void drawClicked(){
        if (draw)
            focused = !focused;
        else{
            draw = true;
            highlight = false;
            erase = false;
            focused = true;
        }
    }

    public void highlightClicked(){
        if (highlight)
            focused = !focused;
        else {
            draw = false;
            highlight = true;
            erase = false;
            focused = true;
        }
    }

    public void eraseClicked(){
        if (erase)
            focused = !focused;
        else {
            draw = false;
            highlight = false;
            erase = true;
            focused = true;
        }
    }

    public void undoClicked(){
        if (!log.isEmpty()) {
            operation op = log.get(log.size() - 1);
            log.remove(log.size() - 1);
            if (op == operation.DRAW) {
                if (!undopaths.isEmpty()) {
                    Pair<Integer, Pair<Path, Paint>> p = undopaths.get(undopaths.size() - 1);
                    paths.get(p.first).remove(p.second);
                    undopaths.remove(undopaths.size() - 1);
                    redolog.add(operation.DRAW);
                    redopaths.add(p);
                }
            }
            else {
                if (!undoerasestack.isEmpty()){
                    redolog.add(operation.ERASE);
                    redoerasestack.add(undoerasestack.get(undoerasestack.size() - 1));
                    for (Pair<Integer, Pair<Path, Paint>> pair : undoerasestack.get(undoerasestack.size() - 1)){
                        paths.get(pair.first).add(pair.second);
                    }
                    undoerasestack.remove(undoerasestack.size() - 1);
                }
            }
        }
    }

    public void redoClicked(){
        if (!redolog.isEmpty()) {
            operation op = redolog.get(redolog.size() - 1);
            redolog.remove(redolog.size() - 1);
            if (op == operation.DRAW) {
                if (!redopaths.isEmpty()) {
                    Pair<Integer, Pair<Path, Paint>> p = redopaths.get(redopaths.size() - 1);
                    paths.get(p.first).add(p.second);
                    redopaths.remove(redopaths.size() - 1);
                    log.add(operation.DRAW);
                    undopaths.add(p);
                }
            }
            else {
                if (!redoerasestack.isEmpty()){
                    log.add(operation.ERASE);
                    undoerasestack.add(redoerasestack.get(redoerasestack.size() - 1));
                    for (Pair<Integer, Pair<Path, Paint>> pair : redoerasestack.get(redoerasestack.size() - 1)){
                        paths.get(pair.first).remove(pair.second);
                    }
                    redoerasestack.remove(redoerasestack.size() - 1);
                }
            }
        }
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    public int getPageindex() {
        return pageindex;
    }
}
