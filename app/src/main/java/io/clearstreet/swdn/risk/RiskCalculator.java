package io.clearstreet.swdn.risk;

import io.clearstreet.swdn.api.RiskApi;
import io.clearstreet.swdn.model.Position;
import io.clearstreet.swdn.position.PositionManager;
import io.clearstreet.swdn.price.PriceRepository;
import io.clearstreet.swdn.refdata.ReferenceDataRepository;

public class RiskCalculator implements RiskApi {

  private final PositionManager positionManager;
  private final PriceRepository priceRepository;
  private final ReferenceDataRepository referenceDataRepository;

  public RiskCalculator(PositionManager positionManager, PriceRepository priceRepository,
      ReferenceDataRepository referenceDataRepository) {
    this.positionManager = positionManager;
    this.priceRepository = priceRepository;
    this.referenceDataRepository = referenceDataRepository;
  }

  @Override
  public double calculateAccountPnl(String accountName) {
    double pnl = 0;
    for (Position position : positionManager.getPositionsForAccount(accountName)) {
      pnl += calculatePositionPnl(position);
    }
    return pnl;
  }

  @Override
  public double calculateMemberPnl(String memberName) {
    double pnl = 0;
    for (Position position : positionManager.getPositionsForMember(memberName)) {
      pnl += calculatePositionPnl(position);
    }
    return pnl;
  }

  @Override
  public double calculateMemberMargin(String memberName) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public double calculateMemberMarketRisk(String memberName) {
    double totalUp = 0;
    double totalDown = 0;

    for (Position position : positionManager.getPositionsForMember(memberName)) {
      double marketPrice = priceRepository.getPrice(position.instrumentName()).orElseThrow();
      String instrumentType = referenceDataRepository.getInstrument(position.instrumentName()).get().type().name();

      if ("option".equals(instrumentType)) {
        totalUp += 0.15 * marketPrice * position.quantity();
        totalDown += -0.10 * marketPrice * position.quantity();
      } else if ("stock".equals(instrumentType)) {
        totalUp += 0.20 * marketPrice * position.quantity();
        totalDown += -0.20 * marketPrice * position.quantity();
      }
    }

    return Math.max(totalUp, totalDown);
  }

  private double calculatePositionPnl(Position position) {
    double price = priceRepository.getPrice(position.instrumentName()).orElseThrow();
    return position.quantity() * price - position.initialValue();
  }

}
