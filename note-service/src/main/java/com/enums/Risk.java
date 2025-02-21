package com.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public enum Risk {
	
	NONE("None"),
	BORDERLINE("Borderline"),
	INDANGER("In Danger"),
	EARLYONSET("Early onset"), 
	empty("");

	private String label = null;

	public static Risk getEnum(String value) {
		return Arrays.stream(Risk.values()).filter(m -> (m.toString().equals(value) || m.name().equals(value))).findAny()
				.orElseThrow(() -> new EnumConstantNotPresentException(Risk.class, value));
	}

}