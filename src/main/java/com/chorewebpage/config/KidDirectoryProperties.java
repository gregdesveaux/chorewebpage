package com.chorewebpage.config;

import com.chorewebpage.model.Kid;
import com.chorewebpage.notification.KidContact;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kids")
public class KidDirectoryProperties {

    private final Map<Kid, KidContact> contacts = new EnumMap<>(Kid.class);

    public Map<Kid, KidContact> getContacts() {
        return contacts;
    }

    public KidContact getContactFor(Kid kid) {
        KidContact contact = contacts.get(kid);
        if (contact != null) {
            return contact;
        }
        KidContact fallback = new KidContact();
        fallback.setName(kid.name());
        return fallback;
    }

    public String getContactName(Kid kid) {
        KidContact contact = getContactFor(kid);
        return contact.getName() != null ? contact.getName() : kid.name();
    }
}
