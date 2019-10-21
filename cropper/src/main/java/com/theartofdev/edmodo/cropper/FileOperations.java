package com.theartofdev.edmodo.cropper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileOperations {

    private static final String UNISALUTE_PATH = "sdcard/unisalute";
    private static final String SCHEME = "content";

    private final Context context;
    private final Uri uri;
    private final AsyncTask<List<PdfFile>, Integer, Boolean> exportFiles;

    public FileOperations(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;

        exportFiles = new SaveFileAsync().execute();
    }

    private class SaveFileAsync extends AsyncTask<List<PdfFile>, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(List<PdfFile>... lists) {
            try {
                InputStream inputStream =  context.getContentResolver().openInputStream(uri);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                File usbFolder = new File(UNISALUTE_PATH);
                if (!usbFolder.exists()) {
                    usbFolder.mkdirs();
                    usbFolder.setReadable(true, false);
                    usbFolder.setWritable(true, false);
                    usbFolder.setExecutable(true, false);
                }
                FileOutputStream fos = new FileOutputStream(new File(usbFolder.getPath(), getFileName(uri)));
                byte[] b = new byte[100*1024];
                int j;

                while (!exportFiles.isCancelled()) {
                    if (!((j = bis.read(b)) != -1)) break;
                    fos.write(b, 0, j);
                }

                fos.flush();
                fos.getFD().sync();

                fos.close();
                bis.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals(SCHEME)) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Bitmap openPdfWithAndroidSDK(int pageNumber, String fileName) throws IOException {
        PdfRenderer pdfRenderer;
        PdfRenderer.Page pdfPage;

        File fileCopy = new File(UNISALUTE_PATH, fileName);

        // We will get a page from the PDF file by calling openPage
        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(fileCopy, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(fileDescriptor);
        pdfPage = pdfRenderer.openPage(pageNumber);

        // Create a new bitmap and render the page contents on to it
        Bitmap bitmap = Bitmap.createBitmap(pdfPage.getWidth(), pdfPage.getHeight(), Bitmap.Config.ARGB_8888);
        pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        return bitmap;
    }
}
