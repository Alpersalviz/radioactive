package com.active.radioactive.data.model;

import java.io.Serializable;
import java.util.List;

public class StationModel implements Serializable {

    public String Id;
    public String Name;
    public String IconUrl;
    public String Description;
    public List<UrlModel> Urls;
}
