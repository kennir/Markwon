package ru.noties.markwon.html.tag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.StrongEmphasis;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.html.HtmlTag;

public class StrongEmphasisHandler extends SimpleTagHandler {
    @Nullable
    @Override
    public Object getSpans(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull HtmlTag tag) {
        final SpanFactory spanFactory = configuration.spansFactory().get(StrongEmphasis.class);
        if (spanFactory == null) {
            return null;
        }
        return spanFactory.getSpans(configuration, renderProps);
    }
}
