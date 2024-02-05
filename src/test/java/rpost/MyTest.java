package rpost;

import rpost.company.AbstractDcCompany;
import rpost.company.AbstractDcCompany_;
import rpost.company.AbstractDcCompanyRepository;
import rpost.company.DcCompany;
import rpost.company.DcCompany_;
import rpost.company.DcCompanySeed;
import rpost.company.DcCompanySeed_;
import rpost.company.RcCompany;
import rpost.company.RcCompanyUser;
import jakarta.persistence.criteria.JoinType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import rpost.company.RcCompanyUser_;

import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
public class MyTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    AbstractDcCompanyRepository repo;

    @BeforeEach
    void setup() {
        RcCompany rc = new RcCompany();
        rc.setDisplayName("rc");

        RcCompanyUser rcu = new RcCompanyUser();
        rcu.setRcCompany(rc);

        DcCompanySeed seed = new DcCompanySeed();
        seed.setInvitedBy(rcu);
        seed.setDisplayName("test");

        DcCompany dc = new DcCompany();
        dc.setRcCompany(rc);
        dc.setDisplayName("test");

        em.persistAndFlush(rc);
        em.persistAndFlush(rcu);
        em.persistAndFlush(seed);
        em.persistAndFlush(dc);
    }

    @Test
    void bug1() {
        Specification<DcCompanySeed> specification = (root, query, cb) -> cb.isNotNull(
            root.join(DcCompanySeed_.invitedBy, JoinType.LEFT)
                .join(RcCompanyUser_.rcCompany, JoinType.LEFT)
        );
        repo.findAll((root, query, cb) -> specification.toPredicate(cb.treat(root, DcCompanySeed.class), query, cb));
    }

    @Test
    void bug2() {
        Specification<DcCompanySeed> seedOwnership = (root, query, cb) -> cb.isNotNull(
            root.get(DcCompanySeed_.invitedBy)
                .get(RcCompanyUser_.rcCompany)
        );
        Specification<DcCompany> dcOwnership = (root, query, cb) -> cb.isNotNull(
            root.get(DcCompany_.rcCompany)
        );
        Specification<AbstractDcCompany> dcSpecification = Specification.allOf(
            (root, query, cb) -> cb.equal(root.get(AbstractDcCompany_.DISPLAY_NAME), "test"),
            (root, query, cb) -> dcOwnership.toPredicate(cb.treat(root, DcCompany.class), query, cb)
        );
        Specification<AbstractDcCompany> seedSpecification = Specification.allOf(
            (root, query, cb) -> cb.equal(root.get(AbstractDcCompany_.DISPLAY_NAME), "test"),
            (root, query, cb) -> seedOwnership.toPredicate(cb.treat(root, DcCompanySeed.class), query, cb)
        );

        List<Long> anyOf = repo.findAll(Specification.anyOf(dcSpecification, seedSpecification))
            .stream()
            .map(AbstractDcCompany::getId)
            .collect(Collectors.toList());
        List<Long> dc = repo.findAll(dcSpecification)
            .stream()
            .map(AbstractDcCompany::getId)
            .collect(Collectors.toList());

        Assertions.assertThat(anyOf).containsAll(dc);
    }

}
