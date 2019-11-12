package skyjacker.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "shocks")
public class SkyShock {

    @Id
    @Column(name = "SHOCK_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "WEB_LINK")
    private String webLink;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "DESCRIPTION")
    private String desc;

    @Column(name = "IMG_LINKS")
    private String imgLinks;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "shocks_categories",
            joinColumns = { @JoinColumn(name = "SHOCK_ID") },
            inverseJoinColumns = { @JoinColumn(name = "CATEGORY_ID") }
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "shocks_sk_notes",
            joinColumns = { @JoinColumn(name = "SHOCK_ID") },
            inverseJoinColumns = { @JoinColumn(name = "NOTE_ID") }
    )
    private Set<SpecAndKitNote> notes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shock")
    private Set<Fitment> fitments = new HashSet<>();

    @Override
    public String toString() {
        return "SkyShock{" +
                "webLink='" + webLink + '\'' +
                ", title='" + title + '\'' +
                ", sku='" + sku + '\'' +
                ", desc='" + desc + '\'' +
                ", imgLinks='" + imgLinks + '\'' +
                '}';
    }

    public String getWebLink() {
        return webLink;
    }
    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getImgLinks() {
        return imgLinks;
    }
    public void setImgLinks(String imgLinks) {
        this.imgLinks = imgLinks;
    }
    public Set<Category> getCategories() {
        return categories;
    }
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
    public Set<SpecAndKitNote> getNotes() {
        return notes;
    }
    public void setNotes(Set<SpecAndKitNote> notes) {
        this.notes = notes;
    }
    public Set<Fitment> getFitments() {
        return fitments;
    }
    public void setFitments(Set<Fitment> fitments) {
        this.fitments = fitments;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
}
