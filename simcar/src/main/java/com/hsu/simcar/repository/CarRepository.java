package com.hsu.simcar.repository;

import com.hsu.simcar.domain.Car;
import com.hsu.simcar.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllBySeller(Member seller);
}