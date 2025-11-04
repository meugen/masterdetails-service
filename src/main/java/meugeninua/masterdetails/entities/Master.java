package meugeninua.masterdetails.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Entity
@Table(name = "masters")
public class Master {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "masters_sequence")
    @SequenceGenerator(name = "masters_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "master", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<Detail> details;

    @Column(name = "count", nullable = false)
    private Integer count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
