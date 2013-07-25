/**
 * 
 */
package com.archerabi.wheredoigo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * @author gautamichitteti
 * 
 */
public class DirectionPointerCanvas extends View {

	private Paint paint = new Paint();

	private float angle = 0;

	private int radius;

	private Matrix originalMatrix = null;

	private Bitmap arrowBitMap = null;
	
	public DirectionPointerCanvas(Context context) {
		super(context);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		if (originalMatrix == null) {
			originalMatrix = canvas.getMatrix();
		}
		if(arrowBitMap == null){
			arrowBitMap = BitmapFactory.decodeResource(getResources(),R.drawable.arrow);
			arrowBitMap = Bitmap.createScaledBitmap(arrowBitMap,(int)(getWidth()*0.6), (int)(getWidth() * 0.6), false);
		}
		radius = getWidth() / 3;
		int centerx = getWidth() / 2;
		int centery = getHeight() / 2;
		paint.setStyle(Paint.Style.FILL);
		canvas.drawColor(Color.WHITE);
		paint.setColor(Color.BLUE);
		canvas.setMatrix(originalMatrix);
		// Translate to the center of the canvas, rotate by angle and
		// retranslate back to original origin
		canvas.translate(centerx, centery);
		canvas.rotate(angle);
		canvas.translate(-centerx, -centery);

		int x = (int) (centerx);
		int y = (int) (centery - radius);
		canvas.drawText("N", x, y, paint);

		canvas.drawBitmap(arrowBitMap, centerx - arrowBitMap.getWidth()/2, centery - arrowBitMap.getHeight()/2, null);
	}

	/**
	 * @param angle
	 *            in degrees
	 */
	public void setAngle(int angle) {
		this.angle = angle;
		invalidate();
	}
}
