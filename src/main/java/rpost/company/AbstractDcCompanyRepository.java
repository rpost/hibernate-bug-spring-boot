package rpost.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AbstractDcCompanyRepository extends JpaRepository<AbstractDcCompany, Long>, JpaSpecificationExecutor<AbstractDcCompany> {
}
