package com.hsu.simcar.repository;

import com.hsu.simcar.domain.Car;
import com.hsu.simcar.domain.Favorite;
import com.hsu.simcar.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByMemberAndCar(Member member, Car car);
    Optional<Favorite> findByMemberAndCar(Member member, Car car);
    List<Favorite> findAllByMember(Member member);
    void deleteAllByMember(Member member);
}