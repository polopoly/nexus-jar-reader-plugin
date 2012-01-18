package com.atex.nexus.plugin.jarreader;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.configuration.ConfigurationException;
import org.sonatype.nexus.plugins.RepositoryCustomizer;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.repository.RequestProcessor;

public class JarReaderRepositoryCustomizer implements RepositoryCustomizer {
    @Inject
    private @Named("jarReader")
    RequestProcessor jarReaderRequestProcessor;

    public boolean isHandledRepository(Repository repository) {
        // handle proxy reposes only
        // return repository.getRepositoryKind().isFacetAvailable( ProxyRepository.class );

        return true;
    }

    public void configureRepository(Repository repository) throws ConfigurationException {
        repository.getRequestProcessors().put("jarReader", jarReaderRequestProcessor);
    }
}
