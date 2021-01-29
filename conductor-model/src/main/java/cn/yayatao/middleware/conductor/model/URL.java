package cn.yayatao.middleware.conductor.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/***
 * url 标识节点信息
 * @author fireflyhoo
 */
public class URL implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 协议
     */
    private final String protocol;

    /***
     * 服务器名称
     */
    private final String host;

    /***
     * 端口
     */
    private final int port;

    /***
     * 参数
     */
    private final Map<String, String> parameters;

    public URL(String protocol, String host, int port, Map<String, String> parameters) {
        this.protocol = protocol;
        this.host = host;
        this.port = (Math.max(port, 0));
        if (parameters == null) {
            parameters = new HashMap<String, String>();
        } else {
            parameters = new HashMap<String, String>(parameters);
        }
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public static URL valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String host = null;
        int port = 0;
        Map<String, String> parameters = null;
        // separator between body and parameters
        int i = url.indexOf("?");
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");
            parameters = new HashMap<String, String>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if(i == 0){ throw new IllegalStateException("url missing protocol: \"" + url + "\"");}
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if(i>=0) {
                if(i == 0){ throw new IllegalStateException("url missing protocol: \"" + url + "\"");}
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }
        while(url.startsWith("/")){
            url = url.substring(1);
        }
        i = url.indexOf("/");
        if (i >= 0) {
            url = url.substring(0, i);
        }
        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) {
            host = url;
        }
        return new URL(protocol, host, port, parameters);
    }

    public static String decode(String value) {
        if(value != null && value.length() != 0) {
            try {
                return URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            return "";
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public URL setPort(int port) {
        return new URL(protocol, host, port, getParameters());
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String key) {
        return this.parameters.get(key);
    }

    public String getParameter(String key, String defaultValue) {
        String value = this.parameters.get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        URL other = (URL) obj;
        if (host == null) {
            if (other.host != null){
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (port != other.port){
            return false;
        }
        if (protocol == null) {
            if (other.protocol != null){
                return false;
            }
        } else if (!protocol.equals(other.protocol)){
            return false;
        }
        return true;
    }

    private void buildParameters(StringBuilder buf, boolean concat) {
        if (getParameters() != null && getParameters().size() > 0) {
            boolean first = true;
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(getParameters()).entrySet()) {
                if (entry.getKey() != null && entry.getKey().length() > 0) {
                    if (first) {
                        if (concat) {
                            buf.append("?");
                        }
                        first = false;
                    } else {
                        buf.append("&");
                    }
                    buf.append(entry.getKey());
                    buf.append("=");
                    buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (protocol != null && protocol.length() > 0) {
            buf.append(protocol);
            buf.append("://");
        }
        String host = getHost();
        if (host != null && host.length() > 0) {
            buf.append(host);
            if (port > 0) {
                buf.append(":");
                buf.append(port);
            }
        }
        buildParameters(buf, true);
        return buf.toString();
    }
}