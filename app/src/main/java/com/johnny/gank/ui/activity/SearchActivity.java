package com.johnny.gank.ui.activity;
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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.johnny.gank.R;
import com.johnny.gank.action.ActionType;
import com.johnny.gank.action.QueryActionCreator;
import com.johnny.gank.data.ui.GankNormalItem;
import com.johnny.gank.store.SearchStore;
import com.johnny.gank.ui.adapter.QueryGankAdapter;
import com.johnny.rxflux.Store;
import com.johnny.rxflux.StoreObserver;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
public class SearchActivity extends BaseActivity implements StoreObserver {

    @BindView(R.id.toolbar) Toolbar vToolbar;
    @BindView(R.id.search_view) SearchView vSearchView;
    @BindView(R.id.recycler_view) RecyclerView vRecyclerView;
    @BindView(R.id.empty_view) View vEmptyView;

    @Inject SearchStore mStore;
    @Inject QueryActionCreator mQueryActionCreator;

    private QueryGankAdapter mAdapter;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initSearchView();
        initRecyclerView();
        handleIntent(getIntent());
        initInjector();
        mStore.setObserver(this);
        mStore.register(ActionType.QUERY_GANK);
    }

    private void initInjector() {
        getAppComponent()
            .searchActivityComponent()
            .activity(this)
            .build()
            .inject(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void initSearchView() {
        vSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryGank(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new QueryGankAdapter();
        mAdapter.setOnItemClickListener(new QueryGankAdapter.OnItemClickListener() {
            @Override
            public void onClickNormalItem(View view, GankNormalItem normalItem) {
                WebviewActivity.openUrl(SearchActivity.this, normalItem.url, normalItem.desc);
            }
        });
        vRecyclerView.setHasFixedSize(true);
        vRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        vRecyclerView.setAdapter(mAdapter);
    }

    private void handleIntent(Intent intent) {
        vSearchView.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStore.unRegister();
    }

    private void queryGank(String queryText) {
        mQueryActionCreator.query(queryText);
    }

    @Override
    public void onChange(Store store, String actionType) {
        mAdapter.updateData(mStore.getGankList());
        vEmptyView.setVisibility(null == mStore.getGankList() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onError(Store store, String actionType) {
        mAdapter.clearData();
        vEmptyView.setVisibility(View.VISIBLE);
    }
}
