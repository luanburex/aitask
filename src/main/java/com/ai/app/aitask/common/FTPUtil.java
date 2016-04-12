package com.ai.app.aitask.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

public class FTPUtil {
    private FTPClient client;
    private String    host;
    private int       port;
    private String    account;
    private String    password;

    public FTPUtil(String host, int port, String account, String password) {
        this.host = host;
        this.port = port;
        this.account = account;
        this.password = password;
        this.client = new FTPClient();
        // this.client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
    }

    public boolean retrieveFile(File remoteFile, File localFile) {
        return retrieveFile(remoteFile.getPath().replace("\\", "/"),
                localFile.getPath().replace("\\", "/"));
    }

    public boolean retrieveFile(String remotePath, String localPath) {
        boolean result = false;
        try {
            client.connect(host, port);
            client.login(account, password);
            client.enterLocalPassiveMode();
            if (client.getStatus(remotePath).contains(new File(remotePath).getName())) {
                result = client.retrieveFile(remotePath, new FileOutputStream(localPath));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getModifyTime(String remotePath) {
        String result = null;
        try {
            client.connect(host, port);
            client.login(account, password);
            client.enterLocalPassiveMode();
            result = client.getModificationTime("alextemp/082505.jar");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean appendFile(File remoteFile, File localFile) {
        // TODO windows下getPath会得到\, 暂时手动替换成linux的/
        return appendFile(remoteFile.getPath().replace("\\", "/"),
                localFile.getPath().replace("\\", "/"));
    }

    public boolean appendFile(String remotePath, String localPath) {
        boolean result = false;
        try {
            client.connect(host, port);
            client.login(account, password);
            client.enterLocalPassiveMode();
            result = client.appendFile(remotePath, FileUtil.readStream(localPath));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean deleteFile(String remotePath) {
        boolean result = false;
        try {
            client.connect(host, port);
            client.login(account, password);
            client.enterLocalPassiveMode();
            result = client.deleteFile(remotePath);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
