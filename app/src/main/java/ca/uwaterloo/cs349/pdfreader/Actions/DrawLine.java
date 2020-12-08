package ca.uwaterloo.cs349.pdfreader.Actions;

import android.view.View;

import ca.uwaterloo.cs349.pdfreader.DrawableObjects.Line;
import ca.uwaterloo.cs349.pdfreader.Model;

public class DrawLine extends Action{
    private Line line;

    public DrawLine(Line line, int pageNumber) {
        super(pageNumber);
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    @Override
    public void doAction(Model model) {
        model.getDrawableObjects().get(getPageNumber()).add(line);
    }

    @Override
    public void undoAction(Model model) {
        model.getDrawableObjects().get(getPageNumber()).remove(line);
    }
}
