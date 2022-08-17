package pers.lzy.template.excel.test.simple;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/28  17:06
 * 个人名片
 */
public class Card {

    private String name;

    private String phoneNumber;

    private String email;

    private String company;

    public Card() {
    }

    public Card(String name, String phoneNumber, String email, String company) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
