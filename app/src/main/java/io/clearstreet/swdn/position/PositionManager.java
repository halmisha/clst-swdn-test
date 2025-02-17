package io.clearstreet.swdn.position;

import io.clearstreet.swdn.api.PositionApi;
import io.clearstreet.swdn.api.TradeApi;
import io.clearstreet.swdn.model.Account;
import io.clearstreet.swdn.model.Position;
import io.clearstreet.swdn.model.Trade;
import io.clearstreet.swdn.model.TradeSide;
import io.clearstreet.swdn.refdata.ReferenceDataRepository;

import java.util.*;

public class PositionManager implements TradeApi, PositionApi {

  private final ReferenceDataRepository referenceDataManager;
  private final List<Trade> trades = new ArrayList<>();

  public PositionManager(ReferenceDataRepository referenceDataManager) {
    this.referenceDataManager = referenceDataManager;
  }

  @Override
  public boolean enterTrade(Trade trade) {
    if (trade == null || trade.tradeId() == null || trade.accountName() == null || trade.instrumentName() == null) {
      throw new IllegalArgumentException("Trade or its fields cannot be null");
    }

    switch (trade.tradeType()) {
      case NEW:
        trades.add(trade);
        break;
      case REPLACE:
        if (trades.stream().noneMatch(t -> t.tradeId().equals(trade.tradeId()))) {
          throw new IllegalArgumentException("Trade to replace does not exist: " + trade.tradeId());
        }
        trades.removeIf(t -> t.tradeId().equals(trade.tradeId()));
        trades.add(trade);
        break;
      case CANCEL:
        if (trades.stream().noneMatch(t -> t.tradeId().equals(trade.tradeId()))) {
          throw new IllegalArgumentException("Trade to cancel does not exist: " + trade.tradeId());
        }
        trades.removeIf(t -> t.tradeId().equals(trade.tradeId()));
        break;
      default:
        throw new IllegalArgumentException("Invalid trade type: " + trade.tradeType());
    }
    return true;
  }

  @Override
  public List<Position> getPositionsForMember(String memberName) {
    Map<PositionKey, Position> positions = new HashMap<>();
    for (Trade trade : trades) {
      Optional<Account> accountOpt = referenceDataManager.getAccount(trade.accountName());
      if (accountOpt.isPresent() && accountOpt.get().memberName().equals(memberName)) {
        PositionKey key = new PositionKey(trade.accountName(), trade.instrumentName());
        Position position = positions.get(key);
        if (position == null) {
          position = new Position(trade.accountName(), trade.instrumentName(), 0, 0);
        }
        double quantityChange = trade.side() == TradeSide.BUY ? trade.quantity() : -trade.quantity();

        positions.put(key, new Position(trade.accountName(), trade.instrumentName(),
                position.quantity() + quantityChange,
                position.initialValue() + quantityChange * trade.price()));
      }
    }
    return new ArrayList<>(positions.values());
  }

  @Override
  public List<Position> getPositionsForAccount(String accountName) {
    Map<PositionKey, Position> positions = new HashMap<>();
    for (Trade trade : trades) {
      if (trade.accountName().equals(accountName)) {
        PositionKey key = new PositionKey(trade.accountName(), trade.instrumentName());
        Position position = positions.get(key);
        if (position == null) {
          position = new Position(trade.accountName(), trade.instrumentName(), 0, 0);
        }

        double quantityChange = trade.side().equals(TradeSide.BUY) ? trade.quantity() : -trade.quantity();

        positions.put(key, new Position(trade.accountName(), trade.instrumentName(),
            position.quantity() + quantityChange,
            position.initialValue() + quantityChange * trade.price()));
      }
    }
    return new ArrayList<>(positions.values());
  }

  private record PositionKey(String accountName, String instrumentName) {

  }
}
