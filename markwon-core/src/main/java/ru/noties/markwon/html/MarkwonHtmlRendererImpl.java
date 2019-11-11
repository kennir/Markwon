package ru.noties.markwon.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.noties.markwon.MarkwonVisitor;

class MarkwonHtmlRendererImpl extends MarkwonHtmlRenderer {

    private final boolean allowNonClosedTags;
    private final Map<String, TagHandler> tagHandlers;

    MarkwonHtmlRendererImpl(boolean allowNonClosedTags, @NonNull Map<String, TagHandler> tagHandlers) {
        this.allowNonClosedTags = allowNonClosedTags;
        this.tagHandlers = tagHandlers;
    }

    @Override
    public void render(
            @NonNull final MarkwonVisitor visitor,
            @NonNull MarkwonHtmlParser parser) {

        final int end;
        if (!allowNonClosedTags) {
            end = HtmlTag.NO_END;
        } else {
            end = visitor.length();
        }

        parser.flushInlineTags(end, new MarkwonHtmlParser.FlushAction<HtmlTag.Inline>() {
            @Override
            public void apply(@NonNull List<HtmlTag.Inline> tags) {

                TagHandler handler;

                for (HtmlTag.Inline inline : tags) {

                    // if tag is not closed -> do not render
                    if (!inline.isClosed()) {
                        continue;
                    }

                    handler = tagHandler(inline.name());
                    if (handler != null) {
                        handler.handle(visitor, MarkwonHtmlRendererImpl.this, inline);
                    }
                }
            }
        });

        parser.flushBlockTags(end, new MarkwonHtmlParser.FlushAction<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull List<HtmlTag.Block> tags) {

                TagHandler handler;

                for (HtmlTag.Block block : tags) {

                    if (!block.isClosed()) {
                        continue;
                    }

                    handler = tagHandler(block.name());
                    if (handler != null) {
                        handler.handle(visitor, MarkwonHtmlRendererImpl.this, block);
                    } else {
                        // see if any of children can be handled
                        apply(block.children());
                    }
                }
            }
        });

        parser.reset();
    }

    @Nullable
    @Override
    public TagHandler tagHandler(@NonNull String tagName) {
        return tagHandlers.get(tagName);
    }

    static class BuilderImpl implements Builder {

        private final Map<String, TagHandler> tagHandlers = new HashMap<>(2);
        private boolean allowNonClosedTags;

        @NonNull
        @Override
        public Builder allowNonClosedTags(boolean allowNonClosedTags) {
            this.allowNonClosedTags = allowNonClosedTags;
            return this;
        }

        @NonNull
        @Override
        public Builder setHandler(@NonNull String tagName, @Nullable TagHandler tagHandler) {
            if (tagHandler == null) {
                tagHandlers.remove(tagName);
            } else {
                tagHandlers.put(tagName, tagHandler);
            }
            return this;
        }

        @NonNull
        @Override
        public Builder setHandler(@NonNull Collection<String> tagNames, @Nullable TagHandler tagHandler) {
            if (tagHandler == null) {
                for (String tagName : tagNames) {
                    tagHandlers.remove(tagName);
                }
            } else {
                for (String tagName : tagNames) {
                    tagHandlers.put(tagName, tagHandler);
                }
            }
            return this;
        }

        @Nullable
        @Override
        public TagHandler getHandler(@NonNull String tagName) {
            return tagHandlers.get(tagName);
        }

        @NonNull
        @Override
        public MarkwonHtmlRenderer build() {
            // okay, let's validate that we have at least one tagHandler registered
            // if we have none -> return no-op implementation
            return tagHandlers.size() > 0
                    ? new MarkwonHtmlRendererImpl(allowNonClosedTags, Collections.unmodifiableMap(tagHandlers))
                    : new MarkwonHtmlRendererNoOp();
        }
    }
}
