package ca.uwaterloo.cs349.pdfreader;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Observable;
import java.util.Observer;

public class ButtonView extends LinearLayout implements Observer{

    private final Model model;

    private final Button drawlinebutton;
    private final Button highlightbutton;
    private final Button erasebutton;
    private final Button undobutton;
    private final Button redobutton;

    private int on = Color.WHITE;
    private int off = Color.BLACK;

    public ButtonView(Context context, final Model model) {
        super(context);
        View.inflate(context, R.layout.button_layout,this);
        this.model = model;
        model.addObserver(this);

        drawlinebutton = findViewById(R.id.drawlinebutton);
        highlightbutton = findViewById(R.id.highlightbutton);
        erasebutton = findViewById(R.id.erasebutton);
        undobutton = findViewById(R.id.undobutton);
        redobutton = findViewById(R.id.redobutton);

        drawlinebutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                model.switchMode(Model.Mode.DRAWLINE);
            }
        });
        highlightbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                model.switchMode(Model.Mode.HIGHLIGHT);
            }
        });
        erasebutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                model.switchMode(Model.Mode.ERASE);
            }
        });
        undobutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                model.undoClicked();
            }
        });
        redobutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                model.redoClicked();
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        drawlinebutton.setTextColor(off);
        highlightbutton.setTextColor(off);
        erasebutton.setTextColor(off);
        switch (model.getMode()){
            case DRAWLINE:
                drawlinebutton.setTextColor(on);
                break;
            case HIGHLIGHT:
                highlightbutton.setTextColor(on);
                break;
            case ERASE:
                erasebutton.setTextColor(on);
                break;
            case READ:
                break;
        }
    }
}
