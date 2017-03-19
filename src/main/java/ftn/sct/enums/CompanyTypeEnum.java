package ftn.sct.enums;

public enum CompanyTypeEnum {
	DOO, SP, DD;

	public static CompanyTypeEnum fromString(String value) {
		if (value.equalsIgnoreCase(DOO.toString())) {
			return DOO;
		} else if (value.equalsIgnoreCase(SP.toString())) {
			return SP;
		} else if (value.equalsIgnoreCase(DD.toString())) {
			return DD;
		}
		return null;
	}
}
