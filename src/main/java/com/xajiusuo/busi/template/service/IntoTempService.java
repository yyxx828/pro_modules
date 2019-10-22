package com.xajiusuo.busi.template.service;

import com.xajiusuo.busi.template.entity.IntoRecord;
import com.xajiusuo.busi.template.entity.IntoTemp;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;


public interface IntoTempService extends BaseService<IntoTemp, Integer> {

    Page<IntoTemp> findPageByEntity(Pageable pageable, IntoTemp manageTemplates);

    /***
     * 文件导入
     * @param entity
     * @param file
     * @param charSet
     * @param startLine
     * @param ir
     * @param user 导入用户
     */
    void importFile(IntoTemp entity, File file, String charSet, Integer startLine, IntoRecord ir, UserInfoVo user);

    /***
     * 模板删除,同时删除导入记录中对应关联,避免造成错误
     * @param id
     */
    void deleteTemplate(Integer id);

    IntoTemp getIntoTemp(Integer id);
}
