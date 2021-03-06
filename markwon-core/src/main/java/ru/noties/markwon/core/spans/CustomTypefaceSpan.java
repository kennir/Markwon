package ru.noties.markwon.core.spans;

import android.graphics.Typeface;
import androidx.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * A span implementation that allow applying custom Typeface. Although it is
 * not used directly by the library, it\'s helpful for customizations.
 * <p>
 * Please note that this implementation does not validate current paint state
 * and won\'t be updating/modifying supplied Typeface.
 *
 * @since 3.0.0
 */
public class CustomTypefaceSpan extends MetricAffectingSpan {

    @NonNull
    public static CustomTypefaceSpan create(@NonNull Typeface typeface) {
        return new CustomTypefaceSpan(typeface);
    }

    private final Typeface typeface;

    public CustomTypefaceSpan(@NonNull Typeface typeface) {
        this.typeface = typeface;
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint p) {
        updatePaint(p);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        updatePaint(tp);
    }

    private void updatePaint(@NonNull TextPaint paint) {
        paint.setTypeface(typeface);
    }
}
