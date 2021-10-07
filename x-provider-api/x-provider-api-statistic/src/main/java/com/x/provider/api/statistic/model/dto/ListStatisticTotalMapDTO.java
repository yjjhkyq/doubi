package com.x.provider.api.statistic.model.dto;

import java.util.HashMap;
import java.util.Map;

public class ListStatisticTotalMapDTO {
    private Map<String, Long> statisticTotalLongValues;
    private Map<String, Long> statisticTotalDoubleValues;

    public Builder builder() {
        return new Builder(this);
    }

    public static class Builder{
        ListStatisticTotalMapDTO listStatisticTotalDTO;

        public Builder(ListStatisticTotalMapDTO listStatisticTotalDTO){
            this.listStatisticTotalDTO = listStatisticTotalDTO;
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

    public Map<String, Long> getStatisticTotalLongValues() {
        return statisticTotalLongValues;
    }

    public void setStatisticTotalLongValues(Map<String, Long> statisticTotalLongValues) {
        this.statisticTotalLongValues = statisticTotalLongValues;
    }

    public Map<String, Long> getStatisticTotalDoubleValues() {
        return statisticTotalDoubleValues;
    }

    public void setStatisticTotalDoubleValues(Map<String, Long> statisticTotalDoubleValues) {
        this.statisticTotalDoubleValues = statisticTotalDoubleValues;
    }
}
