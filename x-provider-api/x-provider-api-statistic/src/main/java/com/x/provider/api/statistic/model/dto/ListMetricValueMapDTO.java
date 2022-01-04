package com.x.provider.api.statistic.model.dto;

import java.util.Map;

public class ListMetricValueMapDTO {
    private Map<String, Long> metricLongValues;
    private Map<String, Long> metricDoubleValues;

    public Builder builder() {
        return new Builder(this);
    }

    public static class Builder{
        ListMetricValueMapDTO listMetricValueMapDTO;

        public Builder(ListMetricValueMapDTO listMetricValueMapDTO){
            this.listMetricValueMapDTO = listMetricValueMapDTO;
        }
//
//        public Long getLongValue(int statTotalItemName, int statisticPeriod, String statisticObjectId){
//            return listStatisticTotalDTO.statisticTotalLongValues.get(getKey(statTotalItemName, statisticPeriod, statisticObjectId));
//        }
//
//        public Long getDoubleValue(int statTotalItemName, int statisticPeriod, String statisticObjectId){
//            return listStatisticTotalDTO.statisticTotalDoubleValues.get(getKey(statTotalItemName, statisticPeriod, statisticObjectId));
//        }
//
//        public static String getKey(int statisticObjectClassEnum, String statisticObjectId, int statTotalItemName, int statisticPeriod,){
//            return statTotalItemName + ":" + statisticPeriod + ":" + statisticObjectId;
//        }
//
//        public void putLongValue(int statTotalItemName, int statisticPeriod, String statisticObjectId, long value){
//            if (listStatisticTotalDTO.statisticTotalLongValues == null){
//                listStatisticTotalDTO.statisticTotalLongValues = new HashMap<>();
//            }
//            listStatisticTotalDTO.statisticTotalLongValues.put(getKey(statTotalItemName, statisticPeriod, statisticObjectId), value);
//        }
//
//        public void putDoubleValue(int statTotalItemName, int statisticPeriod, String statisticObjectId, long value){
//            if (listStatisticTotalDTO.statisticTotalDoubleValues == null){
//                listStatisticTotalDTO.statisticTotalDoubleValues = new HashMap<>();
//            }
//            listStatisticTotalDTO.statisticTotalDoubleValues.put(getKey(statTotalItemName, statisticPeriod, statisticObjectId), value);
//        }
    }

    public Map<String, Long> getMetricLongValues() {
        return metricLongValues;
    }

    public void setMetricLongValues(Map<String, Long> metricLongValues) {
        this.metricLongValues = metricLongValues;
    }

    public Map<String, Long> getMetricDoubleValues() {
        return metricDoubleValues;
    }

    public void setMetricDoubleValues(Map<String, Long> metricDoubleValues) {
        this.metricDoubleValues = metricDoubleValues;
    }
}
