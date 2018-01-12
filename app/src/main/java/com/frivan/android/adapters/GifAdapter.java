package com.frivan.android.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.frivan.android.gifsearcher.R;
import com.frivan.android.models.Datum;
import com.frivan.android.models.FixedWidth;
import com.frivan.android.models.Gif;

import java.util.List;
import java.util.Random;

/**
 * Adapter и Holder для RecyclerView(список GIF)
 */

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.GifHolder> {
    private static final Random rnd = new Random();
    private static final int ALPHA = 255;
    private static final int BOUND_COLOR = 256;

    private List<Datum> mValues;
    private Activity mContext;

    public GifAdapter(Activity context, Gif gif) {
        mContext = context;
        mValues = gif.getData();
    }

    @Override
    public GifHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_gif, parent, false);
        return new GifHolder(view);
    }

    @Override
    public void onBindViewHolder(GifHolder holder, int position) {
        holder.bindAlarm(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Назначить список гифок
     *
     * @param gif объект данных
     */
    public void setValues(Gif gif) {
        mValues = gif.getData();
    }

    class GifHolder extends RecyclerView.ViewHolder {
        private ImageView mGifContainer;

        private GifHolder(View itemView) {
            super(itemView);
            mGifContainer = itemView.findViewById(R.id.gif_container);
        }

        /**
         * Перерабатывает используемую view
         *
         * @param data текущая гифка
         */
        private void bindAlarm(Datum data) {
            FixedWidth fixedWidthData = data.getImages().getFixedWidth();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getHeightGif(fixedWidthData));
            int color = generateColor();

            mGifContainer.setLayoutParams(params);
            mGifContainer.setImageDrawable(null);
            mGifContainer.setBackgroundColor(color);

            Glide.with(mContext)
                    .load(Uri.parse(fixedWidthData.getUrl()))
                    .asGif()
                    .into(mGifContainer);
        }

        /**
         * Рассчитывает высоту Gif под пол экрана
         *
         * @param fixedWidthData текущая гифка
         * @return высота
         */
        private int getHeightGif(FixedWidth fixedWidthData) {
            int width = Integer.parseInt(fixedWidthData.getWidth());
            int height = Integer.parseInt(fixedWidthData.getHeight());
            Display display = mContext.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            return (metrics.widthPixels / 2) * height / width;
        }

        /**
         * Генерирует случайный цвет
         *
         * @return цвет
         */
        private int generateColor() {
            return Color.argb(ALPHA, rnd.nextInt(BOUND_COLOR), rnd.nextInt(BOUND_COLOR), rnd.nextInt(BOUND_COLOR));
        }

    }
}
