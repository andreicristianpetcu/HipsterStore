package net.petcu.store.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PricedProduct.
 */
@Entity
@Table(name = "priced_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PricedProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "updated_date")
    private Instant updatedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    private Price price;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PricedProduct id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return this.active;
    }

    public PricedProduct active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getUpdatedDate() {
        return this.updatedDate;
    }

    public PricedProduct updatedDate(Instant updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public PricedProduct product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Price getPrice() {
        return this.price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public PricedProduct price(Price price) {
        this.setPrice(price);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PricedProduct)) {
            return false;
        }
        return getId() != null && getId().equals(((PricedProduct) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PricedProduct{" +
            "id=" + getId() +
            ", active='" + getActive() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            "}";
    }
}
