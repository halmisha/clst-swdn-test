package io.clearstreet.swdn.refdata;

import io.clearstreet.swdn.api.ReferenceDataApi;
import io.clearstreet.swdn.model.Account;
import io.clearstreet.swdn.model.Instrument;
import io.clearstreet.swdn.model.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceDataRepository implements ReferenceDataApi {

  private final Map<String, Member> members = new ConcurrentHashMap<>();
  private final Map<String, Account> accounts = new ConcurrentHashMap<>();
  private final Map<String, Instrument> instruments = new ConcurrentHashMap<>();

  @Override
  public boolean enterInstrument(Instrument instrument) {
    if (instrument == null || instrument.instrumentName() == null) {
      throw new IllegalArgumentException("Instrument or instrument name cannot be null");
    }
    instruments.put(instrument.instrumentName(), instrument);
    return true;
  }

  @Override
  public boolean enterAccount(Account account) {
    if (account == null || account.accountName() == null) {
      throw new IllegalArgumentException("Account or account name cannot be null");
    }
    accounts.put(account.accountName(), account);
    return true;
  }

  @Override
  public boolean enterMember(Member member) {
    if (member == null || member.memberName() == null) {
      throw new IllegalArgumentException("Member or member name cannot be null");
    }
    members.put(member.memberName(), member);
    return true;
  }

  @Override
  public Optional<Instrument> getInstrument(String instrumentName) {
    if (instrumentName == null) {
      return Optional.empty();
      //throw new IllegalArgumentException("Instrument name is null");
    }
    Instrument instrument = instruments.get(instrumentName);
    if (instrument == null) {
      return Optional.empty();
      //throw new IllegalArgumentException("Instrument not found: "+ instrumentName);
    }
    return Optional.of(instrument);
  }

  @Override
  public Optional<Account> getAccount(String accountName) {
    if (accountName == null) {
      return Optional.empty();
      //throw new IllegalArgumentException("Account name is null");
    }
    Account account = accounts.get(accountName);
    if (account == null) {
      return Optional.empty();
      //throw new IllegalArgumentException("Account not found: "+ accountName);
    }
    return Optional.of(account);
  }

  @Override
  public Optional<Member> getMember(String memberName) {
    if (memberName == null) {
      return Optional.empty();
      //throw new IllegalArgumentException("Member name is null");
    }
    Member member = members.get(memberName);
    if (member == null) {
      return Optional.empty();
      //throw new IllegalArgumentException("Member not found: "+ memberName);
    }
    return Optional.of(member);
  }
}
