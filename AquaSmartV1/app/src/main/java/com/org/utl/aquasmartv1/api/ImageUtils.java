package com.org.utl.aquasmartv1.api;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public static Bitmap loadImage(Context context, String imageData) {
        if (imageData == null || imageData.isEmpty()) {
            return null;
        }

        try {
            if (isContentUri(imageData)) {
                Uri uri = Uri.parse(imageData);
                return getBitmapFromUri(context, uri);
            } else if (isBase64(imageData)) {
                return base64ToBitmap(imageData);
            }
        } catch (Exception e) {
            Log.e("ImageUtils", "Error al cargar imagen", e);
        }
        return null;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();

        try {
            // Intenta abrir directamente con MediaStore (para Android 10+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    return MediaStore.Images.Media.getBitmap(resolver, uri);
                } catch (SecurityException e) {
                    // Si falla por permisos, intenta con InputStream
                }
            }

            // Método alternativo con permisos explícitos
            try (InputStream input = resolver.openInputStream(uri)) {
                if (input != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2; // Reduce tamaño para evitar OutOfMemory
                    return BitmapFactory.decodeStream(input, null, options);
                }
            }
        } catch (Exception e) {
            Log.e("ImageUtils", "Error al cargar imagen", e);
        }

        return null;
    }

    private static Uri extractRealUriFromGooglePhotosUri(Uri googlePhotosUri) {
        try {
            String uriStr = googlePhotosUri.toString();
            // Buscar la URI real embebida en el parámetro
            int start = uriStr.indexOf("content%3A%2F%2F");
            if (start != -1) {
                String realUriStr = uriStr.substring(start);
                realUriStr = realUriStr.replace("content%3A%2F%2F", "content://");
                realUriStr = realUriStr.split("/ORIGINAL/")[0];
                return Uri.parse(realUriStr);
            }
        } catch (Exception e) {
            Log.e("ImageUtils", "Error al extraer URI real", e);
        }
        return null;
    }

    private static int calculateInSampleSize(Uri uri, Context context, int reqWidth, int reqHeight) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(inputStream, null, options);
        }

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return "";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String base64String) {
        try {
            if (base64String == null || base64String.isEmpty()) {
                return null;
            }

            String pureBase64 = base64String.contains(",") ?
                    base64String.split(",")[1] : base64String;

            byte[] decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            Log.e("ImageUtils", "Error al decodificar Base64", e);
            return null;
        }
    }

    public static boolean isContentUri(String uriString) {
        return uriString != null && uriString.startsWith("content://");
    }

    public static boolean isBase64(String data) {
        return data != null && data.matches("^[a-zA-Z0-9+/]*={0,2}$");
    }
}