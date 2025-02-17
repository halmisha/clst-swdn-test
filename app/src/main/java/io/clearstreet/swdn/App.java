/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.clearstreet.swdn;

import io.clearstreet.swdn.api.PositionApi;
import io.clearstreet.swdn.api.PriceApi;
import io.clearstreet.swdn.api.ReferenceDataApi;
import io.clearstreet.swdn.api.RiskApi;
import io.clearstreet.swdn.api.TradeApi;
import io.clearstreet.swdn.position.PositionManager;
import io.clearstreet.swdn.price.PriceRepository;
import io.clearstreet.swdn.refdata.ReferenceDataRepository;
import io.clearstreet.swdn.risk.RiskCalculator;

public class App {

  private final ReferenceDataRepository referenceDataRepository = new ReferenceDataRepository();
  private final PriceRepository priceRepository = new PriceRepository();
  private final PositionManager positionManager = new PositionManager(referenceDataRepository);
  private final RiskCalculator riskCalculator = new RiskCalculator(positionManager,
      priceRepository, referenceDataRepository);

  public App() {
    start();
  }

  private void start() {
    // Do nothing for now
  }

  public PositionApi getPositionApi() {
    return positionManager;
  }

  public ReferenceDataApi getReferenceDataApi() {
    return referenceDataRepository;
  }

  public TradeApi getTradeApi() {
    return positionManager;
  }

  public RiskApi getRiskApi() {
    return riskCalculator;
  }

  public PriceApi getPriceApi() {
    return priceRepository;
  }

}
