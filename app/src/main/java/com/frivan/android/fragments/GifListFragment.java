package com.frivan.android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.frivan.android.adapters.GifAdapter;
import com.frivan.android.gifsearcher.R;
import com.frivan.android.models.Gif;
import com.frivan.android.utils.GiphyApiUtil;
import com.frivan.android.utils.KeyboardUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Список гифок
 */

public class GifListFragment extends Fragment {
    private static final int NUMBER_COLUMNS = 2;
    private static final long TIMEOUT_SEARCH = 600L;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GifAdapter mAdapter;
    private CompositeDisposable mCompositeDisposable;
    private Disposable mDisposable;

    {
        mCompositeDisposable = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gif_list, container, false);
        initView(view);
        return view;
    }

    /**
     * Инициализирует используемые view
     *
     * @param view корневое представление
     */
    private void initView(View view) {
        EditText searchEditText = view.findViewById(R.id.search_edit_text);
        RecyclerView recyclerView = view.findViewById(R.id.list);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(NUMBER_COLUMNS, StaggeredGridLayoutManager.VERTICAL));

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    getGifs(GiphyApiUtil.getApi().getSearchGifs(textView.getText().toString(), getString(R.string.api_key)));
                    KeyboardUtils.hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        RxTextView.textChanges(searchEditText)
                .debounce(TIMEOUT_SEARCH, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .skip(1)
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(CharSequence charSequence) throws Exception {
                        return charSequence.length() > 2;
                    }
                })
                .map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(CharSequence charSequence) throws Exception {
                        return charSequence.toString();
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        getGifs(GiphyApiUtil.getApi().getSearchGifs(s, getString(R.string.api_key)));
                    }
                });

        getTrendingGifs();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTrendingGifs();
            }
        });
    }

    /**
     * Обновляет гифки из раздела Trending
     */
    private void refreshTrendingGifs() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        mDisposable = GiphyApiUtil.getApi().getTrendingGifs(getString(R.string.api_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Gif>() {
                    @Override
                    public void accept(Gif gif) throws Exception {
                        if (gif != null) {
                            updateUI(gif);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.msg_error, Snackbar.LENGTH_SHORT).show();
                        }
                        mDisposable.dispose();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mDisposable.dispose();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
        mCompositeDisposable.add(mDisposable);
    }

    /**
     * Получить данные из раздела Trending
     */
    private void getTrendingGifs() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        mDisposable = GiphyApiUtil.getApi().getTrendingGifs(getString(R.string.api_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Gif>() {
                    @Override
                    public void accept(Gif gif) throws Exception {
                        if (gif != null) {
                            updateUI(gif);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.msg_error, Snackbar.LENGTH_SHORT).show();
                        }
                        mDisposable.dispose();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mDisposable.dispose();
                    }
                });
        mCompositeDisposable.add(mDisposable);
    }

    /**
     * Получить данные
     *
     * @param observable источник данных
     */
    private void getGifs(Observable<Gif> observable) {
        Disposable disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Gif>() {
                    @Override
                    public void accept(Gif gif) throws Exception {
                        if (gif != null) {
                            updateUI(gif);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.msg_error, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * Обновление списка гифок
     *
     * @param gif объект данных
     */
    private void updateUI(Gif gif) {
        if (mAdapter == null) {
            mAdapter = new GifAdapter(getActivity(), gif);
            if (getView() != null) {
                RecyclerView recyclerView = getView().findViewById(R.id.list);
                recyclerView.setAdapter(mAdapter);
            }
        } else {
            mAdapter.setValues(gif);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(mConnReceiver);
        }
    }

    /**
     * Автоматически обновляет список Trending при появлении интернета
     */
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if ((activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
                        || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) && mAdapter == null
                        && mDisposable.isDisposed()) {
                    getTrendingGifs();
                }

            }
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    /**
     * Создает новый объект фрагмента
     *
     * @return new GifListFragment
     */
    public static Fragment newInstance() {
        return new GifListFragment();
    }
}
