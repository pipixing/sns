package com.pipixing.sns.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class Feed {
    private int id;
    //这里的type指的是EVENTTYPE
    private int type;
    private int user_id;
    private Date created_date;
    private String data;
    private JSONObject dataJSON = null;

    public Feed setId(int id){this.id=id;return this;}
    public Feed setType(int type){this.type=type;return this;}
    public Feed setUserId(int userId){this.user_id=userId;return this;}
    public Feed setCreateDate(Date creatDate){this.created_date=creatDate;return this;}
    public Feed setData(String data){this.data=data;dataJSON = JSONObject.parseObject(data);return this;}
    public Feed setDataJSON(JSONObject dataObject){this.dataJSON=dataObject;dataJSON = JSONObject.parseObject(data);return this;}

    public int getId(){return id;}
    public int getType(){return type;}
    public int getUserId(){return user_id;}
    public Date getCreatedDate(){return created_date;}
    public String getData(){return data;}
    public JSONObject getDataJSON(){return dataJSON;}

}
