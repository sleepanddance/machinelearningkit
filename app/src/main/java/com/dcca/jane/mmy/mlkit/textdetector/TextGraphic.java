/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dcca.jane.mmy.mlkit.textdetector;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.dcca.jane.mmy.mlkit.graphic.GraphicOverlay;
import com.dcca.jane.mmy.mlkit.graphic.GraphicOverlay.Graphic;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.Text.Element;
import com.google.mlkit.vision.text.Text.Line;
import com.google.mlkit.vision.text.Text.TextBlock;

import java.util.Arrays;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class TextGraphic extends Graphic {

  private static final String TAG = "TextGraphic";

  private static final int TEXT_COLOR = Color.BLACK;
  private static final int MARKER_COLOR = Color.WHITE;
  private static final float TEXT_SIZE = 54.0f;
  private static final float STROKE_WIDTH = 4.0f;

  private final Paint rectPaint;
  private final Paint textPaint;
  private final Paint labelPaint;
  private final Text text;
  private final Boolean shouldGroupTextInBlocks;

  TextGraphic(GraphicOverlay overlay, Text text, Boolean shouldGroupTextInBlocks) {
    super(overlay);

    this.text = text;
    this.shouldGroupTextInBlocks = shouldGroupTextInBlocks;

    rectPaint = new Paint();
    rectPaint.setColor(MARKER_COLOR);
    rectPaint.setStyle(Paint.Style.STROKE);
    rectPaint.setStrokeWidth(STROKE_WIDTH);

    textPaint = new Paint();
    textPaint.setColor(TEXT_COLOR);
    textPaint.setTextSize(TEXT_SIZE);

    labelPaint = new Paint();
    labelPaint.setColor(MARKER_COLOR);
    labelPaint.setStyle(Paint.Style.FILL);
    // Redraw the overlay, as this graphic has been added.
    postInvalidate();
  }

  /** Draws the text block annotations for position, size, and raw value on the supplied canvas. */
  @Override
  public void draw(Canvas canvas) {
    Log.d(TAG, "Text is: " + text.getText());
    for (TextBlock textBlock : text.getTextBlocks()) {
      // Renders the text at the bottom of the box.
      Log.d(TAG, "TextBlock text is: " + textBlock.getText());
      Log.d(TAG, "TextBlock boundingbox is: " + textBlock.getBoundingBox());
      Log.d(TAG, "TextBlock cornerpoint is: " + Arrays.toString(textBlock.getCornerPoints()));
      if (shouldGroupTextInBlocks) {
        drawText(
            textBlock.getText(),
            new RectF(textBlock.getBoundingBox()),
            TEXT_SIZE * textBlock.getLines().size() + 2 * STROKE_WIDTH,
            canvas);
      } else {
        for (Line line : textBlock.getLines()) {
          Log.d(TAG, "Line text is: " + line.getText());
          Log.d(TAG, "Line boundingbox is: " + line.getBoundingBox());
          Log.d(TAG, "Line cornerpoint is: " + Arrays.toString(line.getCornerPoints()));
          drawText(
              line.getText(),
              new RectF(line.getBoundingBox()),
              TEXT_SIZE + 2 * STROKE_WIDTH,
              canvas);

          for (Element element : line.getElements()) {
            Log.d(TAG, "Element text is: " + element.getText());
            Log.d(TAG, "Element boundingbox is: " + element.getBoundingBox());
            Log.d(TAG, "Element cornerpoint is: " + Arrays.toString(element.getCornerPoints()));
            Log.d(TAG, "Element language is: " + element.getRecognizedLanguage());
          }
        }
      }
    }
  }

  private void drawText(String text, RectF rect, float textHeight, Canvas canvas) {
    // If the image is flipped, the left will be translated to right, and the right to left.
    float x0 = translateX(rect.left);
    float x1 = translateX(rect.right);
    rect.left = min(x0, x1);
    rect.right = max(x0, x1);
    rect.top = translateY(rect.top);
    rect.bottom = translateY(rect.bottom);
    canvas.drawRect(rect, rectPaint);
    float textWidth = textPaint.measureText(text);
    canvas.drawRect(
        rect.left - STROKE_WIDTH,
        rect.top - textHeight,
        rect.left + textWidth + 2 * STROKE_WIDTH,
        rect.top,
        labelPaint);
    // Renders the text at the bottom of the box.
    canvas.drawText(text, rect.left, rect.top - STROKE_WIDTH, textPaint);
  }
}
