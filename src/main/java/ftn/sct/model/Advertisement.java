package ftn.sct.model;

import java.util.Date;
import java.util.List;

import ftn.sct.enums.AdvertisementStatusEnum;
import ftn.sct.enums.RealEstateTypeEnum;

public class Advertisement {

	private String id;
	private String name;
	private String description;
	private Address address;
	private Float price;
	private AdvertisementStatusEnum status;
	private String email;
	private String telephone;
	private Float numOfRooms;
	private Float numOfBathrooms;
	private RealEstateTypeEnum type;
	private Integer floorNum;
	private Integer buildYear;
	private Boolean furnitureProvided;
	private List<String> equipmentList; // not in hashCode & equals
	private List<String> pictureIds; // not in hashCode & equals
	private Float squareSurface;
	private String ownerId;
	private Date createdDate;
	private Date modifiedDate;
	private Date publishedDate;

	public Advertisement() {

	}

	public Advertisement(String name, RealEstateTypeEnum type) {
		super();
		this.name = name;
		this.type = type;
	}

	public Advertisement(String name, String email, AdvertisementStatusEnum status, RealEstateTypeEnum type) {
		super();
		this.name = name;
		this.status = status;
		this.email = email;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public AdvertisementStatusEnum getStatus() {
		return status;
	}

	public void setStatus(AdvertisementStatusEnum status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Float getNumOfRooms() {
		return numOfRooms;
	}

	public void setNumOfRooms(Float numOfRooms) {
		this.numOfRooms = numOfRooms;
	}

	public Float getNumOfBathrooms() {
		return numOfBathrooms;
	}

	public void setNumOfBathrooms(Float numOfBathrooms) {
		this.numOfBathrooms = numOfBathrooms;
	}

	public RealEstateTypeEnum getType() {
		return type;
	}

	public void setType(RealEstateTypeEnum type) {
		this.type = type;
	}

	public Integer getFloorNum() {
		return floorNum;
	}

	public void setFloorNum(Integer floorNum) {
		this.floorNum = floorNum;
	}

	public Integer getBuildYear() {
		return buildYear;
	}

	public void setBuildYear(Integer buildYear) {
		this.buildYear = buildYear;
	}

	public Boolean getFurnitureProvided() {
		return furnitureProvided;
	}

	public void setFurnitureProvided(Boolean furnitureProvided) {
		this.furnitureProvided = furnitureProvided;
	}

	public List<String> getEquipmentList() {
		return equipmentList;
	}

	public void setEquipmentList(List<String> equipmentList) {
		this.equipmentList = equipmentList;
	}

	public List<String> getPictureIds() {
		return pictureIds;
	}

	public void setPictureIds(List<String> pictureIds) {
		this.pictureIds = pictureIds;
	}

	public Float getSquareSurface() {
		return squareSurface;
	}

	public void setSquareSurface(Float squareSurface) {
		this.squareSurface = squareSurface;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((buildYear == null) ? 0 : buildYear.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((floorNum == null) ? 0 : floorNum.hashCode());
		result = prime * result + ((furnitureProvided == null) ? 0 : furnitureProvided.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((numOfBathrooms == null) ? 0 : numOfBathrooms.hashCode());
		result = prime * result + ((numOfRooms == null) ? 0 : numOfRooms.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((publishedDate == null) ? 0 : publishedDate.hashCode());
		result = prime * result + ((squareSurface == null) ? 0 : squareSurface.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((telephone == null) ? 0 : telephone.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Advertisement other = (Advertisement) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (buildYear == null) {
			if (other.buildYear != null)
				return false;
		} else if (!buildYear.equals(other.buildYear))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (floorNum == null) {
			if (other.floorNum != null)
				return false;
		} else if (!floorNum.equals(other.floorNum))
			return false;
		if (furnitureProvided == null) {
			if (other.furnitureProvided != null)
				return false;
		} else if (!furnitureProvided.equals(other.furnitureProvided))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (numOfBathrooms == null) {
			if (other.numOfBathrooms != null)
				return false;
		} else if (!numOfBathrooms.equals(other.numOfBathrooms))
			return false;
		if (numOfRooms == null) {
			if (other.numOfRooms != null)
				return false;
		} else if (!numOfRooms.equals(other.numOfRooms))
			return false;
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (publishedDate == null) {
			if (other.publishedDate != null)
				return false;
		} else if (!publishedDate.equals(other.publishedDate))
			return false;
		if (squareSurface == null) {
			if (other.squareSurface != null)
				return false;
		} else if (!squareSurface.equals(other.squareSurface))
			return false;
		if (status != other.status)
			return false;
		if (telephone == null) {
			if (other.telephone != null)
				return false;
		} else if (!telephone.equals(other.telephone))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Advertisement [id=" + id + ", name=" + name + ", description=" + description + ", address=" + address
				+ ", price=" + price + ", email=" + email + "]";
	}
}
