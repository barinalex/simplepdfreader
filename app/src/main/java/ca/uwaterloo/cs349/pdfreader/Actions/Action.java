package ca.uwaterloo.cs349.pdfreader.Actions;

import android.view.View;

import ca.uwaterloo.cs349.pdfreader.Model;

public class Action implements Act{
    private int pageNumber;

    public Action(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public void doAction(Model model) {

    }

    @Override
    public void undoAction(Model model) {

    }
}
