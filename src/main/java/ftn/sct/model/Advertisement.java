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
	private List<String> equipmentList;
	private Float squareSurface;
	private String ownerId;
	private Date createdDate;
	private Date modifiedDate;
	private Date publishedDate;

}
