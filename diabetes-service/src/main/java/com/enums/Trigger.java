package com.enums;

import java.util.Arrays;
import com.google.common.base.MoreObjects;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum Trigger {

    HEMOGLOBINE("Hémoglobine A1C"),
    MICROALBUMINE("Microalbumine"),
    TAILLE("Taille"),
    POIDS("Poids"),
    FUMEUR("Fumeur"),
    FUMEUSE("Fumeuse"),
    ANORMAL("Anormal"),
    CHOLESTEROL("Cholestérol"),
    VERTIGES("Vertiges"),
    RECHUTE("Rechute"),
    REACTION("Réaction"),
    ANTICORPS("Anticorps");
	
	
	
    private String label = null;

	@Override
	public String toString() {
		return MoreObjects.firstNonNull(this.label, this.name());
	}

	public static Trigger getEnum(String value) {
		return Arrays.stream(Trigger.values()).filter(m -> (m.toString().equals(value) || m.name().equals(value))).findAny()
				.orElseThrow(() -> new EnumConstantNotPresentException(Trigger.class, value));
	}

	
	
}
