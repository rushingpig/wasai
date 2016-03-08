package com.seastar.wasai.views.extendedcomponent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.view.View;

import com.seastar.wasai.Entity.FocusPosition;
import com.seastar.wasai.Entity.Line;
import com.seastar.wasai.utils.ImageUtil;
import com.seastar.wasai.utils.StringUtil;

public class DrawView extends View {
	private Context context;
	private FocusPosition focusPosition;
	private int imageHeight;
	private int imageWidth;
	private float lineHeight;
	private float straitLineHeight;
	private int textMoreWidth;
	private int textPadding;
	private int textPaddingBottom;

	public DrawView(Context context, int imageWidth,int imageHeight, FocusPosition focusPosition) {
		super(context);
		this.context = context;
		this.focusPosition = focusPosition;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		straitLineHeight = getActuralHeight(90);
		lineHeight = straitLineHeight / 2;
		textMoreWidth = ImageUtil.dip2px(context, 30);
		textPadding = ImageUtil.dip2px(context, 14);
		textPaddingBottom = ImageUtil.dip2px(context, 6);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		Paint paint = new Paint();
		if(StringUtil.isNotEmpty(focusPosition.getCenter())){
			String[] centerPosition = focusPosition.getCenter().split(",");
			paint.setTextSize(ImageUtil.sp2px(context, 14));
			paint.setShadowLayer(5,3,3,0XFF333333);
			if(StringUtil.isNotEmpty(focusPosition.getColor())){
				String[] colors = focusPosition.getColor().split(",");
				if(focusPosition.getColor().split(",").length == 3){
					paint.setARGB(255, Integer.parseInt(colors[0].trim()), Integer.parseInt(colors[1].trim()), Integer.parseInt(colors[2].trim()));
				}
				paint.setStrokeWidth(ImageUtil.dip2px(context, 1.0f));
				float startX = getActuralWidth(Float.parseFloat(centerPosition[0]));
				float startY = getActuralHeight(Float.parseFloat(centerPosition[1]));
				
				switch (focusPosition.getTemp()) {
				case 1:
					drawTemp1(canvas, paint, startX, startY);
					break;
				case 2:
					drawTemp2(canvas, paint, startX, startY);
					break;
				case 3:
					drawTemp3(canvas, paint, startX, startY);
					break;
				default:
					break;
				}
			}
		}
	}

	private void drawTemp1(Canvas canvas, Paint paint, float startX, float startY) {
		Line line = focusPosition.getLines();
		if (line != null) {
			if (line.getLine1() != null) {
				canvas.drawLine(startX, startY, startX, startY - straitLineHeight, paint);
				float textWidth = paint.measureText(line.getLine1()) + textMoreWidth;
				canvas.drawLine(startX, startY - straitLineHeight, startX + textWidth, startY - straitLineHeight, paint);
				canvas.drawText(line.getLine1(), startX + textPadding, startY - straitLineHeight - textPaddingBottom, paint);
			}

			if (line.getLine2() != null) {
				float textWidth = paint.measureText(line.getLine2()) + textMoreWidth;
				canvas.drawLine(startX, startY, startX + textWidth, startY, paint);
				canvas.drawText(line.getLine2(), startX + textPadding, startY - textPaddingBottom, paint);
			}

			if (line.getLine3() != null) {
				canvas.drawLine(startX, startY, startX, startY + straitLineHeight, paint);
				float textWidth = paint.measureText(line.getLine3()) + textMoreWidth;
				canvas.drawLine(startX, startY + straitLineHeight, startX + textWidth, startY + straitLineHeight, paint);
				canvas.drawText(line.getLine3(), startX + textPadding, startY + straitLineHeight - textPaddingBottom, paint);
			}
		}
	}

	private void drawTemp2(Canvas canvas, Paint paint, float startX, float startY) {
		Line line = focusPosition.getLines();
		if (line != null) {
			if (line.getLine1() != null) {
				canvas.drawLine(startX, startY, startX - lineHeight, startY - lineHeight, paint);
				float textWidth = paint.measureText(line.getLine1()) + textMoreWidth;
				canvas.drawLine(startX - lineHeight, startY - lineHeight, startX - lineHeight - textWidth, startY
						- lineHeight, paint);
				canvas.drawText(line.getLine1(), startX - lineHeight - textWidth + textPadding, startY - lineHeight
						- textPaddingBottom, paint);
			}

			if (line.getLine2() != null) {
				canvas.drawLine(startX, startY, startX + lineHeight, startY - lineHeight, paint);
				float textWidth = paint.measureText(line.getLine2()) + textMoreWidth;
				canvas.drawLine(startX + lineHeight, startY - lineHeight, startX + lineHeight + textWidth, startY
						- lineHeight, paint);
				canvas.drawText(line.getLine2(), startX + lineHeight + textPadding, startY - lineHeight
						- textPaddingBottom, paint);
			}

			if (line.getLine3() != null) {
				canvas.drawLine(startX, startY, startX + lineHeight, startY + lineHeight, paint);
				float textWidth = paint.measureText(line.getLine3()) + textMoreWidth;
				canvas.drawLine(startX + lineHeight, startY + lineHeight, startX + lineHeight + textWidth, startY
						+ lineHeight, paint);
				canvas.drawText(line.getLine3(), startX + lineHeight + textPadding, startY + lineHeight
						- textPaddingBottom, paint);
			}

			if (line.getLine4() != null) {
				canvas.drawLine(startX, startY, startX - lineHeight, startY + lineHeight, paint);
				float textWidth = paint.measureText(line.getLine4()) + textMoreWidth;
				canvas.drawLine(startX - lineHeight, startY + lineHeight, startX - lineHeight - textWidth, startY
						+ lineHeight, paint);
				canvas.drawText(line.getLine4(), startX - lineHeight - textWidth + textPadding, startY + lineHeight
						- textPaddingBottom, paint);
			}
		}
	}

	private void drawTemp3(Canvas canvas, Paint paint, float startX, float startY) {
		Line line = focusPosition.getLines();
		if (line != null) {
			if (line.getLine1() != null) {
				canvas.drawLine(startX, startY, startX, startY - straitLineHeight, paint);
				float textWidth = paint.measureText(line.getLine1()) + textMoreWidth;
				canvas.drawLine(startX, startY - straitLineHeight, startX - textWidth, startY - straitLineHeight, paint);
				canvas.drawText(line.getLine1(), startX - textWidth + textPadding, startY - straitLineHeight - textPaddingBottom, paint);
			}

			if (line.getLine2() != null) {
				float textWidth = paint.measureText(line.getLine2()) + textMoreWidth;
				canvas.drawLine(startX, startY, startX - textWidth, startY, paint);
				canvas.drawText(line.getLine2(), startX - textWidth + textPadding, startY - textPaddingBottom, paint);
			}

			if (line.getLine3() != null) {
				canvas.drawLine(startX, startY, startX, startY + straitLineHeight, paint);
				float textWidth = paint.measureText(line.getLine3()) + textMoreWidth;
				canvas.drawLine(startX, startY + straitLineHeight, startX - textWidth, startY + straitLineHeight, paint);
				canvas.drawText(line.getLine3(), startX - textWidth + textPadding, startY + straitLineHeight - textPaddingBottom, paint);
			}
		}
	}

	private float getActuralHeight(float value) {
		float actualValue = imageHeight * value / 750;
		return actualValue;
	}

	private float getActuralWidth(float value) {
		float actualValue = imageWidth * value / 750;
		return actualValue;
	}
}