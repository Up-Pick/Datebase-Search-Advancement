package uppick.dsadvancement.dsa.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uppick.dsadvancement.dsa.entity.Auction;
import uppick.dsadvancement.dsa.enums.AuctionStatus;
import uppick.dsadvancement.dsa.repository.AuctionRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyAuctionDataInitializer {

	private final AuctionRepository auctionRepository;

	private static final int TOTAL = 1_000_000;
	private static final int BATCH_SIZE = 1000;

	@PostConstruct
	public void init() {

		long count = auctionRepository.count();
		if (count > 0) return;

		Random random = new Random();
		AuctionStatus[] values = AuctionStatus.values();

		List<Auction> auctionList = new ArrayList<>();

		LongStream.range(0, TOTAL).forEach(i -> {

			int randomStatus = random.nextInt(values.length);

			Auction auction = Auction.builder()
				.productId(i)
				.registerId(random.nextLong(100_000L))
				.lastBidderId((randomStatus < 2) ? random.nextLong(100_000L) : null)
				.currentPrice((randomStatus < 2) ? 60_000L + random.nextLong(30_000L) : null)
				.minPrice(10_000L + random.nextLong(40_000L))
				.startAt(LocalDateTime.now().plusDays(random.nextInt(10)))
				.status(values[randomStatus])
				.endAt(LocalDateTime.now()
					.plusDays(15 + random.nextInt(10))
					.plusHours(random.nextInt(24))
					.plusMinutes(random.nextInt(60))
					.plusSeconds(random.nextInt(60)))
				.build();

			auctionList.add(auction);

			if (i % BATCH_SIZE == 0) {
				auctionRepository.saveAll(auctionList);
				auctionList.clear();
			}

			// 4️⃣ 진행 로그
			if (i % 10000 == 0) {
				log.info("Inserted {} records", i);
			}
		});


		// 남은 데이터 처리
		if (!auctionList.isEmpty())
			auctionRepository.saveAll(auctionList);

		log.info("Inserted All records -> {}", auctionRepository.count());
	}
}
