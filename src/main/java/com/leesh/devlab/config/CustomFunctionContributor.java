package com.leesh.devlab.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Component;

@Component
public class CustomFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        BasicType<Double> resolveType = functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.DOUBLE);
        functionContributions.getFunctionRegistry().registerPattern("match","MATCH(?1) AGAINST (?2 IN BOOLEAN MODE)", resolveType);
        functionContributions.getFunctionRegistry().registerPattern("matches", "MATCH(?1, ?2) AGAINST (?3 IN BOOLEAN MODE)", resolveType);
    }
}
