package ru.noties.markwon.html;

import android.support.annotation.NonNull;

import java.io.IOException;

abstract class AppendableUtils {

    static void appendQuietly(@NonNull Appendable appendable, char c) {
        try {
            appendable.append(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void appendQuietly(@NonNull Appendable appendable, @NonNull CharSequence cs) {
        try {
            appendable.append(cs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void appendQuietly(@NonNull Appendable appendable, @NonNull CharSequence cs, int start, int end) {
        try {
            appendable.append(cs, start, end);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static <T extends Appendable & CharSequence> void appendNewLine(@NonNull T output){
        final int MAX_REPEATED_NEWLINE_ALLOWED = 2;

        boolean repeated = true;

        final int length = output.length();
        if (length > MAX_REPEATED_NEWLINE_ALLOWED) {
            for (int i = 0; i < MAX_REPEATED_NEWLINE_ALLOWED; i++) {
                if (output.charAt(length - i - 1) != '\n') {
                    repeated = false;
                    break;
                }
            }

            if (!repeated) {
                appendQuietly(output, '\n');
            }
        }
    }


    private AppendableUtils() {
    }
}
