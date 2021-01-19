package com.epam.healenium.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.PsiShortNamesCache;
import okhttp3.HttpUrl;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by nkobzev.
 */
public class Utils {

    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    /**
     * @param filePath
     * @param project
     */
    public static void openFileInPanel(final String filePath, final Project project) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(filePath);
                if (file != null && file.isValid()) {
                    FileEditorProvider[] providers = FileEditorProviderManager.getInstance()
                            .getProviders(project, file);
                    if (providers.length != 0) {
                        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
                        FileEditorManager.getInstance(project).openTextEditor(descriptor, false);
                    }
                }
            }
        });
    }

    public static HttpUrl.Builder getURLBuilder(Project project) {
        return HttpUrl.parse("http://"
                + getHealeniumPropertyValue(project, "serverHost") + ":"
                + getHealeniumPropertyValue(project, "serverPort")
                + "/healenium/healing")
                .newBuilder();
    }

    public static String getHealeniumPropertyValue(Project project, String key) {
        PsiFile[] files = PsiShortNamesCache.getInstance(project).getFilesByName("healenium.properties");
        if (files != null && files.length > 0) {
            List<PsiElement> psiElementList = Utils.iterableTree(files[0].getChildren(), null);
            return psiElementList.stream()
                    .filter(element -> element.getFirstChild() != null
                            && element.getLastChild() != null
                            && key.equals(element.getFirstChild().getText()))
                    .findFirst()
                    .map(element -> element.getLastChild().getText())
                    .get();
        }
        return null;
    }

    public static List<PsiElement> iterableTree(PsiElement[] children, Predicate<PsiElement> predicate) {
        List<PsiElement> psiElementList = new ArrayList();
        if (children == null || children.length == 0) {
            return psiElementList;
        }

        Arrays.stream(children).
                forEach(element -> {
                    if (predicate == null || predicate.test(element)) {
                        psiElementList.add(element);
                    }
                    psiElementList.addAll(iterableTree(element.getChildren(), predicate));

                });

        return psiElementList;
    }

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
