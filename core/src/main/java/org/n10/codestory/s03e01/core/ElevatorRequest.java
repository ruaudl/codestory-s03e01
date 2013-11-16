package org.n10.codestory.s03e01.core;

import java.util.HashMap;
import java.util.Map;

public class ElevatorRequest {

	private String target;
	private Map<String, String> parameters;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getParameter(String key) {
		if (parameters == null) {
			return null;
		}
		return parameters.get(key);
	}

	public void addParameter(String key, String value) {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		this.parameters.put(key, value);
	}

	public Integer getParameterAsInteger(String key) {
		if (parameters == null && parameters.get(key) == null) {
			return null;
		}

		return Integer.valueOf(parameters.get(key));
	}

}
