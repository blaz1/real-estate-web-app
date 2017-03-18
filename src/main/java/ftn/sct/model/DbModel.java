package ftn.sct.model;

import org.springframework.data.annotation.Id;

public abstract class DbModel {

	@Id
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
