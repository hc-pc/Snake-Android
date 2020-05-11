package com.zhm.Tools;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class SnakeView extends View {

	private int areaSize = 10;
	private int areaWidth = 0;
	private int areaHeight = 0;
	private int offsetX = 0;
	private int offsetY = 0;
	
	private int foodX = 0;
	private int foodY = 0;
	private int snakeDirection = 0;
	private int SnakeLength = 4;
	private int getFood = 0;
	private int[] mSnakeX = new int[100];
	private int[] mSnakeY = new int[100];
	public static final int DIR_UP = 0;
	public static final int DIR_DOWN = 1;
	public static final int DIR_LEFT = 2;
	public static final int DIR_RIGHT = 3;
	private static final int SNAKE_MOVE = 1;
	
	private int SNAKE_STATUES = 0;
	private static final int STATUES_START = 1;
	private static final int STATUES_DEAD = 2;
	private static final int STATUES_PAUSE = 3;
	
	private Timer timer = null;
	private Handler handler = null;
	
	private Paint backgroundPaint = new Paint();
	private Paint headPaint = new Paint();
	private Paint bodyPaint = new Paint();
	private Paint foodPaint = new Paint();
	private Paint borderPaint = new Paint();
	
	public SnakeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SnakeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public SnakeView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
		backgroundPaint.setColor(Color.BLACK);
		headPaint.setColor(Color.RED);
		bodyPaint.setColor(Color.GREEN);
		foodPaint.setColor(Color.BLUE);
		borderPaint.setColor(Color.YELLOW);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(3);
		initSnake();
		if (handler == null) {
			handler = new Handler() {
				public void handleMessage(Message msg) {
					switch(msg.what) {
						case SNAKE_MOVE:
							snakeMove();
							break;
						default:
							break;
					}
				}
			};
		}
		if (timer == null) {
			timer = new Timer();
		}
		if (timer != null) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = SNAKE_MOVE;
					handler.sendMessage(msg);
				}
			}, 100, 500);
		}
	}
	
	private void initSnake() {
		mSnakeX[0] = 3;
		mSnakeY[0] = 1;
		
		mSnakeX[1] = 2;
		mSnakeY[1] = 1;
		
		mSnakeX[2] = 1;
		mSnakeY[2] = 1;
		
		mSnakeX[3] = 0;
		mSnakeY[3] = 1;
		
		foodX = 10;
		foodY = 1;
		snakeDirection = DIR_RIGHT;
		SNAKE_STATUES = STATUES_DEAD;
		SnakeLength = 4;
		getFood = 0;
		if (onEatFoodListener != null) {
			onEatFoodListener.onEatFood(getFood);
		}
	}
	
	protected void onDraw(Canvas canvas) {
		int i = 0;
		super.onDraw(canvas);
		canvas.drawRect(offsetX, offsetY, offsetX + areaSize * areaWidth, offsetY + areaSize * areaHeight, backgroundPaint);
		
		canvas.drawRect(offsetX - 2, offsetY - 2, offsetX + areaSize * areaWidth + 1, offsetY + areaSize * areaHeight + 1, borderPaint);
		
		canvas.drawRect(offsetX + foodX * areaSize, offsetY + foodY * areaSize, offsetX + (foodX + 1) * areaSize, offsetY + (foodY + 1) * areaSize, foodPaint);
		
		for (i = 0; i < SnakeLength; i++) {
			if (i == 0) {
				canvas.drawRect(offsetX + mSnakeX[i] * areaSize, offsetY + mSnakeY[i] * areaSize, offsetX + (mSnakeX[i] + 1) * areaSize, offsetX + (mSnakeY[i] + 1) * areaSize, headPaint);
			}
			else {
				canvas.drawRect(offsetX + mSnakeX[i] * areaSize, offsetY + mSnakeY[i] * areaSize, offsetX + (mSnakeX[i] + 1) * areaSize, offsetX + (mSnakeY[i] + 1) * areaSize, bodyPaint);
			}
		}
	}
	
	private void snakeMove() {
		int i = 0;
		int newSnakeX = 0;
		int newSnakeY = 0;
		switch (snakeDirection) {
			case DIR_UP:
				newSnakeX = mSnakeX[0];
				newSnakeY = mSnakeY[0]-1;
				break;
			case DIR_DOWN:
				newSnakeX = mSnakeX[0];
				newSnakeY = mSnakeY[0]+1;
				break;
			case DIR_LEFT:
				newSnakeX = mSnakeX[0]-1;
				newSnakeY = mSnakeY[0];
				break;
			case DIR_RIGHT:
				newSnakeX = mSnakeX[0]+1;
				newSnakeY = mSnakeY[0];
				break;
			default:
				newSnakeX = mSnakeX[0];
				newSnakeY = mSnakeY[0];
				break;
		}
		for (i = SnakeLength - 1; i > 0; i--) {
			mSnakeX[i] = mSnakeX[i - 1];
			mSnakeY[i] = mSnakeY[i - 1];
		}
		mSnakeX[0] = newSnakeX;
		mSnakeY[0] = newSnakeY;
		if (mSnakeX[0] < 0 || mSnakeX[0] >= areaWidth || mSnakeY[0] < 0 || mSnakeY[0] >= areaHeight) {
			SNAKE_STATUES = STATUES_DEAD;
			if (onGameOverListener != null) {
				onGameOverListener.onGameOver(getFood);
			}
			return;
		}
		if (SnakeLength > 4) {
			for (i = 3; i < SnakeLength - 1; i++) {
				if (mSnakeX[i] == newSnakeX && mSnakeY[i] == newSnakeY) {
					SNAKE_STATUES = STATUES_DEAD;
					if (onGameOverListener != null) {
						onGameOverListener.onGameOver(getFood);
					}
					return;
				}
			}
		}
		
		if (newSnakeX == foodX && newSnakeY == foodY) {
			Random random = new Random();
			foodX = random.nextInt(areaWidth);
			foodY = random.nextInt(areaHeight);
			getFood++;
			SnakeLength++;
			if (onEatFoodListener != null) {
				onEatFoodListener.onEatFood(getFood);
			}
		}
		invalidate();
	}
	
	public void control(int dir) {
		switch (snakeDirection) {
			case DIR_UP:
				if (dir == DIR_DOWN) return;
				break;
			case DIR_DOWN:
				if (dir == DIR_UP) return;
				break;
			case DIR_LEFT:
				if (dir == DIR_RIGHT) return;
				break;
			case DIR_RIGHT:
				if (dir == DIR_LEFT) return;
				break;
			default:
				return;
		}
		snakeDirection = dir;
	}
	
	public void startGame() {
		if (SNAKE_STATUES == STATUES_DEAD) {
			init();
			SNAKE_STATUES = STATUES_START;
			return;
		}
		if (SNAKE_STATUES == STATUES_PAUSE) {
			SNAKE_STATUES = STATUES_START;
			return;
		}
		if (onGameStartListener != null) {
			onGameStartListener.onGameStart(getFood);
		}
	}
	
	public void pauseGame() {
		if (SNAKE_STATUES == STATUES_START) {
			SNAKE_STATUES = STATUES_PAUSE;
		}
	}
	
	public interface OnGameStartListener {
		void onGameStart(int getFood);
	}
	
	public interface OnEatFoodListener {
		void onEatFood(int getFood);
	}
	
	public interface OnGameOverListener {
		void onGameOver(int getFood);
	}

	public void setOnGameStartListener(OnGameStartListener onGameStartListener) {
		this.onGameStartListener = onGameStartListener;
	}
	
	public void setOnEatFoodListener(OnEatFoodListener onEatFoodListener) {
		this.onEatFoodListener = onEatFoodListener;
	}

	public void setOnGameOverlistener(OnGameOverListener onGameOverListener) {
		this.onGameOverListener = onGameOverListener;
	}

	private OnGameStartListener onGameStartListener;
	private OnEatFoodListener onEatFoodListener; 
	private OnGameOverListener onGameOverListener;
	
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		areaWidth = width / areaSize - 1;
		areaHeight = height / areaSize - 1;
		offsetX = (width - areaSize * areaWidth) / 2;
		offsetY = (height - areaSize * areaHeight) / 2;
	}
}
