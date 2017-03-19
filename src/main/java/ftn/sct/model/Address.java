package ftn.sct.model;

public class Address {
	private String id;
	private City city;
	private String street;
	private String houseNumber;
	private Integer floor;
	private Float appartmentNumber;
	private long langitude;
	private long longitude;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public Float getAppartmentNumber() {
		return appartmentNumber;
	}

	public void setAppartmentNumber(Float appartmentNumber) {
		this.appartmentNumber = appartmentNumber;
	}

	public long getLangitude() {
		return langitude;
	}

	public void setLangitude(long langitude) {
		this.langitude = langitude;
	}

	public long getLongitude() {
		return longitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appartmentNumber == null) ? 0 : appartmentNumber.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((floor == null) ? 0 : floor.hashCode());
		result = prime * result + ((houseNumber == null) ? 0 : houseNumber.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (int) (langitude ^ (langitude >>> 32));
		result = prime * result + (int) (longitude ^ (longitude >>> 32));
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (appartmentNumber == null) {
			if (other.appartmentNumber != null)
				return false;
		} else if (!appartmentNumber.equals(other.appartmentNumber))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (floor == null) {
			if (other.floor != null)
				return false;
		} else if (!floor.equals(other.floor))
			return false;
		if (houseNumber == null) {
			if (other.houseNumber != null)
				return false;
		} else if (!houseNumber.equals(other.houseNumber))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (langitude != other.langitude)
			return false;
		if (longitude != other.longitude)
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Address [city=" + city + ", street=" + street + ", houseNumber=" + houseNumber + ", floor=" + floor
				+ ", appartmentNumber=" + appartmentNumber + ", langitude=" + langitude + ", longitude=" + longitude
				+ "]";
	}

}
