package io.clearstreet.swdn.price;

import io.clearstreet.swdn.api.PriceApi;
import io.clearstreet.swdn.model.Price;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PriceRepository implements PriceApi {

  Map<String, Price> prices = new HashMap<>();

  @Override
  public boolean enterPrice(Price price) {
    if (price == null || price.instrumentName() == null) {
      throw new IllegalArgumentException("Price or instrument name cannot be null");
    }
    if (price.price() < 0) {
      throw new IllegalArgumentException("Price value cannot be negative");
    }
    prices.put(price.instrumentName(), price);
    return true;
  }

  @Override
  public Optional<Double> getPrice(String instrumentName) {
    if (instrumentName == null) {
      return Optional.empty();
    }
    Price price = prices.get(instrumentName);
    if (price == null) {
      throw new IllegalArgumentException("Price not found for instrument: "+ instrumentName);
    }
    return Optional.of(price).map(Price::price);
  }
}
