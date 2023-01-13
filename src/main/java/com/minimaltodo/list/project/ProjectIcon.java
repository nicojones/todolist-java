package com.minimaltodo.list.project;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectIcon {
    /**
     * DEFAULT!
     */
    CIRCLE("circle"),

    GRADE("grade"),
    FAVORITE("favorite"),
    HOME("home"),
    PUBLIC("public"),
    ROCKET("rocket"),
    MOOD("mood"),
    NAVIGATION("navigation"),
    // CALENDAR_TODAY("calendar today"),
    EXTENSION("extension"),
    SUNNY("sunny"),
    LIGHTBULB("lightbulb"),
    COMMENT("comment"),
    LANDSCAPE("landscape"),
    BUILDING("building"),
    PHOTO("photo");

    public final String label;

    private ProjectIcon(String label) {
        this.label = label;
    }

    @JsonCreator
	public static ProjectIcon decode(final String code) {
		return Stream.of(ProjectIcon.values()).filter(targetEnum -> targetEnum.label.equals(code)).findFirst().orElse(null);
	}
	
	@JsonValue
	public String getLabel() {
		return label;
	}
}
