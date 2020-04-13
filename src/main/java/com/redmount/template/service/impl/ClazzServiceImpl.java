package com.redmount.template.service.impl;

import com.redmount.template.core.AbstractModelService;
import com.redmount.template.model.ClazzModel;
import com.redmount.template.repo.ClazzModelRepo;
import com.redmount.template.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ClazzServiceImpl extends AbstractModelService<ClazzModel> implements ClazzService {
    @Resource
    ClazzModelRepo repo;

    @Override
    public ClazzModel getAutomatically(String pk, String relations) {
        ClazzModel clazzModel = super.getAutomatically(pk, relations);
        clazzModel.setStudentsCount(repo.getStudentsCountByClassPk(clazzModel.getPk()));
        return clazzModel;
    }
}
