package com.stayease.api.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Role {
	CUSTOMER("CUSTOMER"),
	HOTELMANAGER("HOTELMANAGER"),
	ADMIN("ADMIN");
	
	private final String value;

    public static Role get(final String name) {
        return Stream.of(Role.values())
            .filter(p -> p.name().equals(name.toUpperCase()) || p.getValue().equals(name.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid badge name: %s", name)));
    }

}