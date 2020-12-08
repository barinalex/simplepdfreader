package ca.uwaterloo.cs349.pdfreader.Actions;

import android.view.View;

import ca.uwaterloo.cs349.pdfreader.Model;

public interface Act {
    public void doAction(Model model);
    public void undoAction(Model model);
}
