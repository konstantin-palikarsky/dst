package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPlatformUser;

import javax.persistence.*;

@MappedSuperclass
@Inheritance(
        strategy = InheritanceType.TABLE_PER_CLASS
)
public abstract class PlatformUser implements IPlatformUser {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(nullable = false)
    private String tel;

    private Double avgRating;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}
