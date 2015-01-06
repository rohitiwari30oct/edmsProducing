package com.edms.service;

import javax.jcr.NodeIterator;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public interface FolderService {

	public NodeIterator listFolders(String folderPath);
}
