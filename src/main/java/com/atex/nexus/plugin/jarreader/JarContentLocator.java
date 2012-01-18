package com.atex.nexus.plugin.jarreader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.sonatype.nexus.mime.MimeUtil;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.ContentLocator;
import org.sonatype.nexus.proxy.repository.Repository;

public class JarContentLocator implements ContentLocator {
    private Repository repository;
    private String path;
    // private File file;
    private File siteJarFile;
    private MimeUtil mimeUtil;

    public JarContentLocator(Repository repository, String path, MimeUtil mimeUtil) throws LocalStorageException {
        this.repository = repository;
        this.path = path;
        this.mimeUtil = mimeUtil;

        ResourceStoreRequest siteRequest = JarUtil.getSiteRequest(new ResourceStoreRequest(path));

        URL siteJarUrl = repository.getLocalStorage().getAbsoluteUrlFromBase(repository, siteRequest);
        siteJarFile = new File(siteJarUrl.getPath());

        /*try {
            URL fileUrl = new URL("jar:file:" + siteJarUrl.getPath() + "!" + getFilePathInJar(path));
            file = new File(fileUrl.getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
        /*
        try {
            ZipFile zipFile = new ZipFile(siteJarFile);
            ZipEntry zipEntry = zipFile.getEntry(getFilePathInJar(path));
            zipEntry.
            
            
            
            
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private String getFilePathInJar(String path) {
        int index = path.indexOf(JarUtil.SITE_CONTENT_PATTERN);

        String filePath = path.substring(index + JarUtil.SITE_CONTENT_PATTERN.length() + 1);
        return filePath;
    }

    @Override
    public InputStream getContent() throws IOException {
        // return new StringContentLocator("Just to say hi").getContent();
        // return FileUtils.openInputStream(file);

        try {
            ZipFile zipFile = new ZipFile(siteJarFile);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                System.out.println(entry.getName());
            }

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
