package ftn.sct.enums;

public enum UserTypeEnum {
	BUYER, SELLER;

	public static UserTypeEnum fromString(String value) {
		if (value.equalsIgnoreCase(BUYER.toString())) {
			return BUYER;
		} else if (value.equalsIgnoreCase(SELLER.toString())) {
			return SELLER;
		}
		return null;
	}
}
