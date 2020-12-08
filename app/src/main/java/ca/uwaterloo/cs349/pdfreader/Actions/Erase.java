package ca.uwaterloo.cs349.pdfreader.Actions;

import android.view.View;

import java.util.ArrayList;

import ca.uwaterloo.cs349.pdfreader.DrawableObjects.DrawableObject;
import ca.uwaterloo.cs349.pdfreader.Model;

public class Erase extends Action{
    private ArrayList<DrawableObject> drawableObjects;

    public Erase(ArrayList<DrawableObject> drawableObjects, int pageNumber) {
        super(pageNumber);
        this.drawableObjects = drawableObjects;
    }

    @Override
    public void doAction(Model model) {
        for (DrawableObject drawableObject : drawableObjects){
            model.getDrawableObjects().get(getPageNumber()).remove(drawableObject);
        }
    }

    @Override
    public void undoAction(Model model) {
        for (DrawableObject drawableObject : drawableObjects){
            model.getDrawableObjects().get(getPageNumber()).add(drawableObject);
        }
    }
}
