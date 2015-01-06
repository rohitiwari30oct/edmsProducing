package com.edms.service;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import edms.core.Config;

@Service
@Component
public class FolderServiceImpl implements FolderService {
	
	@Override
	public NodeIterator listFolders(String folderPath) {
		// TODO Auto-generated method stub
		Repository repository = new TransientRepository();
		NodeIterator nodeIterator=null;
		try {
			Session jcrsession = repository.login(new SimpleCredentials(
					Config.JCR_USERNAME, Config.JCR_PASSWORD.toCharArray()));
			
			Node root = jcrsession.getRootNode();
			nodeIterator = root.getNodes(folderPath);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeIterator;
	}

	
}
