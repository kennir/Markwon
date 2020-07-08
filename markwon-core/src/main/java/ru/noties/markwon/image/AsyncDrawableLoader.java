package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncDrawableLoader {

    /**
     * @since 3.0.0
     */
    public interface DrawableProvider {
        @Nullable
        Drawable provide();
    }

    /**
     * @since 3.0.0
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @since 3.0.0
     */
    @NonNull
    public static AsyncDrawableLoader noOp() {
        return new AsyncDrawableLoaderNoOp();
    }


    public abstract void load(@NonNull String destination, @NonNull AsyncDrawable drawable);

    public abstract void cancel(@NonNull String destination);

    @Nullable
    public abstract Drawable placeholder();

    public static class Builder {

        ExecutorService executorService;
        final Map<String, SchemeHandler> schemeHandlers = new HashMap<>(3);
        final Map<String, MediaDecoder> mediaDecoders = new HashMap<>(3);
        MediaDecoder defaultMediaDecoder;
        DrawableProvider placeholderDrawableProvider;
        DrawableProvider errorDrawableProvider;

        AsyncDrawableLoader implementation;

        @NonNull
        public Builder executorService(@NonNull ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        @NonNull
        public Builder addSchemeHandler(@NonNull String scheme, @NonNull SchemeHandler schemeHandler) {
            schemeHandlers.put(scheme, schemeHandler);
            return this;
        }

        @NonNull
        public Builder addSchemeHandler(@NonNull Collection<String> schemes, @NonNull SchemeHandler schemeHandler) {
            for (String scheme : schemes) {
                schemeHandlers.put(scheme, schemeHandler);
            }
            return this;
        }

        @NonNull
        public Builder addMediaDecoder(@NonNull String contentType, @NonNull MediaDecoder mediaDecoder) {
            mediaDecoders.put(contentType, mediaDecoder);
            return this;
        }

        @NonNull
        public Builder addMediaDecoder(@NonNull Collection<String> contentTypes, @NonNull MediaDecoder mediaDecoder) {
            for (String contentType : contentTypes) {
                mediaDecoders.put(contentType, mediaDecoder);
            }
            return this;
        }

        @NonNull
        public Builder removeSchemeHandler(@NonNull String scheme) {
            schemeHandlers.remove(scheme);
            return this;
        }

        @NonNull
        public Builder removeMediaDecoder(@NonNull String contentType) {
            mediaDecoders.remove(contentType);
            return this;
        }

        @NonNull
        public Builder defaultMediaDecoder(@Nullable MediaDecoder mediaDecoder) {
            this.defaultMediaDecoder = mediaDecoder;
            return this;
        }

        /**
         * @since 3.0.0
         */
        @NonNull
        public Builder placeholderDrawableProvider(@NonNull DrawableProvider placeholderDrawableProvider) {
            this.placeholderDrawableProvider = placeholderDrawableProvider;
            return this;
        }

        /**
         * @since 3.0.0
         */
        @NonNull
        public Builder errorDrawableProvider(@NonNull DrawableProvider errorDrawableProvider) {
            this.errorDrawableProvider = errorDrawableProvider;
            return this;
        }

        /**
         * Please note that if implementation is supplied, all configuration properties
         * (scheme-handlers, media-decoders, placeholder, etc) of this builder instance
         * will be ignored.
         *
         * @param implementation {@link AsyncDrawableLoader} implementation to be used.
         * @since 3.0.1
         */
        @NonNull
        public Builder implementation(@NonNull AsyncDrawableLoader implementation) {
            this.implementation = implementation;
            return this;
        }

        @NonNull
        public AsyncDrawableLoader build() {

            // NB, all other configuration properties will be ignored if
            // implementation is specified
            if (implementation != null) {
                return implementation;
            }

            // if we have no schemeHandlers -> we cannot show anything
            // OR if we have no media decoders
            if (schemeHandlers.size() == 0
                    || (mediaDecoders.size() == 0 && defaultMediaDecoder == null)) {
                return new AsyncDrawableLoaderNoOp();
            }

            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();
            }

            return new AsyncDrawableLoaderImpl(this);
        }
    }
}
