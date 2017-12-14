package com.yezhou.example.mycalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2017/10/31.
 */

public class MyDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Drawable mDivider;
    private int mOrientation;
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    public static final int[] ATRRS  = new int[]{ android.R.attr.listDivider};     //通过获取系统属性中的listDivider来添加，在AndroidManifest.xml中的AppTheme中设置

    public MyDecoration(Context context, int orientation) {
        this.mContext = context;
        final TypedArray ta = context.obtainStyledAttributes(ATRRS);    // TypedArray 用于存放 android 自定义控件 把属性集 和我们自己定义的属性集合建立映射关系
        this.mDivider = ta.getDrawable(0);
        ta.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {    // 设置屏幕方向
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");    // 此异常表明向方法传递了一个不合法或不正确的参数
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL_LIST) {    // 水平 画竖线
            drawVerricalLine(c, parent, state);
        } else {    // 子项布局垂直排列时花在两个布局之间(上布局的底部)
            drawHorizontalLine(c, parent, state);
        }
    }

    public void drawHorizontalLine(Canvas c, RecyclerView parent, RecyclerView.State state) {    // 画横线 Android.graphics

        int left = parent.getPaddingLeft();    // 得到布局的左边距
        int right = parent.getWidth() - parent.getPaddingRight();    // 布局右边的终点 parent的宽度-(减去)布局的右边距(此程序得到的是一条占满屏幕宽度的线)
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();    // 获取child的布局信息 params 参数
            int top = child.getBottom() + params.bottomMargin;    // item View(Textview)的底部+布局中的(页下空白)
            int bottom = top +mDivider.getIntrinsicHeight();    // 高度 + Drawable的固有高度 dp为单位
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVerricalLine(Canvas c, RecyclerView parent, RecyclerView.State state) {    //画竖线
        int top = parent.getPaddingTop();    // 获取parent的上边距
        int bottom = parent.getHeight() - parent.getPaddingBottom();    //parent的高度-parent的下边距
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();    // 获得child的布局信息
            int left = child.getRight() + params.rightMargin ;    // 从第一个item(item0)的尾部开始加入分隔线  View的最右边 + 布局的页右空白
            int right = left + mDivider.getIntrinsicHeight() ;    // 两个布局之间的空隙 (此处应该与getItemOffsets()方法中的偏移数值相呼应)
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL_LIST) {    // 画竖线 往右偏移
            Log.d("admin",""+mDivider.getIntrinsicWidth()+" "+mDivider.getIntrinsicHeight());    // 为何输出Width为负数
            outRect.set(0, 0,mDivider.getIntrinsicHeight(), 0);    // mDivider.getIntrinsicHeight()
        } else {     ////画横线，往下偏移一个分割线的高度
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        }
    }
}
