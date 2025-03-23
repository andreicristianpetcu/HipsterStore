package net.petcu.store.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;
import net.petcu.store.domain.enumeration.DiscountType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Discount.
 */
@Entity
@Table(name = "discount")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Discount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "discount_code")
    private UUID discountCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType;

    @Column(name = "used")
    private Boolean used;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Discount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getDiscountCode() {
        return this.discountCode;
    }

    public Discount discountCode(UUID discountCode) {
        this.setDiscountCode(discountCode);
        return this;
    }

    public void setDiscountCode(UUID discountCode) {
        this.discountCode = discountCode;
    }

    public DiscountType getDiscountType() {
        return this.discountType;
    }

    public Discount discountType(DiscountType discountType) {
        this.setDiscountType(discountType);
        return this;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public Boolean getUsed() {
        return this.used;
    }

    public Discount used(Boolean used) {
        this.setUsed(used);
        return this;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Discount)) {
            return false;
        }
        return getId() != null && getId().equals(((Discount) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Discount{" +
            "id=" + getId() +
            ", discountCode='" + getDiscountCode() + "'" +
            ", discountType='" + getDiscountType() + "'" +
            ", used='" + getUsed() + "'" +
            "}";
    }
}
