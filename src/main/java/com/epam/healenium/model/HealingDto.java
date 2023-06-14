package com.epam.healenium.model;

import com.intellij.psi.PsiElement;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

public class HealingDto {

    private String locator;
    private String className;
    private String methodName;
    private Set<HealingResultDto> results;
    private PsiElement methodCall;

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Set<HealingResultDto> getResults() {
        return results;
    }

    public void setResults(Set<HealingResultDto> results) {
        this.results = results;
    }

    public PsiElement getMethodCall() {
        return methodCall;
    }

    public HealingDto setMethodCall(PsiElement methodCall) {
        this.methodCall = methodCall;
        return this;
    }
}
