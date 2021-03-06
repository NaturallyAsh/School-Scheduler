package com.example.ashleighwilson.schoolscheduler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.LinearLayout.LayoutParams;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.ashleighwilson.schoolscheduler.notes.OnDrawChangedListener;
import com.example.ashleighwilson.schoolscheduler.notes.SketchView;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.feio.android.checklistview.utils.AlphaManager;

public class SketchFragment extends Fragment implements OnDrawChangedListener
{
    private static final String TAG = SketchFragment.class.getSimpleName();

    @BindView(R.id.sketch_stroke)
    ImageView stroke;
    @BindView(R.id.sketch_eraser)
    ImageView eraser;
    @BindView(R.id.drawing)
    SketchView mSketchView;
    @BindView(R.id.sketch_undo)
    ImageView undo;
    @BindView(R.id.sketch_redo)
    ImageView redo;
    @BindView(R.id.sketch_erase)
    ImageView erase;
    private int seekBarStrokeProgress, seekBarEraserProgress;
    private View popupLayout, popupEraserLayout;
    private ImageView strokeImageView, eraserImageView;
    private int size;
    private ColorPicker mColorPicker;
    private int oldColor;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_sketch, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getNotesActivity().getToolbar().setNavigationOnClickListener(v -> getNotesActivity().onBackPressed());
        //getNotesActivity().getSupportActionBar();

        mSketchView.setOnDrawChangedListener(this);

        Uri baseUri = getArguments().getParcelable("base");
        if (baseUri != null)
        {
            Bitmap bmp;
            try{
                bmp = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(baseUri));
                mSketchView.setBackgroundBitmap(getActivity(), bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (getNotesActivity().getSupportActionBar() != null)
        {
            getNotesActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
            getNotesActivity().getSupportActionBar().setTitle("Sketch");
            getNotesActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        stroke.setOnClickListener(v -> {
            if (mSketchView.getMode() == SketchView.STROKE) {
                showPopup(v, SketchView.STROKE);
            } else {
                mSketchView.setMode(SketchView.STROKE);
                AlphaManager.setAlpha(eraser, 0.4f);
                AlphaManager.setAlpha(stroke, 1f);
            }
        });

        AlphaManager.setAlpha(eraser, 0.4f);
        eraser.setOnClickListener(v -> {
            if (mSketchView.getMode() == SketchView.ERASER) {
                showPopup(v, SketchView.ERASER);
            } else {
                mSketchView.setMode(SketchView.ERASER);
                AlphaManager.setAlpha(stroke, 0.4f);
                AlphaManager.setAlpha(eraser, 1f);
            }
        });

        undo.setOnClickListener(v -> mSketchView.undo());
        redo.setOnClickListener(v -> mSketchView.redo());
        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForErase();
            }
            private void askForErase()
            {
                new MaterialDialog.Builder(getActivity())
                        .content("Erase sketch?")
                        .positiveText("Confirm")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                mSketchView.erase();
                            }
                        });
            }
        });

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupLayout = inflater.inflate(R.layout.popup_sketch_stroke, null);
        LayoutInflater inflateEraser = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupEraserLayout = inflateEraser.inflate(R.layout.popup_sketch_eraser, null);

        strokeImageView = (ImageView) popupLayout.findViewById(R.id.stroke_circle);
        final Drawable circleDrawable = getResources().getDrawable(R.drawable.circle2);
        size = circleDrawable.getIntrinsicWidth();

        eraserImageView = (ImageView) popupEraserLayout.findViewById(R.id.stroke_circle);
        size = circleDrawable.getIntrinsicWidth();

        setSeekbarProgress(SketchView.DEFAULT_STROKE_SIZE, SketchView.STROKE);
        setSeekbarProgress(SketchView.DEFAULT_ERASER_SIZE, SketchView.ERASER);

        mColorPicker = (ColorPicker) popupLayout.findViewById(R.id.stroke_color_picker);
        mColorPicker.addSVBar((SVBar) popupLayout.findViewById(R.id.svbar));
        mColorPicker.addOpacityBar((OpacityBar) popupLayout.findViewById(R.id.opacitybar));
        mColorPicker.setOnColorChangedListener(mSketchView::setStrokeColor);
        mColorPicker.setColor(mSketchView.getStrokeColor());
        mColorPicker.setOldCenterColor(mSketchView.getStrokeColor());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    public void save()
    {
        Bitmap bitmap = mSketchView.getBitmap();
        if (bitmap != null)
        {
            try{
                Uri uri = getArguments().getParcelable(MediaStore.EXTRA_OUTPUT);
                File bitmapFile = new File(uri.getPath());
                FileOutputStream out = new FileOutputStream(bitmapFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                if (bitmapFile.exists()) {
                    getNotesActivity().sketchUri = uri;
                } else {
                    //getNotesActivity().showMessage("Error", )
                    Log.i(TAG, "Error");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showPopup(View anchor, final int eraserOrStroke)
    {
        boolean isErasing = eraserOrStroke == SketchView.ERASER;

        oldColor = mColorPicker.getColor();

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        PopupWindow popup = new PopupWindow(getActivity());
        popup.setContentView(isErasing ? popupEraserLayout : popupLayout);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOnDismissListener(() -> {
            if (mColorPicker.getColor() != oldColor)
                mColorPicker.setOldCenterColor(oldColor);
        });

        popup.setBackgroundDrawable(null);
        popup.showAsDropDown(anchor);

        SeekBar mSeekBar;
        mSeekBar = (SeekBar) (isErasing ? popupEraserLayout
                .findViewById(R.id.stroke_seekbar) : popupLayout
                .findViewById(R.id.stroke_seekbar));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSeekbarProgress(progress, eraserOrStroke);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        int progress = isErasing ? seekBarEraserProgress : seekBarStrokeProgress;
        mSeekBar.setProgress(progress);
    }

    protected void setSeekbarProgress(int progress, int eraserOrStroke)
    {
        int calcProgress = progress > 1 ? progress : 1;

        int newSize = Math.round((size / 100f) * calcProgress);
        int offset = (size - newSize) / 2;

        LayoutParams lp = new LayoutParams(newSize, newSize);
        lp.setMargins(offset, offset, offset, offset);

        if (eraserOrStroke == SketchView.STROKE) {
            strokeImageView.setLayoutParams(lp);
            seekBarStrokeProgress = progress;
        } else {
            eraserImageView.setLayoutParams(lp);
            seekBarEraserProgress = progress;
        }

        mSketchView.setSize(newSize, eraserOrStroke);
    }

    @Override
    public void onDrawChanged()
    {
        if (mSketchView.getPaths().size() > 0)
            AlphaManager.setAlpha(undo, 1f);
        else
            AlphaManager.setAlpha(undo, 0.4f);
        if (mSketchView.getUndoneCount() > 0)
            AlphaManager.setAlpha(redo, 1f);
        else
            AlphaManager.setAlpha(redo, 0.4f);
    }

    private NotesActivity getNotesActivity()
    {
        return (NotesActivity) getActivity();
    }

}
