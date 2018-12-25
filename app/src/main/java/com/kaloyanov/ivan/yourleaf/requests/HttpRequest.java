package com.kaloyanov.ivan.yourleaf.requests;
/*
 * @author ivan.kaloyanov
 */
public class HttpRequest {

    private  String target;
    private String data;
    private String method;

    public HttpRequest(String target, String data, String method){
        this.setTarget(target);
        this.setData(data);
        this.setMethod(method);
    }

    public HttpRequest(String target, String method){
        this(target,null, method);
    }
    public HttpRequest(String target){
        this(target,null, "GET");
    }
    public HttpRequest(){}

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) { this.target = target; }

    public String getData() { return data; }

    public void setData(String data) {
        this.data = data;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
