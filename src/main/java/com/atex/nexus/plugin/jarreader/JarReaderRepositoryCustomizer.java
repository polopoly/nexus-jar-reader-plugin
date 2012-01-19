package com.atex.nexus.plugin.jarreader;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.configuration.ConfigurationException;
import org.sonatype.nexus.plugins.RepositoryCustomizer;
import org.sonatype.nexus.proxy.repository.HostedRepository;
import org.sonatype.nexus.proxy.repository.ProxyRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.repository.RequestProcessor;

public class JarReaderRepositoryCustomizer implements RepositoryCustomizer {
    @Inject
    private @Named("jarReader")
    RequestProcessor jarReaderRequestProcessor;

    public boolean isHandledRepository(Repository repository) {
        return repository.getRepositoryKind().isFacetAvailable(HostedRepository.class)
                || repository.getRepositoryKind().isFacetAvailable(ProxyRepository.class);
    }

    public void configureRepository(Repository repository) throws ConfigurationException {
        repository.getRequestProcessors().put("jarReader", jarReaderRequestProcessor);
    }
}
