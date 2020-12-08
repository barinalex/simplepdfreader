package ca.uwaterloo.cs349.pdfreader;

import android.graphics.Path;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import ca.uwaterloo.cs349.pdfreader.Actions.Action;
import ca.uwaterloo.cs349.pdfreader.Actions.DrawLine;
import ca.uwaterloo.cs349.pdfreader.Actions.Erase;
import ca.uwaterloo.cs349.pdfreader.DrawableObjects.DrawableObject;
import ca.uwaterloo.cs349.pdfreader.DrawableObjects.HighlightLine;
import ca.uwaterloo.cs349.pdfreader.DrawableObjects.SimpleLine;

/**
 * sample code created by J. J. Hartmann on 11/19/2017. was used from https://git.uwaterloo.ca/cs349-public/1209/-/blob/master/08.Android/5.MVC_1/app/src/main/java/com/uwaterloo/cs349/mvc1/Model.java
 */

/**
 * Class Model
 * - Stores a persistent state for the application.
 */
public class Model extends Observable
{
    // Private Variables
    private int pageCounter;
    private int pagesAmount;

    //new model
    private int MAXDOSTACKSIZE = 20;
    public ArrayList<Action> doStack = new ArrayList<>();
    public ArrayList<Action> redoStack = new ArrayList<>();
    public ArrayList<ArrayList<DrawableObject>> drawableObjects = new ArrayList<>();

    public enum Mode{
        DRAW,
        HIGHLIGHT,
        ERASE,
        READ
    }
    private Mode mode = Mode.READ;

    //logs
    public final int logSize = 10;

    public ArrayList<Mode> log = new ArrayList<>();
    public ArrayList<Mode> redolog = new ArrayList<>();

    /**
     * Model Constructor:
     * - Init member variables
     */
    Model() {
        pageCounter = 0;
        drawableObjects.add(new ArrayList<DrawableObject>());
    }

    public Mode getMode() {
        return mode;
    }

    public void switchMode(Mode mode) {
        if (this.mode == mode){
            this.mode = Mode.READ;
        }
        else {
            this.mode = mode;
        }
        setChanged();
        notifyObservers();
    }

    public ArrayList<ArrayList<DrawableObject>> getDrawableObjects() {
        return drawableObjects;
    }

    /**
     * Get pageCounter Values
     * @return Current value mCounter
     */
    public int getPageCounter()
    {
        return pageCounter;
    }

    /**
     * Set pageCounter Value
     * @param i
     * -- Value to set Counter
     */
    public void setPageCounter(int i)
    {
        Log.d("DEMO", "Model: set counter to " + pageCounter);
        this.pageCounter = i;
    }

    public int getPagesAmount() {
        return pagesAmount;
    }

    public void setPagesAmount(int pagesAmount) {
        this.pagesAmount = pagesAmount;
    }

    /**
     * Increment pageCounter by 1
     */
    public void incrementPageCounter()
    {
        if (pageCounter + 1 < pagesAmount) {
            pageCounter++;
            if (pageCounter >= drawableObjects.size()) {
                drawableObjects.add(new ArrayList<DrawableObject>());
            }
            Log.d("DEMO", "Model: increment counter to " + pageCounter);

            // Observable API
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Decrement pageCounter by 1
     */
    public void decrementPageCounter()
    {
        if (pageCounter > 0) {
            pageCounter--;
            Log.d("DEMO", "Model: increment counter to " + pageCounter);

            // Observable API
            setChanged();
            notifyObservers();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addAction(Path path){
        switch (mode){
            case DRAW:
                SimpleLine sl = new SimpleLine(path);
                DrawLine dsl = new DrawLine(sl, getPageCounter());
                dsl.doAction(this);
                doStack.add(dsl);
                break;
            case HIGHLIGHT:
                HighlightLine hl = new HighlightLine(path);
                DrawLine dhl = new DrawLine(hl, pageCounter);
                dhl.doAction(this);
                doStack.add(dhl);
                break;
            case ERASE:
                ArrayList<DrawableObject> toDelete = new ArrayList<>();
                for (DrawableObject drawableObject: drawableObjects.get(getPageCounter())){
                    if (drawableObject.intersects(path))
                        toDelete.add(drawableObject);
                }
                Erase erase = new Erase(toDelete, getPageCounter());
                erase.doAction(this);
                doStack.add(erase);

                break;
        }
        redoStack.clear();
        while (doStack.size() > MAXDOSTACKSIZE)
            doStack.remove(0);
    }


    public void undoClicked(){
        if (!doStack.isEmpty()) {
            Action action = doStack.get(doStack.size() - 1);
            action.undoAction(this);
            doStack.remove(doStack.size() - 1);
            redoStack.add(action);
        }
    }

    public void redoClicked(){
        if (!redoStack.isEmpty()) {
            Action action = redoStack.get(redoStack.size() - 1);
            action.doAction(this);
            redoStack.remove(redoStack.size() - 1);
            doStack.add(action);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Observable Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Helper method to make it easier to initialize all observers
     */
    public void initObservers()
    {
        setChanged();
        notifyObservers();
    }

    /**
     * Deletes an observer from the set of observers of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     *
     * @param o the observer to be deleted.
     */
    @Override
    public synchronized void deleteObserver(Observer o)
    {
        super.deleteObserver(o);
    }

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param o an observer to be added.
     * @throws NullPointerException if the parameter o is null.
     */
    @Override
    public synchronized void addObserver(Observer o)
    {
        super.addObserver(o);
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    @Override
    public synchronized void deleteObservers()
    {
        super.deleteObservers();
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyObservers(null)</tt></blockquote>
     *
     * @see Observable#clearChanged()
     * @see Observable#hasChanged()
     * @see Observer#update(Observable, Object)
     */
    @Override
    public void notifyObservers()
    {
        super.notifyObservers();
    }
}
