package com.example.patientclient01;
import com.example.dingdan.*;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//订单管理
public class DingDanActivity extends Activity {
	//
	// 
	private ViewPager mPager;//
	private List<View> listViews; // 
	private ImageView cursor;//
	private TextView t1, t2, t3, t4;// 
	private int offset = 0;// 
	private int currIndex = 0;//
	private int bmpW;// 
	Context context = null;
	LocalActivityManager manager = null;
	private MyPagerAdapter myPagerAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_ddgl);
		context = DingDanActivity.this;
		manager = new LocalActivityManager(this , true);
		manager.dispatchCreate(savedInstanceState);
		InitImageView();
		InitTextView();
		InitViewPager();
	}

	/**
	 *
	 */
	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		t4 = (TextView) findViewById(R.id.text4);
		t1.setBackgroundResource(R.drawable.qbdd_xz);
		t2.setBackgroundResource(R.drawable.dfkdd_wxz);
		t3.setBackgroundResource(R.drawable.dwcdd_wxz);
		t4.setBackgroundResource(R.drawable.dpjdd_wxz);
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));
	}

	/**
	 *
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		/*
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.dd_qb, null));
		listViews.add(mInflater.inflate(R.layout.dd_dfk, null));
		listViews.add(mInflater.inflate(R.layout.dd_dpj, null));
		*/
		Intent intent1 = new Intent(context, DingDanQuanBu.class);
		listViews.add(getView("DingDanQuanBu", intent1));
		Intent intent2 = new Intent(context, DingDanDaiFuKuan.class);
		listViews.add(getView("DingDanDaiFuKuan", intent2));
		Intent intent3 = new Intent(context, DingDanDaiKaiShi.class);
		listViews.add(getView("DingDanDaiKaiShi", intent3));
		Intent intent4 = new Intent(context, DingDanDaiPingJia.class);
		listViews.add(getView("DingDanDaiPingJia", intent4));

		myPagerAdapter = new MyPagerAdapter(listViews);
		mPager.setAdapter(myPagerAdapter);
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		myPagerAdapter.notifyDataSetChanged();
	}
	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}
	/**
	 *
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();//
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 
		offset = (screenW / 4 - bmpW) / 2;//
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 
	}

	/**
	 *
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;
		private int mChildCount = 0;
		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}
		@Override
		public void notifyDataSetChanged() {
			mChildCount = getCount();
			super.notifyDataSetChanged();
		}
		@Override
		public int getItemPosition(Object object) {
			if ( mChildCount > 0) {
				mChildCount --;
				return POSITION_NONE;
			}
			return super.getItemPosition(object);
		}
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 *
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 *
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;//
		int two = one * 2;// 

		@Override
		public void onPageSelected(int arg0) {
			/*
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			*/
			Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);
			currIndex = arg0;
			switch (arg0) {
				case 0:
					t1.setBackgroundResource(R.drawable.qbdd_xz);
					t2.setBackgroundResource(R.drawable.dfkdd_wxz);
					t3.setBackgroundResource(R.drawable.dwcdd_wxz);
					t4.setBackgroundResource(R.drawable.dpjdd_wxz);
					break;
				case 1:
					t1.setBackgroundResource(R.drawable.qbdd_wxz);
					t2.setBackgroundResource(R.drawable.dfkdd_xz);
					t3.setBackgroundResource(R.drawable.dwcdd_wxz);
					t4.setBackgroundResource(R.drawable.dpjdd_wxz);
					break;
				case 2:
					t1.setBackgroundResource(R.drawable.qbdd_wxz);
					t2.setBackgroundResource(R.drawable.dfkdd_wxz);
					t3.setBackgroundResource(R.drawable.dwcdd_xz);
					t4.setBackgroundResource(R.drawable.dpjdd_wxz);
					break;
				case 3:
					t1.setBackgroundResource(R.drawable.qbdd_wxz);
					t2.setBackgroundResource(R.drawable.dfkdd_wxz);
					t3.setBackgroundResource(R.drawable.dwcdd_wxz);
					t4.setBackgroundResource(R.drawable.dpjdd_xz);
					break;
			}
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	/*
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_HOME){
        	Toast.makeText(this, "请使用退出按钮退出程序！Home键已禁止！",Toast.LENGTH_LONG).show();
        	return false;
        }else if(keyCode == KeyEvent.KEYCODE_BACK){
        	Toast.makeText(this, "请使用按钮操作程序！返回键已禁止！", Toast.LENGTH_LONG).show();
        	return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    */
}