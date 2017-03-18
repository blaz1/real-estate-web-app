package ftn.sct;

import ftn.sct.model.DbModel;

public class ResponseWrapper<T extends DbModel> {
	private T object;
	private String error;

	public ResponseWrapper() {
		new ResponseWrapper<>(null, null);
	}

	public ResponseWrapper(T object) {
		new ResponseWrapper<>(object, null);
	}

	public ResponseWrapper(String error) {
		new ResponseWrapper<T>(null, error);
	}

	public ResponseWrapper(T object, String error) {
		this.object = object;
		this.error = error;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
