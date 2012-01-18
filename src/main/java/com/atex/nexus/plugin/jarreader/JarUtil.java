package com.atex.nexus.plugin.jarreader;

import org.sonatype.nexus.proxy.ResourceStoreRequest;

public class JarUtil {
    public static final String JAR_PATTERN = ".jar";
    public static final String JAR_CONTENT_PATTERN = JAR_PATTERN + "_";

    public static ResourceStoreRequest getSiteRequest(ResourceStoreRequest request) {
        int index = request.getRequestPath().indexOf(JAR_PATTERN);
        ResourceStoreRequest siteRequest = new ResourceStoreRequest(request);
        siteRequest.setRequestPath(request.getRequestPath().substring(0, index + JAR_PATTERN.length()));
        return siteRequest;
    }
}
