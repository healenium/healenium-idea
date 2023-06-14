package com.epam.healenium.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nkobzev.
 */
public class Utils {

    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

//    /**
//     * @param filePath
//     * @param project
//     */
//    public static void openFileInPanel(final String filePath, final Project project) {
//        ApplicationManager.getApplication().invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(filePath);
//                if (file != null && file.isValid()) {
//                    FileEditorProvider[] providers = FileEditorProviderManager.getInstance()
//                            .getProviders(project, file);
//                    if (providers.length != 0) {
//                        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
//                        FileEditorManager.getInstance(project).openTextEditor(descriptor, false);
//                    }
//                }
//            }
//        });
//    }

    /**
     *
     */
    public static void openDirectory(String directory) {
        File file = new File(directory);
        if (!file.exists() || !file.isDirectory()) {
            return;
        }
        try {
            java.awt.Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public static boolean isConnected(String url) {
        try {
            HttpURLConnection conn =
                    (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");
            return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
