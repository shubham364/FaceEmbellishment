package com.google.firebase.samples.apps.mlkit.java.facedetection;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay.Graphic;

/** Graphic instance for rendering face contours graphic overlay view. */
public class FaceContourGraphic extends Graphic {

  private static final float BOX_STROKE_WIDTH = 5.0f;

  private final static Paint boxPaint = new Paint();

  private volatile FirebaseVisionFace firebaseVisionFace;


  public FaceContourGraphic(GraphicOverlay overlay, FirebaseVisionFace face) {
    super(overlay);

    this.firebaseVisionFace = face;

    boxPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    boxPaint.setPathEffect(new CornerPathEffect(1.0f));
  }

  public static void applyColor(int color) {
    boxPaint.setColor(color);
    boxPaint.setAlpha(70);
    boxPaint.setPathEffect(new CornerPathEffect(10));
    boxPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.LIGHTEN));
    boxPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.OVERLAY));
    boxPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.DST_IN));
    boxPaint.setMaskFilter(new EmbossMaskFilter(new float[] {0f, 1f, 1f}, 0.8f, 50f, 4f));
  }

  /** Draws the face annotations for position on the supplied canvas. */
  @Override
  public void draw(Canvas canvas) {
    FirebaseVisionFace face = firebaseVisionFace;
    if (face == null) {
      return;
    }

    //Arrays to store the points. Even number and odd number indexes represent 'x' and 'y' coordinates respectively.
    float[] upperLipTopPoints = new float[22];
    float[] upperLipBottomPoints = new float[18];
    //Array to store the points along which the path will be drawn.
    float[] upperLipPoints = new float[80];

    FirebaseVisionFaceContour contour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP);

    int i = 0;
    for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : contour.getPoints()) {
      float px = translateX(point.getX());
      float py = translateY(point.getY());
      upperLipTopPoints[i++] = px;
      upperLipTopPoints[i++] = py;
    }

    contour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM);

    i = 0;
    for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : contour.getPoints()) {
      float px = translateX(point.getX());
      float py = translateY(point.getY());
      upperLipBottomPoints[i++] = px;
      upperLipBottomPoints[i++] = py;
    }

    int in = 0;

    //Path's starting point.
    upperLipPoints[in++] = upperLipTopPoints[0];
    upperLipPoints[in++] = upperLipTopPoints[1];

    //Every point is stored twice. Once being the destination and once being the source of the line segment.
    for(i = 2; i < 22; i += 2) {
      upperLipPoints[in++] = upperLipTopPoints[i];
      upperLipPoints[in++] = upperLipTopPoints[i + 1];
      upperLipPoints[in++] = upperLipTopPoints[i];
      upperLipPoints[in++] = upperLipTopPoints[i + 1];
    }

    //For the path to be closed we need to complete a round around the lips and thus the starting point of the path for upperLipBottom is from the last point.
    //For reference please see the picture at "https://firebase.google.com/docs/ml-kit/images/examples/face_contours.svg" to understand the indexes used.
    for(int j = 16; j > -1; j -= 2) {
      upperLipPoints[in++] = upperLipBottomPoints[j];
      upperLipPoints[in++] = upperLipBottomPoints[j + 1];
      upperLipPoints[in++] = upperLipBottomPoints[j];
      upperLipPoints[in++] = upperLipBottomPoints[j + 1];
    }

    //Path's ending point.
    upperLipPoints[in++] = upperLipTopPoints[0];
    upperLipPoints[in] = upperLipTopPoints[1];

    Path path = new  Path();
    path.moveTo(upperLipPoints[0],upperLipPoints[1]);

    for(i = 2; i < 80; i += 2) {
      path.lineTo(upperLipPoints[i],upperLipPoints[i+1]);
    }

    canvas.drawPath(path,boxPaint);

    //Arrays to store the points. Even number and odd number indexes represent 'x' and 'y' coordinates respectively.
    float[] lowerLipTopPoints = new float[18];
    float[] lowerLipBottomPoints = new float[18];
    //Array to store the points along which the path will be drawn.
    float[] lowerLipPoints = new float[80];

    contour = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM);

    i = 0;
    for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : contour.getPoints()) {
      float px = translateX(point.getX());
      float py = translateY(point.getY());
      lowerLipBottomPoints[i++] = px;
      lowerLipBottomPoints[i++] = py;
    }

    contour = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP);

    i = 0;
    for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : contour.getPoints()) {
      float px = translateX(point.getX());
      float py = translateY(point.getY());
      lowerLipTopPoints[i++] = px;
      lowerLipTopPoints[i++] = py;
    }

    in = 0;
    //Path's starting point.
    lowerLipPoints[in++] = upperLipTopPoints[0];
    lowerLipPoints[in++] = upperLipTopPoints[1];

    //The path has started from the left side of the Lip.
    path.moveTo(upperLipTopPoints[0],upperLipTopPoints[1]);

    //Every point is stored twice. Once being the destination and once being the source of the line segment.
    //For reference please see the picture at "https://firebase.google.com/docs/ml-kit/images/examples/face_contours.svg" to understand the indexes used.
    for(int j = 16; j > -1; j -= 2) {
      lowerLipPoints[in++] = lowerLipTopPoints[j];
      lowerLipPoints[in++] = lowerLipTopPoints[j + 1];
      lowerLipPoints[in++] = lowerLipTopPoints[j];
      lowerLipPoints[in++] = lowerLipTopPoints[j + 1];
    }

    //The end point of lowerLipTop is connected with the end points of upperLipTop.
    lowerLipPoints[in++] = upperLipTopPoints[20];
    lowerLipPoints[in++] = upperLipTopPoints[21];
    lowerLipPoints[in++] = upperLipTopPoints[20];
    lowerLipPoints[in++] = upperLipTopPoints[21];

    for(i = 0; i < 18; i += 2) {
      lowerLipPoints[in++] = lowerLipBottomPoints[i];
      lowerLipPoints[in++] = lowerLipBottomPoints[i + 1];
      lowerLipPoints[in++] = lowerLipBottomPoints[i];
      lowerLipPoints[in++] = lowerLipBottomPoints[i + 1];
    }

    //Path's ending point.
    lowerLipPoints[in++] = upperLipTopPoints[0];
    lowerLipPoints[in] = upperLipTopPoints[1];

    for(i = 0; i < 80; i += 2) {
      path.lineTo(lowerLipPoints[i],lowerLipPoints[i+1]);
    }

    canvas.drawPath(path,boxPaint);

  }
}
