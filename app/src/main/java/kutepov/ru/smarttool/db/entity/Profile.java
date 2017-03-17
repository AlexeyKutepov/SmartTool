package kutepov.ru.smarttool.db.entity;

import com.j256.ormlite.field.DatabaseField;

/**
 * Профиль-визитка пользователя
 */

public class Profile {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String lastName;

    @DatabaseField
    private String firstName;

    @DatabaseField
    private String middleName;

    @DatabaseField
    private String phone;

    @DatabaseField
    private String email;

    public Profile() {
    }

    public Profile(String lastName, String firstName, String middleName, String phone, String email) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
