package rpost.company;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class RcCompanyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private RcCompany rcCompany;

    public RcCompany getRcCompany() {
        return rcCompany;
    }

    public void setRcCompany(RcCompany rcCompany) {
        this.rcCompany = rcCompany;
    }

}
