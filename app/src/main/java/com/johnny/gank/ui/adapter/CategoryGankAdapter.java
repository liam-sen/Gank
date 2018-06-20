package com.johnny.gank.ui.adapter;
/*
 * Copyright (C) 2016 Johnny Shieh Open Source Project
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

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnny.gank.R;
import com.johnny.gank.data.ui.GankNormalItem;
import com.johnny.gank.util.AppUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
public class CategoryGankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GankNormalItem> mItems;
    private int mCurPage = 0;

    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onClickNormalItem(View view, GankNormalItem normalItem);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }

    public void updateData(int page, List<GankNormalItem> list) {
        if(null == list || list.size() == 0) {
            return;
        }
        if(page - mCurPage > 1) {
            return;
        }
        if(page <= 1) {
            if(null == mItems || !mItems.containsAll(list)) {
                mItems = list;
                notifyDataSetChanged();
                mCurPage = page;
            }
        }else if(1 == page - mCurPage && null != mItems) {
            int size = mItems.size();
            mItems.addAll(size, list);
            notifyItemRangeInserted(size, list.size());
            mCurPage = page;
        }
    }

    public int getCurPage() {
        return mCurPage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NormalViewHolder) {
            NormalViewHolder normalHolder = (NormalViewHolder) holder;
            final GankNormalItem normalItem = mItems.get(position);
            normalHolder.title.setText(getGankTitleStr(normalItem.desc, normalItem.who));
            normalHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null != mItemClickListener) {
                        mItemClickListener.onClickNormalItem(v, normalItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mItems ? 0 : mItems.size();
    }

    private CharSequence getGankTitleStr(String desc, String who) {
        if(TextUtils.isEmpty(who)) {
            return desc;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(desc);
        SpannableString spannableString = new SpannableString(" (" + who + ")");
        spannableString.setSpan(new TextAppearanceSpan(AppUtil.getAppContext(), R.style.SummaryTextAppearance), 0, spannableString.length(), 0);
        builder.append(spannableString);
        return builder;
    }


    public static class NormalViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) TextView title;
        @BindView(R.id.align_pic) ImageView pic;

        public NormalViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_gank, parent, false));
            ButterKnife.bind(this, itemView);
            pic.setVisibility(View.GONE);
        }
    }
}
