package ca.uwaterloo.cs349.pdfreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// PDF sample code from
// https://medium.com/@chahat.jain0/rendering-a-pdf-document-in-android-activity-fragment-using-pdfrenderer-442462cb8f9a
// Issues about cache etc. are not at all obvious from documentation, so read this carefully.

public class MainActivity extends AppCompatActivity {

    private Model model;

    final String LOGNAME = "pdf_viewer";
    final String FILENAME = "shannon1948.pdf";
    final int FILERESID = R.raw.shannon1948;

    // manage the pages of the PDF, see below
    PdfRenderer pdfRenderer;
    private ParcelFileDescriptor parcelFileDescriptor;
    private PdfRenderer.Page currentPage;

    // custom ImageView class that captures strokes and draws them over the image
    private LinearLayout pdflayout;
    private PDFimage pageImage;
    private TextView titleView;
    private TextView pageView;

    //last touch coordinates;
    private float prevX;
    private float prevY;

    private long startTime;
    private final long MAXSCROLLTIME = 150;
    private final long MINSCROLLTIME = 40;
    private final int MINSCROLLDISTANCE = 40;

    //zoom
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    //scroll
    private GestureDetector gestureDetector;
    private boolean scrolled = false;

    //screen size
    private int screenwidth;
    private int screenheight;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new Model();

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenwidth = metrics.widthPixels;
        screenheight = metrics.heightPixels;

        pdflayout = findViewById(R.id.pdfLayout);

        pageImage = new PDFimage(this, model);
        pdflayout.addView(pageImage);
        pdflayout.setEnabled(true);
        pageImage.setMinimumWidth(screenwidth);
        pageImage.setMinimumHeight(screenheight);


        ButtonView buttonView = new ButtonView(this, model);
        ViewGroup buttonViewGroup = (ViewGroup) findViewById(R.id.buttonViewGroup);
        buttonViewGroup.addView(buttonView);

        // open page 0 of the PDF
        // it will be displayed as an image in the pageImage (above)
        try {
            openRenderer(this);
            showPage(model.getPageCounter());
        } catch (IOException exception) {
            Log.d(LOGNAME, "Error opening PDF");
        }
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        gestureDetector = new GestureDetector(this, new MyGestureListener());

        titleView = findViewById(R.id.title);
        titleView.setText(FILENAME);
        pageView = findViewById(R.id.page);
        pageView.setText((model.getPageCounter() + 1) + "/" + pdfRenderer.getPageCount());

        model.setPagesAmount(pdfRenderer.getPageCount());
        model.initObservers();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            closeRenderer();
        } catch (IOException ex) {
            Log.d(LOGNAME, "Unable to close PDF renderer");
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            scaleFactor = Math.max(0.3f, Math.min(scaleFactor, 5.0f));
            pageImage.setScaleX(scaleFactor);
            pageImage.setScaleY(scaleFactor);
            return true;
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            scaleFactor = 1;
            pageImage.setScaleX(scaleFactor);
            pageImage.setScaleY(scaleFactor);
            pageImage.setX(0);
            pageImage.setY(0);
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (System.currentTimeMillis() - startTime > MAXSCROLLTIME)
                return true;
            if (System.currentTimeMillis() - startTime > MINSCROLLTIME) {
                if (distanceY > MINSCROLLDISTANCE) {
                    model.incrementPageCounter();
                    showPage(model.getPageCounter());
                    startTime = System.currentTimeMillis();
                    scrolled = true;
                } else if (distanceY < -MINSCROLLDISTANCE) {
                    model.decrementPageCounter();
                    showPage(model.getPageCounter());
                    startTime = System.currentTimeMillis();
                    scrolled = true;
                }
                pageView.setText((model.getPageCounter() + 1) + "/" + pdfRenderer.getPageCount());
            }
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                prevX = event.getX();
                prevY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!scrolled && System.currentTimeMillis() - startTime > MAXSCROLLTIME) {
                    pageImage.setX(pageImage.getX() + (event.getX() - prevX));
                    pageImage.setY(pageImage.getY() + (event.getY() - prevY));
                }
                prevX = event.getX();
                prevY = event.getY();
                break;
                case MotionEvent.ACTION_UP:
                    scrolled = false;
                    break;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file = new File(context.getCacheDir(), FILENAME);
        if (!file.exists()) {
            // pdfRenderer cannot handle the resource directly,
            // so extract it into the local cache directory.
            InputStream asset = this.getResources().openRawResource(FILERESID);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

        // capture PDF data
        // all this just to get a handle to the actual PDF representation
        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
        }
    }

    // do this before you quit!
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            currentPage.close();
        }
        pdfRenderer.close();
        parcelFileDescriptor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        // Close the current page before opening another one.
        if (null != currentPage) {
            currentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);

        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        // Display the page
        pageImage.setImage(bitmap);
    }
}
