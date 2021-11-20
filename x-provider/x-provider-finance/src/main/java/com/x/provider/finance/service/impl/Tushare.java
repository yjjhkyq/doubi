package com.x.provider.finance.service.impl;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.http.HttpRequest;
import com.x.core.utils.JsonUtil;
import com.x.provider.finance.configure.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class Tushare {

	private static final String host = "http://api.waditu.com";
	private final ApplicationConfig applicationConfig;

	public Tushare(ApplicationConfig applicationConfig){
		this.applicationConfig = applicationConfig;
	}

	public List<Map> Data(String api_name, String fields, Map<String, Object> params) {
		Map<String, Object> request = new HashMap<>();
		request.put("api_name", api_name);
		request.put("fields", fields);
		request.put("token", applicationConfig.getTushareToken());
		request.put("params", params);
		String result = HttpRequest.post(host).body(JsonUtil.toJSONString(request)).execute().body();
		
		if (StringUtils.isEmpty(result)) {
			throw new RuntimeException(StrFormatter.format("get stock date error api_name{} ", api_name)) ;
		}
		TushareAjaxResult mapResult = new TushareAjaxResult(JsonUtil.parseObject(result, HashMap.class));
		if (!mapResult.isSuccess()) {
			throw new RuntimeException(StrFormatter.format("get stock data ajax result error {}", result)) ;
		}
		return mapResult.getData();
	}
	
	private List<Map> covertToMapBeanList(final List<String> fields, List<List<Object>> fieldValues) {
		 final List<Map> result = new ArrayList<>();
		 if (fieldValues.size() == 0) {
			return result;
		}
		fieldValues.stream().forEach((s) -> {
			result.add(covertToMap(fields, s));
		});
		return result;
	}
	

	private Map<String, Object> covertToMap(List<String> fields, List<Object> fieldValues) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (fieldValues == null || fieldValues.size() == 0) {
			return result;
		}
		for(int i = 0; i < fields.size(); i ++) {
			result.put(fields.get(i), fieldValues.get(i));
		}
		return result;
	}
	
	public class TushareAjaxResult
	{
		private HashMap result;
		
		public TushareAjaxResult(HashMap hashMap) {
			result = hashMap;
		}

		public int getCode() {
			return Integer.parseInt(String.valueOf(result.get("code")));
		}
		
		public String msg() {
			return String.valueOf(result.get("msg"));
		}

		public boolean isSuccess(){
			return getCode() == 0;
		}

		public List<Map> getData() {
			HashMap<String, Object> data = (HashMap<String, Object>)result.get("data");
			return covertToMapBeanList((List<String>)data.get("fields"), (List<List<Object>>)data.get("items"));
		}
	}
	
	public List<Map> stockBasic() {
		String fields = "ts_code,symbol,name,cnspell,area,industry,fullname,enname,market,exchange,curr_type,list_status,"
				+ "list_date,delist_date,is_hs";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("is_hs", null);
		params.put("list_status", null);
		params.put("exchange", null);
		return Data("stock_basic", fields, params);
	}

}
