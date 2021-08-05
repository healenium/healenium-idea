package com.epam.healenium.model;

import java.util.Arrays;

public enum ByArgumentEnum {

    BY_ID("By.id", "id"),
    BY_NAME("By.name", "name"),
    BY_CLASS_NAME("By.className", "className"),
    BY_CSS("By.cssSelector", "css"),
    BY_TAG_NAME("By.tagName", "tagName"),
    BY_PARTIAL_LINK_TEXT("By.partialLinkText", "partialLinkText"),
    BY_LINK_TEXT("By.linkText", "linkText"),
    BY_XPATH("By.xpath", "xpath");

    private String locatorValue;
    private String annotationValue;

    ByArgumentEnum(String locatorValue, String annotationValue) {
        this.locatorValue = locatorValue;
        this.annotationValue = annotationValue;
    }

    public static String getAnnotationValue(String locatorValue) {
        return Arrays.stream(ByArgumentEnum.values())
                .filter(a -> locatorValue.equals(a.locatorValue))
                .findFirst()
                .map(a -> a.annotationValue)
                .orElse(null);
    }

}
