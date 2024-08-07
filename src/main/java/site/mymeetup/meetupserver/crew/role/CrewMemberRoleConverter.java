package site.mymeetup.meetupserver.crew.role;

import jakarta.persistence.AttributeConverter;

public class CrewMemberRoleConverter implements AttributeConverter<CrewMemberRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CrewMemberRole attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Unknown role status");
        }
        return attribute.getStatus();
    }

    @Override
    public CrewMemberRole convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Unknown role status");
        }
        return CrewMemberRole.enumOf(dbData);
    }

}
