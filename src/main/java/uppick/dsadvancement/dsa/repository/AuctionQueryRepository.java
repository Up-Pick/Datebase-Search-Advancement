package uppick.dsadvancement.dsa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import uppick.dsadvancement.dsa.Dto.response.ProductPurchasedDto;
import uppick.dsadvancement.dsa.entity.QAuction;
import uppick.dsadvancement.dsa.enums.AuctionStatus;

@Repository
@RequiredArgsConstructor
public class AuctionQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QAuction auction = QAuction.auction;

	public Page<ProductPurchasedDto> getPurchasedProductInfoByRegisterId(long registerId, Pageable pageable) {

		List<ProductPurchasedDto> qResponseList = jpaQueryFactory
			.select(
				Projections.constructor(
					ProductPurchasedDto.class,
					auction.currentPrice
				)
			)
			.from(auction)
			.where(auction.lastBidderId.eq(registerId)
				.and(auction.status.eq(AuctionStatus.FINISHED)))
			.orderBy(auction.endAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = Optional.ofNullable(
			jpaQueryFactory
				.select(auction.count())
				.from(auction)
				.where(auction.registerId.eq(registerId)
					.and(auction.status.eq(AuctionStatus.FINISHED)))
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(qResponseList, pageable, total);
	}
}
