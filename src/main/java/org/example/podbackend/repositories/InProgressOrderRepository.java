package org.example.podbackend.repositories;

import org.example.podbackend.common.enums.StatusOrder;
import org.example.podbackend.entities.InProgressOrder;
import org.example.podbackend.modules.orders.response.OrderFilterResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface InProgressOrderRepository extends BaseRepository<InProgressOrder, Long> {
  InProgressOrder findByIdAndMerchantId(Long id, Long merchantId);

  InProgressOrder findByIdAndMerchantIdAndStatusIn(long id, long merchantId, Collection<StatusOrder> statuses);
  
  @Query("SELECT i " +
          "FROM InProgressOrder i " +
          "WHERE i.merchant.id = :merchantId " +
          "AND i.status IN(:statuses) " +
          "AND i.createdAt >= :fromDate AND i.createdAt <= :toDate"
  )
  List<InProgressOrder> filter(Long merchantId, List<StatusOrder> statuses, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
