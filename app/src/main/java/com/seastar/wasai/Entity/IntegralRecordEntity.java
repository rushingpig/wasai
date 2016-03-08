package com.seastar.wasai.Entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jamie on 2015/6/23.
 */
public class IntegralRecordEntity implements Serializable{
    public List<IntegralRecordListEntity> list;
    public IntegralRecordExtraEntity extra;

}
