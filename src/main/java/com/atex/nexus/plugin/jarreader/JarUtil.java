package com.atex.nexus.plugin.jarreader;

import org.sonatype.nexus.proxy.ResourceStoreRequest;

public class JarUtil {
    public static final String SITE_JAR_PATTERN = "-site.jar";
    public static final String SITE_CONTENT_PATTERN = SITE_JAR_PATTERN + "_";

    public static ResourceStoreRequest getSiteRequest(ResourceStoreRequest request) {
        int index = request.getRequestPath().indexOf(SITE_JAR_PATTERN);
        ResourceStoreRequest siteRequest = new ResourceStoreRequest(request);
        siteRequest.setRequestPath(request.getRequestPath().substring(0, index + SITE_JAR_PATTERN.length()));
        return siteRequest;
    }
}
