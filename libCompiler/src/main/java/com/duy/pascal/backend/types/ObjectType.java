package com.duy.pascal.backend.types;

public abstract class ObjectType extends InfoType {

    public abstract DeclaredType getMemberType(String name);

    public String getEntityType() {
        return "object type";
    }

}