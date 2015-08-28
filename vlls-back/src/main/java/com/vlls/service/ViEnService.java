//package com.vlls.service;
//
//import com.vlls.exception.ServerTechnicalException;
//import com.vlls.jpa.dict.vien.ViEn;
//import com.vlls.jpa.dict.vien.ViEnRepository;
//import com.vlls.web.model.DictItemResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//
///**
//* Created by thongvh on 2014/10/23.
//*/
//@Service
//public class ViEnService extends DictionaryService {
//
//    @Autowired
//    private ViEnRepository viEnRepository;
//
//    public List<Map<Integer, String>> getWordList(String key, int size) {
//        List<Map<Integer, String>> list = viEnRepository.findListWordByWordGreaterThanEqual(key, new PageRequest(0, size));
//        return list;
//    }
//
//    public List<DictItemResponse> get(Integer id) throws ServerTechnicalException {
//        ViEn viEn = viEnRepository.findOne(id);
//        List<DictItemResponse> itemResponses = this.parseWordContent(viEn.getWord(), viEn.getContent(), viEn.getHasAudio());
//        return itemResponses;
//    }
//}
