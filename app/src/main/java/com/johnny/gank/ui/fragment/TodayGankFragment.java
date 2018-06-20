package com.johnny.gank.ui.fragment;
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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.johnny.gank.R;
import com.johnny.gank.action.ActionType;
import com.johnny.gank.action.TodayGankActionCreator;
import com.johnny.gank.data.ui.GankGirlImageItem;
import com.johnny.gank.data.ui.GankNormalItem;
import com.johnny.gank.di.component.TodayGankFragmentComponent;
import com.johnny.gank.stat.StatName;
import com.johnny.gank.store.TodayGankStore;
import com.johnny.gank.ui.activity.MainActivity;
import com.johnny.gank.ui.activity.PictureActivity;
import com.johnny.gank.ui.activity.WebviewActivity;
import com.johnny.gank.ui.adapter.GankListAdapter;
import com.johnny.rxflux.Store;
import com.johnny.rxflux.StoreObserver;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
public class TodayGankFragment extends BaseFragment implements
    StoreObserver, SwipeRefreshLayout.OnRefreshListener, GankListAdapter.OnItemClickListener{

    public static final String TAG = TodayGankFragment.class.getSimpleName();

    @BindView(R.id.refresh_layout) SwipeRefreshLayout vRefreshLayout;
    @BindView(R.id.recycler_view) RecyclerView vWelfareRecycler;

    private TodayGankFragmentComponent mComponent;

    @Inject TodayGankStore mStore;
    @Inject TodayGankActionCreator mActionCreator;

    private GankListAdapter mAdapter;

    public static TodayGankFragment newInstance() {
        return new TodayGankFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInjector();
    }

    // the method can not be call in onCreate
    // because getMainActivityComponent will be null when MainActivity is restore from last save status.
    private void initInjector() {
        mComponent = ((MainActivity)getActivity()).getMainActivityComponent()
            .todayGankFragmentComponent()
            .build();
        mComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_refresh_recycler, container, false);
        ButterKnife.bind(this, contentView);

        vRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        vRefreshLayout.setOnRefreshListener(this);
        vWelfareRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        vWelfareRecycler.setHasFixedSize(true);
        mAdapter = new GankListAdapter(this);
        mAdapter.setOnItemClickListener(this);
        vWelfareRecycler.setAdapter(mAdapter);

        mStore.register(ActionType.GET_TODAY_GANK);
        mStore.setObserver(this);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                vRefreshLayout.setRefreshing(true);
                refreshData();
            }
        });
    }

    @Override
    public void onDestroyView() {
        mStore.unRegister();
        super.onDestroyView();
    }

    private void refreshData() {
        mActionCreator.getTodayGank();
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    protected String getStatPageName() {
        return StatName.PAGE_TODAY;
    }

    @Override
    public void onClickNormalItem(View view, GankNormalItem normalItem) {
        if(null != normalItem && !TextUtils.isEmpty(normalItem.url)) {
            WebviewActivity.openUrl(mComponent.getActivity(), normalItem.url, normalItem.desc);
        }
    }

    @Override
    public void onClickGirlItem(View view, GankGirlImageItem girlItem) {
        if(null != girlItem && !TextUtils.isEmpty(girlItem.imgUrl)) {
            startActivity(PictureActivity.newIntent(mComponent.getActivity(), girlItem.imgUrl, girlItem.publishedAt));
        }
    }

    @Override
    public void onChange(Store store, String actionType) {
        vRefreshLayout.setRefreshing(false);
        mAdapter.swapData(mStore.getItems());
    }

    @Override
    public void onError(Store store, String actionType) {
        vRefreshLayout.setRefreshing(false);
    }
}
