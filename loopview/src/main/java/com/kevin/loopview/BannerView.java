/*
 * Copyright (c) 2018 Kevin zhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kevin.loopview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kevin.loopview.internal.BaseLoopAdapter;
import com.kevin.loopview.internal.BaseLoopView;

/**
 * BannerView
 *
 * @author zwenkai@foxmail.com, Created on 2015-1-14 19:30:18
 *         Major Function：<b>自定义控件可以自动跳动的ViewPager</b>
 *         <p/>
 *         Note: If you modify this class please fill in the following content as a record.
 * @author mender，Modified Date Modify Content:
 */

public class BannerView extends BaseLoopView {

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set the custom layout to be inflated for the loop views.
     *
     * @param layoutResId Layout id to be inflated
     */
    public void setLoopLayout(int layoutResId) {
        mLoopLayoutId = layoutResId;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void initRealView() {
        View view = null;

        if (mLoopLayoutId != 0) {
            // If there is a custom loop view layout id set, try and inflate it
            view = LayoutInflater.from(getContext()).inflate(mLoopLayoutId, null);
            // ViewPager
            mViewPager = (ViewPager) view.findViewById(R.id.loop_view_pager);
            // 指示点父控件
            dotsView = (LinearLayout) view.findViewById(R.id.loop_view_dots);
            // 描述文字
            descText = (TextView) view.findViewById(R.id.loop_view_desc);
        }

        if (view == null) {
            view = createDefaultView();
        }

        setScrollDuration(1000);    // 设置页面切换时间
        this.addView(view);
    }

    private View createDefaultView() {
        RelativeLayout contentView = new RelativeLayout(getContext());
        int viewWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        ViewGroup.LayoutParams viewParams = new ViewGroup.LayoutParams(viewWidth, viewHeight);
        contentView.setLayoutParams(viewParams);
        // 初始化ViewPager
        mViewPager = new ViewPager(getContext());
        mViewPager.setId(R.id.loop_view_pager);
        int viewPagerWidth = LayoutParams.MATCH_PARENT;
        int viewPagerHeight = LayoutParams.WRAP_CONTENT;
        LayoutParams viewPagerParams = new LayoutParams(viewPagerWidth, viewPagerHeight);
        this.addView(mViewPager, viewPagerParams);
        // 初始化下方指示条
        RelativeLayout bottomLayout = new RelativeLayout(getContext());
        int bottomLayoutWidth = LayoutParams.MATCH_PARENT;
        int bottomLayoutHeight = LayoutParams.WRAP_CONTENT;
        LayoutParams bottomLayoutParams = new LayoutParams(bottomLayoutWidth, bottomLayoutHeight);
        bottomLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mViewPager.getId());
        Drawable mBackground = new ColorDrawable(Color.DKGRAY);
        mBackground.setAlpha((int) (0.3 * 255));
        bottomLayout.setBackgroundDrawable(mBackground);
        bottomLayout.setGravity(Gravity.CENTER_VERTICAL);
        this.addView(bottomLayout, bottomLayoutParams);
        // 初始化指示点父控件
        dotsView = new LinearLayout(getContext());
        dotsView.setId(R.id.loop_view_dots);
        int dotsViewWidth = LayoutParams.WRAP_CONTENT;
        int dotsViewHeight = LayoutParams.WRAP_CONTENT;
        LayoutParams dotsViewParams = new LayoutParams(dotsViewWidth, dotsViewHeight);
        dotsView.setOrientation(LinearLayout.HORIZONTAL);
        dotsViewParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        dotsViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        bottomLayout.addView(dotsView, dotsViewParams);
        // 初始描述文字
        descText = new TextView(getContext());
        int descTextWidth = LayoutParams.MATCH_PARENT;
        int descTextHeight = LayoutParams.WRAP_CONTENT;
        LayoutParams descTextParams = new LayoutParams(descTextWidth, descTextHeight);
        descTextParams.addRule(RelativeLayout.LEFT_OF, dotsView.getId());
        descText.setSingleLine(true);
        descText.getPaint().setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        descText.setTextColor(Color.WHITE);
        descText.setGravity(Gravity.LEFT);
        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        descText.setPadding(padding, padding, padding, padding);
        bottomLayout.addView(descText, descTextParams);

        return contentView;
    }

    @Override
    protected BaseLoopAdapter initAdapter() {
        return new BannerAdapter(getContext(), mLoopData, mViewPager);
    }

    /**
     * 初始化指示点
     */
    @Override
    protected void initDots(int size) {
        if (null != dotsView) {
            dotsView.removeAllViews();
            for (int i = 0; i < size; i++) {
                ImageView dot = new ImageView(getContext());
                dot.setBackgroundResource(mDotSelector);
                int dotWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
                int dotHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
                LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dotWidth, dotHeight);
                dotParams.setMargins((int) mDotMargin, (int) mDotMargin, (int) mDotMargin, (int) mDotMargin);
                if (i == 0) {
                    dot.setEnabled(true);
                } else {
                    dot.setEnabled(false);
                }
                dotsView.addView(dot, dotParams);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void setOnPageChangeListener() {
        // 数据适配器滑动监听
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int index = position % mLoopData.items.size();
                if (null != dotsView) {
                    dotsView.getChildAt(index).setEnabled(true);
                }
                if (null != dotsView && currentPosition != -1) {
                    dotsView.getChildAt(currentPosition).setEnabled(false);
                }
                currentPosition = index;
                if (null != descText) {
                    if (!TextUtils.isEmpty(mLoopData.items.get(index).desc)) {
                        if (descText.getVisibility() != View.VISIBLE)
                            descText.setVisibility(View.VISIBLE);
                        String imageDesc = mLoopData.items.get(index).desc;
                        descText.setText(imageDesc);
                    } else {
                        if (descText.getVisibility() == View.VISIBLE)
                            descText.setVisibility(View.GONE);
                    }
                }

                // 跳转到头部尾部的监听回调
                if (mOnLoopListener != null) {
                    if (index == 0) {
                        mOnLoopListener.onLoopToStart(index, position);
                    }
                    mOnLoopListener.onLoopToNext(index, position);
                    if (index == mLoopData.items.size() - 1) {
                        mOnLoopListener.onLoopToEnd(index, position);
                    }
                }

            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

}