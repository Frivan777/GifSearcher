package com.frivan.android.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Класс для работы с клавиатурой
 */
public class KeyboardUtils {

    /**
     * Метод для принудительного скрытия клавиатуры
     *
     * @param activity активность
     */
    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * Метод для показа клавиатуры и фокусировки на указанную View
     *
     * @param context  контекст
     * @param editText view, на которую идет фокус
     */
    public static void showKeyboard(Context context, EditText editText) {
        if (context != null && editText != null) {
            editText.requestFocus();
            editText.setSelection(editText.getText().toString().length());
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }
}
