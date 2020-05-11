package com.zhm.MyActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zhm.Tools.DBHelper;
import com.zhm.Tools.SnakeView;
import com.zhm.Tools.SnakeView.OnGameStartListener;
import com.zhm.Tools.SnakeView.OnEatFoodListener;
import com.zhm.Tools.SnakeView.OnGameOverListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    
	private Button startBtn = null;
	private Button pauseBtn = null;
	private Button upBtn = null;
	private Button downBtn = null;
	private Button leftBtn = null;
	private Button rightBtn = null;
	private TextView scoreTxt = null;
	private SnakeView snakeView = null;
	private int Dead = 0;
	private int maxScore = 0;
	private String player = null;
	private Handler mHandler = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
        setListener();
    }
	
	private void init() {
		startBtn = (Button)findViewById(R.id.startBtn);
		pauseBtn = (Button)findViewById(R.id.pauseBtn);
		upBtn = (Button)findViewById(R.id.upBtn);
		downBtn = (Button)findViewById(R.id.downBtn);
		leftBtn = (Button)findViewById(R.id.leftBtn);
		rightBtn = (Button)findViewById(R.id.rightBtn);
		scoreTxt = (TextView)findViewById(R.id.scoreTxt);
		snakeView = (SnakeView)findViewById(R.id.snake);
	}
	
	private void setListener() {
		startBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
            	DBHelper dbhelper = new DBHelper(MainActivity.this);
				SQLiteDatabase db = dbhelper.getWritableDatabase();
				String sql = "select MAX(score) from scoreTbl";
				Cursor cursor = db.rawQuery(sql, null);
				cursor.moveToFirst();
				maxScore = cursor.getInt(0);
				cursor.close();
				db.close();
				if (mHandler == null) {
					mHandler = new Handler(){
						public void handleMessage(Message msg) {
							throw new RuntimeException();
						}
					};
				}
				final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
				final EditText playerNameEdt = (EditText)dialogView.findViewById(R.id.playerNameEdt);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("请输入玩家名");
				builder.setIcon(R.drawable.icon);
				builder.setView(dialogView);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	player = playerNameEdt.getText().toString();
		            	mHandler.sendEmptyMessage(1);
		            }
				});
				builder.show();
				try {
					Looper.getMainLooper();
					Looper.loop();
				} catch(RuntimeException e) {
					
				}
				Dead = 0;
				snakeView.startGame();
			}
		});
		
		pauseBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				snakeView.pauseGame();
			}
		});
		
		upBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				snakeView.control(SnakeView.DIR_UP);
			}
		});
		
		downBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				snakeView.control(SnakeView.DIR_DOWN);
			}
		});
		
		leftBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				snakeView.control(SnakeView.DIR_LEFT);
			}
		});
		
		rightBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				snakeView.control(SnakeView.DIR_RIGHT);
			}
		});
		
		snakeView.setOnGameStartListener(new OnGameStartListener() {
			public void onGameStart(int getFood) {
			}
		});
		
		snakeView.setOnEatFoodListener(new OnEatFoodListener() {
			public void onEatFood(int getFood) {
				scoreTxt.setText("得分：" + getFood + "历史记录：" + maxScore + " 物联网1801 张洪铭");
			}
		});
        
		snakeView.setOnGameOverlistener(new OnGameOverListener() {
			public void onGameOver(final int getFood) {
				if (Dead == 0) {
					Toast.makeText(MainActivity.this, "Game Over Score is : " + getFood, Toast.LENGTH_SHORT).show();
					if (getFood > maxScore) {
						if (mHandler == null) {
							mHandler = new Handler(){
								public void handleMessage(Message msg) {
									throw new RuntimeException();
								}
							};
						}
						final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
						final EditText playerNameEdt = (EditText)dialogView.findViewById(R.id.playerNameEdt);
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setTitle("请输入玩家名");
						builder.setIcon(R.drawable.icon);
						builder.setView(dialogView);
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int which) {
				            	player = playerNameEdt.getText().toString();
				            	mHandler.sendEmptyMessage(1);
				            }
						});
						builder.show();
						try {
							Looper.getMainLooper();
							Looper.loop();
						} catch(RuntimeException e) {
							player = "someone";
						}
		            	DBHelper dbhelper = new DBHelper(MainActivity.this);
						SQLiteDatabase db = dbhelper.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put("name", player);
						values.put("score", getFood);
						db.insert("scoreTbl", null, values);
						db.close();
					}
					
					final View gridView = LayoutInflater.from(MainActivity.this).inflate(R.layout.rank, null);
					final GridView topTenGrv = (GridView)gridView.findViewById(R.id.scoreGrv);
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					int i = 1;
					Map<String, Object> title = new HashMap<String, Object>();
					title.put("rank", "rank");
					title.put("name", "name");
					title.put("score", "score");
					list.add(title);
					
					DBHelper dbhelper = new DBHelper(MainActivity.this);
					SQLiteDatabase db = dbhelper.getWritableDatabase();
					String sql = "select * from scoreTbl order by score desc limit 0,10";
					Cursor cursor = db.rawQuery(sql, null);
					cursor.moveToFirst();
					do {
						Map<String, Object> item = new HashMap<String, Object>();
						item.put("rank", i);
						item.put("name", cursor.getString(1));
						item.put("score", cursor.getInt(2));
						list.add(item);
						i++;
					}while(cursor.moveToNext());
					SimpleAdapter sa = new SimpleAdapter(MainActivity.this, list, R.layout.scoreitem, new String[]{"rank", "name", "score"}, new int[]{R.id.rankTxt, R.id.nameTxt, R.id.scoreTxt});
					topTenGrv.setAdapter(sa);
					cursor.close();
					db.close();
					
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("游戏结束");
					builder.setIcon(R.drawable.icon);
					builder.setView(gridView);
					builder.setPositiveButton("确定", null);
					builder.show();
					Dead = 1;
				}
			}
		});
	}
}