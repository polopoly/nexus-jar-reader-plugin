package com.atex.nexus.plugin.jarreader;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.sonatype.nexus.proxy.IllegalOperationException;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.access.Action;
import org.sonatype.nexus.proxy.item.AbstractStorageItem;
import org.sonatype.nexus.proxy.item.DefaultStorageFileItem;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.item.StringContentLocator;
import org.sonatype.nexus.proxy.repository.ProxyRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.repository.RequestProcessor;
import org.sonatype.nexus.proxy.storage.UnsupportedStorageOperationException;

@Named("jarReader")
public class JarReaderRequestProcessor implements RequestProcessor {
    @Inject
    private Logger logger;

    public boolean process(Repository repository, ResourceStoreRequest request, Action action) {
        if (isJarContentPath(request)) {
            try {
                boolean isExists = false;
                ResourceStoreRequest jarRequest = JarUtil.getJarRequest(request);

                // check if localStorage has the jar or not
                isExists = repository.getLocalStorage().containsItem(repository, jarRequest);

                // the jar not exists at localStorage, try to search at remoteStorage if it is Proxy Repository
                if (!isExists && repository.getRepositoryKind().isFacetAvailable(ProxyRepository.class)) {
                    ProxyRepository proxyRepository = (ProxyRepository) repository;
                    isExists = proxyRepository.getRemoteStorage().containsItem(proxyRepository, jarRequest);

                    // if jar exists at remoteStorage, save it locally
                    if (isExists) {
                        StorageItem item = proxyRepository.getRemoteStorage().retrieveItem(proxyRepository, jarRequest,
                                proxyRepository.getRemoteUrl());
                        proxyRepository.storeItem(false, item);
                    }
                }

                if (isExists) {
                    DefaultStorageFileItem file = new DefaultStorageFileItem(repository, new ResourceStoreRequest(
                            request.getRequestPath()), true, false, new StringContentLocator(JarContentGenerator.ID));

                    file.setContentGeneratorId(JarContentGenerator.ID);
                    repository.storeItem(false, file);
                }
            } catch (LocalStorageException e) {
                logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (UnsupportedStorageOperationException e) {
                logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (IllegalOperationException e) {
                logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (Exception e) {
                logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }

        return true;
    }

    private boolean isJarContentPath(ResourceStoreRequest request) {
        String url = request.getRequestPath();
        if (url.contains(JarUtil.JAR_CONTENT_PATTERN)) {
            if (url.endsWith(JarUtil.JAR_CONTENT_PATTERN + ".sha1"))
                return false;

            if (url.endsWith(JarUtil.JAR_CONTENT_PATTERN + ".md5"))
                return false;

            // if it is not a directory.Eg: /com/atex/plugin/youtube-jar
            if (url.endsWith(JarUtil.JAR_CONTENT_PATTERN))
                return false;

            // if it is not a directory. Eg: not /com/atex/plugin/youtube-jar/
            if (url.endsWith(JarUtil.JAR_CONTENT_PATTERN + "/"))
                return false;

            return true;
        }
        return false;
    }

    public boolean shouldProxy(ProxyRepository repository, ResourceStoreRequest request) {
        return true;
    }

    public boolean shouldCache(ProxyRepository repository, AbstractStorageItem item) {
        return true;
    }
}
