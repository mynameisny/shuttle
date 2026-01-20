package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 乘客 Repository
 */
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long>, PagingAndSortingRepository<Passenger, Long>, JpaSpecificationExecutor<Passenger>
{
    /**
     * 根据员工编号查询乘客
     */
    Optional<Passenger> findByEmployeeNumber(String employeeNumber);

    /**
     * 检查员工编号是否已存在
     */
    boolean existsByEmployeeNumber(String employeeNumber);
}
