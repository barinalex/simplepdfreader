package ca.uwaterloo.cs349.pdfreader;

import android.util.Log;
import java.util.Observable;
import java.util.Observer;

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

    /**
     * Model Constructor:
     * - Init member variables
     */
    Model() {
        pageCounter = 0;
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

    /**
     * Increment pageCounter by 1
     */
    public void incrementPageCounter()
    {
        pageCounter++;
        Log.d("DEMO", "Model: increment counter to " + pageCounter);

        // Observable API
        setChanged();
        notifyObservers();
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
