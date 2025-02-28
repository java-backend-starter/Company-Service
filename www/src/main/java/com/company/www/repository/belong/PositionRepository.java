package com.company.www.repository.belong;

import com.company.www.entity.belong.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Position save(Position position);

    Position findByPositionId(Long id);

    Position findByPositionName(String positionName);
}
