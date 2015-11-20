package com.quadstation.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {

	private Paint paintCirCle, paintArrow, paintText;
	private Path pathArrow, pathLines;
	private float kier;
	private int color, textColor;
	private int height, width;
	String text;
	float scaledDensity = 0;

	public CompassView(Context context) {
		super(context);
		init();
	}

	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// Construct a wedge-shaped path
		paintCirCle = new Paint();
		paintArrow = new Paint();
		paintText = new Paint();
		pathArrow = new Path();
		pathLines = new Path();
		kier = 0;
		height = 80;
		width = 80;
		color = Color.BLUE;
		textColor = Color.YELLOW;
		text = "Compass";
		scaledDensity = 0;

	}

	public void SetHeading(float angle) {
		kier = -angle;
		invalidate();
	}

	private void drawArrow() {
		pathArrow.moveTo(0, -20 * scaledDensity);
		pathArrow.lineTo(-10 * scaledDensity, 30 * scaledDensity);
		pathArrow.lineTo(0, 20 * scaledDensity);
		pathArrow.lineTo(10 * scaledDensity, 30 * scaledDensity);
		pathArrow.close();
	}


	public void SetText(String Text) {
		text = Text;
	}

	public void SetColor(int c, int text_color) {
		color = c;
		textColor = text_color;

		paintArrow.setAntiAlias(true);
		paintArrow.setColor(color);
		paintArrow.setStyle(Paint.Style.FILL_AND_STROKE);

		paintCirCle.setAntiAlias(true);
		paintCirCle.setColor(color);
		paintCirCle.setStyle(Paint.Style.STROKE);
		paintCirCle.setStrokeWidth(2 * scaledDensity);

		paintText.setAntiAlias(true);
		paintText.setColor(textColor);
		paintText.setStyle(Paint.Style.STROKE);
		paintText.setStrokeWidth(1 * scaledDensity);
		paintText.setTextSize(15 * scaledDensity);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		scaledDensity = getResources().getDisplayMetrics().scaledDensity;
		SetColor(color, textColor);
		drawArrow();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		canvas.translate(width / 2, height / 2);
		canvas.save();
		paintCirCle.setStyle(Paint.Style.FILL);
		paintCirCle.setColor(0xFF3366FF);
		int i = 0;
		canvas.save();
		for (i = 0; i <= 72; i = i + 1) {

			canvas.drawLine(0f, 0f, height / 2 * (float) Math.cos(5), height
					/ 2 * (float) Math.sin(5), paintCirCle);
			canvas.rotate(5);
		}
		canvas.restore();

		canvas.drawOval(new RectF(-height / 2 + 7 * scaledDensity, -height / 2
				+ 7 * scaledDensity, height / 2 - 7 * scaledDensity, height / 2
				- 7 * scaledDensity), paintCirCle);
		canvas.drawText("N", 0 - paintText.measureText("N") / 2, -height / 2
				* 3 / 4 + 7 * scaledDensity, paintText);
		canvas.drawText("E", height / 2 * 3 / 4 - paintText.measureText("E")
				/ 2, 0 + 7 * scaledDensity, paintText);
		canvas.drawText("S", 0 - paintText.measureText("S") / 2, height / 2 * 3
				/ 4 + 7 * scaledDensity, paintText);
		canvas.drawText("W",
				-height / 2 * 3 / 4 - 0 - paintText.measureText("W") / 2,
				0 + 7 * scaledDensity, paintText);

		// /
		canvas.save();
		canvas.rotate(kier);
		canvas.drawPath(pathArrow, paintArrow);
		canvas.restore();

		paintCirCle.setStyle(Paint.Style.STROKE);
		paintCirCle.setColor(Color.BLUE);
		canvas.drawOval(new RectF(-height / 2, -height / 2, height / 2,
				height / 2), paintCirCle);

		if (text.length() > 0)
			canvas.drawText(new Float(kier).toString(),
					0 - paintText.measureText(new Float(kier).toString()) / 2,
					-25 * scaledDensity, paintText);

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
}