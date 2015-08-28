//package com.vlls.web.model;
//
//import com.vlls.jpa.dict.DictItemPersistable;
//
///**
// * Created by hiephn on 2014/10/03.
// */
//public class DictItemResponse implements DataResponse<DictItemPersistable> {
//    private String name;
//    private String meaning;
//    private String pronun;
//    private String type;
//    private Boolean hasAudio;
//
//    @Override
//    public void setData(DictItemPersistable entity) {
//        this.name = entity.getWord();
//        this.meaning = entity.getContent();
//        this.hasAudio = entity.getHasAudio();
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getMeaning() {
//        return meaning;
//    }
//
//    public void setMeaning(String meaning) {
//        this.meaning = meaning;
//    }
//
//    public String getPronun() {
//        return pronun;
//    }
//
//    public void setPronun(String pronun) {
//        this.pronun = pronun;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public Boolean getHasAudio() {
//        return hasAudio;
//    }
//
//    public void setHasAudio(Boolean hasAudio) {
//        this.hasAudio = hasAudio;
//    }
//}
