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
import org.sonatype.nexus.proxy.item.StringContentLocator;
import org.sonatype.nexus.proxy.repository.ProxyRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.repository.RequestProcessor;
import org.sonatype.nexus.proxy.storage.UnsupportedStorageOperationException;
import org.sonatype.plexus.appevents.ApplicationEventMulticaster;

@Named("siteScanner")
public class JarReaderRequestProcessor implements RequestProcessor {
    @Inject
    private Logger logger;

    @Inject
    private ApplicationEventMulticaster applicationEventMulticaster;

    public boolean process(Repository repository, ResourceStoreRequest request, Action action) {
        logger.info(request.getRequestPath());

        if (request.getRequestPath().contains(JarUtil.SITE_CONTENT_PATTERN)) {
            try {
                ResourceStoreRequest siteRequest = JarUtil.getSiteRequest(request);

                boolean isExists = repository.getLocalStorage().containsItem(repository, siteRequest);
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

    public boolean shouldProxy(ProxyRepository repository, ResourceStoreRequest request) {
        return true;
    }

    public boolean shouldCache(ProxyRepository repository, AbstractStorageItem item) {
        return true;
    }
}