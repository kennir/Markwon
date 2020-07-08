package ru.noties.markwon.ext.tasklist;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.utils.LeadingMarginUtils;

/**
 * @since 1.0.1
 */
public class TaskListSpan implements LeadingMarginSpan {

    private static final int[] STATE_CHECKED = new int[]{android.R.attr.state_checked};

    private static final int[] STATE_NONE = new int[0];

    private final MarkwonTheme theme;
    private final Drawable drawable;
    private final int blockIndent;

    // @since 2.0.1 field is NOT final (to allow mutation)
    private boolean isDone;


    public TaskListSpan(@NonNull MarkwonTheme theme, @NonNull Drawable drawable, int blockIndent, boolean isDone) {
        this.theme = theme;
        this.drawable = drawable;
        this.blockIndent = blockIndent;
        this.isDone = isDone;
    }

    /**
     * @since 2.0.1
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Update {@link #isDone} property of this span. Please note that this is merely a visual change
     * which is not changing underlying text in any means.
     *
     * @since 2.0.1
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return theme.getBlockMargin() * blockIndent;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if (!first
                || !LeadingMarginUtils.selfStart(start, text, this)) {
            return;
        }

        final int save = c.save();
        try {

            final int width = theme.getBlockMargin();
            final int height = bottom - top;

            final int w = (int) (width * .75F + .5F);
            final int h = (int) (height * .75F + .5F);

            drawable.setBounds(0, 0, w, h);

            if (drawable.isStateful()) {
                final int[] state;
                if (isDone) {
                    state = STATE_CHECKED;
                } else {
                    state = STATE_NONE;
                }
                drawable.setState(state);
            }

            final int l;
            if (dir > 0) {
                l = x + (width * (blockIndent - 1)) + ((width - w) / 2);
            } else {
                l = x - (width * blockIndent) + ((width - w) / 2);
            }

            final int t = top + ((height - h) / 2);

            c.translate(l, t);
            drawable.draw(c);

        } finally {
            c.restoreToCount(save);
        }
    }
}
