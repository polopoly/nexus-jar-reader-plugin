package com.atex.nexus.plugin.jarreader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.sonatype.nexus.mime.MimeUtil;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.ContentLocator;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.Repository;

public class JarContentLocator implements ContentLocator {
    private String path;
    private File siteJarFile;
    private MimeUtil mimeUtil;

    public JarContentLocator(Repository repository, String path, MimeUtil mimeUtil) throws LocalStorageException {
        this.path = path;
        this.mimeUtil = mimeUtil;

        ResourceStoreRequest jarRequest = JarUtil.getJarRequest(new ResourceStoreRequest(path));
        recursiveProcessSiteJarFile(repository, jarRequest);
    }

    /*
     * stop process if return true
     */
    private boolean recursiveProcessSiteJarFile(Repository repository, ResourceStoreRequest siteRequest)
            throws LocalStorageException {

        if (repository.getRepositoryKind().isFacetAvailable(GroupRepository.class)) {
            GroupRepository groupRepository = (GroupRepository) repository;
            List<Repository> memberRepositories = groupRepository.getMemberRepositories();

            for (Repository memberRepo : memberRepositories) {
                boolean result = recursiveProcessSiteJarFile(memberRepo, siteRequest);
                if (result)
                    return true;
            }

            return true;
        } else if (repository.getLocalStorage().containsItem(repository, siteRequest)) {
            URL siteJarUrl = repository.getLocalStorage().getAbsoluteUrlFromBase(repository, siteRequest);
            siteJarFile = new File(siteJarUrl.getPath());
            return true;
        }
        return false;
    }

    private String getFilePathInJar(String path) {
        int index = path.indexOf(JarUtil.JAR_CONTENT_PATTERN);

        String filePath = path.substring(index + JarUtil.JAR_CONTENT_PATTERN.length() + 1);
        return filePath;
    }

    @Override
    public InputStream getContent() throws IOException {
        try {
            ZipFile zipFile = new ZipFile(siteJarFile);

            ZipEntry zipEntry = zipFile.getEntry(getFilePathInJar(path));
            return zipFile.getInputStream(zipEntry);
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getMimeType() {
        return mimeUtil.getMimeType(getFilePathInJar(path));
    }

    @Override
    public boolean isReusable() {
        return true;
    }
}
