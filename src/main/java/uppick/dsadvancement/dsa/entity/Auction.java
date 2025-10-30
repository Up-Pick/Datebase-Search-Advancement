package uppick.dsadvancement.dsa.entity;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uppick.dsadvancement.dsa.enums.AuctionStatus;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "product_id", nullable = false)
	private Long productId;
	@Column(name = "last_bidder_id", nullable = true)
	private Long lastBidderId;
	@Column(name = "register_id", nullable = false)
	private Long registerId;

	@Column(name = "current_price", nullable = true)
	private Long currentPrice; //현재 입찰가

	@Column(name = "min_price", nullable = false)
	private Long minPrice; //최소 입찰가

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private AuctionStatus status;

	@Column(name = "start_at", nullable = false, updatable = false)
	private LocalDateTime startAt;

	@Column(name = "end_at", nullable = false)
	private LocalDateTime endAt;

	@Builder
	private Auction(Long productId, Long registerId, Long lastBidderId, Long currentPrice, Long minPrice, LocalDateTime startAt,
		AuctionStatus status, LocalDateTime endAt) {

		this.productId = productId;
		this.registerId = registerId;
		this.lastBidderId = lastBidderId;
		this.currentPrice = currentPrice;
		this.minPrice = minPrice;
		this.startAt = startAt;
		this.status = status;
		this.endAt = endAt;
	}
}
