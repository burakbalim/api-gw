package com.application.gateway.common.util;

import lombok.Getter;

import java.util.List;

@Getter
public class DynamicWrapper {

    private String className;
    private List<InnerData> innerDataList;

    public DynamicWrapper(String className, List<InnerData> innerDataList) {
        this.className = className;
        this.innerDataList = innerDataList;
    }

    @Override
    public String toString() {
        return "DynamicWrapper{" +
                "className='" + className + '\'' +
                ", innerDataList=" + innerDataList +
                '}';
    }
}
