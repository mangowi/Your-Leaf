package com.kaloyanov.ivan.yourleaf.responses;
/*
 * @author ivan.kaloyanov
 */
public class HttpResponse {

    private String data;
    private Integer status;

    public HttpResponse(Integer status, String data){
        this.setStatus(status);
        this.setData(data);
    }

    public HttpResponse(){ }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
