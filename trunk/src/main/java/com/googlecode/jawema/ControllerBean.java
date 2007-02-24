package com.googlecode.jawema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.myfaces.tobago.model.SheetState;
import org.apache.myfaces.tobago.model.TreeState;

public class ControllerBean {

    private static Logger log = Logger.getLogger(ControllerBean.class.getName());

	private String username;
	private String password;
    private Properties props;
    private String host;
    private String protocol;
    private TreeState treeState;
    private SheetState sheetState;
    private Folder selectedFolder;
    private Session session;
    private Store store;
	private boolean authenticated = false;

    private TreeNode treeNode;

    public boolean isAuthenticated(){
        return authenticated;
    }
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ControllerBean(){
        configure();
		props = new Properties();
        session = Session.getInstance(props, null);
    }
    
    private List<Folder> getFolders() throws MessagingException{
        List<Folder> retValue = new ArrayList<Folder>();
        Folder[] folders = this.getDefaultFolder().list();
        for (int i = 0; i<folders.length; i++){
            retValue.add(folders[i]);
        }
        return retValue;
    }

    
    public TreeNode getFolderView(){
        if (treeNode!=null) return treeNode;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        TreeWrapper wrapper = new TreeWrapper();
        try{
            wrapper.setId("root_1");
            wrapper.setName(store.getDefaultFolder().getURLName().toString());
            wrapper.setValue(store.getDefaultFolder());
            root.setUserObject(wrapper);
            Folder f = null;
            DefaultMutableTreeNode folderNode = null;
            int j = 0;
            for (Iterator<Folder> i = getFolders().iterator(); i.hasNext();){
                f = i.next();
                wrapper = new TreeWrapper();
                wrapper.setName(f.getName());
                if (f.getName().equalsIgnoreCase("INBOX")) setSelectedFolder(f);
                wrapper.setId("folder_"+j++);
                wrapper.setValue(f);
                folderNode = new DefaultMutableTreeNode();
                folderNode.setUserObject(wrapper);                
                root.add(folderNode);
            }
        }catch(Exception e){
            log.throwing(ControllerBean.class.getName(), "getTreeNode", e);
        }
        log.exiting(ControllerBean.class.getName(), "getTreeNode", root);
        this.treeNode = root;
        return root;
    }
    
    public Folder getDefaultFolder(){
        Folder retValue = null;
        try {
            retValue = store.getDefaultFolder();
        } catch (MessagingException e) {
            log.throwing(ControllerBean.class.getName(), "getDefaultFolder", e);
        }
        return retValue;
    }

    public void treeSelected(){
        TreeWrapper wrapper = (TreeWrapper) treeState.getMarker().getUserObject();
        setSelectedFolder(wrapper.getValue());
        log.info(wrapper.getName());
    }
    public void sheetSelected(){
        log.info("how many rows selected? "+sheetState.getSelectedRows().size());
    }
    
    public String login(){
        try{
            store = session.getStore(protocol);
            store.connect(host, username, password);
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            log.info("message count: "+folder.getMessageCount());
            authenticated = true;
        }catch(Exception e){
            authenticated = false;
            log.throwing(ControllerBean.class.getName(), "login", e);
        }
        if(authenticated){
            return "successful";
        }else{
            return "failed";            
        }
	}

    public String getPassword() {
		return password;
	}

    public Message[] getMessages(){
        Message[] retValue = null;
        Folder selected = getSelectedFolder();
        try {
            if (selected.isOpen()) selected.close(false);
            selected.open(Folder.READ_ONLY);
            retValue = selected.getMessages();
        } catch (MessagingException e) {
            log.throwing(getClass().getName(), "getMessages", e);
        }
        return retValue;
    }
    
	private void configure(){
        Properties configProperties = new Properties();
        try {
            configProperties.load(getClass().getResourceAsStream("/config.properties"));
        } catch (Exception e) {
            log.throwing(getClass().getName(), "configure", e);
        }
        protocol = configProperties.getProperty("mail.protocol","imaps");
        host = configProperties.getProperty("mail.host","localhost");
        configProperties = null;   
    }

    public TreeState getTreeState() {
        return treeState;
    }

    public void setTreeState(TreeState treeState) {
        log.info(""+treeState);
        this.treeState = treeState;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(Folder selectedFolder) {
        if (this.selectedFolder != null && this.selectedFolder.isOpen())
            try {
                this.selectedFolder.close(false);
            } catch (MessagingException e) {
                log.throwing(getClass().getName(), "getSelectedFolder", e);
            }
        
        this.selectedFolder = selectedFolder;
    }

    public SheetState getSheetState() {
        return sheetState;
    }

    public void setSheetState(SheetState sheetState) {
        this.sheetState = sheetState;
    }

}
