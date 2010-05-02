package com.google.jawema;

import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@ManagedBean
@SessionScoped
public class UserSession implements Serializable {

    private static Logger logger = Logger.getLogger(UserSession.class.getName());
    private String username;
    private String password;
    private String incomingMailServer;
    private String outgoingMailServer;
    private Store store;
    private String folderName;
    private Message[] messages;
    private int start;
    private int interval;

    public UserSession() {
        start = 1;
        interval = 5;
    }

    public String login() {
        logger.info("");
        if (store == null || !store.isConnected()) {
            Properties props = new Properties();
            props.put("mail.smtp.host", getOutgoingMailServer());
            props.put("mail.imap.host", getIncomingMailServer());
            try {
                javax.mail.Session session = javax.mail.Session.getDefaultInstance(props);
                store = session.getStore("imaps");
                store.connect(getIncomingMailServer(), getUsername(), getPassword());
            } catch (Exception e) {
                e.printStackTrace();
                return "index";
            }
        }
        return "workspace";
    }

    public void next() {
        start += interval;
    }

    public void prev() {
        start -= interval;
        if (start < 1) {
            start = 1;
        }
    }

    public boolean isPrev() {
        if (start == 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isLoggedIn() {
        if (store == null || !store.isConnected()) {
            return false;
        }
        return true;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Folder[] getFolders() {
        Folder[] folders = null;
        try {
            folders = store.getPersonalNamespaces();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return folders;
    }

    public String logout() {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            } catch (MessagingException ex) {
                Logger.getLogger(UserSession.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                store = null;
            }
        }
        return "index";
    }

    public DataModel<Message> getMessages() {
        try {
            if (!store.isConnected()) {
                store.connect();
            }
            Folder folder = store.getFolder("INBOX");
            if (!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }
            messages = folder.getMessages(start, start + interval);
        } catch (Exception e) {
            store = null;
            e.printStackTrace();
        }
        return new ArrayDataModel<Message>(messages);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String getIncomingMailServer() {
        if (incomingMailServer == null) {
            try {
                Context initial = new InitialContext();
                incomingMailServer = (String) initial.lookup("java:comp/env/mail/incomingMailServer");
            } catch (NamingException exception) {
                exception.printStackTrace();
            }
        }
        return incomingMailServer;
    }

    private String getOutgoingMailServer() {
        if (outgoingMailServer == null) {
            try {
                Context initial = new InitialContext();
                outgoingMailServer = (String) initial.lookup("java:comp/env/mail/outgoingMailServer");
            } catch (NamingException exception) {
                exception.printStackTrace();
            }
        }
        return outgoingMailServer;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
