package com.epam.healenium.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl;

import java.util.function.Predicate;

public class Predicates {

    public static Predicate<PsiElement> isPsiJavaTocken() {
        return psiElement -> psiElement instanceof PsiJavaTokenImpl
                && "STRING_LITERAL".equals(((PsiJavaToken) psiElement).getTokenType().toString())
                && psiElement.getText().contains("\"");
    }
}
