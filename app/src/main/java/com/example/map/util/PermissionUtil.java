package com.example.map.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.Optional;

public class PermissionUtil {
    public static Optional<File> fileFromUri(Context context, Uri uri) {
        try {
            var stream = context.getContentResolver().openInputStream(uri);
            if (stream == null) {
                return Optional.empty();
            }
            String filename = String.valueOf(System.currentTimeMillis());
            File targetFile = new File(context.getFilesDir(), filename);
            try (var targetStream = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
                var buffer = new byte[1024];
                int length;
                while ((length = stream.read(buffer)) > 0) {
                    targetStream.write(buffer, 0, length);
                }
                stream.close();
                return Optional.of(targetFile);
            }
        }
        catch (Exception e) {
            Log.e("PermissionUtil", "fileFromUri: ", e);
            return Optional.empty();
        }
    }
}
