package ftn.sct.model;

import java.sql.Timestamp;
import java.util.Date;

public class User extends DbModel {

	private String username;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String telephone;
	private String picture; // TODO implement picture
	// TODO implement type enumerator
	private Date registeredDate;
	private Timestamp lastVisited;

	public User() {
	}

	public User(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	public Timestamp getLastVisited() {
		return lastVisited;
	}

	public void setLastVisited(Timestamp lastVisited) {
		this.lastVisited = lastVisited;
	}

	@Override
	public String toString() {
		// TODO override
		return String.format("Customer[id=%s, firstName='%s', lastName='%s']", id, firstName, lastName);
	}

}