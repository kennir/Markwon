package ru.noties.markwon.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;

/**
 * This class can be used as the last {@link MediaDecoder} to _try_ to handle all rest cases.
 * Here we just assume that supplied InputStream is of image type and try to decode it.
 *
 * @since 1.1.0
 */
public class ImageMediaDecoder extends MediaDecoder {

    @NonNull
    public static ImageMediaDecoder create(@NonNull Resources resources) {
        return new ImageMediaDecoder(resources);
    }

    private final Resources resources;

    @SuppressWarnings("WeakerAccess")
    ImageMediaDecoder(Resources resources) {
        this.resources = resources;
    }

    @Nullable
    @Override
    public Drawable decode(@NonNull InputStream inputStream) {

        final Drawable out;

        // absolutely not optimal... thing
        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (bitmap != null) {
            out = new BitmapDrawable(resources, bitmap);
            DrawableUtils.applyIntrinsicBounds(out);
        } else {
            out = null;
        }

        return out;
    }
}
