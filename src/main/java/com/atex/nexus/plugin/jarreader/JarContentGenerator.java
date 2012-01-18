package com.atex.nexus.plugin.jarreader;

import javax.inject.Named;

import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.nexus.mime.MimeUtil;
import org.sonatype.nexus.proxy.IllegalOperationException;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.item.ContentGenerator;
import org.sonatype.nexus.proxy.item.ContentLocator;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.repository.Repository;

@Named(JarContentGenerator.ID)
public class JarContentGenerator implements ContentGenerator {
    public static final String ID = "JarContentGenerator";

    @Requirement
    private MimeUtil mimeUtil;

    public String getGeneratorId() {
        return ID;
    }

    public ContentLocator generateContent(Repository repository, String path, StorageFileItem item)
            throws IllegalOperationException, ItemNotFoundException, LocalStorageException {
        // make length unknown (since it will be known only in the moment of actual content pull)
        item.setLength(-1);

        return new JarContentLocator(repository, path, mimeUtil);
    }

}
