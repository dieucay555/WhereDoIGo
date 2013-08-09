/**
 * 
 */
package com.archerabi.wheredoigo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.archerabi.wheredoigo.R;

/**
 * @author gautamichitteti
 * 
 */
public class DirectionPointerCanvas extends View {

	private Paint paint = new Paint();

	private float angle = 0;

	private int radius;

	private Bitmap arrowBitMap = null;
	
	/**
	 * @param context
	 */
	public DirectionPointerCanvas(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public DirectionPointerCanvas(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public DirectionPointerCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {

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
