package uppick.dsadvancement.dsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import uppick.dsadvancement.dsa.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

}
